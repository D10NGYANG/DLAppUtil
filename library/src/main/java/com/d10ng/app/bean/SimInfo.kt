package com.d10ng.app.bean

import java.io.Serializable

/**
 * 手机卡信息
 * @Author: D10NG
 * @Time: 2021/5/8 3:24 下午
 */
data class SimInfo(
    /**
     * 存储手机卡的数据库ID
     */
    var id: String = "",

    /**
     * 插卡位置
     * -1 - 曾经插入的卡，现在不在手机里
     * 0 - 卡槽1
     * 1 - 卡槽2
     */
    var slotId: Int = -1,

    /**
     * 手机卡的ICCID
     * - 手机卡的唯一码
     * - 部分手机会隐藏掉一部分
     */
    var iccId: String = "",

    /**
     * 显示名称
     */
    var displayName: String = "",

    /**
     * 运营商名称
     */
    var carrierName: String = "",

    /**
     * 卡号码
     * - 以前的旧卡号码写在卡上的就能读取到，新的手机卡基本都是空的
     */
    var number: String = "",
): Serializable
