package com.d10ng.app.resource

import android.os.Environment
import com.d10ng.app.startup.ctx
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 外置存储器工具
 * @Author d10ng
 * @Date 2023/9/19 10:10
 */

/**
 * 获取外置存储器根目录路径
 * @return String
 */
fun getExternalRootPath(): String {
    val path = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
    return if (path == null) {
        "/storage/emulated/0/"
    } else {
        path.substring(0, path.indexOf("/Android")) + "/"
    }
}

/**
 * 获取APP缓存目录路径
 * @return String
 */
fun getCachePath() = ctx.cacheDir.absolutePath

/**
 * 获取APP文件目录路径
 * @return String
 */
fun getFilesPath() = ctx.filesDir.absolutePath

/**
 * 复制文件
 * @param sourcePath String 源文件路径
 * @param destinationPath String 目标文件路径
 */
fun copyFile(sourcePath: String, destinationPath: String) {
    val sourceFile = File(sourcePath)
    val destinationFile = File(destinationPath)

    FileInputStream(sourceFile).use { inputStream ->
        FileOutputStream(destinationFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}

/**
 * 复制文件夹
 * @param sourcePath String 源文件夹路径
 * @param destinationPath String 目标文件夹路径
 */
fun copyFolder(sourcePath: String, destinationPath: String) {
    val sourceFile = File(sourcePath)
    val destinationFile = File(destinationPath)
    if (!destinationFile.exists()) {
        destinationFile.mkdirs()
    }
    sourceFile.listFiles()?.forEach {
        if (it.isDirectory) {
            copyFolder(it.absolutePath, destinationPath + "/" + it.name)
        } else {
            copyFile(it.absolutePath, destinationPath + "/" + it.name)
        }
    }
}

/**
 * 写入文件
 * > 如果文件不存在则创建，如果存在则追加
 * @param path String 文件路径
 * @param content String 写入内容
 */
fun writeFile(path: String, content: String) {
    val file = File(path)
    if (!file.exists()) file.createNewFile()
    file.appendText(content)
}

/**
 * 复写文件
 * @param path String 文件路径
 * @param content String 写入内容
 */
fun overwriteFile(path: String, content: String) {
    val file = File(path)
    if (!file.exists()) file.createNewFile()
    file.writeText(content)
}