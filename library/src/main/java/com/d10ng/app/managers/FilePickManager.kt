package com.d10ng.app.managers

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.d10ng.app.startup.ctx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * 文件选择器
 * @Author d10ng
 * @Date 2025/11/10 14:02
 */
object FilePickManager {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // 执行器
    private val launcherMap = HashMap<Int, ActivityResultLauncher<Array<String>>>()

    // 结果Flow
    private val resultFlow = MutableSharedFlow<String?>()

    internal fun onComponentActivityCreated(act: ComponentActivity) {
        val id = System.identityHashCode(act)
        if (!launcherMap.containsKey(id)) launcherMap[id] = act
            .registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                uri?.let { handlePickFileResult(it) }
            }
    }

    internal fun onComponentActivityDestroyed(act: ComponentActivity) {
        val id = System.identityHashCode(act)
        launcherMap.remove(id)
    }

    private fun handlePickFileResult(uri: Uri) {
        var fileName: String? = null
        ctx.contentResolver.query(uri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }
        if (fileName == null) {
            scope.launch { resultFlow.emit(null) }
            return
        }
        val file = File(ctx.cacheDir, fileName.split("/").last())
        ctx.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        scope.launch { resultFlow.emit(file.path) }
    }

    suspend fun pick(array: Array<String> = arrayOf("application/octet-stream", "*/*")): String? =
        withContext(Dispatchers.IO) {
            val launcher =
                ActivityManager.topId().let { launcherMap[it] } ?: return@withContext null
            launcher.launch(array)
            resultFlow.first()
        }
}