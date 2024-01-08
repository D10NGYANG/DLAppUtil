package com.d10ng.app.resource

import android.annotation.SuppressLint
import android.content.res.Resources.Theme
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.d10ng.app.startup.ctx

/**
 * 获取资源文件拓展工具
 *
 * @author D10NG
 * @date on 2020/8/5 3:19 PM
 */
private val resources by lazy { ctx.resources }

fun getResText(id: Int) = resources.getText(id)

fun getResFont(id: Int) = resources.getFont(id)
fun getResQuantityText(id: Int, quantity: Int) = resources.getQuantityText(id, quantity)
fun getResString(id: Int) = resources.getString(id)
fun getResString(id: Int, vararg formatArgs: Any) = resources.getString(id, *formatArgs)
fun getResQuantityString(id: Int, quantity: Int, vararg formatArgs: Any) =
    resources.getQuantityString(id, quantity, *formatArgs)

fun getResQuantityString(id: Int, quantity: Int) = resources.getQuantityString(id, quantity)
fun getResText(id: Int, def: CharSequence) = resources.getText(id, def)
fun getResTextArray(id: Int) = resources.getTextArray(id)
fun getResStringArray(id: Int) = resources.getStringArray(id)
fun getResIntArray(id: Int) = resources.getIntArray(id)
fun resObtainTypedArray(id: Int) = resources.obtainTypedArray(id)
fun getResDimension(id: Int) = resources.getDimension(id)
fun getResDimensionPixelOffset(id: Int) = resources.getDimensionPixelOffset(id)
fun getResDimensionPixelSize(id: Int) = resources.getDimensionPixelSize(id)
fun getResFraction(id: Int, base: Int, pbase: Int) = resources.getFraction(id, base, pbase)

@SuppressLint("UseCompatLoadingForDrawables")
fun getResDrawable(id: Int, theme: Theme? = null) = resources.getDrawable(id, theme)
fun getResDrawableForDensity(id: Int, density: Int, theme: Theme? = null) =
    resources.getDrawableForDensity(id, density, theme)

fun getResColor(id: Int) = ContextCompat.getColor(ctx, id)
fun getResColorStateList(id: Int) = ContextCompat.getColorStateList(ctx, id)
fun getResBoolean(id: Int) = resources.getBoolean(id)
fun getResInteger(id: Int) = resources.getInteger(id)

@RequiresApi(Build.VERSION_CODES.Q)
fun getResFloat(id: Int) = resources.getFloat(id)
fun getResLayout(id: Int) = resources.getLayout(id)
fun getResAnimation(id: Int) = resources.getAnimation(id)
fun getResXml(id: Int) = resources.getXml(id)


/**
 * 获取图片ID
 * @param name mipmap里的图片文件名 icon_logo
 * @return [Int] 图片ID
 */
@SuppressLint("DiscouragedApi")
fun getResMipmapId(name: String) = resources.getIdentifier(name, "mipmap", ctx.packageName)

/**
 * 获取图片ID
 * @param name drawable里的图片文件名 icon_logo
 * @return [Int] 图片ID
 */
@SuppressLint("DiscouragedApi")
fun getResDrawableId(name: String) = resources.getIdentifier(name, "drawable", ctx.packageName)

/**
 * 将 Drawable 转换成 Bitmap
 * @receiver Drawable
 * @return Bitmap
 */
fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}