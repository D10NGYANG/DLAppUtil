# AppManager

App管理器

## ① 获取APP版本号

```kotlin
val version: String = appVersion // 1.0.0
```

## ② 获取APP版本号Code

```kotlin
val versionCode: Long = appVersionCode // 1
```

## ③ 重启APP

```kotlin
restartApp()
```

## ④ 检查特定APP是否已安装

> 需要权限：android.permission.QUERY_ALL_PACKAGES

在AndroidManifest.xml中添加声明：

```xml
<!-- 读取应用列表 -->
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"
    tools:ignore="QueryAllPackagesPermission" />
```

```kotlin
val isInstalled: Boolean = existApp("com.tencent.mm")
```

## ⑤ 获取所有已安装APP包名

> 需要权限：android.permission.QUERY_ALL_PACKAGES

在AndroidManifest.xml中添加声明：

```xml
<!-- 读取应用列表 -->
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"
    tools:ignore="QueryAllPackagesPermission" />
```

```kotlin
val list: List<String> = getAllInstalledApp()
```

## ⑥ 判断APP的无障碍服务是否开启

```kotlin
// 无障碍服务Class需要修改成你的服务类
val isEnable: Boolean = isAccessibilityServiceEnabled(AccessibilityService::class.java)
```
