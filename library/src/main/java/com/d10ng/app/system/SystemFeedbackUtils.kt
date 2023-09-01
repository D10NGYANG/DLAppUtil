package com.d10ng.app.system

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * 显示Toast消息
 * @receiver Context
 * @param msg String
 * @param duration Int
 */
fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, null, duration).apply {
        setText(msg)
        show()
    }
}

/**
 * 显示Toast消息
 * @receiver Fragment
 * @param msg String
 * @param duration Int
 */
fun Fragment.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    val context = this.context?: return
    Toast.makeText(context, null, duration).apply {
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
fun Context.vibrate(time: Long) {
    val vibrator = getSystemService(Vibrator::class.java)
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
fun Context.playSystemRingtone() {
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val ringtone = RingtoneManager.getRingtone(this, soundUri)
    ringtone.play()
}