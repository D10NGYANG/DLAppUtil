package com.d10ng.app.permission

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

/**
 * 权限初始化
 * @Author d10ng
 * @Date 2023/9/1 11:47
 */
class PermissionInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        PermissionManager.init(context as Application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}