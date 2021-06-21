package com.d10ng.applib.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


/**
 * 复制字符串到系统剪贴板
 * @receiver Context
 * @param label String 标记
 * @param text String 内容
 */
fun Context.copyToClipboard(label: String, text: String) {
    val clip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val data = ClipData.newPlainText(label, text)
    clip.setPrimaryClip(data)
}

/**
 * 读取粘贴板
 * @receiver Context
 * @return List<String>
 */
fun Context.readClipboard(): List<String> {
    val clip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val data = clip.primaryClip
    if (data == null || data.itemCount == 0) {
        return listOf()
    }
    val list = mutableListOf<String>()
    for (i in 0 until data.itemCount) {
        val item = data.getItemAt(i)
        list.add(item.coerceToText(this).toString())
    }
    return list
}