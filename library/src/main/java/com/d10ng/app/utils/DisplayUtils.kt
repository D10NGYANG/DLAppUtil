package com.d10ng.app.utils

import android.util.TypedValue
import com.d10ng.app.startup.ctx

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 * @receiver Float
 * @return Float
 */
fun Float.dp2px(): Float {
    val scale = ctx.resources.displayMetrics.density
    return this * scale + 0.5f * (if (this >= 0) 1 else -1)
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 * @receiver Float
 * @return Float
 */
fun Float.px2dp(): Float {
    val scale = ctx.resources.displayMetrics.density
    return this / scale + 0.5f * (if (this >= 0) 1 else -1)
}

/**
 * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
 * @receiver Float
 * @return Float
 */
fun Float.sp2px() =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, ctx.resources.displayMetrics)

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
 * @receiver Float
 * @return Float
 */
fun Float.px2sp() =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this, ctx.resources.displayMetrics)
