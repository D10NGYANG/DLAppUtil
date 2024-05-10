package com.d10ng.app.managers

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.d10ng.app.startup.ctx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 权限管理器
 * @Author d10ng
 * @Date 2023/9/1 11:49
 */
object PermissionManager {

    private val scope = CoroutineScope(Dispatchers.IO)

    // 最顶部展示的Activity
    private var topActivity: ComponentActivity? = null

    // 权限申请执行器
    private val launcherMap =
        mutableMapOf<ComponentActivity, ActivityResultLauncher<Array<String>>>()

    // 权限申请结果Flow
    private val resultFlow = MutableSharedFlow<Map<String, Boolean>>()

    internal fun init(app: Application) {
        app.apply {
            registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                    if (p0 !is ComponentActivity) return
                    launcherMap[p0] =
                        p0.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionResult ->
                            scope.launch {
                                resultFlow.emit(permissionResult)
                            }
                        }
                    topActivity = p0
                }

                override fun onActivityStarted(p0: Activity) {}

                override fun onActivityResumed(p0: Activity) {
                    if (p0 !is ComponentActivity) return
                    topActivity = p0
                }

                override fun onActivityPaused(p0: Activity) {}

                override fun onActivityStopped(p0: Activity) {}

                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

                override fun onActivityDestroyed(p0: Activity) {
                    if (p0 !is ComponentActivity) return
                    launcherMap.remove(p0)
                    if (topActivity == p0) topActivity = null
                }
            })
        }
    }

    /**
     * 请求权限
     * @param permission String
     * @return Boolean
     */
    suspend fun request(permission: String): Boolean {
        return request(arrayOf(permission))
    }

    /**
     * 请求权限
     * @param permissions Array<String>
     * @return Boolean
     */
    suspend fun request(permissions: Array<String>): Boolean = withContext(Dispatchers.IO) {
        val launcher = launcherMap[topActivity] ?: return@withContext false
        launcher.launch(permissions)
        resultFlow.filter { it.keys.containsAll(permissions.toList()) }.first().values.all { it }
    }

    /**
     * 请求管理外部存储空间权限
     * @return Boolean
     */
    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun requestManageExternalStorage(): Boolean = withContext(Dispatchers.IO) {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:${ctx.packageName}")
        }
        ActivityManager.startActivityForResult(intent)
        Environment.isExternalStorageManager()
    }

    /**
     * 判断是否有外置存储器管理权限
     * @return Boolean
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun hasManageExternalStorage(): Boolean = Environment.isExternalStorageManager()

    /**
     * 检查权限
     * @param permission String
     * @return Boolean
     */
    fun has(permission: String): Boolean {
        return has(arrayOf(permission))
    }

    /**
     * 检查权限
     * @param permissions Array<out String>
     * @return Boolean
     */
    fun has(permissions: Array<out String>) = permissions.all {
        ContextCompat.checkSelfPermission(ctx, it) == PackageManager.PERMISSION_GRANTED
    }
}