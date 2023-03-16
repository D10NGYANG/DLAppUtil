package com.d10ng.app.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 创建通知
 * @receiver Context
 * @param notifyId Int 通知ID
 * @param importance Int 重要程度
 * @param title String 标题
 * @param content String 内容
 * @param iconResId Int 图标
 * @param hasProgress Boolean 是否有进度条
 * @param max Int 进度条最大值
 * @param progress Int 进度条进度
 * @param indeterminate Boolean 进度条是否无限循环滚动
 * @param tapIntent PendingIntent 点击触发事件
 * @param isAutoCancel Boolean 是否允许点击取消显示
 * @return NotificationCompat.Builder
 */
fun Context.createNotification(
    id: String,
    name: String,
    notifyId: Int,
    importance: Int = NotificationManager.IMPORTANCE_HIGH,
    title: String,
    content: String,
    iconResId: Int,
    hasProgress: Boolean = false,
    max: Int = 100,
    progress: Int = 0,
    indeterminate: Boolean = false,
    tapIntent: PendingIntent? = null,
    isAutoCancel: Boolean = true,
    isVibrationEnable: Boolean = true,
    isVoiceEnable: Boolean = true,
    isLightEnable: Boolean = true,
    lockScreenVisibility: Int = Notification.VISIBILITY_PUBLIC,
    isCanByPassDnd: Boolean = false,
    isShowBadge: Boolean = false,
): NotificationCompat.Builder {
    val time = System.currentTimeMillis()
    val id1 = if (importance == NotificationManager.IMPORTANCE_HIGH) "${id}-${time}" else id
    // 拿到通知栏管理器
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    // 如果当前Android的版本相比Android O，一样或者版本更高，就建通知渠道（Notification Channels ）
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // 1.0 建渠道
        val mChannel = NotificationChannel(id1, name, importance).apply {
            // 允许震动
            enableVibration(isVibrationEnable)
            // 允许灯光
            enableLights(isLightEnable)
            // 锁屏显示
            this.lockscreenVisibility = lockScreenVisibility
            // 允许忽略勿扰模式
            setBypassDnd(isCanByPassDnd)
            // 允许通知进行角标统计数量
            setShowBadge(isShowBadge)
        }
        // 2.0 把通知渠道通过createNotificationChannel()方法给状态栏通知的管理类 NotificationManager
        manager.createNotificationChannel(mChannel)
    }
    // 3.0 Notification这时候可以正常工作了
    val notificationBuilder = NotificationCompat.Builder(this.applicationContext, id1)
        // 设置通知栏标题
        .setContentTitle(title)
        // 设置通知栏显示内容
        .setContentText(content)
        // 通知产生的时间
        .setWhen(time)
        // 设置通知小ICON
        .setSmallIcon(iconResId)
        // 点击触发事件
        .setContentIntent(tapIntent)
        // 允许点击取消显示
        .setAutoCancel(isAutoCancel)
        // 设置通知级别
        .setPriority(NotificationCompat.PRIORITY_MAX)
    if (hasProgress) {
        // 添加进度条
        notificationBuilder.setProgress(max, progress, indeterminate)
    }
    if (isVoiceEnable) {
        // 设置响铃
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND)
    }
    manager.notify(notifyId, notificationBuilder.build())
    if (importance == NotificationManager.IMPORTANCE_HIGH) {
        // 顶部弹出提示
        with(NotificationManagerCompat.from(this)) {
            notify(notifyId, notificationBuilder.build())
        }
    }
    return notificationBuilder
}

/**
 * 更新通知
 * @receiver Context
 * @param notifyId Int
 * @param notificationBuilder Builder
 * @param title String
 * @param content String
 * @param hasProgress Boolean
 * @param max Int
 * @param progress Int
 * @param indeterminate Boolean
 * @return NotificationCompat.Builder
 */
fun Context.updateNotification(
    notifyId: Int,
    notificationBuilder: NotificationCompat.Builder,
    title: String,
    content: String,
    hasProgress: Boolean = false,
    max: Int = 100,
    progress: Int = 0,
    indeterminate: Boolean = false,
    isAutoCancel: Boolean = true
): NotificationCompat.Builder {
    // 拿到通知栏管理器
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationBuilder
        // 设置通知栏标题
        .setContentTitle(title)
        // 设置通知栏显示内容
        .setContentText(content)
        // 允许点击取消显示
        .setAutoCancel(isAutoCancel)
    if (hasProgress) {
        // 添加进度条
        notificationBuilder.setProgress(max, progress, indeterminate)
    } else {
        notificationBuilder.setProgress(0, 0, false)
    }
    manager.notify(notifyId, notificationBuilder.build())
    return notificationBuilder
}

/**
 * 移除通知
 * @receiver Context
 * @param notifyId Int
 */
fun Context.removeNotification(notifyId: Int) {
    // 拿到通知栏管理器
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.cancel(notifyId)
}

/**
 * 移除所有通知
 * @receiver Context
 */
fun Context.removeAllNotification() {
    // 拿到通知栏管理器
    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    manager.cancelAll()
}