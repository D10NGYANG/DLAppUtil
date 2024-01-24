package com.d10ng.app.demo.pages

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
import com.d10ng.app.utils.goToAppNotificationSetting
import com.d10ng.app.utils.goToAppSetting
import com.d10ng.app.utils.goToBrowser
import com.d10ng.app.utils.goToSystemCall
import com.d10ng.app.utils.goToSystemHome
import com.d10ng.app.utils.goToSystemLocationSetting
import com.d10ng.app.utils.goToSystemSms
import com.d10ng.app.utils.goToSystemWifiSetting
import com.d10ng.app.utils.startBaiDuMapMaker
import com.d10ng.app.utils.startBaiduMapNavigation
import com.d10ng.app.utils.startGaoDeMapMaker
import com.d10ng.app.utils.startGaoDeMapNavigation
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

/**
 * 导航工具
 * @Author d10ng
 * @Date 2024/1/24 14:17
 */
@RootNavGraph
@Destination(style = PageTransitions::class)
@Composable
fun NavigationUtilScreen() {
    NavigationUtilScreenView()
}

@Composable
private fun NavigationUtilScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "常见导航工具", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "系统导航", inset = true) {
                Cell(title = "回到桌面", link = true, onClick = { goToSystemHome() })
                Cell(
                    title = "打开系统Wi-Fi设置页面",
                    link = true,
                    onClick = { goToSystemWifiSetting() })
                Cell(
                    title = "打开位置信息设置页面",
                    link = true,
                    onClick = { goToSystemLocationSetting() })
                Cell(
                    title = "打开浏览器访问网站",
                    link = true,
                    onClick = { goToBrowser("https://baidu.com") })
                Cell(
                    title = "打开系统拨打电话页面",
                    link = true,
                    onClick = { goToSystemCall("10010") })
                Cell(
                    title = "打开系统短信页面",
                    link = true,
                    onClick = { goToSystemSms("10010", "101") })
            }
            CellGroup(title = "APP设置", inset = true) {
                Cell(title = "打开App设置页面", link = true, onClick = { goToAppSetting() })
                Cell(
                    title = "打开App通知设置页面",
                    link = true,
                    onClick = { goToAppNotificationSetting() })
            }
            CellGroup(title = "地图", inset = true) {
                Cell(
                    title = "启动百度地图，显示地标位置",
                    link = true,
                    onClick = { startBaiDuMapMaker(40.047669, 116.313082) })
                Cell(
                    title = "启动百度地图，规划驾车路径",
                    link = true,
                    onClick = {
                        startBaiduMapNavigation(
                            34.264642646862,
                            108.95108518068,
                            endLat = 23.157471,
                            endLng = 113.042738
                        )
                    })
                Cell(
                    title = "启动高德地图，显示地标位置",
                    link = true,
                    onClick = { startGaoDeMapMaker(40.047669, 116.313082) })
                Cell(
                    title = "启动高德地图，规划驾车路径",
                    link = true,
                    onClick = {
                        startGaoDeMapNavigation(
                            34.264642646862,
                            108.95108518068,
                            endLat = 23.157471,
                            endLng = 113.042738
                        )
                    })
            }
        }
    }
}

@Preview
@Composable
private fun NavigationUtilScreenPreview() {
    NavigationUtilScreenView()
}