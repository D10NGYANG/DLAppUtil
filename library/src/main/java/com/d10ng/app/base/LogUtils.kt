package com.d10ng.app.base

import android.util.Log

/**
 * 打印工具
 *
 * Author: D10NG
 * Time: 2020/12/4 2:52 下午
 */
abstract class LogUtils(private val tag: String) {

    /**
     * 日志控制开关
     */
    private var debug: Boolean = true

    /**
     * 初始化
     * @param b Boolean
     */
    fun init(b: Boolean) {
        debug = b
    }

    /**
     * 是否为打印模式
     * @return Boolean
     */
    fun isDebug() = debug


    fun i(t: String, m: String) {
        if (debug) Log.i(t, m)
    }


    fun i(m: String) {
        i(tag, m)
    }


    fun d(t: String, m: String) {
        if (debug) Log.d(t, m)
    }


    fun d(m: String) {
        d(tag, m)
    }


    fun e(t: String, m: String) {
        if (debug) Log.e(t, m)
    }


    fun e(m: String) {
        e(tag, m)
    }


    fun et(t: String, m: String, e: Throwable) {
        Log.e(t, m, e)
    }

    fun et(m: String, e: Throwable) {
        et(tag, m, e)
    }


    fun v(t: String, m: String) {
        if (debug) Log.v(t, m)
    }


    fun v(m: String) {
        v(tag, m)
    }
}