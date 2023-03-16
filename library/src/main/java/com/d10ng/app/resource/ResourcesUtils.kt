package com.d10ng.app.resource

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * 获取资源文件拓展工具
 *
 * @author D10NG
 * @date on 2020/8/5 3:19 PM
 */

/**
 * 获取字符串
 * @param id R.string.sure
 * @return [String] 字符串
 */
fun Context.makeStr(id: Int): String {
    return this.resources.getString(id)
}
fun Fragment.makeStr(id: Int): String {
    return this.resources.getString(id)
}

/**
 * 获取数值
 * @receiver Context
 * @param id Int R.integer.time
 * @return [Int] 整型
 */
fun Context.makeDimension(id: Int): Float {
    return this.resources.getDimension(id)
}
fun Fragment.makeDimension(id: Int): Float {
    return this.resources.getDimension(id)
}

/**
 * 获取整型
 * @receiver Context
 * @param id Int R.integer.time
 * @return [Int] 整型
 */
fun Context.makeInt(id: Int): Int {
    return this.resources.getInteger(id)
}
fun Fragment.makeInt(id: Int): Int {
    return this.resources.getInteger(id)
}


/**
 * 获取字符串数组
 * @param id R.array.permissions
 * @return [String] 字符串数组
 */
fun Context.makeStrArray(id: Int): Array<String> {
    return this.resources.getStringArray(id)
}
fun Fragment.makeStrArray(id: Int): Array<String> {
    return this.resources.getStringArray(id)
}

/**
 * 获取颜色资源
 * @receiver Context
 * @param id Int
 * @return Int
 */
fun Context.makeColor(id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Fragment.makeColor(id: Int): Int {
    return ContextCompat.getColor(this.requireContext(), id)
}

/**
 * 获取图片ID
 * @param name mipmap里的图片文件名 icon_logo
 * @return [Int] 图片ID
 */
fun Context.makeMipmapId(name: String): Int {
    return this.resources.getIdentifier(name, "mipmap", this.packageName)
}
fun Fragment.makeMipmapId(name: String): Int {
    return this.resources.getIdentifier(name, "mipmap", this.context?.packageName)
}

/**
 * 获取图片ID
 * @param name drawable里的图片文件名 icon_logo
 * @return [Int] 图片ID
 */
fun Context.makeDrawableId(name: String): Int {
    return this.resources.getIdentifier(name, "drawable", this.packageName)
}
fun Fragment.makeDrawableId(name: String): Int {
    return this.resources.getIdentifier(name, "drawable", this.context?.packageName)
}

/**
 * 获取图片ID的图像
 * @receiver Context
 * @param id Int
 * @return Drawable
 */
@SuppressLint("UseCompatLoadingForDrawables")
fun Context.makeDrawable(id: Int): Drawable {
    return this.resources.getDrawable(id, null)
}

@SuppressLint("UseCompatLoadingForDrawables")
fun Fragment.makeDrawable(id: Int): Drawable {
    return this.resources.getDrawable(id, null)
}

/**
 * 将Drawable资源转换成bitmap
 * @receiver Context
 * @param id Int
 * @return Bitmap?
 */
fun Context.makeBitmapFromDrawable(id: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, id)?: return null
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun Fragment.makeBitmapFromDrawable(id: Int): Bitmap? {
    return this.requireContext().makeBitmapFromDrawable(id)
}