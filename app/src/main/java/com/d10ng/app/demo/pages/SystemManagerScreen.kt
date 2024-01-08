package com.d10ng.app.demo.pages

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.androidID
import com.d10ng.app.managers.phoneManufacturer
import com.d10ng.app.managers.phoneModel
import com.d10ng.app.managers.systemVersion
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

/**
 * 系统管理器页
 * @Author d10ng
 * @Date 2024/1/8 10:55
 */
@RootNavGraph
@Destination(style = PageTransitions::class)
@Composable
fun SystemManagerScreen() {
    SystemManagerScreenView()
}

@Composable
private fun SystemManagerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "系统管理器", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "系统信息", inset = true) {
                Cell(title = "android ID", value = androidID)
                Cell(title = "手机型号", value = phoneModel)
                Cell(title = "手机品牌", value = phoneManufacturer)
                Cell(title = "系统版本号", value = systemVersion)
                Cell(title = "系统API版本", value = Build.VERSION.SDK_INT.toString())
            }
        }
    }
}

@Preview
@Composable
private fun SystemManagerScreenPreview() {
    SystemManagerScreenView()
}