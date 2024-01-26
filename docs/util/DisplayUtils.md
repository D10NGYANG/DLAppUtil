# DisplayUtils

显示工具

## ① 根据手机的分辨率从 dp 的单位 转成为 px(像素)

```kotlin
/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 * @receiver Float
 * @return Float
 */
fun Float.dp2px(): Float
```

```kotlin
3f.dp2px()
```

## ② 根据手机的分辨率从 px(像素) 的单位 转成为 dp

```kotlin
/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 * @receiver Float
 * @return Float
 */
fun Float.px2dp(): Float
```

```kotlin
3f.px2dp()
```

## ③ 根据手机的分辨率从 sp 的单位 转成为 px(像素)

```kotlin
/**
 * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
 * @receiver Float
 * @return Float
 */
fun Float.sp2px(): Float
```

```kotlin
3f.sp2px()
```

## ④ 根据手机的分辨率从 px(像素) 的单位 转成为 sp

```kotlin
/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
 * @receiver Float
 * @return Float
 */
fun Float.px2sp(): Float
```

```kotlin
3f.px2sp()
```