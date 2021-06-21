package com.d10ng.applib.status

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData

/**
 * 卫星状态监听器
 * @Author: D10NG
 * @Time: 2021/3/3 5:41 下午
 */
@RequiresApi(Build.VERSION_CODES.N)
class AGnssStatusCallback: GnssStatus.Callback() {

    val statusLive: MutableLiveData<GnssStatus?> = MutableLiveData(null)

    companion object {

        @Volatile
        private var INSTANCE: AGnssStatusCallback? = null

        @JvmStatic
        fun instant() : AGnssStatusCallback =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AGnssStatusCallback().also {
                    INSTANCE = it
                }
            }

        @JvmStatic
        fun destroy() {
            INSTANCE = null
        }
    }

    override fun onSatelliteStatusChanged(status: GnssStatus) {
        super.onSatelliteStatusChanged(status)
        statusLive.postValue(status)
    }
}

/**
 * 启动卫星状态请求
 * @receiver Context
 * @return MutableLiveData<GnssStatus?>?
 */
@RequiresApi(Build.VERSION_CODES.N)
fun Context.startRequestGnssStatusCallback(): MutableLiveData<GnssStatus?>? {
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return null
    }
    manager.registerGnssStatusCallback(AGnssStatusCallback.instant(), null)
    return AGnssStatusCallback.instant().statusLive
}

/**
 * 停止卫星状态请求
 * @receiver Context
 */
@RequiresApi(Build.VERSION_CODES.N)
fun Context.stopRequestGnssStatusCallback() {
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    manager.unregisterGnssStatusCallback(AGnssStatusCallback.instant())
}