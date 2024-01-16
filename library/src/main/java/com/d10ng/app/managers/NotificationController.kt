package com.d10ng.app.managers

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.d10ng.app.startup.ctx

/**
 * 通知管理器
 * @Author d10ng
 * @Date 2023/9/19 10:19
 */
object NotificationController {

    private lateinit var manager: NotificationManager

    internal fun init(app: Application) {
        manager = app.getSystemService(NotificationManager::class.java)
    }

    /**
     * 判断是否允许发出通知
     * @return Boolean
     */
    fun isEnable(): Boolean {
        return NotificationManagerCompat.from(ctx).areNotificationsEnabled()
    }

    /**
     * 判断通知通道等级是否大于等于默认等级
     * > 只有大于等于默认等级才能正常显示横幅通知（具有声音震动效果）
     * > 经过测试MIUI无效
     * @param channelId String
     * @return Boolean
     */
    fun isBannerNotificationsEnabled(channelId: String): Boolean {
        val channel = manager.getNotificationChannel(channelId) ?: return false
        return channel.importance > NotificationManager.IMPORTANCE_DEFAULT
    }

    /**
     * 创建通知渠道
     * > 渠道ID相同，不会重复创建，但是会更新部分渠道信息（如：渠道名称、渠道描述，以及降低重要程度）
     * @param channelId String 渠道ID
     * @param channelName String 渠道名称
     * @param channelImportance Int 渠道重要程度
     * @param channelDescription String? 渠道描述
     * @param customChannel (NotificationChannel) -> Unit 自定义渠道
     * @return NotificationChannel
     */
    fun createChannel(
        channelId: String,
        channelName: String,
        channelImportance: Int = NotificationManager.IMPORTANCE_HIGH,
        channelDescription: String? = null,
        customChannel: (NotificationChannel) -> Unit = {},
    ): NotificationChannel {
        val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
            // 渠道描述
            description = channelDescription
            // 允许震动
            enableVibration(true)
            // 允许灯光
            enableLights(true)
            // 锁屏显示
            this.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            // 允许忽略勿扰模式
            setBypassDnd(false)
            // 允许通知进行角标统计数量
            setShowBadge(true)
            // 自定义渠道
            customChannel(this)
        }
        manager.createNotificationChannel(channel)
        return channel
    }

    /**
     * 删除通知渠道
     * @param channelId String
     */
    fun removeChannel(channelId: String) {
        manager.deleteNotificationChannel(channelId)
    }

    /**
     * 创建标准文本通知，并创建通知渠道
     * > 需要权限：Manifest.permission.POST_NOTIFICATIONS
     * @param channelId String 渠道ID
     * @param channelName String 渠道名称
     * @param channelImportance Int 渠道重要程度
     * @param channelDescription String? 渠道描述
     * @param customChannel (NotificationChannel) -> Unit 自定义渠道
     * @param notifyId Int 通知ID
     * @param title String 标题
     * @param content String 内容
     * @param bigText String? 大文本，点击展开后显示
     * @param smallIcon Int 小图标
     * @param priority Int 优先级
     * @param category String 类别
     * @param tapIntent PendingIntent? 点击触发事件
     * @return NotificationCompat.Builder
     */
    fun createStandardWithChannel(
        channelId: String,
        channelName: String,
        channelImportance: Int = NotificationManager.IMPORTANCE_HIGH,
        channelDescription: String? = null,
        customChannel: (NotificationChannel) -> Unit = {},
        notifyId: Int,
        title: String,
        content: String,
        bigText: String? = null,
        smallIcon: Int,
        priority: Int = NotificationCompat.PRIORITY_HIGH,
        category: String = NotificationCompat.CATEGORY_MESSAGE,
        tapIntent: PendingIntent? = null,
    ): NotificationCompat.Builder {
        // 创建渠道
        createChannel(channelId, channelName, channelImportance, channelDescription, customChannel)
        return createStandard(
            channelId,
            notifyId,
            title,
            content,
            bigText,
            smallIcon,
            priority,
            category,
            tapIntent
        )
    }

    /**
     * 创建标准文本通知
     * > 需要权限：Manifest.permission.POST_NOTIFICATIONS
     * @param channelId String 渠道ID
     * @param notifyId Int 通知ID
     * @param title String 标题
     * @param content String 内容
     * @param bigText String? 大文本，点击展开后显示
     * @param smallIcon Int 小图标
     * @param priority Int 优先级
     * @param category String 类别
     * @param tapIntent PendingIntent? 点击触发事件
     * @return NotificationCompat.Builder
     */
    @SuppressLint("MissingPermission")
    fun createStandard(
        channelId: String,
        notifyId: Int,
        title: String,
        content: String,
        bigText: String? = null,
        smallIcon: Int,
        priority: Int = NotificationCompat.PRIORITY_HIGH,
        category: String = NotificationCompat.CATEGORY_MESSAGE,
        tapIntent: PendingIntent? = null,
    ): NotificationCompat.Builder {
        // 创建通知
        val builder = NotificationCompat.Builder(ctx, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(smallIcon)
            .setPriority(priority)
            .setCategory(category)
            .setContentIntent(tapIntent)
            .setAutoCancel(true)
        if (bigText != null) {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
        }
        // 显示通知
        with(NotificationManagerCompat.from(ctx)) {
            notify(notifyId, builder.build())
        }
        return builder
    }

    /**
     * 创建带进度通知，并创建通知渠道
     * > 需要权限：Manifest.permission.POST_NOTIFICATIONS
     * @param channelId String 渠道ID
     * @param channelName String 渠道名称
     * @param channelImportance Int 渠道重要程度
     * @param channelDescription String? 渠道描述
     * @param customChannel (NotificationChannel) -> Unit 自定义渠道
     * @param notifyId Int 通知ID
     * @param title String 标题
     * @param content String 内容
     * @param subText String? 副标题，一般用于描述任务进度或剩余时间
     * @param smallIcon Int 小图标
     * @param max Int 进度条最大值
     * @param progress Int 进度条进度
     * @param indeterminate Boolean 进度条是否无限循环滚动
     * @param tapIntent PendingIntent? 点击触发事件
     * @return NotificationCompat.Builder
     */
    fun createProgressWithChannel(
        channelId: String,
        channelName: String,
        channelImportance: Int = NotificationManager.IMPORTANCE_HIGH,
        channelDescription: String? = null,
        customChannel: (NotificationChannel) -> Unit = {},
        notifyId: Int,
        title: String,
        content: String,
        subText: String? = null,
        smallIcon: Int,
        max: Int = 100,
        progress: Int = 0,
        indeterminate: Boolean = false,
        tapIntent: PendingIntent? = null,
    ): NotificationCompat.Builder {
        // 创建渠道
        createChannel(channelId, channelName, channelImportance, channelDescription, customChannel)
        return createProgress(
            channelId,
            notifyId,
            title,
            content,
            subText,
            smallIcon,
            max,
            progress,
            indeterminate,
            tapIntent
        )
    }

    /**
     * 创建带进度通知
     * > 需要权限：Manifest.permission.POST_NOTIFICATIONS
     * @param channelId String 渠道ID
     * @param notifyId Int 通知ID
     * @param title String 标题
     * @param content String 内容
     * @param subText String? 副标题，一般用于描述任务进度或剩余时间
     * @param smallIcon Int 小图标
     * @param max Int 进度条最大值
     * @param progress Int 进度条进度
     * @param indeterminate Boolean 进度条是否无限循环滚动
     * @param tapIntent PendingIntent? 点击触发事件
     * @return NotificationCompat.Builder
     */
    @SuppressLint("MissingPermission")
    fun createProgress(
        channelId: String,
        notifyId: Int,
        title: String,
        content: String,
        subText: String? = null,
        smallIcon: Int,
        max: Int = 100,
        progress: Int = 0,
        indeterminate: Boolean = false,
        tapIntent: PendingIntent? = null,
    ): NotificationCompat.Builder {
        // 创建通知
        val builder = NotificationCompat.Builder(ctx, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(smallIcon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setProgress(max, progress, indeterminate)
            .setContentIntent(tapIntent)
            // 只在第一次显示通知时发出声音
            .setOnlyAlertOnce(true)
        if (subText != null) {
            builder.setSubText(subText)
        }
        // 显示通知
        with(NotificationManagerCompat.from(ctx)) {
            notify(notifyId, builder.build())
        }
        return builder
    }

    /**
     * 创建对话消息通知，并创建通知渠道
     * > 需要权限：Manifest.permission.POST_NOTIFICATIONS
     * @param channelId String 渠道ID
     * @param channelName String 渠道名称
     * @param channelImportance Int 渠道重要程度
     * @param channelDescription String? 渠道描述
     * @param customChannel (NotificationChannel) -> Unit 自定义渠道
     * @param notifyId Int 通知ID
     * @param title String 标题
     * @param content String 内容
     * @param smallIcon Int 小图标
     * @param person Person 发送者
     * @param tapIntent PendingIntent? 点击触发事件
     * @param bindMessages (NotificationCompat.MessagingStyle) -> Unit 绑定消息
     * @return NotificationCompat.Builder
     */
    fun createMessagingWithChannel(
        channelId: String,
        channelName: String,
        channelImportance: Int = NotificationManager.IMPORTANCE_HIGH,
        channelDescription: String? = null,
        customChannel: (NotificationChannel) -> Unit = {},
        notifyId: Int,
        title: String,
        content: String,
        smallIcon: Int,
        person: Person,
        tapIntent: PendingIntent? = null,
        bindMessages: (NotificationCompat.MessagingStyle) -> Unit = {},
    ): NotificationCompat.Builder {
        // 创建渠道
        createChannel(channelId, channelName, channelImportance, channelDescription, customChannel)
        return createMessaging(
            channelId,
            notifyId,
            title,
            content,
            smallIcon,
            person,
            tapIntent,
            bindMessages
        )
    }

    /**
     * 创建对话消息通知
     * > 需要权限：Manifest.permission.POST_NOTIFICATIONS
     * @param channelId String 渠道ID
     * @param notifyId Int 通知ID
     * @param title String 标题
     * @param content String 内容
     * @param smallIcon Int 小图标
     * @param person Person 发送者
     * @param tapIntent PendingIntent? 点击触发事件
     * @param bindMessages (NotificationCompat.MessagingStyle) -> Unit 绑定消息
     * @return NotificationCompat.Builder
     */
    @SuppressLint("MissingPermission")
    fun createMessaging(
        channelId: String,
        notifyId: Int,
        title: String,
        content: String,
        smallIcon: Int,
        person: Person,
        tapIntent: PendingIntent? = null,
        bindMessages: (NotificationCompat.MessagingStyle) -> Unit = {},
    ): NotificationCompat.Builder {
        // 创建通知
        val style = NotificationCompat.MessagingStyle(person).apply(bindMessages)
        val builder = NotificationCompat.Builder(ctx, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(smallIcon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(tapIntent)
            .setAutoCancel(true)
            .setNumber(style.messages.size)
            .setStyle(style)
        // 显示通知
        with(NotificationManagerCompat.from(ctx)) {
            notify(notifyId, builder.build())
        }
        return builder
    }

    /**
     * 更新通知
     * @param notifyId Int
     * @param builder NotificationCompat.Builder
     */
    fun update(
        notifyId: Int,
        builder: NotificationCompat.Builder
    ) {
        manager.notify(notifyId, builder.build())
    }

    /**
     * 移除通知
     * @param notifyId Int
     */
    fun cancel(notifyId: Int) {
        manager.cancel(notifyId)
    }

    /**
     * 移除所有通知
     */
    fun cancelAll() {
        manager.cancelAll()
    }
}