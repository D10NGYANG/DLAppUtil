package com.d10ng.app.status

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * 传感器状态管理器
 * @Author d10ng
 * @Date 2023/9/1 16:29
 */
object SensorStatusManager {

    private lateinit var application: Application
    private lateinit var manager: SensorManager

    private val scope = CoroutineScope(Dispatchers.IO)
    private val eventFlow = MutableSharedFlow<SensorEvent>(extraBufferCapacity = 100)
    private val accuracyFlow = MutableSharedFlow<Pair<Sensor, Int>>(extraBufferCapacity = 100)

    internal fun init(app: Application) {
        application = app
        manager = app.getSystemService(SensorManager::class.java)
    }

    private val callback = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            p0 ?: return
            scope.launch { eventFlow.emit(p0) }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            p0 ?: return
            scope.launch { accuracyFlow.emit(Pair(p0, p1)) }
        }
    }

    /**
     * 获取传感器数据Flow
     * @return SharedFlow<SensorEvent>
     */
    fun getEventFlow() = eventFlow.asSharedFlow()

    /**
     * 获取传感器精度Flow
     * @return SharedFlow<Pair<Sensor, Int>>
     */
    fun getAccuracyFlow() = accuracyFlow.asSharedFlow()

    /**
     * 开始传感器数据监听器
     * 参考：https://developer.android.google.cn/guide/topics/sensors/sensors_overview
     * - [Sensor].TYPE_ACCELEROMETER -> 加速计，测量在所有三个物理轴向（x、y 和 z）上施加在设备上的加速力（包括重力），以 m/s2 为单位。
     * - [Sensor].TYPE_AMBIENT_TEMPERATURE -> 温度传感器，以摄氏度 (°C) 为单位测量环境室温。
     * - [Sensor].TYPE_GRAVITY -> 软件加速计，测量在所有三个物理轴向（x、y、z）上施加在设备上的重力，单位为 m/s2。
     * - [Sensor].TYPE_GYROSCOPE -> 陀螺仪，测量设备在三个物理轴向（x、y 和 z）上的旋转速率，以 rad/s 为单位。
     * - [Sensor].TYPE_LIGHT -> 光线传感器，测量环境光级（照度），以 lx 为单位。
     * - [Sensor].TYPE_LINEAR_ACCELERATION -> 加速计，监测单个轴向上的加速度。
     * - [Sensor].TYPE_MAGNETIC_FIELD -> 地磁场传感器，测量所有三个物理轴向（x、y、z）上的环境地磁场，以 μT 为单位。
     * - [Sensor].TYPE_ORIENTATION -> 屏幕方向，测量设备围绕所有三个物理轴（x、y、z）旋转的度数。从 API 级别 3 开始，您可以结合使用重力传感器、地磁场传感器和 getRotationMatrix() 方法来获取设备的倾角矩阵和旋转矩阵。
     * - [Sensor].TYPE_PRESSURE -> 气压传感器，测量环境气压，以 hPa 或 mbar 为单位。
     * - [Sensor].TYPE_PROXIMITY -> 距离感应器，测量物体相对于设备显示屏幕的距离，以 cm 为单位。该传感器通常用于确定手机是否被举到人的耳边。
     * - [Sensor].TYPE_RELATIVE_HUMIDITY -> 湿度传感器，测量环境的相对湿度，以百分比 (%) 表示。
     * - [Sensor].TYPE_ROTATION_VECTOR -> 屏幕方向，通过提供设备旋转矢量的三个元素来检测设备的屏幕方向。
     * @param type Int 传感器类型 [Sensor].TYPE_*
     */
    fun start(type: Int) {
        manager.registerListener(
            callback,
            manager.getDefaultSensor(type),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    /**
     * 停止传感器数据监听器
     * @param type Int
     */
    fun stop(type: Int) {
        manager.unregisterListener(callback, manager.getDefaultSensor(type))
    }
}