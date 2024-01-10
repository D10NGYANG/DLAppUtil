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
import com.d10ng.app.managers.copyToClipboard
import com.d10ng.app.managers.phoneManufacturer
import com.d10ng.app.managers.phoneModel
import com.d10ng.app.managers.playRingtone
import com.d10ng.app.managers.readClipboard
import com.d10ng.app.managers.showToast
import com.d10ng.app.managers.systemVersion
import com.d10ng.app.managers.vibrate
import com.d10ng.compose.model.UiViewModelManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.feedback.NotifyType
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
            CellGroup(title = "系统交互", inset = true) {
                Cell(title = "显示Toast", link = true, onClick = {
                    showToast("测试Toast显示")
                })
                Cell(title = "震动", link = true, onClick = {
                    vibrate()
                })
                Cell(title = "震动特定时长，2秒", link = true, onClick = {
                    vibrate(2000)
                })
                Cell(title = "播放提示音", link = true, onClick = {
                    playRingtone()
                })
            }
            CellGroup(title = "粘贴板", inset = true) {
                Cell(title = "复制内容到粘贴版", link = true, onClick = {
                    copyToClipboard("", "测试复制内容")
                    UiViewModelManager.showSuccessToast("复制成功")
                })
                Cell(title = "获取粘贴板内容", link = true, onClick = {
                    val contents = readClipboard()
                    UiViewModelManager.showNotify(NotifyType.Success, "获取成功：$contents", 3000)
                })
            }
        }
    }
}

@Preview
@Composable
private fun SystemManagerScreenPreview() {
    SystemManagerScreenView()
}