package com.d10ng.app.demo.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.ui.PageTransitions
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.FilePickManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.launch

/**
 * 文件选择管理器
 * @Author d10ng
 * @Date 2025/11/10 16:05
 */
@Destination<RootGraph>(style = PageTransitions::class)
@Composable
fun FilePickManagerScreen() {
    FilePickManagerScreenView()
}

@Composable
private fun FilePickManagerScreenView() {
    // 选择文件地址
    var selectPath by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "文件选择管理器", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "选择", inset = true) {
                val scope = rememberCoroutineScope()
                Cell(title = "选择文件", link = true, label = selectPath, onClick = {
                    scope.launch {
                        FilePickManager.pick()?.let { path ->
                            selectPath = path
                        }
                    }
                })
            }
        }
    }
}

@Preview
@Composable
private fun FilePickManagerScreenPreview() {
    FilePickManagerScreenView()
}