package com.d10ng.applib.bean

import java.io.Serializable

/**
 * 手机卡信息
 * @Author: D10NG
 * @Time: 2021/5/8 3:24 下午
 */
data class SimInfo(
    var id: String = "",
    var simId: String = "",
    var iccId: String = "",
    var displayName: String = "",
    var carrierName: String = "",
    var number: String = "",
): Serializable
