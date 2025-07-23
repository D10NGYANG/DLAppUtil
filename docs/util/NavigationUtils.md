# NavigationUtils

常用导航工具

## ① 回到系统桌面

```kotlin
goToSystemHome()
```

## ② 打开系统Wi-Fi设置页面

```kotlin
goToSystemWifiSetting()
```

## ③ 打开位置信息设置页面

```kotlin
goToSystemLocationSetting()
```

## ④ 打开浏览器访问网站

> - @param url 网站地址

```kotlin
goToBrowser(url = "https://www.baidu.com")
```

## ⑤ 打开拨号界面

> - @param phone 电话号码

```kotlin
goToSystemCall(phone = "10010")
```

## ⑥ 前往系统短信页面，填充联系人和消息内容

> - @param phone String 电话号码
> - @param content String 消息内容

```kotlin
goToSystemSms(phone = "10010", content = "101")
```

## ⑦ 打开App设置页面

```kotlin
goToAppSetting()
```

## ⑧ 打开App通知设置页面

```kotlin
goToAppNotificationSetting()
```

## ⑨ 打开App通知渠道设置页面

> - @param channelId String 通知渠道ID

```kotlin
goToAppNotificationChannelSetting(channelId = "channel_id")
```

## ⑩ 获取跳转页面Intent

> - @receiver Activity
> - @param clz Class<*> 跳转页面
> - @return Intent

```kotlin
val intent = activity.getClearTopIntent(clz = MainActivity::class.java)
```

## ⑪ 跳转下一个页面，如果栈中有相同的ACT会只保留最新一个到前台

> - @receiver Activity
> - @param clz Class<*> 跳转页面

```kotlin
activity.goTo(clz = MainActivity::class.java)
```

## ⑫ 清空整个Task并跳转页面

> - @receiver Activity
> - @param clz Class<*> 跳转页面

```kotlin
activity.clearTaskGoTo(clz = MainActivity::class.java)
```

## ⑬ 关闭当前页面并跳转下一个页面

> - @receiver Activity
> - @param clz Class<*> 跳转页面

```kotlin
activity.finishGoTo(clz = MainActivity::class.java)
```

## ⑭ 跳转下一个页面，并带返回结果

> - @receiver Activity
> - @param clz Class<*> 跳转页面
> - @param requestCode Int 请求码

```kotlin
activity.goToForResult(clz = MainActivity::class.java, requestCode = 100)
```

## ⑮ 启动百度地图，显示地标位置

```kotlin
/**
 * 启动百度地图，显示地标位置
 * http://api.map.baidu.com/marker?location=40.047669,116.313082&title=我的位置&content=百度奎科大厦&output=html&src=webapp.baidu.openAPIdemo
 * @param lat Double 纬度
 * @param lng Double 经度
 * @param title String 标题
 * @param content String 内容
 * @param coordinate String bd09ll（百度经纬度坐标）bd09mc（百度墨卡托坐标）gcj02（经国测局加密的坐标)wgs84（gps获取的原始坐标）
 */
fun startBaiDuMapMaker(
    lat: Double,
    lng: Double,
    title: String = "位置",
    content: String = "",
    coordinate: String = "gcj02"
)
```kotlin
startBaiDuMapMaker(
    lat = 40.047669,
    lng = 116.313082,
    title = "我的位置",
    content = "百度奎科大厦",
    coordinate = "gcj02"
)
```

## ⑯ 启动百度地图，规划驾车路径

```kotlin
/**
 * 启动百度地图，规划驾车路径
 * http://api.map.baidu.com/direction?origin=latlng:34.264642646862,108.95108518068|name:起点&destination=latlng:23.157471,113.042738|name:终点&mode=driving&region=1&output=html&src=webapp.baidu.openAPIdemo
 * @param startLat Double 起点纬度
 * @param startLng Double 起点经度
 * @param startName String 起点名称
 * @param endLat Double 终点纬度
 * @param endLng Double 终点经度
 * @param endName String 终点名称
 * @param coordinate String bd09ll（百度经纬度坐标）bd09mc（百度墨卡托坐标）gcj02（经国测局加密的坐标)wgs84（gps获取的原始坐标）
 */
fun startBaiduMapNavigation(
    startLat: Double,
    startLng: Double,
    startName: String = "起点",
    endLat: Double,
    endLng: Double,
    endName: String = "终点",
    coordinate: String = "gcj02"
)
```

```kotlin
startBaiduMapNavigation(
    startLat = 34.264642646862,
    startLng = 108.95108518068,
    startName = "起点",
    endLat = 23.157471,
    endLng = 113.042738,
    endName = "终点",
    coordinate = "gcj02"
)
```

## ⑰ 启动高德地图，显示地标位置

```kotlin
/**
 * 启动高德地图，显示地标位置
 * https://uri.amap.com/marker?position=121.287689,31.234527&name=park&src=mypage&coordinate=gaode&callnative=0
 * @param lat Double 纬度
 * @param lng Double 经度
 * @param name String 名称
 * @param coordinate String 坐标系参数coordinate=gaode,表示高德坐标（gcj02坐标），coordinate=wgs84,表示wgs84坐标（GPS原始坐标）
 */
fun startGaoDeMapMaker(
    lat: Double,
    lng: Double,
    name: String = "位置",
    coordinate: String = "gcj02"
)
```

```kotlin
startGaoDeMapMaker(
    lat = 34.264642646862,
    lng = 108.95108518068,
    name = "位置",
    coordinate = "gcj02"
)
```

## ⑱ 启动高德地图，规划驾车路径

```kotlin
/**
 * 启动高德地图，规划驾车路径
 * https://uri.amap.com/navigation?from=116.478346,39.997361,startpoint&to=116.3246,39.966577,endpoint&via=116.402796,39.936915,midwaypoint&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0
 * @param startLat Double 起点纬度
 * @param startLng Double 起点经度
 * @param startName String 起点名称
 * @param endLat Double 终点纬度
 * @param endLng Double 终点经度
 * @param endName String 终点名称
 * @param coordinate String 坐标系参数coordinate=gaode,表示高德坐标（gcj02坐标），coordinate=wgs84,表示wgs84坐标（GPS原始坐标）
 */
fun startGaoDeMapNavigation(
    startLat: Double,
    startLng: Double,
    startName: String = "起点",
    endLat: Double,
    endLng: Double,
    endName: String = "终点",
    coordinate: String = "gcj02"
)
```

```kotlin
startGaoDeMapNavigation(
    startLat = 34.264642646862,
    startLng = 108.95108518068,
    startName = "起点",
    endLat = 23.157471,
    endLng = 113.042738,
    endName = "终点",
    coordinate = "gcj02"
)
```

## ⑲ 跳转到高德地图（需要安装高德地图APP）

> - @param longitude Double 经度
> - @param latitude Double 纬度
> - @param name String 名称
> - @param src String 来源APP包名

```kotlin
goToGaodeMap(
    longitude = 116.313082,
    latitude = 40.047669,
    name = "我的位置",
    src = "com.example.app"
)
```

## ⑳ 跳转到百度地图（需要安装百度地图APP）

> - @param longitude Double 经度
> - @param latitude Double 纬度
> - @param name String 名称
> - @param src String 来源APP包名

```kotlin
goToBaiduMap(
    longitude = 116.313082,
    latitude = 40.047669,
    name = "我的位置",
    src = "com.example.app"
)
```

