package com.d10ng.app.system

import android.content.Context
import java.io.File

/**
 * 将字符串写入文件中
 * @receiver Context
 * @param parentPath String
 * @param fileName String
 * @param value String
 * @param isAppend Boolean
 * @return File
 */
fun Context.saveString2File(
    parentPath: String = "${filesDir.absoluteFile.absolutePath}/",
    fileName: String,
    value: String,
    isAppend: Boolean = false
): File {
    val fileStr = getFileFullPath(parentPath, fileName)
    println("文件路径：$fileStr")
    val file = File(fileStr)
    try {
        if (!file.exists()) {
            val dir = File(file.parent!!)
            dir.mkdirs()
            file.createNewFile()
        }
        if (isAppend) file.appendText(value)
        else file.writeText(value)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return file
}

/**
 * 拼接文件完整路径
 * @receiver Context
 * @param parentPath String
 * @param fileName String
 * @return String
 */
fun Context.getFileFullPath(
    parentPath: String = "${filesDir.absoluteFile.absolutePath}/",
    fileName: String
) = parentPath + fileName