# DLAppUtil
APP相关的通用工具
[![](https://jitpack.io/v/D10NGYANG/DLAppUtil.svg)](https://jitpack.io/#D10NGYANG/DLAppUtil)

## 使用
1 Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
2 Add the dependency
```gradle
dependencies {
    // APP通用工具
    implementation 'com.github.D10NGYANG:DLAppUtil:2.0'
    // 协程
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0'
    // Preference DataStore
    implementation "androidx.datastore:datastore-preferences:1.0.0"
    // 协程请求权限
    implementation 'com.sagar:coroutinespermission:2.0.3'
}
```
3 混淆
```pro
-keep class com.d10ng.applib.** {*;}
-dontwarn com.d10ng.applib.**

# 协程请求权限
-dontwarn com.eazypermissions.common.**
-keep class com.eazypermissions.common.** {*;}
-dontwarn com.eazypermissions.coroutinespermission.**
-keep class com.eazypermissions.coroutinespermission.** {*;}
```
