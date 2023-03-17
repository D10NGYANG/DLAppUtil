package com.d10ng.app.app

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.d10ng.app.app.permission.PermissionManager
import com.d10ng.app.app.permission.PermissionResult
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
fun Context.hasPermission(permission: String): Boolean {
    return hasPermissions(arrayOf(permission))
}

/**
 * 判断权限
 * @receiver Context
 * @param permissions Array<out String>
 * @return Boolean
 */
fun Context.hasPermissions(permissions: Array<out String>) = permissions.all {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * 请求权限
 * @receiver AppCompatActivity
 * @param permissions Array<out String>
 * @return Boolean
 */
suspend fun AppCompatActivity.requestPermissions(vararg permissions: String): Boolean {
    return suspendCoroutine { cont ->
        CoroutineScope(Dispatchers.IO).launch {
            val permissionResult = PermissionManager.requestPermissions(
                this@requestPermissions,
                1,
                *permissions.toList().toTypedArray()
            )
            cont.resume(permissionResult is PermissionResult.PermissionGranted)
        }
    }
}

/**
 * 请求权限
 * @receiver ComponentActivity
 * @param permissions Array<out String>
 * @return Boolean
 */
suspend fun ComponentActivity.requestPermissions(vararg permissions: String): Boolean {
    return suspendCoroutine { cont ->
        CoroutineScope(Dispatchers.IO).launch {
            val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->
                cont.resume(!permissionResult.containsValue(false))
            }
            launcher.launch(permissions.toList().toTypedArray())
        }
    }
}

