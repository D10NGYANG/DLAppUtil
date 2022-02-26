package com.d10ng.applib.app

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.d10ng.applib.app.permission.PermissionManager
import com.d10ng.applib.app.permission.PermissionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 检查权限
 * @receiver Context
 * @param permission String
 * @return Boolean
 */
fun Context.checkPermission(permission: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * 检查权限
 * @receiver AppCompatActivity
 * @param permission String
 * @return Boolean
 */
suspend fun AppCompatActivity.checkPermissionWithBool(permission: String): Boolean {
    return suspendCoroutine { cont ->
        CoroutineScope(Dispatchers.IO).launch {
            val permissionResult = PermissionManager.requestPermissions(
                this@checkPermissionWithBool,
                1,
                permission
            )
            cont.resume(permissionResult is PermissionResult.PermissionGranted)
        }
    }
}
