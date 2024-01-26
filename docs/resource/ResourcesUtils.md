# ResourcesUtils

资源文件拓展工具

## ① 拓展系统API

> 核心思想为将系统API如`context.resources.getString(R.string.name)`
> ，拓展为`getResString(R.string.name)`，以此来简化代码。

```kotlin
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
```

## ② 通过Mipmap资源名称获取资源ID

> - @param name mipmap里的图片文件名 icon_logo
> - @return Int 图片ID

```kotlin
val id = getResMipmapId("icon_logo")
```

## ③ 通过Drawable资源名称获取资源ID

> - @param name drawable里的图片文件名 icon_logo
> - @return Int 图片ID

```kotlin
val id = getResDrawableId("icon_logo")
```

## ④ 将Drawable转换成Bitmap

```kotlin
val bitmap = drawable.toBitmap()
```