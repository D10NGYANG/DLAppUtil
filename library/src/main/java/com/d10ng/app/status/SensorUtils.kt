package com.d10ng.app.status

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData

/**
 * 传感器数据监听器
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
 * @Author: D10NG
 * @Time: 2021/3/3 8:33 下午
 */
class ASensorEventListener(val type: Int): SensorEventListener {

    val valueLive: MutableLiveData<Float?> = MutableLiveData(null)

    companion object {

        @Volatile
        private var INSTANCE_MAP: MutableMap<Int, ASensorEventListener> = mutableMapOf()

        @JvmStatic
        fun instant(type: Int) : ASensorEventListener =
            INSTANCE_MAP[type] ?: synchronized(this) {
                INSTANCE_MAP[type] ?: ASensorEventListener(type).also {
                    INSTANCE_MAP[type] = it
                }
            }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        valueLive.postValue(event?.values?.get(0))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}

/**
 * 开始监听传感器数据
 * @receiver Context
 * @param type Int
 * @return MutableLiveData<Float?>
 */
fun Context.startRequestSensorValue(type: Int): MutableLiveData<Float?> {
    val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = mSensorManager.getDefaultSensor(type)
    mSensorManager.registerListener(ASensorEventListener.instant(type), sensor, SensorManager.SENSOR_DELAY_NORMAL)
    return ASensorEventListener.instant(type).valueLive
}

/**
 * 停止监听传感器数据
 * @receiver Context
 * @param type Int
 */
fun Context.stopRequestSensorValue(type: Int) {
    val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    mSensorManager.unregisterListener(ASensorEventListener.instant(type))
}