package com.d10ng.app.managers

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import com.d10ng.app.startup.ctx

/**
 * APP管理器
 * @Author d10ng
 * @Date 2024/1/6 14:48
 */

// APP包信息
private val packInfo by lazy {
    with(ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }
}

/**
 * APP版本号
 */
val appVersion: String by lazy { packInfo.versionName }

/**
 * APP版本码
 */
val appVersionCode by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packInfo.longVersionCode
    } else {
        @Suppress("DEPRECATION")
        packInfo.versionCode.toLong()
    }
}

/**
 * 重启APP
 */
fun restartApp() {
    ctx.apply {
        packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
        }
        Process.killProcess(Process.myPid())
    }
}

/**
 * 检查APP是否存在
 * @param packageName String
 * @return Boolean
 */
fun existApp(packageName: String): Boolean {
    if (packageName.isEmpty()) return false
    return with(ctx) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}

/**
 * 判断无障碍服务是否开启
 * @param serviceClass Class<out AccessibilityService>
 * @return Boolean
 */
fun isAccessibilityServiceEnabled(serviceClass: Class<out AccessibilityService>): Boolean {
    val serviceString = "${ctx.packageName}/${serviceClass.name}"
    val enabledServicesSetting = Settings.Secure.getString(
        ctx.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    val splitter = TextUtils.SimpleStringSplitter(':')
    enabledServicesSetting?.let {
        splitter.setString(it)
        while (splitter.hasNext()) {
            val accessibilityService = splitter.next()
            if (accessibilityService.equals(serviceString, ignoreCase = true)) {
                return true
            }
        }
    }
    return false
}