package com.d10ng.dlapputil

import android.content.Context
import com.d10ng.app.system.SpfUtils

/** 默认表单 */
const val SP_CONFIG = "config_data"

/**
 * 获取本地存储器工具
 * @receiver Context
 * @param spName String
 * @return SharedPreferences
 */
fun Context.getSpf(spName: String = SP_CONFIG) = SpfUtils.instant(this, spName).getSpf()

/** 测试字符串 */
const val SPF_SAVE_NAME = "save_name"

fun Context.getSpfSaveName(): String =
    getSpf().getString(SPF_SAVE_NAME, null)?: ""

fun Context.setSpfSaveName(value: String) {
    getSpf().edit().putString(SPF_SAVE_NAME, value).apply()
}