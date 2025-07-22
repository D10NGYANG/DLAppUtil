package com.d10ng.app.demo.pages

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.ui.PageTransitions
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.status.NetworkStatusManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

/**
 * 网络状态管理
 * @Author d10ng
 * @Date 2024/1/8 17:24
 */
@Destination<RootGraph>(style = PageTransitions::class)
@Composable
fun NetworkStatusManagerScreen() {
    LaunchedEffect(Unit) {
        PermissionManager.request(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE
            )
        )
    }
    NetworkStatusManagerScreenView()
}

@Composable
private fun NetworkStatusManagerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "网络状态", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val linkProperties by NetworkStatusManager.linkPropertiesFlow.collectAsState()
            val networkCapabilities by NetworkStatusManager.networkCapabilitiesFlow.collectAsState()
            val isAvailable by NetworkStatusManager.isAvailableFlow.collectAsState()
            val networkType by NetworkStatusManager.networkTypeFlow.collectAsState()
            val ipv4 by NetworkStatusManager.ipv4Flow.collectAsState()
            val ipv6 by NetworkStatusManager.ipv6Flow.collectAsState()

            CellGroup(title = "基础信息", inset = true) {
                Cell(title = "网络是否可用", value = if (isAvailable) "可用" else "不可用")
                // IP地址
                Cell(title = "IPv4地址", value = ipv4)
                Cell(title = "IPv6地址", value = ipv6)
                // DNS服务器
                Cell(
                    title = "DNS服务器",
                    value = linkProperties?.dnsServers?.joinToString {
                        it.hostAddress?.toString() ?: ""
                    } ?: "无")
                // 接口名称
                Cell(title = "接口名称", value = linkProperties?.interfaceName ?: "无")
                // 网络类型
                Cell(title = "网络类型", value = networkType.text)
                // 上行带宽
                Cell(
                    title = "上行带宽",
                    value = "${networkCapabilities?.linkUpstreamBandwidthKbps}Kbps"
                )
                // 下行带宽
                Cell(
                    title = "下行带宽",
                    value = "${networkCapabilities?.linkDownstreamBandwidthKbps}Kbps"
                )
            }
        }
    }
}

@Preview
@Composable
private fun NetworkStatusManagerScreenPreview() {
    NetworkStatusManagerScreenView()
}