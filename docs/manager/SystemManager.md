# SystemManager

系统管理器

## ① 获取Android ID

```kotlin
/**
 * Android ID号
 * > ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置。
 * > 从Android O（API级别26）开始，对于没有电话功能的设备，这个值可能不是唯一的。
 */
val id: String = androidId
```

## ② 获取手机型号

```kotlin
val model: String = phoneModel // MI 8
```

## ③ 获取手机品牌

```kotlin
val brand: String = phoneManufacturer // Xiaomi
```

## ④ 获取手机系统版本

```kotlin
val version: String = systemVersion // 10
```

## ⑤ 显示Toast消息

```kotlin
/**
 * 显示Toast消息
 * @param msg String
 * @param duration Int
 */
//fun showToast(msg: String, duration: Int = Toast.LENGTH_SHORT)

showToast("Hello World")
```

## ⑥ 控制手机震动

```kotlin
/**
 * 控制手机震动
 * @param time 震动时间长度 毫秒
 */
//fun vibrate(time: Long = 500)

vibrate()
```

## ⑦ 播放系统提示音

```kotlin
/**
 * 播放系统提示音
 * @param uri Uri 不填则是默认提示音
 */
//fun playRingtone(uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

playRingtone()
```

## ⑧ 复制字符串到系统剪贴板

```kotlin
/**
 * 复制字符串到系统剪贴板
 * @param label String 标记
 * @param text String 内容
 */
//fun copyToClipboard(label: String, text: String)

copyToClipboard("label", "Hello World")
```

## ⑨ 获取系统剪贴板内容

```kotlin
/**
 * 读取粘贴板
 * @return List<String>
 */
//fun readClipboard(): List<String>

val list: List<String> = readClipboard()
```