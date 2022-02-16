package com.d10ng.applib.system

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.d10ng.applib.bean.SimInfo
import org.json.JSONObject

/**
 * 获取手机卡信息列表
 * @receiver Context
 * @return List<SubscriptionInfo>?
 */
@SuppressLint("MissingPermission")
fun Context.getSubscriptionInfoList(): List<SubscriptionInfo> {
    // 手机卡管理器
    val subscriptionManager = getSystemService(AppCompatActivity.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    // 拿到手机里面的手机卡列表
    return subscriptionManager.activeSubscriptionInfoList?: listOf()
}

/**
 * 将手机卡信息转SimInfo
 * @receiver SubscriptionInfo
 * @return SimInfo
 */
fun SubscriptionInfo.toSimInfo(): SimInfo {
    return SimInfo().apply {
        id = this@toSimInfo.subscriptionId.toString()
        slotId = this@toSimInfo.simSlotIndex
        iccId = this@toSimInfo.iccId
        displayName = this@toSimInfo.displayName.toString()
        carrierName = this@toSimInfo.carrierName.toString()
        number = this@toSimInfo.number
    }
}

/**
 * 将手机卡信息列表转SimInfo列表
 * @receiver List<SubscriptionInfo>
 * @return List<SimInfo>
 */
fun List<SubscriptionInfo>.toSimInfoList(): List<SimInfo> {
    val list = mutableListOf<SimInfo>()
    for (item in this) {
        list.add(item.toSimInfo())
    }
    return list
}

/**
 * 判断是否拥有手机卡
 * @receiver Context
 * @return Boolean
 */
fun Context.isHasPhoneCard(): Boolean =
    !getSubscriptionInfoList().isNullOrEmpty()

/**
 * 获取卫星卡信息
 * @receiver Context
 * @return SimInfo?
 */
fun Context.getSatelliteSimInfo(): SimInfo? =
    getSimInfoList().find { it.slotId >= 0 && it.displayName == "电信卫星" }

/**
 * 判断是否有卫星信号
 * @receiver Context
 * @return Boolean
 */
fun Context.isHasSatelliteSignal(): Boolean {
    val info = getSatelliteSimInfo()?: return false
    return info.carrierName != "无服务"
}

/**
 * 读取手机卡信息包含每张插入过的卡
 * @receiver Context
 * @return List<SimInfo>
 */
fun Context.getSimInfoList(): List<SimInfo> {
    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(
            Uri.parse("content://telephony/siminfo"),
            arrayOf("_id", "sim_id", "icc_id", "display_name", "carrier_name", "number"),
            null, null, null
        )
        if (cursor == null) {
            return getSubscriptionInfoList().toSimInfoList()
        }
        val list = mutableListOf<SimInfo>()
        while (cursor.moveToNext()) {
            val info = SimInfo().apply {
                id = cursor.getString(cursor.getColumnIndex("_id"))
                slotId = cursor.getIntOrNull(cursor.getColumnIndex("sim_id"))?: -1
                iccId = cursor.getStringOrNull(cursor.getColumnIndex("icc_id"))?: ""
                displayName = cursor.getStringOrNull(cursor.getColumnIndex("display_name"))?: ""
                carrierName = cursor.getStringOrNull(cursor.getColumnIndex("carrier_name"))?: ""
                number = cursor.getStringOrNull(cursor.getColumnIndex("number"))?: ""
            }
            list.add(info)
            println(info.toString())
        }
        return list
    } catch (e: Exception) {
        e.printStackTrace()
        return getSubscriptionInfoList().toSimInfoList()
    } finally {
        cursor?.close()
    }
}

/**
 * 根据卡的ID获取卡槽位置
 * -1 - 未知
 * 0 - 卡1
 * 1 - 卡2
 * @receiver Context
 * @param subId Long
 * @return Int
 */
fun Context.getSimSlotIndex(subId: Long): Int =
    getSimInfoList().find { it.id == subId.toString() }?.slotId?: -1

/**
 * 获取卫星卡的短信发送器
 * - 优先卫星卡，没有就用默认的
 * @receiver Context
 * @return SmsManager
 */
fun Context.getSatelliteSmsManager(): SmsManager {
    // 拿到手机里面的手机卡列表
    val list = getSimInfoList()
    // 通过反射修改当前短信管理器使用的卡ID
    var sms = SmsManager.getDefault()
    val satelliteInfo = list.find { it.displayName == "电信卫星" && it.carrierName == "电信卫星" }
    val cardId = satelliteInfo?.id?.toIntOrNull()
    if (satelliteInfo != null && cardId != null) {
        sms = SmsManager.getSmsManagerForSubscriptionId(cardId)
    }
    return sms
}

/**
 * 获取短信发送器
 * @receiver Context
 * @param slot Int 卡槽
 * @return SmsManager
 */
fun Context.getSmsManager(slot: Int = -1): SmsManager {
    // 拿到手机里面的手机卡列表
    val list = getSimInfoList()
    var sms = SmsManager.getDefault()
    if (slot < 0) return sms
    val simInfo = list.find { it.slotId == slot }
    val cardId = simInfo?.id?.toIntOrNull()
    if (simInfo != null && cardId != null) {
        sms = SmsManager.getSmsManagerForSubscriptionId(cardId)
    }
    return sms
}