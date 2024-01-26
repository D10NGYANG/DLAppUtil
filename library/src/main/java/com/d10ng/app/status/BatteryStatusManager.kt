package com.d10ng.app.status

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * 电池状态管理器
 * @Author d10ng
 * @Date 2024/1/6 14:28
 */
object BatteryStatusManager {

    // 充电类型
    enum class ChargeType(val value: Int, val text: String) {
        // AC充电
        AC(BatteryManager.BATTERY_PLUGGED_AC, "交流电"),

        // USB充电
        USB(BatteryManager.BATTERY_PLUGGED_USB, "USB充电"),

        // 无线充电
        WIRELESS(BatteryManager.BATTERY_PLUGGED_WIRELESS, "无线充电"),

        ;

        companion object {
            fun parse(value: Int) = entries.firstOrNull { it.value == value }
        }
    }

    // 健康状态
    enum class HealthType(val value: Int, val text: String) {
        // 未知
        UNKNOWN(BatteryManager.BATTERY_HEALTH_UNKNOWN, "未知"),

        // 良好
        GOOD(BatteryManager.BATTERY_HEALTH_GOOD, "良好"),

        // 过热
        OVERHEAT(BatteryManager.BATTERY_HEALTH_OVERHEAT, "过热"),

        // 欠压
        DEAD(BatteryManager.BATTERY_HEALTH_DEAD, "欠压"),

        // 电量过低
        OVER_VOLTAGE(BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE, "电量过低"),

        // 电池不正常
        UNSPECIFIED_FAILURE(BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE, "电池不正常"),

        // 冷却
        COLD(BatteryManager.BATTERY_HEALTH_COLD, "冷却"),

        // 未知错误
        FAILURE(BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE, "未知错误"),

        ;

        companion object {
            fun parse(value: Int) = entries.firstOrNull { it.value == value } ?: UNKNOWN
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    // 电量状态
    private val _batteryFlow = MutableStateFlow<Float?>(null)
    val batteryFlow = _batteryFlow.asStateFlow()

    // 充电状态
    private val _chargeTypeFlow = MutableStateFlow<ChargeType?>(null)
    val chargeTypeFlow = _chargeTypeFlow.asStateFlow()

    // 是否正在充电
    val isChargingFlow = chargeTypeFlow.map { it != null }
        .stateIn(scope, SharingStarted.Eagerly, false)

    // 健康度
    private val _healthFlow = MutableStateFlow(HealthType.UNKNOWN)
    val healthFlow = _healthFlow.asStateFlow()

    // 温度
    private val _temperatureFlow = MutableStateFlow(0.0)
    val temperatureFlow = _temperatureFlow.asStateFlow()

    // 电压
    private val _voltageFlow = MutableStateFlow(0.0)
    val voltageFlow = _voltageFlow.asStateFlow()

    // 技术
    private val _technologyFlow = MutableStateFlow("")
    val technologyFlow = _technologyFlow.asStateFlow()

    // 电量变化广播接收器
    private val batteryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateState(intent)
        }
    }

    /**
     * 初始化
     */
    internal fun init(app: Application) {
        val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        // 初始化电量状态
        updateState(app.registerReceiver(null, batteryIntentFilter))
        app.registerReceiver(batteryChangeReceiver, batteryIntentFilter)
    }

    /**
     * 更新电量状态
     * @param intent Intent?
     */
    private fun updateState(intent: Intent?) {
        intent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
            _batteryFlow.value = level.toFloat() / scale.toFloat() * 100
            val batteryPlugged = it.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            _chargeTypeFlow.value = ChargeType.parse(batteryPlugged)
            val health = it.getIntExtra(BatteryManager.EXTRA_HEALTH, HealthType.UNKNOWN.value)
            _healthFlow.value = HealthType.parse(health)
            val temperature = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            _temperatureFlow.value = temperature / 10.0
            val voltage = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            _voltageFlow.value = voltage / 1000.0
            val technology = it.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: ""
            _technologyFlow.value = technology
        }
    }

    /**
     * 获取当前电量
     * @return Float?
     */
    fun battery(): Float? = _batteryFlow.value
}

/**
 * 获取当前系统电量
 * @return Float?
 */
fun systemBattery() = BatteryStatusManager.battery()