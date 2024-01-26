package com.d10ng.app.resource

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.d10ng.app.startup.ctx
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
 * @param fileName String 文件名 "xxx.txt"
 * @return String
 */
fun readAssetsFile(fileName: String): String {
    val stringBuilder = StringBuilder()
    val bf = BufferedReader(InputStreamReader(ctx.assets.open(fileName)))
    var line: String?
    do {
        line = bf.readLine()
        if (line != null) stringBuilder.append(line) else break
    } while (true)
    bf.close()
    return stringBuilder.toString()
}

/**
 * 将 Assets 中的json文件 读取成 JSONObject
 * @param fileName String 文件名 "xxx.json"
 * @return JSONObject
 */
fun getAssetsJSONObject(fileName: String): JSONObject {
    val str = readAssetsFile(fileName)
    return try {
        JSONObject(str)
    } catch (e: Exception) {
        JSONObject()
    }
}

/**
 * 将 Assets 中的json文件 读取成 JSONArray
 * @param fileName String 文件名 "xxx.json"
 * @return JSONArray
 */
fun getAssetsJSONArray(fileName: String): JSONArray {
    val str = readAssetsFile(fileName)
    return try {
        JSONArray(str)
    } catch (e: Exception) {
        JSONArray()
    }
}

/**
 * 将 Assets 中的图片文件 读取成 Bitmap
 * @param fileName String 文件名 "xxx.png"
 * @return Bitmap?
 */
fun getAssetsBitmap(fileName: String): Bitmap? {
    val ins = ctx.assets.open(fileName)
    return BitmapFactory.decodeStream(ins)
}


/**
 * 将 Assets 中的二进制bin文件 读取成 ByteArray
 * @param fileName [String] 文件名 "xxx.bin"
 * @return [ByteArray]
 */
fun getAssetsBin(fileName: String): ByteArray {
    val input = ctx.assets.open(fileName)
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(1024 * 4)
    var n: Int
    while (-1 != input.read(buffer).also { n = it }) {
        output.write(buffer, 0, n)
    }
    return output.toByteArray()
}