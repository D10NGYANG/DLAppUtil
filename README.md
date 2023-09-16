# DLAppUtil
Android APP 通用工具

## 使用

1 添加仓库
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository' }
  }
}
```

2 添加依赖
```gradle
dependencies {
    // APP通用工具
    implementation 'com.github.D10NGYANG:DLAppUtil:2.4.6'
    // 协程
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

3 权限声明，按需添加

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- 定位状态、GNSS状态 -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <!-- WIFI状态 -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <!-- 发送通知 -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <!-- 读取手机状态 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <!-- 振动 -->
    <uses-permission android:name="android.permission.VIBRATE" />
    <!-- 写入外部存储 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
</manifest>
```

4 混淆

```pro
-keep class com.d10ng.app.** {*;}
-dontwarn com.d10ng.app.**
```
