package com.d10ng.applib.system

import android.content.Context
import android.content.SharedPreferences

/**
 * 轻量键值参数存储
 *
 * @author D10NG
 * @date on 2019-11-07 15:26
 */
class SpfUtils constructor(context: Context, spName: String) {

    private val mSpf = context.getSharedPreferences(spName, Context.MODE_PRIVATE)

    companion object {

        @Volatile
        private var INSTANCE: SpfUtils? = null

        @JvmStatic
        fun instant(context: Context, spName: String) : SpfUtils =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SpfUtils(context, spName).also {
                    INSTANCE = it
                }
            }
    }

    fun getSpf() : SharedPreferences {
        return mSpf
    }
}
