package com.d10ng.applib.system

import android.content.Context
import android.net.*
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow

class NetUtils(context: Context) {

    companion object {

        @Volatile
        private var INSTANCE : NetUtils? = null

        @JvmStatic
        fun instant(context: Context) : NetUtils {
            val temp = INSTANCE
            if (null != temp) {
                return temp
            }
            synchronized(this) {
                val instance = NetUtils(context)
                INSTANCE = instance
                return instance
            }
        }
    }

    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var networkFlow = MutableStateFlow<Network?>(null)
    var linkPropertiesFlow = MutableStateFlow<LinkProperties?>(null)
    var networkCapabilitiesFlow = MutableStateFlow<NetworkCapabilities?>(null)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkFlow.value = manager.activeNetwork
            linkPropertiesFlow.value = manager.getLinkProperties(manager.activeNetwork)
            networkCapabilitiesFlow.value = manager.getNetworkCapabilities(manager.activeNetwork)
        }
        val request = NetworkRequest.Builder().build()
        manager.registerNetworkCallback(request, object: ConnectivityManager.NetworkCallback(){
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
}

/**
 * 判断网络是否可用
 * @receiver Context
 * @return Boolean
 */
fun Context.isNetworkAvailable() : Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val infos = cm.allNetworkInfo
    for (info in infos) {
        if (info != null && info.state == NetworkInfo.State.CONNECTED) return true
    }
    return false
}