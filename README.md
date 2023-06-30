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
    // 或者使用我的镜像仓库
    maven { url 'https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository' }
  }
}
```
2 Add the dependency
```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    
    // APP通用工具
    implementation 'com.github.D10NGYANG:DLAppUtil:2.3.2'
    // 协程
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2'
    // Preference DataStore
    implementation "androidx.datastore:datastore-preferences:1.0.0"
}
```
3 混淆
```pro
-keep class com.d10ng.app.** {*;}
-dontwarn com.d10ng.app.**
```
