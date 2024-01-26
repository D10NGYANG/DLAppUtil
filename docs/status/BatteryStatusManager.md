# BatteryStatusManager

电池状态

---

**枚举类型**

`BatteryStatusManager.ChargeType`

> 充电类型

| #        | value                                   | text  |
|----------|-----------------------------------------|-------|
| AC       | BatteryManager.BATTERY_PLUGGED_AC       | 交流电   |
| USB      | BatteryManager.BATTERY_PLUGGED_USB      | USB充电 |
| WIRELESS | BatteryManager.BATTERY_PLUGGED_WIRELESS | 无线充电  |

`BatteryStatusManager.HealthType`

> 电池健康状态

| #                   | value                                             | text  |
|---------------------|---------------------------------------------------|-------|
| UNKNOWN             | BatteryManager.BATTERY_HEALTH_UNKNOWN             | 未知    |
| GOOD                | BatteryManager.BATTERY_HEALTH_GOOD                | 良好    |
| OVERHEAT            | BatteryManager.BATTERY_HEALTH_OVERHEAT            | 过热    |
| DEAD                | BatteryManager.BATTERY_HEALTH_DEAD                | 没电    |
| OVER_VOLTAGE        | BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE        | 电量过低  |
| UNSPECIFIED_FAILURE | BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE | 电池不正常 |
| COLD                | BatteryManager.BATTERY_HEALTH_COLD                | 冷却    |
| FAILURE             | BatteryManager.BATTERY_HEALTH_FAILURE             | 未知错误  |

## ① 获取电量Flow

```kotlin
// 获取Flow
val batteryFlow: StateFlow<Float?> = BatteryStatusManager.batteryFlow
// 获取当前电量
val battery: Float? = BatteryStatusManager.battery()
// 更简单用法
val battery2: Float? = systemBattery()
```

## ② 获取充电状态Flow

```kotlin
// 获取Flow
val chargeTypeFlow: StateFlow<BatteryStatusManager.ChargeType?> =
    BatteryStatusManager.chargeTypeFlow
```

## ③ 是否正在充电Flow

```kotlin
// 获取Flow
val isChargingFlow: StateFlow<Boolean> = BatteryStatusManager.isChargingFlow
```

## ④ 获取健康状态Flow

```kotlin
// 获取Flow
val healthFlow: StateFlow<BatteryStatusManager.HealthType> = BatteryStatusManager.healthFlow
```

## ⑤ 获取电池温度Flow

```kotlin
// 获取Flow
val temperatureFlow: StateFlow<Double> = BatteryStatusManager.temperatureFlow
```

## ⑥ 获取电池电压Flow

```kotlin
// 获取Flow
val voltageFlow: StateFlow<Double> = BatteryStatusManager.voltageFlow
```

## ⑦ 获取电池技术Flow

```kotlin
// 获取Flow
val technologyFlow: StateFlow<String> = BatteryStatusManager.technologyFlow
```