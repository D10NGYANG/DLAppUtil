package com.d10ng.app.status

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.d10ng.app.startup.ctx
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 电池状态管理器
 * @Author d10ng
 * @Date 2024/1/6 14:28
 */
object BatteryStatusManager {

    // 电量状态
    private val _stateFlow = MutableStateFlow<Float?>(null)
    val stateFlow = _stateFlow.asStateFlow()

    // 电量变化广播接收器
    private val batteryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateState(intent)
        }
    }

    /**
     * 初始化
     */
    fun init() {
        val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        // 初始化电量状态
        updateState(ctx.registerReceiver(null, batteryIntentFilter))
        ctx.registerReceiver(batteryChangeReceiver, batteryIntentFilter)
    }

    /**
     * 更新电量状态
     * @param intent Intent?
     */
    private fun updateState(intent: Intent?) {
        intent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
            _stateFlow.value = level.toFloat() / scale.toFloat() * 100
        }
    }

    /**
     * 获取当前电量
     * @return Float?
     */
    fun status(): Float? = _stateFlow.value
}

/**
 * 获取当前系统电量
 * @return Float?
 */
fun systemBattery() = BatteryStatusManager.status()