package com.d10ng.app.bean

import java.io.Serializable

/**
 * 短信信息
 * @Author: D10NG
 * @Time: 2021/4/29 5:36 PM
 */
data class SmsInfo(
    /**
     * 短信ID
     * - 会重复的，因为当用户删除短信数据库里的短信后，下一条短信就会重复用这个ID
     */
    var id: String = "",

    /**
     * 接收的手机卡ID
     */
    var subId: Long = -1,

    /**
     * 来源号码
     */
    var phone: String = "",

    /**
     * 内容
     */
    var content: String = "",

    /**
     * 消息到达的时间戳
     */
    var time: Long = 0
): Serializable
