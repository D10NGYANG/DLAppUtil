package com.d10ng.app.status

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import com.d10ng.app.startup.ctx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 传感器状态管理器
 * @Author d10ng
 * @Date 2023/9/1 16:29
 */
class SensorStatus(
    type: Int,
) {

    class Event(
        val accuracy: Int,
        val firstEventAfterDiscontinuity: Boolean,
        val sensor: Sensor,
        val timestamp: Long,
        val values: FloatArray
    )

    private fun SensorEvent.toEvent() = Event(
        accuracy = this.accuracy,
        firstEventAfterDiscontinuity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) this.firstEventAfterDiscontinuity else false,
        sensor = this.sensor,
        timestamp = this.timestamp,
        values = this.values
    )

    private val manager = ctx.getSystemService(SensorManager::class.java)
    private var sensor: Sensor? = manager.getDefaultSensor(type)

    // 传感器数据
    private val _eventFlow = MutableStateFlow<Event?>(null)
    val eventFlow = _eventFlow.asStateFlow()

    // 传感器精度
    private val _accuracyFlow = MutableStateFlow<Pair<Sensor, Int>?>(null)
    val accuracyFlow = _accuracyFlow.asStateFlow()

    private val callback = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            p0 ?: return
            _eventFlow.value = p0.toEvent()
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            p0 ?: return
            _accuracyFlow.value = Pair(p0, p1)
        }
    }

    /**
     * 开始传感器数据监听器
     */
    fun start() {
        sensor ?: return
        manager.registerListener(callback, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    /**
     * 停止传感器数据监听器
     */
    fun stop() {
        sensor ?: return
        manager.unregisterListener(callback, sensor)
    }
}