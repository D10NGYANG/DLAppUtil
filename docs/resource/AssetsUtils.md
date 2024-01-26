# AssetsUtils

Assets资源读取工具

## ① 读取文本文件

```kotlin
/**
 * 将 Assets 中的文本文件 读取成字符串
 * @param fileName String 文件名 "xxx.txt"
 * @return String
 */
fun readAssetsFile(fileName: String): String
```

## ② 读取JSON文件

> JSONObject

```kotlin
/**
 * 将 Assets 中的json文件 读取成 JSONObject
 * @param fileName String 文件名 "xxx.json"
 * @return JSONObject
 */
fun getAssetsJSONObject(fileName: String): JSONObject
```

> JSONArray

```kotlin
/**
 * 将 Assets 中的json文件 读取成 JSONArray
 * @param fileName String 文件名 "xxx.json"
 * @return JSONArray
 */
fun getAssetsJSONArray(fileName: String): JSONArray
```

## ③ 读取图片文件

```kotlin
/**
 * 将 Assets 中的图片文件 读取成 Bitmap
 * @param fileName String 文件名 "xxx.png"
 * @return Bitmap?
 */
fun getAssetsBitmap(fileName: String): Bitmap?
```

## ④ 读取bin文件

```kotlin
/**
 * 将 Assets 中的二进制bin文件 读取成 ByteArray
 * @param fileName [String] 文件名 "xxx.bin"
 * @return [ByteArray]
 */
fun getAssetsBin(fileName: String): ByteArray
```
