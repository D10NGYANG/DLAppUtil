package com.d10ng.app.managers

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 照片管理器
 * @Author d10ng
 * @Date 2023/11/27 09:41
 */
object PhotoManager {

    private lateinit var application: Application

    private val scope = CoroutineScope(Dispatchers.IO)

    // 最顶部展示的Activity
    private var topActivity: ComponentActivity? = null

    // 图片获取执行器
    private val launcherMap =
        mutableMapOf<ComponentActivity, ActivityResultLauncher<Intent>>()

    // 结果Flow
    private val resultFlow = MutableSharedFlow<String?>()

    // 权限列表
    private val STORAGE_PERMISSION = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    internal fun init(app: Application) {
        application = app.apply {
            registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                    if (p0 !is ComponentActivity) return
                    launcherMap[p0] =
                        p0.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                            if (result.resultCode == Activity.RESULT_OK) {
                                val data = result.data!!
                                // 选择到图片的uri
                                val uri = data.data
                                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                                // 获取图片的游标
                                val cursor =
                                    contentResolver.query(uri!!, filePathColumn, null, null, null)!!
                                cursor.moveToFirst()
                                // 获取列的指针
                                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                                // 根据指针获取图片路径
                                val picturePath = cursor.getString(columnIndex)
                                cursor.close()
                                scope.launch { resultFlow.emit(picturePath) }
                            } else {
                                scope.launch { resultFlow.emit(null) }
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
     * 选择图片
     * @return String? 图片路径
     */
    suspend fun pick(): String? = withContext(Dispatchers.IO) {
        if (PermissionManager.request(STORAGE_PERMISSION).not()) return@withContext null
        val launcher = launcherMap[topActivity] ?: return@withContext null
        launcher.launch(Intent().apply {
            action = Intent.ACTION_PICK
            type = "image/jpeg"
        })
        resultFlow.first()
    }
}