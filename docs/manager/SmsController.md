# SmsController

短信管理器

---

**参数说明**

`SmsController.Data`

> 短信数据

```kotlin
/**
 * 短信ID
 * > 会重复的，因为当用户删除短信数据库里的短信后，下一条短信就会重复用这个ID
 */
var id: String = ""

/**
 * 插卡位置
 * -1 - 曾经插入的卡，现在不在手机里
 * 0 - 卡槽1
 * 1 - 卡槽2
 */
var subId: Long = -1

/**
 * 来源号码
 */
var phone: String = ""

/**
 * 内容
 */
var content: String = ""

/**
 * 消息到达的时间戳，单位毫秒
 */
var time: Long = 0
```

---

## ① 获取短信Flow

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 读取短信 -->
    <uses-feature android:name="android.hardware.telephony" android:required="false" />

    <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.RECEIVE_SMS" />
</manifest>
```

动态申请权限

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    if (PermissionManager.request(
            arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
            )
        )
    ) {
        // 读取短信
        val smsFlow: SharedFlow<SmsController.Data> = SmsController.receiveFlow
        // 监听短信
        smsFlow.collect { sms ->
            // TODO
        }
    }
}
```

## ② 读取最新一条短信

> 需要动态申请权限：`android.permission.READ_SMS`，请参考[① 获取短信Flow](#①%20获取短信Flow)

```kotlin
val sms: SmsController.Data? = SmsController.readLatest()
```

## ③ 读取最近N条短信

> 需要动态申请权限：`android.permission.READ_SMS`，请参考[① 获取短信Flow](#①%20获取短信Flow)
> - @param count Int 读取的数量

```kotlin
val smsList: List<SmsController.Data> = SmsController.readLatest(10)
```

## ④ 读取指定时间以后的全部消息

> 需要动态申请权限：`android.permission.READ_SMS`，请参考[① 获取短信Flow](#①%20获取短信Flow)
> - @param timestamp Long 时间戳，单位毫秒，获取的是大于等于这个时间的短信

```kotlin
val smsList: List<SmsController.Data> = SmsController.readAfter(curTime - 7 * 24 * 60 * 60 * 1000)
```

## ⑤ 发送文本短信

> 需要动态申请权限：`android.permission.SEND_SMS`、`android.permission.READ_PHONE_STATE`
> ，请参考[① 获取短信Flow](#①%20获取短信Flow)
> > 发送失败会抛出对应异常
> - @param desAddress String 目标号码
> - @param scAddress String? 短信中心号码，默认为null
> - @param content String 短信内容
> - @param overtime Long 超时时间，默认为60秒
> - @param use SmsManager? 使用的短信管理器，相当于指定发送卡槽，不填则使用默认的

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    runCatching {
        SmsController.sendText(
            desAddress = "10010",
            content = "102"
        )
    }.onFailure {
        // 发送失败
        it.printStackTrace()
    }.onSuccess {
        // 发送成功
    }
}
```

## ⑥ 通过指定卡槽获取短信管理器

> 需要动态申请权限：`android.permission.READ_PHONE_STATE`，请参考[① 获取短信Flow](#①%20获取短信Flow)
> - @param slot Int 卡槽，0为卡槽1，1为卡槽2

```kotlin
val smsManager: SmsManager? = SmsController.getSmsManager(0)
```