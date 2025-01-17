# NetworkStatusManager

网络状态管理器

---

**枚举类型**

`NetworkStatusManager.NetworkType`

> 网络类型

| #              | value | text      | 
|----------------|-------|-----------|  
| NONE           | 0     | 无网络       |
| UNKNOWN_MOBILE | 1     | 未知移动网络    |
| G2             | 2     | 2G        |
| G3             | 3     | 3G        |  
| G4             | 4     | 4G        |
| G5             | 5     | 5G        |
| WIFI_2G        | 10    | 2.5G WIFI |
| WIFI_5G        | 11    | 5G WIFI   |
| WIFI_UNKNOWN   | 12    | 未知WIFI类型  |
| ETHERNET       | 20    | 以太网       |
| BLUETOOTH      | 30    | 蓝牙        |
| VPN            | 40    | VPN       |
| UNKNOWN        | 100   | 未知        |

---

## ① 启动网络监听

> 在版本`2.5.8`版本后加入。

```kotlin
NetworkStatusManager.start()
```

## ② 获取网络是否可用

```kotlin
// 获取Flow
val isAvailableFlow: StateFlow<Boolean> = NetworkStatusManager.isAvailableFlow
// 获取当前网络是否可用
val isAvailable: Boolean = NetworkStatusManager.isAvailable()
// 另一个方法
val isAvailable2: Boolean = isNetworkAvailable()
```

## ③ 获取网络Flow

```kotlin
// 获取Flow
val networkFlow: StateFlow<Network?> = NetworkStatusManager.networkFlow
```

## ④ 获取网络属性Flow

```kotlin
// 获取Flow
val linkPropertiesFlow: StateFlow<LinkProperties?> = NetworkStatusManager.linkPropertiesFlow
```

## ⑤ 获取网络能力Flow

```kotlin
// 获取Flow
val networkCapabilitiesFlow: StateFlow<NetworkCapabilities?> =
    NetworkStatusManager.networkCapabilitiesFlow
```

## ⑥ 获取网络类型Flow

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 网络信息 -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
</manifest>
```

动态申请权限

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    if (PermissionManager.request(Manifest.permission.READ_PHONE_STATE)) {
        // 获取Flow
        val networkTypeFlow: StateFlow<NetworkStatusManager.NetworkType> =
            NetworkStatusManager.networkTypeFlow
    } else {
        // 权限不足
    }
}
```

## ⑦ 获取IPV4地址Flow

```kotlin
// 获取Flow
val ipv4Flow: StateFlow<String> = NetworkStatusManager.ipv4Flow
```

## ⑧ 获取IPV6地址Flow

```kotlin
// 获取Flow
val ipv6Flow: StateFlow<String> = NetworkStatusManager.ipv6Flow
```

