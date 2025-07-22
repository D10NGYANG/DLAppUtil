package com.d10ng.app.demo.pages

import android.Manifest
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
import com.d10ng.app.demo.ui.PageTransitions
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.status.LocationStatusManager
import com.d10ng.compose.model.UiViewModelManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.d10ng.datelib.toDateStr
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

/**
 * 定位状态管理
 * @Author d10ng
 * @Date 2024/1/8 16:54
 */
@Destination<RootGraph>(style = PageTransitions::class)
@Composable
fun LocationStatusManagerScreen() {
    LaunchedEffect(Unit) {
        if (PermissionManager.request(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        ) {
            LocationStatusManager.start()
        } else {
            UiViewModelManager.showFailToast("权限不足")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            LocationStatusManager.stop()
        }
    }
    LocationStatusManagerScreenView()
}

@Composable
private fun LocationStatusManagerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "定位状态", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val location by LocationStatusManager.statusFlow.collectAsState(null)
            CellGroup(title = "定位信息", inset = true) {
                Cell(title = "时间", value = location?.time?.toDateStr() ?: "--")
                Cell(title = "经度", value = location?.longitude?.toString() ?: "--")
                Cell(title = "纬度", value = location?.latitude?.toString() ?: "--")
                Cell(title = "海拔", value = "${location?.altitude}米")
                Cell(title = "速度", value = "${location?.speed}m/s")
                Cell(title = "方向", value = "${location?.bearing}度")
                Cell(title = "精度", value = "${location?.accuracy}米")
                Cell(title = "提供者", value = location?.provider ?: "--")
            }
        }
    }
}

@Preview
@Composable
private fun LocationStatusManagerScreenPreview() {
    LocationStatusManagerScreenView()
}