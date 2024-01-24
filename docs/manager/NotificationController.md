# NotificationController

通知管理器

> ⚠️
> ---
> - 每种通知样式在不同的系统上显示效果不同，建议有机会得话，可以在不同的系统上测试一下，然后再决定使用哪种通知样式；
>
> - 更详细的用法可以查看demo中的代码，也可以运行起来看看实际效果；

## ① 判断是否允许发出通知

```kotlin
/**
 * 判断是否允许发出通知
 * @return Boolean
 */
fun isEnable(): Boolean
```

```kotlin
val isEnable = NotificationController.isEnable() // true or false
```

## ② 判断通知渠道是否支持横幅展示

```kotlin
/**
 * 判断通知通道等级是否大于等于默认等级
 * > 只有大于等于默认等级才能正常显示横幅通知（具有声音震动效果）
 * > 经过测试 MIUI 无效
 * @param channelId String
 * @return Boolean
 */
fun isBannerNotificationsEnabled(channelId: String): Boolean
```

```kotlin
val isBanner = NotificationController.isBannerNotificationsEnabled("channelId") // true or false
```

## ③ 创建通知渠道

```kotlin
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
): NotificationChannel
```

```kotlin
val channel = NotificationController.createChannel(
    "channelId",
    "channelName",
    NotificationManager.IMPORTANCE_HIGH,
    "channelDescription"
)
```

## ④ 删除通知渠道

```kotlin
/**
 * 删除通知渠道
 * @param channelId String
 */
fun removeChannel(channelId: String)
```

```kotlin
NotificationController.removeChannel("channelId")
```

## ⑤ 创建标准文本通知

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 发送通知 -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
</manifest>
```

动态申请权限

```kotlin
launch {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PermissionManager.request(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            )
        )
    } else {
        PermissionManager.request(Manifest.permission.SYSTEM_ALERT_WINDOW)
    }
}
```

```kotlin
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
): NotificationCompat.Builder
```

```kotlin
// 创建标准通知
NotificationController.createStandard(
    channelId = "standard_text",
    notifyId = (curTime / 1000).toInt(),
    title = "通知标题，一般为应用名称",
    content = "通知内容，一般为任务信息，或者简要任务的介绍",
    smallIcon = R.drawable.ic_launcher_foreground,
    tapIntent = getIntent()
)
// 创建大文本通知
NotificationController.createStandard(
    channelId = "standard_text",
    notifyId = (curTime / 1000).toInt(),
    title = "通知标题，一般为应用名称",
    content = "大文本通知效果不佳，在MIUI上甚至只有长按才能显示",
    bigText = "大文本通知内容，一般为任务信息，或者简要任务的介绍，如果内容过长，可以使用大文本通知，这样可以显示更多的内容；",
    smallIcon = R.drawable.ic_launcher_foreground,
    tapIntent = getIntent()
)
```

## ⑥ 创建标准文本通知，并创建通知渠道

> 权限相关部分查看上面[⑤ 创建标准文本通知](#⑤%20创建标准文本通知)的说明

```kotlin
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
): NotificationCompat.Builder
```

## ⑦ 创建带进度通知

> 权限相关部分查看上面[⑤ 创建标准文本通知](#⑤%20创建标准文本通知)的说明

```kotlin
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
): NotificationCompat.Builder
```

```kotlin
val id = 100
// 创建带进度通知
val builder = NotificationController.createProgress(
    channelId = "progress",
    notifyId = id,
    title = "通知标题，一般为应用名称",
    content = "通知内容，一般为任务名称",
    subText = "还剩5分钟",
    smallIcon = R.drawable.ic_launcher_foreground,
    tapIntent = getIntent()
)
// 更新进度
scope.launch {
    var p = 0
    while (p < 100) {
        delay(1000)
        p += 10
        builder.setProgress(100, p, false)
        NotificationController.update(id, builder)
    }
    // 移除通知
    NotificationController.cancel(id)
}
```

## ⑧ 创建带进度通知，并创建通知渠道

> 权限相关部分查看上面[⑤ 创建标准文本通知](#⑤%20创建标准文本通知)的说明

```kotlin
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
): NotificationCompat.Builder
```

## ⑨ 创建对话消息通知

> 权限相关部分查看上面[⑤ 创建标准文本通知](#⑤%20创建标准文本通知)的说明

```kotlin
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
): NotificationCompat.Builder
```

单聊

```kotlin
val person = Person.Builder().setName("张三").build()
NotificationController.createMessaging(
    channelId = "dialog",
    notifyId = (curTime / 1000).toInt(),
    title = "两条新消息",
    content = "--",
    smallIcon = R.drawable.ic_launcher_foreground,
    person = person,
    tapIntent = getIntent(),
    bindMessages = {
        it.addMessage(
            "你好，我是张三",
            curTime - 2000,
            person
        )
        it.addMessage(
            "周四晚上你有空参加校园活动吗？",
            curTime,
            person
        )
    }
)
```

群聊

```kotlin
val person = Person.Builder().setName("张三").build()
val person2 = Person.Builder().setName("李四").build()
NotificationController.createMessaging(
    channelId = "dialog",
    notifyId = (curTime / 1000).toInt(),
    title = "两条新消息",
    content = "--",
    smallIcon = R.drawable.ic_launcher_foreground,
    person = person,
    tapIntent = getIntent(),
    bindMessages = {
        it.conversationTitle = "群聊名称"
        it.addMessage(
            "你好，我是张三",
            curTime - 2000,
            person
        )
        it.addMessage(
            "周四晚上你有空参加校园活动吗？",
            curTime - 1000,
            person
        )
        it.addMessage(
            "你好，我是李四",
            curTime - 500,
            person2
        )
        it.addMessage(
            "我有空，你呢？",
            curTime,
            person2
        )
    }
)
```

## ⑩ 创建对话消息通知，并创建通知渠道

> 权限相关部分查看上面[⑤ 创建标准文本通知](#⑤%20创建标准文本通知)的说明

```kotlin
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
): NotificationCompat.Builder
```

## ⑪ 更新通知

```kotlin
/**
 * 更新通知
 * @param notifyId Int
 * @param builder NotificationCompat.Builder
 */
fun update(
    notifyId: Int,
    builder: NotificationCompat.Builder
)
```

```kotlin
NotificationController.update(id, builder)
```

## ⑫ 移除通知

```kotlin
/**
 * 移除通知
 * @param notifyId Int
 */
fun cancel(notifyId: Int)
```

```kotlin
NotificationController.cancel(id)
```

## ⑬ 移除所有通知

```kotlin
/**
 * 移除所有通知
 */
fun cancelAll()
```

```kotlin
NotificationController.cancelAll()
```

## ⑭ 显示华为手机的应用角标

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 华为角标 -->
    <uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE" />
    <uses-permission android:name="android.permission.INTERNET" />
</manifest>
```

> 权限不需要动态申请

```kotlin
/**
 * 显示华为手机的应用角标
 * > 需要权限：com.huawei.android.launcher.permission.CHANGE_BADGE、android.permission.INTERNET
 * > 官方文档：https://developer.huawei.com/consumer/cn/doc/Corner-Guides/30802
 * @param number Int 角标数量
 */
fun showHuaweiBadge(number: Int)
```

```kotlin
NotificationController.showHuaweiBadge(1)
```