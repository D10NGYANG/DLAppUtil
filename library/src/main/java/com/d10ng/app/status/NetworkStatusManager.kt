package com.d10ng.app.status

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.managers.phoneManufacturer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.net.Inet4Address
import java.net.Inet6Address

/**
 * 网络状态管理
 * > 请在AndroidManifest.xml中添加权限，否则无法获取网络状态
 * > <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * > <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * > <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 * > READ_PHONE_STATE 需要动态申请
 * @Author d10ng
 * @Date 2023/9/1 19:39
 */
object NetworkStatusManager {

    // 网络类型
    enum class NetworkType(val value: Int, val text: String) {
        // 无网络
        NONE(0, "无网络"),

        // 未知移动网络
        UNKNOWN_MOBILE(1, "未知移动网络"),

        // 2G网络
        G2(2, "2G"),

        // 3G网络
        G3(3, "3G"),

        // 4G网络
        G4(4, "4G"),

        // 5G网络
        G5(5, "5G"),

        // 2.5G WIFI网络
        WIFI_2G(10, "2.5G WIFI"),

        // 5G WIFI网络
        WIFI_5G(11, "5G WIFI"),

        // 未知WIFI网络
        WIFI_UNKNOWN(12, "未知WIFI类型"),

        // 以太网
        ETHERNET(20, "以太网"),

        // 蓝牙
        BLUETOOTH(30, "蓝牙"),

        // VPN
        VPN(40, "VPN"),

        // 未知
        UNKNOWN(100, "未知")
    }

    private lateinit var manager: ConnectivityManager
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var wifiManager: WifiManager
    private val scope = CoroutineScope(Dispatchers.IO)

    // 网络
    private val _networkFlow = MutableStateFlow<Network?>(null)
    val networkFlow = _networkFlow.asStateFlow()

    // 网络属性
    private val _linkPropertiesFlow = MutableStateFlow<LinkProperties?>(null)
    val linkPropertiesFlow = _linkPropertiesFlow.asStateFlow()

    // 网络能力
    private val _networkCapabilitiesFlow = MutableStateFlow<NetworkCapabilities?>(null)
    val networkCapabilitiesFlow = _networkCapabilitiesFlow.asStateFlow()

    // 网络是否可用
    val isAvailableFlow = networkCapabilitiesFlow.map { it?.hasNet() ?: false }
        .stateIn(scope, SharingStarted.Eagerly, false)

    // 网络类型
    @SuppressLint("MissingPermission")
    val networkTypeFlow = networkCapabilitiesFlow.map {
        if (it == null) NetworkType.NONE
        else if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            val connect = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                || phoneManufacturer.contentEquals("HUAWEI", true)
            ) {
                @Suppress("DEPRECATION")
                wifiManager.connectionInfo
            } else {
                it.transportInfo as? WifiInfo
            }
            if (connect == null) {
                NetworkType.WIFI_UNKNOWN
            } else if (connect.frequency in 4900..5900) {
                NetworkType.WIFI_5G
            } else if (connect.frequency in 2400..2500) {
                NetworkType.WIFI_2G
            } else {
                NetworkType.WIFI_UNKNOWN
            }
        } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            if (PermissionManager.has(Manifest.permission.READ_PHONE_STATE)) {
                when (telephonyManager.dataNetworkType) {
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_1xRTT -> NetworkType.G2

                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.G3

                    TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.G4
                    TelephonyManager.NETWORK_TYPE_NR -> NetworkType.G5
                    else -> NetworkType.UNKNOWN_MOBILE
                }
            } else {
                NetworkType.UNKNOWN_MOBILE
            }
        } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            NetworkType.ETHERNET
        } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
            NetworkType.BLUETOOTH
        } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            NetworkType.VPN
        } else {
            NetworkType.UNKNOWN
        }
    }.stateIn(scope, SharingStarted.Eagerly, NetworkType.UNKNOWN)

    // IPV4地址
    val ipv4Flow = linkPropertiesFlow.map {
        it?.linkAddresses?.firstOrNull { a ->
            !a.address.isLoopbackAddress && a.address is Inet4Address
        }?.address?.hostAddress ?: ""
    }.stateIn(scope, SharingStarted.Eagerly, "")

    // IPV6地址
    val ipv6Flow = linkPropertiesFlow.map {
        it?.linkAddresses?.firstOrNull { a ->
            !a.address.isLoopbackAddress && a.address is Inet6Address
        }?.address?.hostAddress ?: ""
    }.stateIn(scope, SharingStarted.Eagerly, "")

    internal fun init(app: Application) {
        manager = app.getSystemService(ConnectivityManager::class.java)
        telephonyManager = app.getSystemService(TelephonyManager::class.java)
        wifiManager = app.getSystemService(WifiManager::class.java)

        updateNetwork()

        val request = NetworkRequest.Builder().build()
        manager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                updateNetwork()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                updateNetwork()
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
                if (network == manager.activeNetwork) {
                    updateNetwork()
                }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (network == manager.activeNetwork) {
                    updateNetwork()
                }
            }
        })
    }

    private fun updateNetwork() {
        _networkFlow.value = manager.activeNetwork
        _linkPropertiesFlow.value = manager.getLinkProperties(manager.activeNetwork)
        _networkCapabilitiesFlow.value = manager.getNetworkCapabilities(manager.activeNetwork)
    }

    private fun NetworkCapabilities.hasNet() =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    /**
     * 判断网络是否可用
     * @return Boolean
     */
    fun isAvailable() = isAvailableFlow.value
}

/**
 * 判断网络是否可用
 * @return Boolean
 */
fun isNetworkAvailable() = NetworkStatusManager.isAvailable()