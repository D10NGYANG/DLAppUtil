package com.d10ng.app.managers

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.widget.Toast
import com.d10ng.app.startup.ctx

/**
 * 系统管理器
 * @Author d10ng
 * @Date 2024/1/6 14:35
 */

/**
 * Android ID号
 * > ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置。
 * > 从Android O（API级别26）开始，对于没有电话功能的设备，这个值可能不是唯一的。
 */
@delegate:SuppressLint("HardwareIds")
val androidID: String by lazy {
    with(ctx) {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
}

/**
 * 手机型号
 */
val phoneModel: String by lazy { Build.MODEL }

/**
 * 手机品牌
 */
val phoneManufacturer: String by lazy { Build.MANUFACTURER }

/**
 * 系统版本号
 */
val systemVersion: String by lazy { Build.VERSION.RELEASE }

/**
 * 显示Toast消息
 * @param msg String
 * @param duration Int
 */
fun showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(ctx, null, duration).apply {
        setText(msg)
        show()
    }
}

/**
 * 控制手机震动
 * @param time 震动时间长度 毫秒
 */
fun vibrate(time: Long = 500) {
    val vibrator = ctx.getSystemService(Vibrator::class.java)
    vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
}

/**
 * 播放系统提示音
 * @param uri Uri 不填则是默认提示音
 */
fun playRingtone(uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) {
    val ringtone = RingtoneManager.getRingtone(ctx, uri)
    ringtone.play()
}

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