package com.d10ng.app.utils

import android.content.Context
import com.d10ng.app.startup.ctx

/**
 * 轻量键值参数存储
 *
 * @author D10NG
 * @date on 2019-11-07 15:26
 */
abstract class Spf(val name: String) {

    private val mSpf by lazy { ctx.getSharedPreferences(name, Context.MODE_PRIVATE) }

    fun getSpf() = mSpf
}

/**
 * 轻量键值参数存储单例
 */
val SpfInstant = (object : Spf(ctx.packageName) {}).getSpf()