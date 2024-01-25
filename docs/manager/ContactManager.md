[TOC]

# ContactManager

联系人管理器

---

**参数说明**

`ContactManager.Data`

> 联系人数据

```kotlin
// 联系人姓名
val name: String
// 联系人电话号码
val number: String
```

---

## ① 选择联系人

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    // 选择联系人
    val contact: ContactManager.Data? = ContactManager.pick()
}
```

## ② 获取联系人列表

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 读取联系人 -->
    <uses-permission android:name="android.permission.READ_CONTACTS" />
</manifest>
```

动态申请权限

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    if (PermissionManager.request(Manifest.permission.READ_CONTACTS)) {
        // 读取联系人
        val contacts: List<ContactManager.Data> = ContactManager.list()
    }
}
```

## ③ 添加联系人

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 写入联系人 -->
    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
</manifest>
```

动态申请权限

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    if (PermissionManager.request(Manifest.permission.WRITE_CONTACTS)) {
        // 写入联系人
        val result = ContactManager.add(
            ContactManager.Data(
                name = "张三",
                number = "18888888888"
            )
        )
        if (result) {
            // 添加成功
        } else {
            // 添加失败
        }
    }
}
```

## ④ 添加或更新联系人

> 如果联系人已存在则更新，否则添加

在AndroidManifest.xml中添加权限

```xml

<manifest>
    <!-- 读取联系人 -->
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <!-- 写入联系人 -->
    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
</manifest>
```

动态申请权限

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    if (PermissionManager.request(
            arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            )
        )
    ) {
        // 添加或更新联系人
        val result = ContactManager.addOrUpdate(
            ContactManager.Data(
                name = "张三",
                number = "18888888888"
            )
        )
        if (result) {
            // 添加成功
        } else {
            // 添加失败
        }
    }
}
```
           