package com.d10ng.app.system

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.telephony.SmsManager
import androidx.core.database.getLongOrNull
import com.d10ng.app.bean.SmsInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
 * @param smsManager SmsManager 短信发送器
 * @return MutableStateFlow<SendSmsStatus>? 发送状态
 */
fun Context.sendSmsMessage(
    destinationAddress: String,
    scAddress: String?,
    text: String,
    overTime: Long = 60 * 1000,
    smsManager: SmsManager = SmsManager.getDefault()
): MutableStateFlow<SendSmsStatus> {
    // 新建一个发送状态
    var statusLive: MutableStateFlow<SendSmsStatus>? = MutableStateFlow(SendSmsStatus.SENDING)
    if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
        // 缺少发送短信权限
        statusLive?.value = SendSmsStatus.FAILED
    } else if (!isHasPhoneCard()) {
        // 缺少手机卡
        statusLive?.value = SendSmsStatus.FAILED
    } else {
        // 新建子线程安排发送
        CoroutineScope(Dispatchers.IO).launch {
            // 读取当前时间戳，做一个唯一标记
            val time = System.currentTimeMillis()
            // 发送状态监听标记
            val sentSmsAction = "SSA-${time}"
            // 接受状态监听标记
            val deliveredSmsAction = "DSA-${time}"
            // 选择手机卡进行发送
            smsManager.sendTextMessage(
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
                        statusLive?.value = SendSmsStatus.SUCCESS
                    } else {
                        // 发送失败
                        statusLive?.value = SendSmsStatus.FAILED
                    }
                    // 取消监听
                    context?.unregisterReceiver(this)
                }
            }, IntentFilter(sentSmsAction))
            // 目标用户接收监听
            registerReceiver(object : BroadcastReceiver(){
                override fun onReceive(context: Context?, intent: Intent?) {
                    // 接收成功
                    statusLive?.value = SendSmsStatus.RECEIVE
                    statusLive = null
                    // 取消监听
                    context?.unregisterReceiver(this)
                }
            }, IntentFilter(sentSmsAction))
            // 超时
            delay(overTime)
            if (statusLive?.value == SendSmsStatus.SENDING) {
                statusLive?.value = SendSmsStatus.OVERTIME
            }
            statusLive = null
        }
    }
    return statusLive!!
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
            arrayOf("_id", "sub_id", "address", "read", "body", "date"),
            null, null, "date desc limit 1"
        )
        if (cursor == null) {
            return null
        }
        while (cursor.moveToNext()) {
            val info = SmsInfo()
            info.id = cursor.getString(cursor.getColumnIndex("_id").coerceAtLeast(0))
            info.subId = cursor.getLongOrNull(cursor.getColumnIndex("sub_id"))?: -1
            info.content = cursor.getString(cursor.getColumnIndex("body").coerceAtLeast(0))
            info.phone = cursor.getString(cursor.getColumnIndex("address").coerceAtLeast(0))
            info.time = cursor.getString(cursor.getColumnIndex("date").coerceAtLeast(0)).toLongOrNull()?: 0
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
            arrayOf("_id", "sub_id", "address", "read", "body", "date"),
            "( date > $time )", null, "date asc"
        )
        if (cursor == null) {
            return listOf()
        }
        val list = mutableListOf<SmsInfo>()
        while (cursor.moveToNext()) {
            val info = SmsInfo()
            info.id = cursor.getString(cursor.getColumnIndex("_id").coerceAtLeast(0))
            info.subId = cursor.getLongOrNull(cursor.getColumnIndex("sub_id"))?: -1
            info.content = cursor.getString(cursor.getColumnIndex("body").coerceAtLeast(0))
            info.phone = cursor.getString(cursor.getColumnIndex("address").coerceAtLeast(0))
            info.time = cursor.getString(cursor.getColumnIndex("date").coerceAtLeast(0)).toLongOrNull()?: 0
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