package com.d10ng.app.system

import android.content.Context

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 * @receiver Float
 * @param context Context
 * @return Float
 */
fun Float.dp2px(context: Context): Float {
    val scale = context.resources.displayMetrics.density
    return this * scale + 0.5f * (if (this >= 0) 1 else -1)
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 * @receiver Float
 * @param context Context
 * @return Float
 */
fun Float.px2dp(context: Context): Float {
    val scale = context.resources.displayMetrics.density
    return this / scale + 0.5f * (if (this >= 0) 1 else -1)
}

/**
 * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
 * @receiver Float
 * @param context Context
 * @return Float
 */
fun Float.sp2px(context: Context): Float {
    val scale = context.resources.displayMetrics.scaledDensity
    return this * scale + 0.5f
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
 * @receiver Float
 * @param context Context
 * @return Float
 */
fun Float.px2sp(context: Context): Float {
    val scale = context.resources.displayMetrics.scaledDensity
    return this / scale + 0.5f
}
