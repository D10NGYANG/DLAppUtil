# SensorStatus

传感器状态

--- 

**参数说明**

`SensorStatus.Event`

> 传感器事件

```kotlin
// 精度
val accuracy: Int
// 是否是不连续事件的第一个事件
val firstEventAfterDiscontinuity: Boolean
// 传感器
val sensor: Sensor
// 时间戳
val timestamp: Long
// 传感器数据
val values: FloatArray
```

---

## ① 启动传感器状态监听

```kotlin
// 举例：监听光线传感器
val status = SensorStatus(Sensor.TYPE_LIGHT).apply { start() }
```

## ② 获取传感器数据Flow

```kotlin
// 传感器数据
val eventFlow: StateFlow<SensorStatus.Event?> = status.eventFlow
// 传感器精度
val accuracyFlow: StateFlow<Pair<Sensor, Int>?> = status.accuracyFlow
```

## ③ 停止传感器状态监听

```kotlin
status.stop()
```

## ④ 传感器类型

```text
Sensor.TYPE_ACCELEROMETER -> "加速计"
Sensor.TYPE_ACCELEROMETER_LIMITED_AXES -> "限轴加速计"
Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED -> "限轴未校准加速计"
Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> "未校准的加速计"
Sensor.TYPE_AMBIENT_TEMPERATURE -> "温度传感器"
Sensor.TYPE_GAME_ROTATION_VECTOR -> "游戏旋转矢量"
Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> "地磁旋转矢量"
Sensor.TYPE_GRAVITY -> "重力传感器"
Sensor.TYPE_GYROSCOPE -> "陀螺仪"
Sensor.TYPE_GYROSCOPE_LIMITED_AXES -> "限轴陀螺仪"
Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED -> "限轴未校准陀螺仪"
Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> "未校准的陀螺仪"
Sensor.TYPE_HEADING -> "方向传感器"
Sensor.TYPE_HEAD_TRACKER -> "头部追踪器"
Sensor.TYPE_HEART_BEAT -> "心跳传感器"
Sensor.TYPE_HEART_RATE -> "心率传感器"
Sensor.TYPE_HINGE_ANGLE -> "铰链角度传感器"
Sensor.TYPE_LIGHT -> "光线传感器"
Sensor.TYPE_LINEAR_ACCELERATION -> "线性加速度传感器"
Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT -> "低延迟脱身检测"
Sensor.TYPE_MAGNETIC_FIELD -> "磁场传感器"
Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> "未校准的磁场传感器"
Sensor.TYPE_MOTION_DETECT -> "动作检测器"
Sensor.TYPE_ORIENTATION -> "方向传感器" // Deprecated
Sensor.TYPE_POSE_6DOF -> "六自由度姿态传感器"
Sensor.TYPE_PRESSURE -> "气压传感器"
Sensor.TYPE_PROXIMITY -> "接近传感器"
Sensor.TYPE_RELATIVE_HUMIDITY -> "相对湿度传感器"
Sensor.TYPE_ROTATION_VECTOR -> "旋转矢量传感器"
Sensor.TYPE_SIGNIFICANT_MOTION -> "显著运动检测器"
Sensor.TYPE_STATIONARY_DETECT -> "静止检测器"
Sensor.TYPE_STEP_COUNTER -> "计步器"
Sensor.TYPE_STEP_DETECTOR -> "步伐探测器"
Sensor.TYPE_TEMPERATURE -> "温度传感器" // Deprecated
// Add any additional sensor types if necessary
else -> "未知ID ${sensor.type}"
```