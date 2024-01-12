package com.d10ng.app.demo.pages

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.MainActivity
import com.d10ng.app.demo.R
import com.d10ng.app.demo.app
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.NotificationController
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.utils.goToAppNotificationChannelSetting
import com.d10ng.app.utils.goToAppNotificationSetting
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.PageTransitions
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.d10ng.datelib.curTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 通知管理器
 * @Author d10ng
 * @Date 2024/1/11 17:20
 */
@RootNavGraph
@Destination(style = PageTransitions::class)
@Composable
fun NotificationControllerScreen() {
    LaunchedEffect(Unit) {
        launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionManager.request(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.SYSTEM_ALERT_WINDOW
                    )
                )
            } else {
                PermissionManager.request(Manifest.permission.SYSTEM_ALERT_WINDOW)
            }
        }
    }
    NotificationControllerScreenView()
}

@Composable
private fun NotificationControllerScreenView() {
    LaunchedEffect(Unit) {
        // 创建标准文本通知渠道
        NotificationController.createChannel(
            channelId = "standard_text",
            channelName = "标准文本",
            channelImportance = NotificationManager.IMPORTANCE_HIGH,
            channelDescription = "标准文本通知渠道，通知服务前台运行、任务信息之类的内容",
        )
        // 创建带进度条的通知渠道
        NotificationController.createChannel(
            channelId = "progress",
            channelName = "进度条",
            channelImportance = NotificationManager.IMPORTANCE_DEFAULT,
            channelDescription = "进度条通知渠道，通知后台任务进度之类的内容",
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "通知管理器", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "基本信息", inset = true) {
                var isEnable by remember {
                    mutableStateOf(NotificationController.isEnable())
                }
                Cell(
                    title = "是否允许显示通知",
                    value = if (isEnable) "允许" else "不允许",
                    link = true,
                    label = "点击进行更新",
                    onClick = {
                        isEnable = NotificationController.isEnable()
                    })
                Cell(title = "打开通知设置", link = true, onClick = {
                    goToAppNotificationSetting()
                })
            }
            CellGroup(title = "标准文本", inset = true) {
                var isEnable by remember {
                    mutableStateOf(NotificationController.isBannerNotificationsEnabled("standard_text"))
                }
                Cell(
                    title = "渠道是否允许横幅通知",
                    value = if (isEnable) "允许" else "不允许",
                    link = true,
                    label = "点击进行更新（⚠️MIUI无效）",
                    onClick = {
                        isEnable =
                            NotificationController.isBannerNotificationsEnabled("standard_text")
                    })
                Cell(title = "打开通知渠道设置", link = true, onClick = {
                    goToAppNotificationChannelSetting("standard_text")
                })
                Cell(title = "创建标准通知", link = true, onClick = {
                    NotificationController.createStandard(
                        channelId = "standard_text",
                        notifyId = (curTime / 1000).toInt(),
                        title = "通知标题，一般为应用名称",
                        content = "通知内容，一般为任务信息，或者简要任务的介绍",
                        smallIcon = R.drawable.ic_launcher_foreground,
                        tapIntent = getIntent()
                    )
                })
                Cell(title = "创建大文本通知", link = true, onClick = {
                    NotificationController.createStandard(
                        channelId = "standard_text",
                        notifyId = (curTime / 1000).toInt(),
                        title = "通知标题，一般为应用名称",
                        content = "大文本通知效果不佳，在MIUI上甚至只有长按才能显示",
                        bigText = "大文本通知内容，一般为任务信息，或者简要任务的介绍，如果内容过长，可以使用大文本通知，这样可以显示更多的内容；",
                        smallIcon = R.drawable.ic_launcher_foreground,
                        tapIntent = getIntent()
                    )
                })
            }
            CellGroup(title = "进度条", inset = true) {
                Cell(title = "打开通知渠道设置", link = true, onClick = {
                    goToAppNotificationChannelSetting("progress")
                })
                val scope = rememberCoroutineScope()
                Cell(title = "创建明确进度条通知", link = true, onClick = {
                    val id = 100
                    val builder = NotificationController.createProgress(
                        channelId = "progress",
                        notifyId = id,
                        title = "通知标题，一般为应用名称",
                        content = "通知内容，一般为任务名称",
                        subText = "还剩5分钟",
                        smallIcon = R.drawable.ic_launcher_foreground,
                        tapIntent = getIntent()
                    )
                    scope.launch {
                        var p = 0
                        while (p < 100) {
                            delay(1000)
                            p += 10
                            builder.setProgress(100, p, false)
                            NotificationController.update(id, builder)
                        }
                    }
                })
                Cell(title = "创建不明确进度条通知", link = true, onClick = {
                    val id = 101
                    NotificationController.createProgress(
                        channelId = "progress",
                        notifyId = id,
                        title = "通知标题，一般为应用名称",
                        content = "通知内容，一般为任务名称",
                        subText = "进行中",
                        max = 0,
                        indeterminate = true,
                        smallIcon = R.drawable.ic_launcher_foreground,
                        tapIntent = getIntent()
                    )
                })
            }
        }
    }
}

private fun getIntent(): PendingIntent {
    val intent = Intent(app, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    return PendingIntent.getActivity(
        app,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

@Preview
@Composable
private fun NotificationControllerScreenPreview() {
    NotificationControllerScreenView()
}