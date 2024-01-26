# GnssStatusManager

卫星状态管理器

## ① 启动卫星状态请求

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
        // 开启卫星状态请求
        GnssStatusManager.start()
    } else {
        // 权限不足
    }
}
```

## ② 关闭卫星状态请求

```kotlin
GnssStatusManager.stop()
```

## ③ 获取卫星状态Flow

```kotlin
// 获取Flow
val gnssStatusFlow: StateFlow<GnssStatus?> = GnssStatusManager.statusFlow
```