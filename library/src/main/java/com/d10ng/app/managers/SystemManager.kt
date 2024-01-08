package com.d10ng.app.managers

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import com.d10ng.app.startup.ctx

/**
 * 系统管理器
 * @Author d10ng
 * @Date 2024/1/6 14:35
 */

/**
 * Android ID号
 * > ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置。
 * > 从Android O（API级别26）开始，对于没有电话功能的设备，这个值可能不是唯一的。
 */
@delegate:SuppressLint("HardwareIds")
val androidID by lazy {
    with(ctx) {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
}

/**
 * 手机型号
 */
val phoneModel by lazy { Build.MODEL }

/**
 * 手机品牌
 */
val phoneManufacturer by lazy { Build.MANUFACTURER }

/**
 * 系统版本号
 */
val systemVersion by lazy { Build.VERSION.RELEASE }