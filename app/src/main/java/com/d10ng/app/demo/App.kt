package com.d10ng.app.demo

import android.app.Application

/**
 * 全局应用
 * @Author d10ng
 * @Date 2024/1/9 15:50
 */
val app by lazy { App.instance }

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}