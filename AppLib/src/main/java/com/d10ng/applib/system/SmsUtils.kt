package com.d10ng.applib.system

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getStringOrNull
import androidx.lifecycle.MutableLiveData
import com.d10ng.applib.bean.SimInfo
import com.d10ng.applib.bean.SmsInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 短信发送状态
 * @Author: D10NG
 * @Time: 2021/3/4 7:41 下午
 */
enum class SendSmsStatus{
    // 发送中
    SENDING,
    // 发送成功
    SUCCESS,
    // 发送失败
    FAILED,
    // 目标用户接收成功
    RECEIVE,
    // 发送超时
    OVERTIME
}

/**
 * 发送短信
 * @receiver Context
 * @param destinationAddress String 目标电话号码
 * @param scAddress String 短信中心号码
 * @param text String 短信内容
 * @param overTime Long 超时时间
 * @return MutableLiveData<SendSmsStatus>? 发送状态
 */
fun Context.sendSmsMessage(
    destinationAddress: String,
    scAddress: String?,
    text: String,
    overTime: Long = 60 * 1000
): MutableLiveData<SendSmsStatus> {
    // 新建一个发送状态
    var statusLive: MutableLiveData<SendSmsStatus>? = MutableLiveData(SendSmsStatus.SENDING)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
        // 缺少发送短信权限
        statusLive?.postValue(SendSmsStatus.FAILED)
    } else if (!isHasSatelliteSignal()) {
        // 缺少手机卡/没有信号
        statusLive?.postValue(SendSmsStatus.FAILED)
    } else {
        // 新建子线程安排发送
        GlobalScope.launch {
            // 读取当前时间戳，做一个唯一标记
            val time = System.currentTimeMillis()
            // 发送状态监听标记
            val sentSmsAction = "SSA-${time}"
            // 接受状态监听标记
            val deliveredSmsAction = "DSA-${time}"
            // 选择手机卡进行发送
            getSatelliteSmsManager().sendTextMessage(
                destinationAddress, scAddress, text,
                PendingIntent.getBroadcast(this@sendSmsMessage, 0, Intent(sentSmsAction), 0),
                PendingIntent.getBroadcast(this@sendSmsMessage, 1, Intent(deliveredSmsAction), 0)
            )
            // 发送监听
            registerReceiver(object : BroadcastReceiver(){
                override fun onReceive(context: Context?, intent: Intent?) {
                    //LogUtils.e("发送短信", "结果码=$resultCode")
                    if (resultCode == Activity.RESULT_OK) {
                        // 发送成功
                        statusLive?.postValue(SendSmsStatus.SUCCESS)
                    } else {
                        // 发送失败
                        statusLive?.postValue(SendSmsStatus.FAILED)
                    }
                    // 取消监听
                    context?.unregisterReceiver(this)
                }
            }, IntentFilter(sentSmsAction))
            // 目标用户接收监听
            registerReceiver(object : BroadcastReceiver(){
                override fun onReceive(context: Context?, intent: Intent?) {
                    // 接收成功
                    statusLive?.postValue(SendSmsStatus.RECEIVE)
                    statusLive = null
                    // 取消监听
                    context?.unregisterReceiver(this)
                }
            }, IntentFilter(sentSmsAction))
            // 超时
            delay(overTime)
            if (statusLive?.value == SendSmsStatus.SENDING) {
                statusLive?.postValue(SendSmsStatus.OVERTIME)
            }
            statusLive = null
        }
    }
    return statusLive!!
}

/**
 * 获取手机卡信息列表
 * @receiver Context
 * @return List<SubscriptionInfo>?
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@SuppressLint("MissingPermission")
fun Context.getPhoneCardInfoList(): List<SubscriptionInfo>? {
    // 手机卡管理器
    val subscriptionManager = getSystemService(AppCompatActivity.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    // 拿到手机里面的手机卡列表
    return subscriptionManager.activeSubscriptionInfoList
}

/**
 * 判断是否拥有手机卡
 * @receiver Context
 * @return Boolean
 */
fun Context.isHasPhoneCard(): Boolean {
    return getSimInfo().find { it.simId.toIntOrNull()?: -1 >= 0 } != null
}

/**
 * 获取卫星卡信息
 * @receiver Context
 * @return SimInfo?
 */
fun Context.getSatelliteSimInfo(): SimInfo? = getSimInfo().find { it.simId.toIntOrNull()?: -1 >= 0 && it.displayName == "电信卫星" }

/**
 * 判断是否有卫星信号
 * @receiver Context
 * @return Boolean
 */
fun Context.isHasSatelliteSignal(): Boolean {
    val info = getSatelliteSimInfo()?: return false
    return info.carrierName == "电信卫星"
}

/**
 * 判断至少有一张卡有信号
 * @receiver Context
 * @return Boolean
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
fun Context.isAtLeastOneCardHasSignal(): Boolean {
    // 拿到手机里面的手机卡列表
    val list = getPhoneCardInfoList()?: listOf()
    if (list.isEmpty()) return false
    for (card in list) {
        if (!card.carrierName.contains("没有服务")) return true
    }
    return false
}

/**
 * 获取短信发送器
 * @receiver Context
 * @return SmsManager
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@SuppressLint("MissingPermission")
fun Context.getSmsManager(): SmsManager {
    // 拿到手机里面的手机卡列表
    val list = getPhoneCardInfoList()?: listOf()
    println(list)
    // [{id=13, iccId=898659171[****] simSlotIndex=1 displayName=电信卫星 carrierName=电信卫星 nameSource=0 iconTint=-16746133 dataRoaming=0 iconBitmap=android.graphics.Bitmap@a2d6fc2 mcc 0 mnc 0 isEmbedded false accessRules null}]
    // 通过反射修改当前短信管理器使用的卡ID
    val sms = SmsManager.getDefault()
    if (list.isNotEmpty()) {
        for (i in list.indices) {
            if (list[i].carrierName.contains("没有服务")) continue
            try {
                val clz = SmsManager::class.java
                val field = clz.getDeclaredField("mSubId")
                field.isAccessible = true
                val subscriptionInfoClz = SubscriptionInfo::class.java
                val cardIdField = subscriptionInfoClz.getDeclaredField("mId")
                cardIdField.isAccessible = true
                field.set(sms, cardIdField.get(list[i]))
                break
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return sms
}

/**
 * 获取卫星卡的短信发送器
 * @receiver Context
 * @return SmsManager
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
@SuppressLint("MissingPermission")
fun Context.getSatelliteSmsManager(): SmsManager {
    // 拿到手机里面的手机卡列表
    val list = getPhoneCardInfoList()?: listOf()
    // 通过反射修改当前短信管理器使用的卡ID
    val sms = SmsManager.getDefault()
    val satelliteInfo = list.find { it.displayName == "电信卫星" && it.carrierName == "电信卫星" }
    if (satelliteInfo != null) {
        try {
            val clz = SmsManager::class.java
            val field = clz.getDeclaredField("mSubId")
            field.isAccessible = true
            val subscriptionInfoClz = SubscriptionInfo::class.java
            val cardIdField = subscriptionInfoClz.getDeclaredField("mId")
            cardIdField.isAccessible = true
            field.set(sms, cardIdField.get(satelliteInfo))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return sms
}

/**
 * 读取最新一条短信
 * @receiver Context
 * @return SmsInfo?
 */
fun Context.readNewSms(): SmsInfo? {
    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("_id", "address", "read", "body", "date"),
            null, null, "date desc limit 1"
        )
        if (cursor == null) {
            return null
        }
        while (cursor.moveToNext()) {
            val info = SmsInfo()
            info.id = cursor.getString(cursor.getColumnIndex("_id"))
            info.content = cursor.getString(cursor.getColumnIndex("body"))
            info.phone = cursor.getString(cursor.getColumnIndex("address"))
            info.time = cursor.getString(cursor.getColumnIndex("date")).toLongOrNull()?: 0
            return info
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }
    return null
}

/**
 * 读取指定时间以后的全部消息
 * @receiver Context
 * @param time Long
 * @return List<SmsInfo>
 */
fun Context.readNewSmsList(time: Long): List<SmsInfo> {
    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("_id", "address", "read", "body", "date"),
            "( date > $time )", null, "date asc"
        )
        if (cursor == null) {
            return listOf()
        }
        val list = mutableListOf<SmsInfo>()
        while (cursor.moveToNext()) {
            val info = SmsInfo()
            info.id = cursor.getString(cursor.getColumnIndex("_id"))
            info.content = cursor.getString(cursor.getColumnIndex("body"))
            info.phone = cursor.getString(cursor.getColumnIndex("address"))
            info.time = cursor.getString(cursor.getColumnIndex("date")).toLongOrNull()?: 0
            list.add(info)
        }
        return list
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }
    return listOf()
}

/**
 * 读取手机卡信息包含每张插入过的卡
 * @receiver Context
 * @return List<SimInfo>
 */
fun Context.getSimInfo(): List<SimInfo> {
    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(
            Uri.parse("content://telephony/siminfo"),
            arrayOf("_id", "sim_id", "icc_id", "display_name", "carrier_name", "number"),
            null, null, null
        )
        if (cursor == null) {
            return listOf()
        }
        val list = mutableListOf<SimInfo>()
        while (cursor.moveToNext()) {
            val info = SimInfo().apply {
                id = cursor.getString(cursor.getColumnIndex("_id"))
                simId = cursor.getString(cursor.getColumnIndex("sim_id"))
                iccId = cursor.getString(cursor.getColumnIndex("icc_id"))
                displayName = cursor.getString(cursor.getColumnIndex("display_name"))
                carrierName = cursor.getString(cursor.getColumnIndex("carrier_name"))
                number = cursor.getStringOrNull(cursor.getColumnIndex("number"))?: ""
            }
            list.add(info)
            println(info.toString())
        }
        return list
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }
    return listOf()
}