package com.d10ng.applib.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import java.io.File

/**
 * 获取当前应用的版本号
 * @receiver Context
 * @return String
 */
fun Context.appVersion(): String {
    // getPackageName()是你当前类的包名，0代表是获取版本信息
    val packInfo = packageManager.getPackageInfo(packageName, 0)
    return packInfo.versionName
}

/**
 * 获取当前应用的版本码
 * @receiver Context
 * @return Int
 */
fun Context.appVersionCode(): Int {
    // getPackageName()是你当前类的包名，0代表是获取版本信息
    val packInfo = packageManager.getPackageInfo(packageName, 0)
    return packInfo.versionCode
}

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
 * # ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置。
 * @receiver Context
 * @return String
 */
@SuppressLint("HardwareIds")
fun Context.androidId(): String =
    Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

/**
 * 读取系统电量
 * @receiver Context
 * @return Int
 */
fun Context.systemBattery(): Int {
    val batteryInfoIntent = applicationContext.registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    )
    val level = batteryInfoIntent?.getIntExtra("level", 0)?: 0
    val batterySum = batteryInfoIntent?.getIntExtra("scale", 100)?: 100
    return 100 * level / batterySum
}

/**
 * 判断手机位置信息是否打开
 * @receiver Context
 * @return Boolean
 */
fun Context.isLocationServerEnabled(): Boolean {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
            val locationMode =  Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.LOCATION_MODE
            )
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
            val manager = getSystemService(LocationManager::class.java)
            manager.isLocationEnabled
        }
        else -> {
            val locationProviders = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }
}

/**
 * 获取根目录路径
 * @receiver Activity
 * @return String
 */
fun Context.getExternalRootPath(): String {
    val path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    return if (path == null) {
        "/storage/emulated/0/"
    } else {
        path.substring(0, path.indexOf("/Android")) + "/"
    }
}

/**
 * 检查APP是否存在
 * @receiver Context
 * @param packageName String
 * @return Boolean
 */
fun Context.isAppInPhone(packageName: String): Boolean {
    if (packageName.isEmpty()) return false
    return File("${filesDir.path}$packageName").exists()
}