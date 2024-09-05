package com.d10ng.app.status

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 定位状态管理
 * @Author d10ng
 * @Date 2023/9/15 16:49
 */
object LocationStatusManager {

    private lateinit var manager: LocationManager

    private val _statusFlow: MutableStateFlow<Location?> = MutableStateFlow(null)
    val statusFlow = _statusFlow.asStateFlow()

    internal fun init(app: Application) {
        manager = app.getSystemService(LocationManager::class.java)
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            _statusFlow.value = p0
        }

        // 修复在Android8中开启定位监听导致崩溃的问题
        @Suppress("DEPRECATED")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            println("LocationListener onStatusChanged $provider $status $extras")
        }
    }

    /**
     * 检查定位信息是否开启
     * @return Boolean
     */
    fun isEnable() = LocationManagerCompat.isLocationEnabled(manager)

    /**
     * 启动定位请求
     * > 注意：需要在AndroidManifest.xml中添加定位权限，并在APP中动态申请权限，否则无法获取定位信息
     * > <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * > <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
     * @param providers List<String> 定位提供者
     * @param minTimeMs Long 最小时间间隔，单位毫秒
     * @param minDistanceM Float 最小距离间隔，单位米
     */
    @SuppressLint("MissingPermission")
    fun start(
        providers: List<String> = manager.getProviders(true),
        minTimeMs: Long = 0,
        minDistanceM: Float = 0f
    ) {
        providers.map { provider ->
            if (manager.isProviderEnabled(provider)) {
                _statusFlow.value = manager.getLastKnownLocation(provider)
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
}

/**
 * 检查定位信息是否开启
 * @return Boolean
 */
fun isLocationEnabled() = LocationStatusManager.isEnable()