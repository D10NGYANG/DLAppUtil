package com.d10ng.app.demo.pages

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.PhotoManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.AppShape
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.launch
import java.io.File

/**
 * 图片管理器
 * @Author d10ng
 * @Date 2024/1/19 11:46
 */
@RootNavGraph
@Destination(style = PageTransitions::class)
@Composable
fun PhotoManagerScreen() {
    PhotoManagerScreenView()
}

@Composable
private fun PhotoManagerScreenView() {
    // 选择图片地址
    var selectPath by remember { mutableStateOf("") }
    val previewBitmap = remember(selectPath) {
        val imgFile = File(selectPath)
        if (imgFile.exists()) BitmapFactory.decodeFile(imgFile.absolutePath).asImageBitmap()
        else null
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "图片管理器", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "选择", inset = true) {
                val scope = rememberCoroutineScope()
                Cell(title = "选择JPG图片", link = true, onClick = {
                    scope.launch {
                        PhotoManager.pick()?.let { path ->
                            selectPath = path
                        }
                    }
                })
                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap,
                        contentDescription = "Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.White, AppShape.RC.v8)
                            .clip(AppShape.RC.v8)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PhotoManagerScreenPreview() {
    PhotoManagerScreenView()
}