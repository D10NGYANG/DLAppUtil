# PhysicalButtonAccessibilityService

手持机机身按键监听服务

## ① 判断服务是否开启

> 当前服务为无障碍服务

```kotlin
val isEnable: Boolean = PhysicalButtonAccessibilityService.isEnable()
```

## ② 开启服务

```kotlin
PhysicalButtonAccessibilityService.start()
```

## ③ 获取单击按键事件Flow

```kotlin
PhysicalButtonAccessibilityService.clickEventFlow
    .onEach {
        // TODO 处理单击按键事件
    }
    .launchIn(lifecycleScope)
```

## ④ 获取长按按键事件Flow

```kotlin
PhysicalButtonAccessibilityService.longPressEventFlow
    .onEach {
        // TODO 处理长按按键事件
    }
    .launchIn(lifecycleScope)
```