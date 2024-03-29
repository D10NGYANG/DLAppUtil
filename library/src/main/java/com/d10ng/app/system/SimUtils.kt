package com.d10ng.app.system

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.d10ng.app.bean.SimInfo
import com.d10ng.app.startup.ctx

/**
 * 获取手机卡信息列表
 * @return List<SubscriptionInfo>?
 */
@SuppressLint("MissingPermission")
fun getSubscriptionInfoList(): List<SubscriptionInfo> {
    // 手机卡管理器
    val subscriptionManager = ctx.getSystemService(SubscriptionManager::class.java)
    // 拿到手机里面的手机卡列表
    return subscriptionManager.activeSubscriptionInfoList ?: listOf()
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
 * @return Boolean
 */
fun isHasPhoneCard(): Boolean =
    getSubscriptionInfoList().isNotEmpty()

/**
 * 获取卫星卡信息
 * @return SimInfo?
 */
fun getSatelliteSimInfo(): SimInfo? =
    getSimInfoList().find { it.slotId >= 0 && it.displayName.contains("卫星") }

/**
 * 判断是否有卫星信号
 * @return Boolean
 */
fun isHasSatelliteSignal(): Boolean {
    val info = getSatelliteSimInfo() ?: return false
    return info.carrierName != "无服务"
}

/**
 * 读取手机卡信息包含每张插入过的卡
 * @return List<SimInfo>
 */
fun getSimInfoList(): List<SimInfo> {
    var cursor: Cursor? = null
    try {
        cursor = ctx.contentResolver.query(
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
                id = cursor.getString(cursor.getColumnIndex("_id").coerceAtLeast(0))
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
 * @param subId Long
 * @return Int
 */
fun getSimSlotIndex(subId: Long): Int =
    getSimInfoList().find { it.id == subId.toString() }?.slotId ?: -1

/**
 * 获取卫星卡的短信发送器
 * - 优先卫星卡，没有就用默认的
 * @return SmsManager
 */
fun getSatelliteSmsManager(): SmsManager {
    // 通过反射修改当前短信管理器使用的卡ID
    var sms = ctx.getSystemService(SmsManager::class.java)
    // 获取卫星卡信息
    val satelliteInfo = getSatelliteSimInfo()
    if (satelliteInfo != null) {
        val cardId = satelliteInfo.id.toIntOrNull()
        if (cardId != null) {
            sms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                sms.createForSubscriptionId(cardId)
            } else {
                SmsManager.getSmsManagerForSubscriptionId(cardId)
            }
        }
    }
    return sms
}

/**
 * 获取短信发送器
 * @param slot Int 卡槽
 * @return SmsManager
 */
fun getSmsManager(slot: Int = -1): SmsManager {
    // 拿到手机里面的手机卡列表
    val list = getSimInfoList()
    var sms = ctx.getSystemService(SmsManager::class.java)
    if (slot < 0) return sms
    val simInfo = list.find { it.slotId == slot }
    val cardId = simInfo?.id?.toIntOrNull()
    if (simInfo != null && cardId != null) {
        sms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sms.createForSubscriptionId(cardId)
        } else {
            SmsManager.getSmsManagerForSubscriptionId(cardId)
        }
    }
    return sms
}