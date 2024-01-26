# LocationStatusManager

定位状态

## ① 检查定位信息是否开启

```kotlin
val isEnable: Boolean = LocationStatusManager.isEnable()
// 另一种方式
val isEnable2: Boolean = isLocationEnabled()
```

## ② 开启定位

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 定位权限 -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
</manifest>
```

动态申请权限

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    if (PermissionManager.request(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    ) {
        // 开启定位
        LocationStatusManager.start()
    } else {
        // 权限不足
    }
}
```

## ③ 关闭定位

```kotlin
LocationStatusManager.stop()
```

## ④ 获取定位状态Flow

```kotlin
// 获取Flow
val locationStatusFlow: StateFlow<Location?> = LocationStatusManager.statusFlow
```
