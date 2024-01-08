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
import com.d10ng.app.demo.pages.destinations.AppManagerScreenDestination
import com.d10ng.app.demo.pages.destinations.SystemManagerScreenDestination
import com.d10ng.app.demo.utils.go
import com.d10ng.app.utils.goToSystemHome
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

/**
 * 首页
 * @Author d10ng
 * @Date 2024/1/8 09:51
 */
@RootNavGraph(start = true)
@Destination(style = PageTransitions::class)
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
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreenView()
}