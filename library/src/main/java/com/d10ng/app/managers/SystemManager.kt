package com.d10ng.app.managers

import android.annotation.SuppressLint
import android.media.RingtoneManager
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
 */
fun playRingtone() {
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val ringtone = RingtoneManager.getRingtone(ctx, soundUri)
    ringtone.play()
}