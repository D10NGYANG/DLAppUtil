package com.d10ng.app.status

import android.app.Application
import android.location.GnssStatus
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 卫星状态管理器
 * @Author d10ng
 * @Date 2023/9/1 16:18
 */
object GnssStatusManager {

    private lateinit var application: Application
    private lateinit var manager: LocationManager

    // 状态Flow
    private val statusFlow: MutableStateFlow<GnssStatus?> = MutableStateFlow(null)

    internal fun init(app: Application) {
        application = app
        manager = app.getSystemService(LocationManager::class.java)
    }

    private val callback = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            super.onSatelliteStatusChanged(status)
            statusFlow.value = status
        }
    }

    /**
     * 启动卫星状态请求
     */
    @RequiresPermission(
        anyOf = [
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        ]
    )
    fun start(): StateFlow<GnssStatus?> {
        manager.registerGnssStatusCallback(callback, null)
        return getStatusFlow()
    }

    /**
     * 停止卫星状态请求
     */
    fun stop() {
        manager.unregisterGnssStatusCallback(callback)
    }

    /**
     * 获取状态Flow
     * @return StateFlow<GnssStatus?>
     */
    fun getStatusFlow() = statusFlow.asStateFlow()
}