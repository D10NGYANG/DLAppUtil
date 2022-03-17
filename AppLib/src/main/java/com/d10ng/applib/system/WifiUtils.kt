package com.d10ng.applib.system

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

/** 保存上一次扫描结果，仅Module内可见 */
internal var lastScanResult:List<ScanResult>? = null

/**
 * 获取内网IP地址
 */
val localIPAddress: String?
    get() {
        val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val intf: NetworkInterface = en.nextElement()
            val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
            while (enumIpAddr.hasMoreElements()) {
                val inetAddress: InetAddress = enumIpAddr.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    return inetAddress.hostAddress?.toString()
                }
            }
        }
        return null
    }

/**
 * 获取WI-FI系统管理器
 * @receiver Context
 * @return WifiManager?
 */
fun Context.getWifiManager() : WifiManager? {
    return applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
}

/**
 * 扫描热点并获取扫描结果
 * @receiver Context
 * @return List<ScanResult>
 */
fun Context.scanWifi() : List<ScanResult> {
    val wm = getWifiManager()?: return emptyList()
    // 打开Wi-Fi
    if (!wm.isWifiEnabled) wm.isWifiEnabled = true
    // 扫描
    wm.startScan()
    lastScanResult = wm.scanResults
    return lastScanResult ?: listOf()
}

/**
 * 获取连接Wi-Fi信息
 * @receiver Context
 * @return WifiInfo?
 */
fun Context.getConnectedWifiInfo(): WifiInfo? {
    val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    return wm?.connectionInfo
}

/**
 * 获取Wi-Fi名称
 * @receiver WifiInfo
 * @return String
 */
fun WifiInfo.getWifiSSID(): String {
    val len = this.ssid.length
    return if (this.ssid.startsWith("\"") && this.ssid.endsWith("\"")) {
        this.ssid.substring(1, len - 1)
    } else {
        this.ssid
    }
}

/**
 * 获取当前连接Wi-Fi名
 * # 如果没有定位权限，获取到的名字将为  unknown ssid
 * @receiver Context
 * @return String
 */
fun Context.getConnectedWifiSSID() : String {
    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    val wifiInfo = wifiManager?.connectionInfo?: return ""
    return wifiInfo.getWifiSSID()
}

/**
 * 检查当前连接的网络是否为5G WI-FI
 * @receiver Context
 * @return Boolean
 */
fun Context.is5GWifiConnected() : Boolean {
    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
    val wifiInfo = wifiManager?.connectionInfo?: return false
    // 频段
    val frequency: Int = wifiInfo.frequency
    return frequency in 4900..5900
}

/**
 * 判断连接Wi-Fi是否需要密码
 * @receiver Context
 * @return Boolean
 */
fun Context.isConnectWifiNeedPassword() : Boolean {
    val list = scanWifi()
    val connectInfo = getConnectedWifiInfo()?: return false
    for (wifi in list) {
        if (wifi.SSID == connectInfo.getWifiSSID() && wifi.BSSID == connectInfo.bssid) {
            val capabilities = wifi.capabilities?: return false
            return capabilities.contains("WPA", true)
                    || capabilities.contains("WEP", true)
        }
    }
    return false
}