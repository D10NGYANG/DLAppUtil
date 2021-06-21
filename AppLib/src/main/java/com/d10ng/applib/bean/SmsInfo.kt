package com.d10ng.applib.bean

import java.io.Serializable

/**
 * 短信信息
 * @Author: D10NG
 * @Time: 2021/4/29 5:36 PM
 */
data class SmsInfo(
    var id: String = "",
    var phone: String = "",
    var content: String = "",
    var time: Long = 0
): Serializable
