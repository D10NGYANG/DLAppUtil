package com.d10ng.applib.app

import android.content.Context
import android.content.Intent
import android.os.Process

/**
 * 重启APP
 * @receiver Activity
 */
fun Context.restartApp() {
    packageManager.getLaunchIntentForPackage(packageName)?.apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(this)
    }
    Process.killProcess(Process.myPid())
}