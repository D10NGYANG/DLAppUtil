package com.d10ng.app.infos

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import com.d10ng.app.startup.ctx
import java.io.File

/**
 * 应用管理器
 * @Author d10ng
 * @Date 2023/9/19 09:56
 */

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
 * 获取应用包信息
 * @receiver PackageManager
 * @param packageName String
 * @param flags Int
 * @return PackageInfo
 */
private fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        getPackageInfo(packageName, flags)
    }

/**
 * 获取当前应用的版本号
 * @return String
 */
fun appVersion(): String {
    // getPackageName()是你当前类的包名，0代表是获取版本信息
    val packInfo =
        with(ctx) { packageManager.getPackageInfoCompat(packageName, 0) }
    return packInfo.versionName
}

/**
 * 获取当前应用的版本码
 * @return Int
 */
fun appVersionCode(): Int {
    // getPackageName()是你当前类的包名，0代表是获取版本信息
    val packInfo = with(ctx) { packageManager.getPackageInfoCompat(packageName, 0) }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) packInfo.longVersionCode.toInt()
    else @Suppress("DEPRECATION") packInfo.versionCode
}

/**
 * 检查APP是否存在
 * @param packageName String
 * @return Boolean
 */
fun existApp(packageName: String): Boolean {
    if (packageName.isEmpty()) return false
    return File("${ctx.filesDir.path}$packageName").exists()
}