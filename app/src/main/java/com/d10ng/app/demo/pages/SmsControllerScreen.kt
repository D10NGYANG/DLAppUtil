package com.d10ng.app.demo.pages

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.managers.SmsController
import com.d10ng.common.calculate.isOnlyNumber
import com.d10ng.compose.model.UiViewModelManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.AppText
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.base.CellRow
import com.d10ng.compose.ui.dialog.builder.InputDialogBuilder
import com.d10ng.compose.ui.navigation.NavBar
import com.d10ng.compose.ui.sheet.SheetColumn
import com.d10ng.compose.ui.sheet.builder.SheetBuilder
import com.d10ng.datelib.curTime
import com.d10ng.datelib.toDateStr
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.launch

/**
 * 短信控制器
 * @Author d10ng
 * @Date 2024/1/17 10:15
 */
@RootNavGraph
@Destination(style = PageTransitions::class)
@Composable
fun SmsControllerScreen() {
    LaunchedEffect(Unit) {
        launch {
            PermissionManager.request(
                arrayOf(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.SEND_SMS
                )
            )
        }
    }
    SmsControllerScreenView()
}

@Composable
private fun SmsControllerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "短信管理器", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val scope = rememberCoroutineScope()
            CellGroup(title = "查看", inset = true) {
                Cell(title = "获取最新一条短信", link = true, onClick = {
                    if (PermissionManager.has(Manifest.permission.READ_SMS)) {
                        scope.launch {
                            val sms = SmsController.readLatest()
                            if (sms != null) showSmsList(listOf(sms))
                            else UiViewModelManager.showFailToast("没有短信")

                        }
                    } else {
                        UiViewModelManager.showFailToast("权限不足")
                    }
                })
                Cell(title = "获取最近指定条数短信", label = "测试值：10", link = true, onClick = {
                    if (PermissionManager.has(Manifest.permission.READ_SMS)) {
                        scope.launch {
                            showSmsList(SmsController.readLatest(10))
                        }
                    } else {
                        UiViewModelManager.showFailToast("权限不足")
                    }
                })
                Cell(
                    title = "获取最近指定时间以后短信",
                    label = "测试值：最近7天",
                    link = true,
                    onClick = {
                        if (PermissionManager.has(Manifest.permission.READ_SMS)) {
                            scope.launch {
                                showSmsList(SmsController.readAfter(curTime - 7 * 24 * 60 * 60 * 1000))
                            }
                        } else {
                            UiViewModelManager.showFailToast("权限不足")
                        }
                    })
            }
            CellGroup(title = "监听", inset = true) {
                val data by SmsController.receiveFlow.collectAsState(initial = SmsController.Data())
                Cell(title = "监听短信接收")
                if (data.content.isNotEmpty()) {
                    CellRow {
                        val sb = StringBuilder().apply {
                            appendLine("消息ID：${data.id}")
                            appendLine("插卡ID：${data.subId}")
                            appendLine("号码：${data.phone}")
                            appendLine("时间：${data.time.toDateStr()}")
                            appendLine("内容：${data.content}")
                        }
                        Text(
                            text = sb.toString(),
                            style = AppText.Normal.Body.default,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(19.dp)
                        )
                    }
                }
            }
            CellGroup(title = "发送", inset = true) {
                Cell(title = "发送短信", link = true, onClick = {
                    if (PermissionManager.has(
                            arrayOf(
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_PHONE_STATE
                            )
                        )
                    ) {
                        UiViewModelManager.showDialog(InputDialogBuilder(
                            title = "发送短信",
                            inputs = listOf(
                                InputDialogBuilder.Input(
                                    initValue = "10010",
                                    label = "目标号码",
                                    keyboardType = KeyboardType.Number,
                                    verify = { s ->
                                        (s.isOnlyNumber() && s.isNotEmpty()).let {
                                            InputDialogBuilder.Verify(
                                                it,
                                                if (it) "" else "请输入正确的号码"
                                            )
                                        }
                                    }
                                ),
                                InputDialogBuilder.Input(
                                    initValue = "101",
                                    label = "短信内容",
                                    keyboardType = KeyboardType.Text,
                                    verify = { s ->
                                        (s.isNotEmpty()).let {
                                            InputDialogBuilder.Verify(
                                                it,
                                                if (it) "" else "请输入短信内容"
                                            )
                                        }
                                    }
                                )
                            ),
                            onConfirmClick = { list ->
                                scope.launch {
                                    runCatching {
                                        SmsController.sendText(
                                            desAddress = list[0],
                                            content = list[1]
                                        )
                                    }.onFailure {
                                        it.printStackTrace()
                                        it.message?.let { m ->
                                            UiViewModelManager.showErrorNotify(
                                                m
                                            )
                                        }
                                    }.onSuccess {
                                        UiViewModelManager.showSuccessToast("发送成功")
                                    }
                                }
                                true
                            }
                        ))
                    } else {
                        UiViewModelManager.showFailToast("权限不足")
                    }
                })
            }
        }
    }
}

private fun showSmsList(data: List<SmsController.Data>) {
    val sb = StringBuilder()
    data.forEach {
        sb.appendLine("消息ID：${it.id}")
        sb.appendLine("插卡ID：${it.subId}")
        sb.appendLine("号码：${it.phone}")
        sb.appendLine("时间：${it.time.toDateStr()}")
        sb.appendLine("内容：${it.content}")
        sb.appendLine()
    }
    UiViewModelManager.showSheet(CustomSheetBuilder(
        title = "短信详情",
        content = {
            Text(
                text = sb.toString(),
                style = AppText.Normal.Body.default,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(19.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
    ))
}

private class CustomSheetBuilder(
    // 标题
    private val title: String,
    // 内容
    private val content: @Composable () -> Unit,
) : SheetBuilder() {
    @Composable
    override fun Build() {
        SheetColumn {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = AppText.Bold.Title.large,
                    textAlign = TextAlign.Center
                )
            }
            content()
        }
    }
}

@Preview
@Composable
private fun SmsControllerScreenPreview() {
    SmsControllerScreenView()
}