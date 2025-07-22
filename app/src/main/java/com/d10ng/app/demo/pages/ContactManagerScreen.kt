package com.d10ng.app.demo.pages

import android.Manifest
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
import com.d10ng.app.managers.ContactManager
import com.d10ng.app.managers.PermissionManager
import com.d10ng.common.calculate.isMobileNumber
import com.d10ng.compose.model.UiViewModelManager
import com.d10ng.compose.ui.AppColor
import com.d10ng.compose.ui.base.Cell
import com.d10ng.compose.ui.base.CellArrowDirection
import com.d10ng.compose.ui.base.CellGroup
import com.d10ng.compose.ui.dialog.builder.InputDialogBuilder
import com.d10ng.compose.ui.feedback.NotifyType
import com.d10ng.compose.ui.navigation.NavBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.launch

/**
 * 联系人管理器
 * @Author d10ng
 * @Date 2024/1/16 11:17
 */
@Destination<RootGraph>(style = PageTransitions::class)
@Composable
fun ContactManagerScreen() {
    ContactManagerScreenView()
}

@Composable
private fun ContactManagerScreenView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral.bg)
            .navigationBarsPadding()
    ) {
        NavBar(title = "联系人管理器", onClickBack = { back() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val scope = rememberCoroutineScope()
            CellGroup(title = "读取", inset = true) {
                // 是否展示联系人列表
                var isShow by remember {
                    mutableStateOf(false)
                }
                // 联系人列表
                var list by remember {
                    mutableStateOf(emptyList<ContactManager.Data>())
                }
                Cell(
                    title = "读取联系人",
                    link = true,
                    arrowDirection = if (isShow) CellArrowDirection.UP else CellArrowDirection.DOWN,
                    onClick = {
                        if (isShow) {
                            isShow = false
                        } else {
                            isShow = true
                            scope.launch {
                                if (PermissionManager.request(Manifest.permission.READ_CONTACTS)) {
                                    list = ContactManager.list()
                                } else {
                                    UiViewModelManager.showErrorNotify("权限申请失败")
                                }
                            }
                        }
                    }
                )
                if (isShow) {
                    list.forEach {
                        Cell(title = it.name, value = it.number)
                    }
                }
            }
            CellGroup(title = "选择", inset = true) {
                Cell(title = "选择联系人", link = true, onClick = {
                    scope.launch {
                        val data = ContactManager.pick()
                        if (data != null) {
                            UiViewModelManager.showNotify(
                                NotifyType.Success,
                                "选择成功：${data.name} ${data.number}"
                            )
                        } else {
                            UiViewModelManager.showErrorNotify("选择取消")
                        }
                    }
                })
            }
            CellGroup(title = "添加", inset = true) {
                Cell(title = "添加联系人", link = true, onClick = {
                    scope.launch {
                        if (PermissionManager.request(Manifest.permission.WRITE_CONTACTS)) {
                            UiViewModelManager.showDialog(InputDialogBuilder(
                                title = "添加联系人",
                                inputs = listOf(
                                    InputDialogBuilder.Input(
                                        placeholder = "请输入姓名",
                                        label = "姓名",
                                        verify = {
                                            it.isNotEmpty().let { res ->
                                                InputDialogBuilder.Verify(
                                                    res,
                                                    if (res) "" else "姓名不能为空"
                                                )
                                            }
                                        }
                                    ),
                                    InputDialogBuilder.Input(
                                        placeholder = "请输入手机号",
                                        label = "手机号",
                                        verify = {
                                            it.isMobileNumber().let { res ->
                                                InputDialogBuilder.Verify(
                                                    res,
                                                    if (res) "" else "手机号格式错误"
                                                )
                                            }
                                        }
                                    )
                                ),
                                onConfirmClick = {
                                    val result =
                                        ContactManager.add(ContactManager.Data(it[0], it[1]))
                                    if (result) {
                                        UiViewModelManager.showNotify(
                                            NotifyType.Success,
                                            "添加成功"
                                        )
                                    } else {
                                        UiViewModelManager.showErrorNotify("添加失败")
                                    }
                                    true
                                }
                            ))
                        } else {
                            UiViewModelManager.showErrorNotify("权限申请失败")
                        }
                    }
                })
                Cell(title = "添加或更新联系人", link = true, onClick = {
                    scope.launch {
                        if (PermissionManager.request(
                                arrayOf(
                                    Manifest.permission.WRITE_CONTACTS,
                                    Manifest.permission.READ_CONTACTS
                                )
                            )
                        ) {
                            UiViewModelManager.showDialog(InputDialogBuilder(
                                title = "添加或更新联系人",
                                inputs = listOf(
                                    InputDialogBuilder.Input(
                                        placeholder = "请输入姓名",
                                        label = "姓名",
                                        verify = {
                                            it.isNotEmpty().let { res ->
                                                InputDialogBuilder.Verify(
                                                    res,
                                                    if (res) "" else "姓名不能为空"
                                                )
                                            }
                                        }
                                    ),
                                    InputDialogBuilder.Input(
                                        placeholder = "请输入手机号",
                                        label = "手机号",
                                        verify = {
                                            it.isMobileNumber().let { res ->
                                                InputDialogBuilder.Verify(
                                                    res,
                                                    if (res) "" else "手机号格式错误"
                                                )
                                            }
                                        }
                                    )
                                ),
                                onConfirmClick = {
                                    val result = ContactManager.addOrUpdate(
                                        ContactManager.Data(
                                            it[0],
                                            it[1]
                                        )
                                    )
                                    if (result) {
                                        UiViewModelManager.showNotify(
                                            NotifyType.Success,
                                            "添加成功"
                                        )
                                    } else {
                                        UiViewModelManager.showErrorNotify("添加失败")
                                    }
                                    true
                                }
                            ))
                        } else {
                            UiViewModelManager.showErrorNotify("权限申请失败")
                        }
                    }
                })
            }
        }
    }
}

@Preview
@Composable
private fun ContactManagerScreenPreview() {
    ContactManagerScreenView()
}