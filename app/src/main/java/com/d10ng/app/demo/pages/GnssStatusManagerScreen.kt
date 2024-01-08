package com.d10ng.app.demo.pages

import android.Manifest
import android.location.GnssStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.status.GnssStatusManager
import com.d10ng.app.status.LocationStatusManager
import com.d10ng.compose.model.UiViewModelManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

/**
 * GNSS状态管理页面
 * @Author d10ng
 * @Date 2024/1/8 16:19
 */
@RootNavGraph
@Destination(style = PageTransitions::class)
@Composable
fun GnssStatusManagerScreen() {

    LaunchedEffect(Unit) {
        if (PermissionManager.request(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        ) {
            LocationStatusManager.start()
            GnssStatusManager.start()
        } else {
            UiViewModelManager.showFailToast("权限不足")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            LocationStatusManager.stop()
            GnssStatusManager.stop()
        }
    }

    GnssStatusManagerScreenView()
}

@Composable
private fun GnssStatusManagerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "GNSS状态", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val status by GnssStatusManager.statusFlow.collectAsState()
            CellGroup(title = "基础信息", inset = true) {
                Cell(title = "检测到的卫星总数", value = status?.satelliteCount?.toString() ?: "--")
            }
            for (i in 0 until (status?.satelliteCount ?: 0)) {
                CellGroup(title = "卫星${i + 1}", inset = true) {
                    Cell(title = "卫星编号", value = status?.getSvid(i)?.toString() ?: "--")
                    Cell(
                        title = "方位角",
                        value = "${status?.getAzimuthDegrees(i)}度"
                    )
                    Cell(
                        title = "高度角",
                        value = "${status?.getElevationDegrees(i)}度"
                    )
                    Cell(title = "信噪比(dB)", value = "${status?.getCn0DbHz(i)}dB")
                    Cell(
                        title = "是否有年历",
                        value = if (status?.hasAlmanacData(i) == true) "是" else "否"
                    )
                    Cell(
                        title = "是否有星历",
                        value = if (status?.hasEphemerisData(i) == true) "是" else "否"
                    )
                    Cell(
                        title = "是否在定位中采用",
                        value = if (status?.usedInFix(i) == true) "是" else "否"
                    )
                    Cell(
                        title = "卫星类型",
                        value = status?.getConstellationTypeText(i) ?: "--"
                    )
                }
            }
        }
    }
}

private fun GnssStatus.getConstellationTypeText(i: Int): String {
    return when (getConstellationType(i)) {
        GnssStatus.CONSTELLATION_BEIDOU -> "北斗"
        GnssStatus.CONSTELLATION_GALILEO -> "伽利略"
        GnssStatus.CONSTELLATION_GLONASS -> "格洛纳斯"
        GnssStatus.CONSTELLATION_GPS -> "GPS"
        GnssStatus.CONSTELLATION_IRNSS -> "印度区域导航卫星系统"
        GnssStatus.CONSTELLATION_QZSS -> "日本区域导航卫星系统"
        GnssStatus.CONSTELLATION_SBAS -> "卫星增强系统"
        GnssStatus.CONSTELLATION_UNKNOWN -> "未知"
        else -> "未知"
    }
}

@Preview
@Composable
private fun GnssStatusManagerScreenPreview() {
    GnssStatusManagerScreenView()
}