# PhotoManager

图片管理器

## ① 选择图片

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    PhotoManager.pick()?.let { path ->
        // path - 图片路径
    }
}
```

在Jetpack compose中显示图片

```kotlin
val path by remember { mutableStateOf("") }
val previewBitmap = remember(path) {
    val imgFile = File(path)
    if (imgFile.exists()) BitmapFactory.decodeFile(imgFile.absolutePath).asImageBitmap()
    else null
}
if (previewBitmap != null) {
    Image(
        bitmap = previewBitmap,
        contentDescription = "Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
```

## ② 创建缩略图并保存

```kotlin
/**
 * 创建缩略图并保存
 * @param path String 图片路径
 * @param reqWidth Int 缩略图宽度
 * @param reqHeight Int 缩略图高度
 * @param outputPath String 输出路径
 */
fun createAndSaveThumbnail(path: String, reqWidth: Int, reqHeight: Int, outputPath: String) 
```

```kotlin
PhotoManager.createAndSaveThumbnail(
    path = path,
    reqWidth = 100,
    reqHeight = 100,
    outputPath = "${getFilesPath()}/thumbnail_${curTime}.jpg"
)
```

## ③ 保存Bitmap到指定路径

```kotlin
/**
 * 保存Bitmap到指定路径
 * @param bitmap Bitmap 图片
 * @param outputPath String 输出路径
 * @param quality Int 图片质量，0-100，默认50
 */
fun saveBitmap(bitmap: Bitmap, outputPath: String, quality: Int = 50)
```

```kotlin
PhotoManager.saveBitmap(
    bitmap = BitmapFactory.decodeFile(path),
    outputPath = "${getFilesPath()}/bitmap_${curTime}.jpg"
)
```

## ④ 创建缩略图Bitmap

```kotlin
/**
 * 创建缩略图
 * @param path String 图片路径
 * @param reqWidth Int 缩略图宽度
 * @param reqHeight Int 缩略图高度
 * @return Bitmap? 缩略图
 */
fun createThumbnail(path: String, reqWidth: Int, reqHeight: Int): Bitmap?
```

```kotlin
val thumbnailBitmap = PhotoManager.createThumbnail(
    path = path,
    reqWidth = 100,
    reqHeight = 100
)
```

## ⑤ 保存图片到相册

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 写入外置存储 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
</manifest>
```

动态申请权限

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    if (PermissionManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        PhotoManager.saveToAlbum(
            // @param file File 图片文件
            file = File(path),
            // @param albumName String 相册名称
            albumName = "AlbumName"
        )
    }
}
```