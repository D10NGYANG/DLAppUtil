package com.d10ng.app.status

import android.annotation.SuppressLint
import android.app.Application
import android.location.GnssStatus
import android.location.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 卫星状态管理器
 * @Author d10ng
 * @Date 2023/9/1 16:18
 */
object GnssStatusManager {

    private lateinit var manager: LocationManager

    // 状态
    private val _statusFlow: MutableStateFlow<GnssStatus?> = MutableStateFlow(null)
    val statusFlow = _statusFlow.asStateFlow()

    internal fun init(app: Application) {
        manager = app.getSystemService(LocationManager::class.java)
    }

    private val callback = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            super.onSatelliteStatusChanged(status)
            _statusFlow.value = status
        }
    }

    /**
     * 启动卫星状态请求
     * > 注意：需要在AndroidManifest.xml中添加定位权限，并在APP中动态申请权限，否则无法获取定位信息
     * > <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * @return StateFlow<GnssStatus?>
     */
    @SuppressLint("MissingPermission")
    fun start(): StateFlow<GnssStatus?> {
        manager.registerGnssStatusCallback(callback, null)
        return statusFlow
    }

    /**
     * 停止卫星状态请求
     */
    fun stop() {
        manager.unregisterGnssStatusCallback(callback)
    }
}