package com.d10ng.dlapputil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.d10ng.app.app.reqPermissions
import com.d10ng.app.system.DatastoreUtils
import com.d10ng.app.system.NetUtils
import com.d10ng.app.system.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化
        DatastoreUtils.init("new_datastore", SP_CONFIG)

        // 写入 SPF
        setSpfSaveName("你好，SPF")
        println("SPF 打印：${getSpfSaveName()}")
        // 迁移到 DataStore
        CoroutineScope(Dispatchers.IO).launch {
            // 读取
            println("dataStore 打印：${readSaveName()}")
            // 写入 datastore
            writeSaveName("你好，dataStore")
            // 再次读取
            println("dataStore 打印：${readSaveName()}")
        }

        println("isNetworkAvailable = ${isNetworkAvailable()}")

        CoroutineScope(Dispatchers.IO).launch {
            NetUtils.instant(this@MainActivity).networkCapabilitiesFlow.collect {
                println("isNetworkAvailable = ${isNetworkAvailable()}")
            }
        }

        // 请求权限
        val result = reqPermissions(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)
        lifecycleScope.launch {
            result.collect {
                println("权限请求结果：$it")
            }
        }
    }
}