package com.d10ng.app.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.d10ng.app.base.ActivityManager
import com.d10ng.app.base.PermissionManager
import com.d10ng.app.status.GnssStatusManager
import com.d10ng.app.status.NetworkStatusManager
import com.d10ng.app.status.SensorStatusManager

/**
 * 启动初始化
 * @Author d10ng
 * @Date 2023/9/1 13:51
 */
class StartupInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        context as Application
        PermissionManager.init(context)
        ActivityManager.init(context)
        GnssStatusManager.init(context)
        SensorStatusManager.init(context)
        NetworkStatusManager.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}