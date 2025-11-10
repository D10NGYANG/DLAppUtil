# FilePickManager

文件选择管理器

## ① 选择文件

```kotlin
/**
 * 选择文件
 * @param array Array<String> MIME 类型数组，默认 ["application/octet-stream", "*/*"]
 * @return String? 选中文件复制到缓存目录的路径，取消返回 null
 */
suspend fun pick(array: Array<String> = arrayOf("application/octet-stream", "*/*")): String?
```

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    val path: String? = FilePickManager.pick(arrayOf("image/*"))
    if (path != null) {
        // 选中的文件已复制到 cacheDir，下次可直接读取使用
    } else {
        // 用户取消或文件解析失败
    }
}
```

## ② 指定 MIME 类型示例

```kotlin
// 选择 PDF
FilePickManager.pick(arrayOf("application/pdf"))

// 选择图片
FilePickManager.pick(arrayOf("image/*"))

// 选择视频或音频
FilePickManager.pick(arrayOf("video/*", "audio/*"))

// 任意类型（默认包含）
FilePickManager.pick(arrayOf("application/octet-stream", "*/*"))
```

## ③ 注意事项

- 使用 SAF（ActivityResultContracts.OpenDocument），通常不需要外置存储读取权限。
- 选中的文件会被复制到 `cacheDir`，若需长期保存请自行移动到持久目录。
- 已集成到 Activity 生命周期（通过 ActivityManager），无需在 Activity 手动注册。