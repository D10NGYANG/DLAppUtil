package com.d10ng.app.demo.pages

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.ui.PageTransitions
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.PermissionManager
import com.d10ng.compose.model.UiViewModelManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.feedback.NotifyType
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.launch

/**
 * 权限管理器
 * @Author d10ng
 * @Date 2024/1/11 15:53
 */
@Destination<RootGraph>(style = PageTransitions::class)
@Composable
fun PermissionManagerScreen() {
    PermissionManagerScreenView()
}

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@Composable
private fun PermissionManagerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "权限管理器", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "使用方式", inset = true) {
                Cell(title = "检查权限（定位示例）", link = true, onClick = {
                    val res = PermissionManager.has(locationPermissions)
                    UiViewModelManager.showNotify(NotifyType.Primary, "是否有定位权限：$res")
                })
                val scope = rememberCoroutineScope()
                Cell(title = "异步申请权限（定位示例）", link = true, onClick = {
                    scope.launch {
                        val res = PermissionManager.request(locationPermissions)
                        UiViewModelManager.showNotify(NotifyType.Primary, "定位权限申请结果：$res")
                    }
                })
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Cell(title = "申请存储管理权限（需要跳转）", link = true, onClick = {
                        scope.launch {
                            val res = PermissionManager.requestManageExternalStorage()
                            UiViewModelManager.showNotify(
                                NotifyType.Primary,
                                "存储权限申请结果：$res"
                            )
                        }
                    })
                }
            }
        }
    }
}

@Preview
@Composable
private fun PermissionManagerScreenPreview() {
    PermissionManagerScreenView()
}