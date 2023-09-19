package com.d10ng.app.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.d10ng.app.managers.ActivityManager
import com.d10ng.app.startup.ctx

/**
 * 回到手机首页
 */
fun goToSystemHome() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    ActivityManager.top().value?.startActivity(intent)
}

/**
 * 打开浏览器访问网站
 * @param url 网站地址
 */
fun goToBrowser(url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    ActivityManager.top().value?.startActivity(intent)
}

/**
 * 打开系统拨打电话页面
 * @param phone 电话号码
 */
fun goToSystemCall(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    val data = Uri.parse("tel:$phone")
    intent.data = data
    ActivityManager.top().value?.startActivity(intent)
}

/**
 * 前往系统短信页面，填充联系人和消息内容
 * @receiver Activity
 * @param phone String 电话号码
 * @param content String 消息内容
 */
fun goToSystemSms(phone: String, content: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    val data = Uri.parse("smsto:$phone")
    intent.data = data
    intent.putExtra("sms_body", content)
    ActivityManager.top().value?.startActivity(intent)
}

/**
 * 打开Wi-Fi设置页面
 * @receiver Activity
 */
fun goToSystemWifiSetting() {
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    ActivityManager.top().value?.startActivity(intent)
}

/**
 * 打开位置信息设置页面
 * @receiver Activity
 * @return Intent
 */
fun goToSystemLocationSetting() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    ActivityManager.top().value?.startActivity(intent)
}

/**
 * 打开App设置页面
 * @receiver Activity
 * @return Intent
 */
fun goToAppSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", ctx.packageName, null)
    intent.data = uri
    ActivityManager.top().value?.startActivity(intent)
}

/**
 * 获取跳转页面Intent
 * @receiver Activity
 * @param clz Class<*>
 * @return Intent
 */
fun Activity.getClearTopIntent(clz: Class<*>) : Intent {
    val intent = Intent(this, clz)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    return intent
}

/**
 * 跳转下一个页面，如果栈中有相同的ACT会只保留最新一个到前台
 * @receiver Activity
 * @param clz Class<*>
 */
fun Activity.goTo(clz: Class<*>) {
    val intent = getClearTopIntent(clz)
    startActivity(intent)
}

/**
 * 清空整个Task并跳转页面
 * @receiver Activity
 * @param clz Class<*>
 */
fun Activity.clearTaskGoTo(clz: Class<*>) {
    val intent = Intent(this, clz)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

/**
 * 关闭当前页面并跳转下一个页面
 * @receiver Activity
 * @param clz Class<*>
 */
fun Activity.finishGoTo(clz: Class<*>) {
    goTo(clz)
    finish()
}

/**
 * 跳转下一个页面，并带返回结果
 * @param clz 跳转页面
 * @param requestCode 请求码
 */
fun Activity.goToForResult(clz: Class<*>, requestCode: Int) {
    val intent = getClearTopIntent(clz)
    startActivityForResult(intent, requestCode)
}

/**
 * 启动百度地图，显示地标位置
 * http://api.map.baidu.com/marker?location=40.047669,116.313082&title=我的位置&content=百度奎科大厦&output=html&src=webapp.baidu.openAPIdemo
 * @param lat Double
 * @param lng Double
 * @param title String
 * @param content String
 * @param coordinate String bd09ll（百度经纬度坐标）bd09mc（百度墨卡托坐标）gcj02（经国测局加密的坐标)wgs84（gps获取的原始坐标）
 */
fun startBaiDuMapMaker(
    lat: Double,
    lng: Double,
    title: String = "位置",
    content: String = "",
    coordinate: String = "gcj02"
) {
    val builder = StringBuilder("http://api.map.baidu.com/marker?").apply {
        append("location=$lat,$lng")
        append("&title=$title")
        append("&content=$content")
        append("&coord_type=$coordinate")
        append("&output=html&src=webapp.baidu.openAPIdemo")
    }
    goToBrowser(builder.toString())
}

/**
 * 启动百度地图，规划驾车路径
 * http://api.map.baidu.com/direction?origin=latlng:34.264642646862,108.95108518068|name:起点&destination=latlng:23.157471,113.042738|name:终点&mode=driving&region=1&output=html&src=webapp.baidu.openAPIdemo
 * @receiver Activity
 * @param startLat Double
 * @param startLng Double
 * @param startName String
 * @param endLat Double
 * @param endLng Double
 * @param endName String
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
) {
    val builder = StringBuilder("http://api.map.baidu.com/direction?").apply {
        append("origin=latlng:$startLat,$startLng|name:$startName")
        append("&destination=latlng:$endLat,$endLng|name:$endName")
        append("&coord_type=$coordinate")
        append("&mode=driving&region=1&output=html&src=webapp.baidu.openAPIdemo")
    }
    goToBrowser(builder.toString())
}

/**
 * 启动高德地图，显示地标位置
 * https://uri.amap.com/marker?position=121.287689,31.234527&name=park&src=mypage&coordinate=gaode&callnative=0
 * @receiver Activity
 * @param lat Double
 * @param lng Double
 * @param name String
 * @param coordinate String 坐标系参数coordinate=gaode,表示高德坐标（gcj02坐标），coordinate=wgs84,表示wgs84坐标（GPS原始坐标）
 */
fun startGaoDeMapMaker(
    lat: Double,
    lng: Double,
    name: String = "位置",
    coordinate: String = "gcj02"
) {
    val builder = StringBuilder("https://uri.amap.com/marker?").apply {
        append("position=$lng,$lat")
        append("&name=$name")
        append("&coordinate=$coordinate")
        // 是否尝试调起高德地图APP并在APP中查看，0表示不调起，1表示调起, 默认值为0
        append("&callnative=1")
    }
    goToBrowser(builder.toString())
}

/**
 * 启动高德地图，规划驾车路径
 * https://uri.amap.com/navigation?from=116.478346,39.997361,startpoint&to=116.3246,39.966577,endpoint&via=116.402796,39.936915,midwaypoint&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0
 * @receiver Activity
 * @param startLat Double
 * @param startLng Double
 * @param startName String
 * @param endLat Double
 * @param endLng Double
 * @param endName String
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
) {
    val builder = StringBuilder("https://uri.amap.com/navigation?").apply {
        append("from=$startLng,$startLat,$startName")
        append("&to=$endLng,$endLat,$endName")
        // 出行方式：驾车：mode=car；公交：mode=bus；步行：mode=walk；骑行：mode=ride；
        append("&mode=car")
        // 当mode=car(驾车)：0:推荐策略,1:避免拥堵,2:避免收费,3:不走高速（仅限移动端）当mode=bus(公交):0:最佳路线,1:换乘少,2:步行少,3:不坐地铁
        append("&policy=0")
        append("&coordinate=$coordinate")
        // 是否尝试调起高德地图APP并在APP中查看，0表示不调起，1表示调起, 默认值为0
        append("&callnative=1")
    }
    goToBrowser(builder.toString())
}
