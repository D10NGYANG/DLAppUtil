package com.d10ng.app.demo.pages

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.d10ng.app.demo.ui.PageTransitions
import com.d10ng.app.demo.utils.back
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.status.LocationStatusManager
import com.d10ng.app.status.NmeaStatusManager
import com.d10ng.compose.model.UiViewModelManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.AppText
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.d10ng.datelib.toDateStr
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * NMEA状态管理页面
 * @Author d10ng
 * @Date 2024/3/22 14:10
 */
@Destination<RootGraph>(style = PageTransitions::class)
@Composable
fun NmeaStatusManagerScreen() {

    val data = remember {
        mutableStateListOf<NmeaStatusManager.Status>()
    }

    LaunchedEffect(Unit) {
        if (PermissionManager.request(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        ) {
            LocationStatusManager.start()
            NmeaStatusManager.start()
        } else {
            UiViewModelManager.showFailToast("权限不足")
        }
        launch(Dispatchers.IO) {
            NmeaStatusManager.statusFlow.collect {
                if (data.size >= 100) {
                    data.removeAt(data.size - 1)
                }
                data.add(0, it)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            LocationStatusManager.stop()
            NmeaStatusManager.stop()
        }
    }

    NmeaStatusManagerScreenView(data)
}

@Composable
private fun NmeaStatusManagerScreenView(
    data: List<NmeaStatusManager.Status> = listOf()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "NMEA信息", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            data.forEach { value ->
                ItemView(value)
            }
        }
    }
}

@Composable
private fun ItemView(
    value: NmeaStatusManager.Status
) {
    CellGroup(
        inset = true,
        modifier = Modifier
            .padding(top = 19.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(19.dp)
        ) {
            Text(
                text = value.timestamp.toDateStr(),
                style = AppText.Normal.Tips.mini
            )
            Text(
                text = value.message.trimEnd(),
                style = AppText.Normal.Body.default,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun NmeaStatusManagerScreenPreview() {
    NmeaStatusManagerScreenView()
}