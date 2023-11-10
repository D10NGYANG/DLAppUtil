package com.d10ng.app.system

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.d10ng.app.startup.ctx
import com.d10ng.app.status.NetworkStatusManager
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
 * @return (WifiManager..WifiManager?)
 */
fun getWifiManager() = ctx.getSystemService(WifiManager::class.java)

/**
 * 开启Wi-Fi
 * @receiver Activity
 */
fun Activity.enabledWifi(requestCode: Int = 1) {
    val wm = getWifiManager()?: return
    if (wm.isWifiEnabled) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
        startActivityForResult(panelIntent, requestCode)
    } else {
        wm.isWifiEnabled = true
    }
}

/**
 * 扫描热点并获取扫描结果
 * @return List<ScanResult>
 */
@SuppressLint("MissingPermission")
fun scanWifi(): List<ScanResult> {
    val wm = getWifiManager() ?: return emptyList()
    // 打开Wi-Fi
    if (!wm.isWifiEnabled) wm.isWifiEnabled = true
    // 扫描
    wm.startScan()
    lastScanResult = wm.scanResults
    return lastScanResult ?: listOf()
}

/**
 * 获取连接Wi-Fi信息
 * @return WifiInfo?
 */
fun getConnectedWifiInfo(): WifiInfo? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        NetworkStatusManager.getNetworkCapabilitiesFlow().value?.transportInfo as? WifiInfo
    } else {
        getWifiManager()?.connectionInfo
    }
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
 * @return String
 */
fun getConnectedWifiSSID(): String {
    val wifiInfo = getConnectedWifiInfo() ?: return ""
    return wifiInfo.getWifiSSID()
}

/**
 * 检查当前连接的网络是否为5G WI-FI
 * @return Boolean
 */
fun is5GWifiConnected(): Boolean {
    val wifiInfo = getConnectedWifiInfo() ?: return false
    // 频段
    val frequency: Int = wifiInfo.frequency
    return frequency in 4900..5900
}

/**
 * 判断连接Wi-Fi是否需要密码
 * @return Boolean
 */
fun isConnectWifiNeedPassword(): Boolean {
    val list = scanWifi()
    val connectInfo = getConnectedWifiInfo() ?: return false
    for (wifi in list) {
        if (wifi.SSID == connectInfo.getWifiSSID() && wifi.BSSID == connectInfo.bssid) {
            val capabilities = wifi.capabilities ?: return false
            return capabilities.contains("WPA", true)
                    || capabilities.contains("WEP", true)
        }
    }
    return false
}