package com.d10ng.app.utils

import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresPermission
import com.d10ng.app.startup.ctx
import com.google.android.material.snackbar.Snackbar

/**
 * 显示Toast消息
 * @receiver Context
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
 * 显示SnackBar
 * @param msg 消息内容
 * @param duration 显示时间长度
 */
fun View.showSnackBar(msg: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, msg, duration).show()
}

/**
 * 控制手机震动
 * @param time 震动时间长度 毫秒
 */
@RequiresPermission(android.Manifest.permission.VIBRATE)
fun vibrate(time: Long) {
    val vibrator = ctx.getSystemService(Vibrator::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(time)
    }
}

/**
 * 播放系统提示音
 * @receiver Context
 */
fun playSystemRingtone() {
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val ringtone = RingtoneManager.getRingtone(ctx, soundUri)
    ringtone.play()
}