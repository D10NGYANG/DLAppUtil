package com.d10ng.applib.resource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader

/**
 * Assets资源读取工具
 *
 * @author D10NG
 * @date on 2019-10-31 09:54
 */

/**
 * 将 Assets 中的文本文件 读取成字符串
 * @receiver [Context]
 * @param fileName [String] 文件名 "xxx.txt"
 * @return [String]
 */
fun Context.getAssetsTxt(fileName: String): String {
    val stringBuilder = StringBuilder()
    val bf = BufferedReader(
        InputStreamReader(
            assets.open(fileName)
        )
    )
    var line: String?
    do {
        line = bf.readLine()
        if (line != null) {
            stringBuilder.append(line)
        } else {
            break
        }
    } while (true)
    bf.close()
    return stringBuilder.toString()
}

/**
 * 将 Assets 中的json文件 读取成 JSONObject
 * @receiver [Context]
 * @param fileName [String] 文件名 "xxx.json"
 * @return [String]
 */
fun Context.getAssetsJSONObject(fileName: String): JSONObject {
    val str = getAssetsTxt(fileName)
    return try {
        JSONObject(str)
    } catch (e: Exception) {
        JSONObject()
    }
}

/**
 * 将 Assets 中的json文件 读取成 JSONArray
 * @receiver [Context]
 * @param fileName [String] 文件名 "xxx.json"
 * @return [String]
 */
fun Context.getAssetsJSONArray(fileName: String): JSONArray {
    val str = getAssetsTxt(fileName)
    return try {
        JSONArray(str)
    } catch (e: Exception) {
        JSONArray()
    }
}

/**
 * 将 Assets 中的图片文件 读取成 Bitmap
 * @receiver [Context]
 * @param fileName [String] 文件名 "xxx.png"
 * @return [Bitmap]?
 */
fun Context.getAssetsBitmap(fileName: String): Bitmap? {
    val ins = assets.open(fileName)
    return BitmapFactory.decodeStream(ins)
}


/**
 * 将 Assets 中的二进制bin文件 读取成 ByteArray
 * @receiver [Context]
 * @param fileName [String] 文件名 "xxx.bin"
 * @return [ByteArray]
 */
fun Context.getAssetsBin(fileName: String): ByteArray {
    val input = assets.open(fileName)
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(1024 * 4)
    var n: Int
    while (-1 != input.read(buffer).also { n = it }) {
        output.write(buffer, 0, n)
    }
    return output.toByteArray()
}