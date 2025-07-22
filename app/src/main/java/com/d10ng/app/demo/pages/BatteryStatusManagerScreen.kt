package com.d10ng.app.demo.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.app.demo.ui.PageTransitions
import com.d10ng.app.demo.utils.back
import com.d10ng.app.status.BatteryStatusManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

/**
 * 电池状态管理器
 * @Author d10ng
 * @Date 2024/1/8 15:17
 */
@Destination<RootGraph>(style = PageTransitions::class)
@Composable
fun BatteryStatusManagerScreen() {
    BatteryStatusManagerScreenView()
}

@Composable
private fun BatteryStatusManagerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "电池状态", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            CellGroup(title = "电池信息", inset = true) {
                val battery by BatteryStatusManager.batteryFlow.collectAsState()
                Cell(title = "电量", value = "${battery}%")
                val isCharging by BatteryStatusManager.isChargingFlow.collectAsState(false)
                Cell(title = "是否充电", value = if (isCharging) "是" else "否")
                val chargeType by BatteryStatusManager.chargeTypeFlow.collectAsState(null)
                Cell(title = "充电类型", value = chargeType?.text ?: "未知")
                val health by BatteryStatusManager.healthFlow.collectAsState(BatteryStatusManager.HealthType.UNKNOWN)
                Cell(title = "健康", value = health.text)
                val temperature by BatteryStatusManager.temperatureFlow.collectAsState(0)
                Cell(title = "温度", value = "${temperature}℃")
                val voltage by BatteryStatusManager.voltageFlow.collectAsState(0)
                Cell(title = "电压", value = "${voltage}V")
                val technology by BatteryStatusManager.technologyFlow.collectAsState(null)
                Cell(title = "技术", value = technology ?: "未知")
            }
        }
    }
}

@Preview
@Composable
private fun BatteryStatusManagerScreenPreview() {
    BatteryStatusManagerScreenView()
}