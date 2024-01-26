# ExternalUtils

外置存储器工具

## ① 获取外置存储器根目录路径

```kotlin
val path: String = getExternalRootPath()
```

## ② 获取APP缓存目录路径

```kotlin
val path: String = getCachePath()
```

## ③ 获取APP文件目录路径

```kotlin
val path: String = getFilesPath()
```

## ④ 复制文件

> - @param sourcePath String 源文件路径
> - @param destinationPath String 目标文件路径

```kotlin
copyFile(srcPath, destPath)
```

## ⑤ 复制文件夹

> - @param sourcePath String 源文件夹路径
> - @param destinationPath String 目标文件夹路径

```kotlin
copyFolder(srcPath, destPath)
```

## ⑥ 写入文件

> 如果文件不存在则创建，如果存在则追加
> - @param path String 文件路径
> - @param content String 写入内容

```kotlin
writeFile(path, content)
```

## ⑦ 复写文件

> 如果文件不存在则创建，如果存在则覆盖
> - @param path String 文件路径
> - @param content String 写入内容

```kotlin
overwriteFile(path, content)
```