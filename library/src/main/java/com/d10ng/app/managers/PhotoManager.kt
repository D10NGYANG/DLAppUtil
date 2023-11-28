package com.d10ng.app.managers

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
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
import java.io.File
import java.io.FileOutputStream

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
    private val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

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
                                val uri = data.data!!
                                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                                // 获取图片的游标
                                val cursor =
                                    contentResolver.query(uri, filePathColumn, null, null, null)!!
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
        // 检查权限
        if (PermissionManager.request(permissionList).not()) return@withContext null
        val launcher = launcherMap[topActivity] ?: return@withContext null
        launcher.launch(Intent().apply {
            action = Intent.ACTION_PICK
            type = "image/jpeg"
        })
        resultFlow.first()
    }

    /**
     * 创建缩略图并保存
     * @param path String 图片路径
     * @param reqWidth Int 缩略图宽度
     * @param reqHeight Int 缩略图高度
     * @param outputPath String 输出路径
     */
    fun createAndSaveThumbnail(path: String, reqWidth: Int, reqHeight: Int, outputPath: String) {
        val bitmap = createThumbnail(path, reqWidth, reqHeight) ?: return
        runCatching { saveBitmap(bitmap, outputPath, 50) }
    }

    /**
     * 保存图片
     * @param bitmap Bitmap
     * @param outputPath String
     * @param quality Int
     */
    fun saveBitmap(bitmap: Bitmap, outputPath: String, quality: Int = 50) {
        val file = File(outputPath)
        file.parentFile?.mkdirs()
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
        }
    }

    /**
     * 创建缩略图
     * @param path String 图片路径
     * @param reqWidth Int 缩略图宽度
     * @param reqHeight Int 缩略图高度
     * @return Bitmap? 缩略图
     */
    fun createThumbnail(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        // 首先设置 inJustDecodeBounds = true 来获取图片的尺寸
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(path, options)

        // 计算缩放比例
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // 使用计算出的 inSampleSize 值再次解析图片
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, options)
    }

    /**
     * 计算缩放比例
     * @param options Options 图片参数
     * @param reqWidth Int 缩略图宽度
     * @param reqHeight Int 缩略图高度
     * @return Int 缩放比例
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // 原始图片的高度和宽度
        val height: Int = options.outHeight
        val width: Int = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // 计算最大的 inSampleSize 值，这是一个保持
            // 总高度和总宽度都大于或等于请求的高度和宽度的2的幂。
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}