package com.d10ng.app.utils

import android.content.ClipData
import android.content.ClipboardManager
import com.d10ng.app.startup.ctx


/**
 * 复制字符串到系统剪贴板
 * @param label String 标记
 * @param text String 内容
 */
fun copyToClipboard(label: String, text: String) {
    val clip = ctx.getSystemService(ClipboardManager::class.java)
    val data = ClipData.newPlainText(label, text)
    clip.setPrimaryClip(data)
}

/**
 * 读取粘贴板
 * @return List<String>
 */
fun readClipboard(): List<String> {
    val clip = ctx.getSystemService(ClipboardManager::class.java)
    val data = clip.primaryClip
    if (data == null || data.itemCount == 0) {
        return listOf()
    }
    val list = mutableListOf<String>()
    for (i in 0 until data.itemCount) {
        val item = data.getItemAt(i)
        list.add(item.coerceToText(ctx).toString())
    }
    return list
}