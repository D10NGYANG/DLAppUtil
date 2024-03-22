package com.d10ng.app.status

import android.annotation.SuppressLint
import android.app.Application
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * NMEA原始定位信息管理器
 * @Author d10ng
 * @Date 2024/3/22 13:52
 */
object NmeaStatusManager {

    data class Status(
        val timestamp: Long,
        val message: String
    )

    private lateinit var manager: LocationManager
    private val scope = CoroutineScope(Dispatchers.IO)

    // 状态
    private val _statusFlow: MutableSharedFlow<Status> = MutableSharedFlow()
    val statusFlow = _statusFlow.asSharedFlow()

    internal fun init(app: Application) {
        manager = app.getSystemService(LocationManager::class.java)
    }

    private val callback = OnNmeaMessageListener { message, _ ->
        scope.launch { _statusFlow.emit(Status(System.currentTimeMillis(), message)) }
    }

    private val executor = Executors.newSingleThreadExecutor()

    /**
     * 启动NMEA原始定位信息请求
     * > 注意：需要在AndroidManifest.xml中添加定位权限，并在APP中动态申请权限，否则无法获取定位信息
     * > <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * @return StateFlow<Status?>
     */
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    fun start(): SharedFlow<Status> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            manager.addNmeaListener(executor, callback)
        } else {
            manager.addNmeaListener(callback)
        }
        return _statusFlow
    }

    /**
     * 停止NMEA原始定位信息请求
     */
    fun stop() {
        manager.removeNmeaListener(callback)
    }
}