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
import com.d10ng.app.demo.ui.PageTransitions
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.appVersion
import com.d10ng.app.managers.appVersionCode
import com.d10ng.app.managers.existApp
import com.d10ng.app.managers.restartApp
import com.d10ng.compose.model.UiViewModelManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.dialog.builder.InputDialogBuilder
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.launch

/**
 * APP管理器
 * @Author d10ng
 * @Date 2024/1/8 11:08
 */
@Destination<RootGraph>(style = PageTransitions::class)
@Composable
fun AppManagerScreen() {
    AppManagerScreenView()
}

@Composable
private fun AppManagerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "APP管理器", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "APP信息", inset = true) {
                Cell(title = "版本号", value = appVersion)
                Cell(title = "版本码", value = appVersionCode.toString())
            }
            CellGroup(title = "APP控制", inset = true) {
                Cell(title = "重启APP", link = true, onClick = {
                    restartApp()
                })
                Cell(title = "查询特定APP是否已安装", link = true, onClick = {
                    UiViewModelManager.showDialog(InputDialogBuilder(
                        title = "包名",
                        inputs = listOf(
                            InputDialogBuilder.Input(
                                initValue = "com.tencent.mm",
                                label = "请输入APP包名",
                                verify = { value ->
                                    val pass = value.isNotEmpty()
                                    InputDialogBuilder.Verify(
                                        pass = pass,
                                        errorText = if (pass) "" else "请输入APP包名"
                                    )
                                }
                            )
                        ),
                        onConfirmClick = {
                            launch {
                                val res = existApp(it[0])
                                UiViewModelManager.showToast(
                                    if (res) "已安装" else "未安装",
                                    3000
                                )
                            }
                            true
                        }
                    ))
                })
            }
        }
    }
}

@Preview
@Composable
private fun AppManagerScreenPreview() {
    AppManagerScreenView()
}