package com.d10ng.app.managers

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.d10ng.app.startup.ctx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 照片管理器
 * @Author d10ng
 * @Date 2023/11/27 09:41
 */
object PhotoManager {

    private val scope = CoroutineScope(Dispatchers.IO)

    // 最顶部展示的Activity
    private var topActivity: ComponentActivity? = null

    // 图片获取执行器
    private val launcherMap =
        mutableMapOf<ComponentActivity, ActivityResultLauncher<Intent>>()

    // 结果Flow
    private val resultFlow = MutableSharedFlow<String?>()

    internal fun init(app: Application) {
        app.apply {
            registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                    if (p0 !is ComponentActivity) return
                    launcherMap[p0] =
                        p0.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                            handlePickPhotoResult(result)
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
     * 处理选择图片结果
     * @param result ActivityResult
     */
    private fun handlePickPhotoResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data!!
            // 选择到图片的uri
            val uri = data.data!!
            // 选择到图片的文件名
            var fileName: String? = null
            ctx.contentResolver.query(
                uri, null, null, null, null
            )?.use {
                if (it.moveToFirst()) {
                    fileName =
                        it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                }
            }
            val file = File(ctx.cacheDir, fileName?.split("/")?.last() ?: "select.jpg")
            ctx.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            scope.launch { resultFlow.emit(file.path) }
        } else {
            scope.launch { resultFlow.emit(null) }
        }
    }

    /**
     * 选择图片
     * @return String? 图片路径
     */
    suspend fun pick(): String? = withContext(Dispatchers.IO) {
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

    /**
     * 保存图片到相册
     * > 需要权限：android.permission.WRITE_EXTERNAL_STORAGE
     * @param file File 图片文件
     * @param albumName String 相册名称
     */
    fun saveImageToGallery(file: File, albumName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") // or "image/png"
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/$albumName"
                )
            }

            val uri =
                ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ctx.contentResolver.openOutputStream(uri ?: return).use { outputStream ->
                FileInputStream(file).use { inputStream ->
                    inputStream.copyTo(outputStream ?: return)
                }
            }

        } else {
            // 旧版本的处理方式
            val destination =
                File(ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName)
            if (!destination.exists()) {
                destination.mkdirs()
            }
            val newFile = File(destination, file.name)
            FileInputStream(file).use { input ->
                FileOutputStream(newFile).use { output ->
                    input.copyTo(output)
                }
            }

            // 最后通知图库更新
            MediaScannerConnection.scanFile(ctx, arrayOf(newFile.toString()), null, null)
        }
    }
}