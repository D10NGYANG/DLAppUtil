package com.d10ng.app.base

import android.annotation.SuppressLint
import android.os.Build

/**
 * Android 9开始限制开发者调用非官方API方法和接口(即用反射直接调用源码)
 * 弹框提示 Detected problems with API compatibility(visit g.co/dev/appcompat for more info)
 * 隐藏警告弹框
 */
@SuppressLint("DiscouragedPrivateApi", "SoonBlockedPrivateApi")
fun closeDetectedProblemApiDialog() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        return
    }
    try {
        @SuppressLint("PrivateApi")
        val clsPkgParser = Class.forName("android.content.pm.PackageParser\$Package")
        val constructor = clsPkgParser.getDeclaredConstructor(String::class.java)
        constructor.isAccessible = true
        @SuppressLint("PrivateApi")
        val clsActivityThread = Class.forName("android.app.ActivityThread")
        val method = clsActivityThread.getDeclaredMethod("currentActivityThread")
        method.isAccessible = true
        val activityThread = method.invoke(null)
        val hiddenApiWarning = clsActivityThread.getDeclaredField("mHiddenApiWarningShown")
        hiddenApiWarning.isAccessible = true
        hiddenApiWarning.setBoolean(activityThread, true)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}