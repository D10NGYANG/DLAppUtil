package com.d10ng.app.managers

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Activity管理器
 * @Author d10ng
 * @Date 2023/9/1 13:53
 */
object ActivityManager {

    private lateinit var application: Application

    /** 页面列表 */
    private val activityListFlow: MutableStateFlow<List<Activity>> = MutableStateFlow(listOf())
    val listFlow = activityListFlow.asStateFlow()

    /** 栈顶页面 */
    private val topActivityFlow: MutableStateFlow<Activity?> = MutableStateFlow(null)
    val topFlow = topActivityFlow.asStateFlow()

    internal fun init(app: Application) {
        application = app.apply {
            registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                    val list = activityListFlow.value.toMutableList()
                    if (list.contains(p0).not()) list += p0
                    activityListFlow.value = list
                    topActivityFlow.value = p0
                }

                override fun onActivityStarted(p0: Activity) {}

                override fun onActivityResumed(p0: Activity) {
                    topActivityFlow.value = p0
                }

                override fun onActivityPaused(p0: Activity) {}

                override fun onActivityStopped(p0: Activity) {}

                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

                override fun onActivityDestroyed(p0: Activity) {
                    val list = activityListFlow.value.toMutableList()
                    if (list.contains(p0)) list -= p0
                    activityListFlow.value = list
                    if (topActivityFlow.value == p0) topActivityFlow.value = null
                }
            })
        }
    }

    /**
     * 获取Activity实例
     * @return T?
     */
    inline fun <reified T : Activity> get(): T? {
        return listFlow.value.find { it::class == T::class } as? T
    }

    /**
     * 获取当前Activity实例
     * @return Activity?
     */
    fun top() = topFlow.value

    /**
     * 注销最顶部的Activity
     */
    fun finishTop() {
        top()?.finish()
    }
}