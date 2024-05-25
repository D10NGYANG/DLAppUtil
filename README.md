# DLAppUtil

Android APP 通用工具

> 最新版本 `ver=2.5.5`

建议先把demo运行起来看看效果

## 安装说明

1 添加仓库

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository")
    }
}
```

2 添加依赖

```kotlin
dependencies {
    // APP通用工具
    implementation("com.github.D10NGYANG:DLAppUtil:$ver")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_ver")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlin_coroutines_ver")
}
```

3 混淆

```pro
-keep class com.d10ng.app.** {*;}
-dontwarn com.d10ng.app.**
```

## 使用说明

### 管理器

- `ActivityManager` [Activity管理器](docs/manager/ActivityManager.md)
- `AppManager` [App管理器](docs/manager/AppManager.md)
- `SystemManager` [系统管理器](docs/manager/SystemManager.md)
- `PermissionManager` [权限管理器](docs/manager/PermissionManager.md)
- `NotificationController` [通知管理器](docs/manager/NotificationController.md)
- `ContactManager` [联系人管理器](docs/manager/ContactManager.md)
- `SmsController` [短信管理器](docs/manager/SmsController.md)
- `PhotoManager` [图片管理器](docs/manager/PhotoManager.md)

### 状态

- `BatteryStatusManager` [电池状态管理器](docs/status/BatteryStatusManager.md)
- `LocationStatusManager` [定位状态管理器](docs/status/LocationStatusManager.md)
- `GnssStatusManager` [卫星状态管理器](docs/status/GnssStatusManager.md)
- `NetworkStatusManager` [网络状态管理器](docs/status/NetworkStatusManager.md)
- `SensorStatus` [传感器状态](docs/status/SensorStatus.md)

### 工具

- `NavigationUtils` [常用页面导航工具](docs/util/NavigationUtils.md)
- `DisplayUtils` [显示工具](docs/util/DisplayUtils.md)
- `ActivityUIUtils` [活动界面工具类](docs/util/ActivityUIUtils.md)

### 资源

- `AssetsUtils` [Assets资源读取工具](docs/resource/AssetsUtils.md)
- `ExternalUtils` [外置存储器工具](docs/resource/ExternalUtils.md)
- `ResourcesUtils` [资源读取工具](docs/resource/ResourcesUtils.md)

### 服务

- `PhysicalButtonAccessibilityService` [手持机机身按键监听服务](docs/service/PhysicalButtonAccessibilityService.md)
