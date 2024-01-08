package com.d10ng.app.demo.utils

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.d10ng.app.managers.ActivityManager

/**
 * 导航控制器工具类
 * @Author d10ng
 * @Date 2023/11/15 16:35
 */
object NavControllerUtils {

    private val map = mutableMapOf<Activity, NavController>()

    fun register(activity: Activity, navController: NavController) {
        map[activity] = navController
    }

    fun getTop() = ActivityManager.top().value?.let { map[it] }
}

/**
 * 导航到上一个页面
 */
fun back() {
    NavControllerUtils.getTop()?.navigateUp()
}

/**
 * 导航到指定页面
 * @param route String
 * @param singleTop Boolean
 */
fun go(route: String, singleTop: Boolean = true) {
    NavControllerUtils.getTop()?.navigate(route) { launchSingleTop = singleTop }
}

/**
 * 导航到指定页面
 * @param route String
 * @param builder [@kotlin.ExtensionFunctionType] Function1<NavOptionsBuilder, Unit>
 */
fun go(route: String, builder: NavOptionsBuilder.() -> Unit) {
    NavControllerUtils.getTop()?.navigate(route, builder)
}