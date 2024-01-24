# DLAppUtil

Android APP 通用工具

> 最新版本 `ver=2.5.0`

## 安装说明

1 添加仓库

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
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

### 状态

### 工具

### 资源

### 服务
