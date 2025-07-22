package com.d10ng.app.demo.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.destinations.AppManagerScreenDestination
import com.d10ng.app.demo.destinations.BatteryStatusManagerScreenDestination
import com.d10ng.app.demo.destinations.ContactManagerScreenDestination
import com.d10ng.app.demo.destinations.GnssStatusManagerScreenDestination
import com.d10ng.app.demo.destinations.LocationStatusManagerScreenDestination
import com.d10ng.app.demo.destinations.NavigationUtilScreenDestination
import com.d10ng.app.demo.destinations.NetworkStatusManagerScreenDestination
import com.d10ng.app.demo.destinations.NmeaStatusManagerScreenDestination
import com.d10ng.app.demo.destinations.NotificationControllerScreenDestination
import com.d10ng.app.demo.destinations.PermissionManagerScreenDestination
import com.d10ng.app.demo.destinations.PhotoManagerScreenDestination
import com.d10ng.app.demo.destinations.SensorStatusManagerScreenDestination
import com.d10ng.app.demo.destinations.SmsControllerScreenDestination
import com.d10ng.app.demo.destinations.SystemManagerScreenDestination
import com.d10ng.app.demo.ui.PageTransitions
import com.d10ng.app.demo.utils.go
import com.d10ng.app.utils.goToSystemHome
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

/**
 * 首页
 * @Author d10ng
 * @Date 2024/1/8 09:51
 */
@Destination<RootGraph>(start = true, style = PageTransitions::class)
@Composable
fun HomeScreen() {
    HomeScreenView()

    BackHandler {
        goToSystemHome()
    }
}

@Composable
private fun HomeScreenView(
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "首页")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "管理器", inset = true) {
                Cell(title = "系统管理器", link = true, onClick = {
                    go(SystemManagerScreenDestination.route)
                })
                Cell(title = "APP管理器", link = true, onClick = {
                    go(AppManagerScreenDestination.route)
                })
                Cell(title = "权限管理器", link = true, onClick = {
                    go(PermissionManagerScreenDestination.route)
                })
                Cell(title = "通知管理器", link = true, onClick = {
                    go(NotificationControllerScreenDestination.route)
                })
                Cell(title = "联系人管理器", link = true, onClick = {
                    go(ContactManagerScreenDestination.route)
                })
                Cell(title = "短信管理器", link = true, onClick = {
                    go(SmsControllerScreenDestination.route)
                })
                Cell(title = "图片媒体管理器", link = true, onClick = {
                    go(PhotoManagerScreenDestination.route)
                })
            }
            CellGroup(title = "状态", inset = true) {
                Cell(title = "电池电量状态", link = true, onClick = {
                    go(BatteryStatusManagerScreenDestination.route)
                })
                Cell(title = "定位状态", link = true, onClick = {
                    go(LocationStatusManagerScreenDestination.route)
                })
                Cell(title = "GNSS状态", link = true, onClick = {
                    go(GnssStatusManagerScreenDestination.route)
                })
                Cell(title = "NMEA状态", link = true, onClick = {
                    go(NmeaStatusManagerScreenDestination.route)
                })
                Cell(title = "网络状态", link = true, onClick = {
                    go(NetworkStatusManagerScreenDestination.route)
                })
                Cell(title = "传感器状态", link = true, onClick = {
                    go(SensorStatusManagerScreenDestination.route)
                })
            }
            CellGroup(title = "工具", inset = true) {
                Cell(title = "常见导航工具", link = true, onClick = {
                    go(NavigationUtilScreenDestination.route)
                })
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreenView()
}