package com.d10ng.app.status

import android.app.Application
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * 网络状态管理
 * @Author d10ng
 * @Date 2023/9/1 19:39
 */
object NetworkStatusManager {

    private lateinit var application: Application
    private lateinit var manager: ConnectivityManager

    private val networkFlow = MutableStateFlow<Network?>(null)
    private val linkPropertiesFlow = MutableStateFlow<LinkProperties?>(null)
    private val networkCapabilitiesFlow = MutableStateFlow<NetworkCapabilities?>(null)

    fun init(app: Application) {
        application = app
        manager = app.getSystemService(ConnectivityManager::class.java)

        networkFlow.value = manager.activeNetwork
        linkPropertiesFlow.value = manager.getLinkProperties(manager.activeNetwork)
        networkCapabilitiesFlow.value = manager.getNetworkCapabilities(manager.activeNetwork)

        val request = NetworkRequest.Builder().build()
        manager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkFlow.value = network
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networkFlow.value = null
                linkPropertiesFlow.value = null
                networkCapabilitiesFlow.value = null
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
                networkFlow.value = network
                linkPropertiesFlow.value = linkProperties
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                networkFlow.value = network
                networkCapabilitiesFlow.value = networkCapabilities
            }
        })
    }

    fun getNetworkFlow() = networkFlow.asStateFlow()
    fun getLinkPropertiesFlow() = linkPropertiesFlow.asStateFlow()
    fun getNetworkCapabilitiesFlow() = networkCapabilitiesFlow.asStateFlow()

    private fun NetworkCapabilities.hasNet() =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    fun isNetworkAvailableFlow() = getNetworkCapabilitiesFlow().map { it?.hasNet() ?: false }
    fun isNetworkAvailable() = getNetworkCapabilitiesFlow().value?.hasNet() ?: false
}

/**
 * 判断网络是否可用
 * @return Boolean
 */
fun isNetworkAvailable() = NetworkStatusManager.isNetworkAvailable()