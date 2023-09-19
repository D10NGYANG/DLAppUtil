package com.d10ng.app.infos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import com.d10ng.app.startup.ctx

/**
 * 系统管理器
 * @Author d10ng
 * @Date 2023/9/19 10:02
 */

/**
 * 读取手机型号
 * @return String
 */
fun phoneModel(): String = Build.MODEL

/**
 * 读取手机品牌
 * @return String
 */
fun phoneManufacturer(): String = Build.MANUFACTURER

/**
 * 获取当前手机系统版本号
 * @return String
 */
fun systemVersion(): String = Build.VERSION.RELEASE

/**
 * 读取Android ID号
 * > ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置。
 * @return String
 */
@SuppressLint("HardwareIds")
fun androidId(): String =
    with(ctx) { Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) }

/**
 * 读取系统电量
 * @return Int
 */
fun systemBattery(): Int {
    val batteryInfoIntent = ctx.registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    )
    val level = batteryInfoIntent?.getIntExtra("level", 0) ?: 0
    val batterySum = batteryInfoIntent?.getIntExtra("scale", 100) ?: 100
    return 100 * level / batterySum
}