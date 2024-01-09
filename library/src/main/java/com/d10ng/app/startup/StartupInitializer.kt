package com.d10ng.app.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.d10ng.app.managers.ActivityManager
import com.d10ng.app.managers.ContactManager
import com.d10ng.app.managers.NotificationController
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.managers.PhotoManager
import com.d10ng.app.status.BatteryStatusManager
import com.d10ng.app.status.GnssStatusManager
import com.d10ng.app.status.LocationStatusManager
import com.d10ng.app.status.NetworkStatusManager
import com.d10ng.app.status.SensorStatusManager

/**
 * 启动初始化
 * @Author d10ng
 * @Date 2023/9/1 13:51
 */
internal class StartupInitializer : Initializer<Unit> {

    companion object {
        lateinit var application: Application
    }

    override fun create(context: Context) {
        application = context as Application
        PermissionManager.init(context)
        ActivityManager.init(context)
        LocationStatusManager.init(context)
        GnssStatusManager.init(context)
        SensorStatusManager.init(context)
        NetworkStatusManager.init(context)
        NotificationController.init(context)
        ContactManager.init(context)
        PhotoManager.init(context)
        BatteryStatusManager.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}

internal val ctx by lazy { StartupInitializer.application }