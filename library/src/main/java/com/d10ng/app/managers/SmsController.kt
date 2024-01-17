package com.d10ng.app.managers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import androidx.core.database.getLongOrNull
import com.d10ng.app.startup.ctx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 短信管理器
 * @Author d10ng
 * @Date 2024/1/16 15:11
 */
object SmsController {

    data class Data(
        /**
         * 短信ID
         * > 会重复的，因为当用户删除短信数据库里的短信后，下一条短信就会重复用这个ID
         */
        var id: String = "",

        /**
         * 插卡位置
         * -1 - 曾经插入的卡，现在不在手机里
         * 0 - 卡槽1
         * 1 - 卡槽2
         */
        var subId: Long = -1,

        /**
         * 来源号码
         */
        var phone: String = "",

        /**
         * 内容
         */
        var content: String = "",

        /**
         * 消息到达的时间戳
         */
        var time: Long = 0
    )

    private val projection = arrayOf("_id", "sub_id", "address", "body", "date")
    private val inboxUri = Uri.parse("content://sms/inbox")

    // 最新短信接收事件Flow
    private val _receiveFlow = MutableSharedFlow<Data>()
    val receiveFlow = _receiveFlow.asSharedFlow()

    /**
     * 短信监听器
     * > 需要权限：android.permission.RECEIVE_SMS
     */
    class Receiver : BroadcastReceiver() {
        val scope = CoroutineScope(Dispatchers.IO)
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
            scope.launch {
                val phoneSubId = intent.extras?.getInt("phone", -1) ?: -1
                val fullMsg = messages.joinToString("") { it.messageBody ?: "" }
                val m = messages[0]
                val data = Data().apply {
                    id = 0.toString()
                    subId = phoneSubId.toLong()
                    phone = m.originatingAddress ?: ""
                    content = fullMsg
                    time = m.timestampMillis
                }
                while (data.id == 0.toString()) {
                    readLatestByTime(data.time).find {
                        it.phone == data.phone && it.content.contains(
                            data.content
                        )
                    }
                        ?.let {
                            data.id = it.id
                            data.subId = it.subId
                            data.time = it.time
                            data.content = it.content
                        }
                    delay(100)
                }
                _receiveFlow.emit(data)
            }
        }
    }

    /**
     * 读取最新一条短信
     * > 需要权限：android.permission.READ_SMS
     * @return Data?
     */
    fun readLatest(): Data? {
        try {
            ctx.contentResolver.query(
                inboxUri, projection, null, null,
                "date desc limit 1"
            )?.use { c ->
                if (c.moveToFirst()) {
                    return c.getData()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 读取最近N条短信
     * > 需要权限：android.permission.READ_SMS
     * @param count Int
     * @return List<Data>
     */
    fun readLatest(count: Int): List<Data> {
        if (count <= 0) return listOf()
        try {
            ctx.contentResolver.query(
                inboxUri, projection, null, null,
                "date desc limit $count"
            )?.use { c ->
                val list = mutableListOf<Data>()
                while (c.moveToNext()) {
                    list.add(c.getData())
                }
                return list
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }

    /**
     * 读取指定时间以后的全部消息
     * > 需要权限：android.permission.READ_SMS
     * @param timestamp Long 时间戳，单位毫秒，获取的是大于等于这个时间的短信
     * @return List<Data>
     */
    fun readLatestByTime(timestamp: Long): List<Data> {
        try {
            ctx.contentResolver.query(
                inboxUri, projection,
                "( date >= $timestamp )", null, "date asc"
            )?.use { c ->
                val list = mutableListOf<Data>()
                while (c.moveToNext()) {
                    list.add(c.getData())
                }
                return list
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }

    /**
     * 从Cursor中读取数据
     * @receiver Cursor
     * @return Data
     */
    private fun Cursor.getData(): Data {
        return Data().apply {
            id = getString(getColumnIndex("_id").coerceAtLeast(0))
            subId = getLongOrNull(getColumnIndex("sub_id")) ?: -1
            content = getString(getColumnIndex("body").coerceAtLeast(0))
            phone = getString(getColumnIndex("address").coerceAtLeast(0))
            time = getString(getColumnIndex("date").coerceAtLeast(0)).toLongOrNull() ?: 0
        }
    }

    /**
     * 发送文本短信
     * > 需要权限：android.permission.SEND_SMS、android.permission.READ_PHONE_STATE
     * > 发送失败会抛出对应异常。
     * @param desAddress String 目标号码
     * @param scAddress String? 短信中心号码，默认为null
     * @param content String 短信内容
     * @param overtime Long 超时时间，默认为60秒
     * @param use SmsManager? 使用的短信管理器，相当于指定发送卡槽，不填则使用默认的
     */
    suspend fun sendText(
        desAddress: String,
        scAddress: String? = null,
        content: String,
        overtime: Long = 60 * 1000,
        use: SmsManager? = null
    ) {
        // 判断是否具备发送短信的权限
        if (PermissionManager.has(
                arrayOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE
                )
            ).not()
        ) {
            throw Exception("缺少发送短信的权限！")
        }
        // 获取短信管理器
        val smsManager = use ?: getDefaultSmsManager()
        // 读取当前时间戳，做一个唯一标记
        val time = System.currentTimeMillis()
        // 发送状态监听标记
        val ssa = "SSA-${time}"
        // 接受状态监听标记
        val dsa = "DSA-${time}"
        coroutineScope {
            // 启动发送结果监听
            val sendResultDeferred = async { listenSendResult(ssa, overtime) }
            // 启动接收结果监听
            val receiveResultDeferred = async { listenReceiveResult(dsa, overtime) }

            // 发送短信
            smsManager.sendTextMessage(
                desAddress,
                scAddress,
                content,
                PendingIntent.getBroadcast(ctx, 0, Intent(ssa), PendingIntent.FLAG_UPDATE_CURRENT),
                PendingIntent.getBroadcast(ctx, 1, Intent(dsa), PendingIntent.FLAG_UPDATE_CURRENT)
            )

            // 等待结果
            val sendResult = sendResultDeferred.await()
            val receiveResult = receiveResultDeferred.await()

            // 处理结果
            if (sendResult != true) {
                throw Exception("发送短信失败！")
            }
            if (receiveResult != true) {
                throw Exception("目标用户未成功接收短信！")
            }
        }
    }

    /**
     * 获取默认的短信管理器
     * @return SmsManager
     */
    private fun getDefaultSmsManager(): SmsManager {
        val defSubId = SubscriptionManager.getDefaultSmsSubscriptionId()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ctx.getSystemService(SmsManager::class.java).createForSubscriptionId(defSubId)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
    }

    /**
     * 通过指定卡槽获取短信管理器
     * > 需要权限：android.permission.READ_PHONE_STATE
     * @param slot Int 卡槽
     * @return SmsManager
     */
    @SuppressLint("MissingPermission")
    fun getSmsManager(slot: Int): SmsManager {
        if (slot < 0) return getDefaultSmsManager()
        val subId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            SubscriptionManager.getSubscriptionId(slot)
        } else {
            ctx.getSystemService(SubscriptionManager::class.java)
                .getActiveSubscriptionInfoForSimSlotIndex(slot)?.subscriptionId
        }
        if (subId == null) return getDefaultSmsManager()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ctx.getSystemService(SmsManager::class.java).createForSubscriptionId(subId)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getSmsManagerForSubscriptionId(subId)
        }
    }

    /**
     * 监听短信发送或接收结果
     * @param action String 广播接收器的意图过滤器动作
     * @param overtime Long 超时时间，单位毫秒
     * @param onReceive (resultCode: Int, intent: Intent?) -> Boolean  接收到广播时的处理函数，返回Boolean表示成功或失败
     * @return Boolean? 发送成功返回true, 发送失败返回false, 超时返回null
     */
    private suspend fun listenForResult(
        action: String,
        overtime: Long,
        onReceive: (Int, Intent?) -> Boolean = { _, _ -> true }
    ): Boolean? {
        val channel = Channel<Boolean>()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                CoroutineScope(Dispatchers.IO).launch {
                    channel.send(onReceive(resultCode, intent))
                }
            }
        }

        ctx.registerReceiver(receiver, IntentFilter(action))

        return try {
            withTimeoutOrNull(overtime) {
                channel.receive()
            }
        } finally {
            ctx.unregisterReceiver(receiver)
            channel.close()
        }
    }

    /**
     * 发送短信结果监听
     * @param action String
     * @param overtime Long
     * @return Boolean?
     */
    private suspend fun listenSendResult(action: String, overtime: Long): Boolean? {
        return listenForResult(action, overtime) { code, _ ->
            code == Activity.RESULT_OK
        }
    }

    /**
     * 发送短信目标用户接收监听
     * @param action String
     * @param overtime Long
     * @return Boolean?
     */
    private suspend fun listenReceiveResult(action: String, overtime: Long): Boolean? {
        return listenForResult(action, overtime)
    }
}