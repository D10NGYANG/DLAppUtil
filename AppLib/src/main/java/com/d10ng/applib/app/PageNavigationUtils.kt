package com.d10ng.applib.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * 回到手机首页
 */
fun Activity.goToSystemHome() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    startActivity(intent)
}

/**
 * 打开浏览器访问网站
 * @param url 网站地址
 */
fun Activity.goToBrowser(url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}

/**
 * 打开系统拨打电话页面
 * @param phone 电话号码
 */
fun Activity.goToSystemCall(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    val data = Uri.parse("tel:$phone")
    intent.data = data
    startActivity(intent)
}

/**
 * 前往系统短信页面，填充联系人和消息内容
 * @receiver Activity
 * @param phone String 电话号码
 * @param content String 消息内容
 */
fun Activity.goToSystemSms(phone: String, content: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    val data = Uri.parse("smsto:$phone")
    intent.data = data
    intent.putExtra("sms_body", content)
    startActivity(intent)
}

/**
 * 打开Wi-Fi设置页面
 * @receiver Activity
 */
fun Activity.goToSystemWifiSetting() {
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(intent)
}

/**
 * 打开位置信息设置页面
 * @receiver Activity
 * @return Intent
 */
fun Activity.goToSystemLocationSetting() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(intent)
}

/**
 * 打开App设置页面
 * @receiver Activity
 * @return Intent
 */
fun Activity.goToAppSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
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
