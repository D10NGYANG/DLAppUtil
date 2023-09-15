package com.d10ng.app.status

import android.app.Application
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 定位状态管理
 * @Author d10ng
 * @Date 2023/9/15 16:49
 */
object LocationStatusManager {

    private lateinit var application: Application
    private lateinit var manager: LocationManager

    private val statusFlow: MutableStateFlow<Location?> = MutableStateFlow(null)

    internal fun init(app: Application) {
        application = app
        manager = app.getSystemService(LocationManager::class.java)
    }

    private val locationListener = LocationListener { location ->
        statusFlow.value = location
    }

    /**
     * 检查定位是否可用
     * @return Boolean
     */
    fun enable() = LocationManagerCompat.isLocationEnabled(manager)

    /**
     * 启动定位请求
     */
    @RequiresPermission(
        anyOf = [
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        ]
    )
    fun start(
        providers: List<String> = manager.getProviders(true),
        minTimeMs: Long = 0,
        minDistanceM: Float = 0f
    ) {
        providers.map { provider ->
            if (manager.isProviderEnabled(provider)) {
                statusFlow.value = manager.getLastKnownLocation(provider)
                manager.requestLocationUpdates(
                    provider,
                    minTimeMs,
                    minDistanceM,
                    locationListener
                )
            }
        }
    }

    /**
     * 停止定位请求
     */
    fun stop() {
        manager.removeUpdates(locationListener)
    }

    /**
     * 获取状态Flow
     * @return StateFlow<Location?>
     */
    fun getStatusFlow() = statusFlow.asStateFlow()
}

/**
 * 检查定位是否可用
 * @return Boolean
 */
fun isLocationEnabled() = LocationStatusManager.enable()