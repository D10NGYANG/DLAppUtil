package com.d10ng.app.demo.pages

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.app
import com.d10ng.app.demo.utils.back
import com.d10ng.app.status.SensorStatus
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample

/**
 * 传感器状态管理
 * @Author d10ng
 * @Date 2024/1/9 14:38
 */
@RootNavGraph
@Destination(style = PageTransitions::class)
@Composable
fun SensorStatusManagerScreen() {
    SensorStatusManagerScreenView()
}

@OptIn(FlowPreview::class)
@Composable
private fun SensorStatusManagerScreenView() {

    LaunchedEffect(Unit) {
        SensorStatusManagerScreenViewData.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            SensorStatusManagerScreenViewData.stop()
        }
    }

    val data by SensorStatusManagerScreenViewData.dataFlow.collectAsState(initial = emptyMap())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "传感器状态", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            data.forEach { (sensor, sensorEvent) ->
                CellGroup(title = sensor.name, inset = true) {
                    Cell(
                        title = "类型", value = when (sensor.type) {
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
                        }
                    )
                    Cell(title = "供应商", value = sensor.vendor)
                    Cell(title = "版本", value = sensor.version.toString())
                    Cell(title = "最大范围", value = sensor.maximumRange.toString())
                    Cell(title = "分辨率", value = sensor.resolution.toString())
                    Cell(title = "功耗", value = sensor.power.toString())
                    // 传感器状态
                    Cell(title = "状态时间", value = sensorEvent?.timestamp?.toString() ?: "")
                    Cell(
                        title = "精度", value = when (sensorEvent?.accuracy) {
                            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "高精度"
                            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "中等精度"
                            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "低精度"
                            SensorManager.SENSOR_STATUS_NO_CONTACT -> "无接触"
                            SensorManager.SENSOR_STATUS_UNRELIABLE -> "不可靠"
                            else -> "未知"
                        }
                    )
                    Cell(title = "数据", value = sensorEvent?.values?.joinToString(",") ?: "")
                }
            }
        }
    }
}

private object SensorStatusManagerScreenViewData {
    private val sensorManager = app.getSystemService(SensorManager::class.java)
    private val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL).sortedBy { it.type }
    private val statusMap = mutableMapOf<Sensor, SensorStatus>().apply {
        sensors.forEach { sensor ->
            this[sensor] = SensorStatus(sensor.type)
        }
    }

    @OptIn(FlowPreview::class)
    val dataFlow =
        combine(statusMap.map { p -> p.value.eventFlow.map { k -> p.key to k } }) { pairs ->
            pairs.associate { it }
        }.sample(500)

    fun start() {
        statusMap.values.forEach { it.start() }
    }

    fun stop() {
        statusMap.values.forEach { it.stop() }
    }
}

@Preview
@Composable
private fun SensorStatusManagerScreenPreview() {
    SensorStatusManagerScreenView()
}