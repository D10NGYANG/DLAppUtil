# ActivityManager

活动管理器

## ① 获取栈中的Activity列表Flow

```kotlin
val listFlow: StateFlow<List<Activity>> = ActivityManager.listFlow
```

## ② 获取栈顶的ActivityFlow

```kotlin
val topFlow: StateFlow<Activity?> = ActivityManager.topFlow
```

## ③ 获取当前Activity实例

```kotlin
val current: Activity? = ActivityManager.top()
```

## ④ 注销当前Activity

```kotlin
ActivityManager.finishTop()
```

## ⑤ 获取特定Activity实例

```kotlin
val mainAct: MainActivity? = ActivityManager.get<MainActivity>()
```
