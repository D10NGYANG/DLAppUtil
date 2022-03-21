package com.d10ng.dlapputil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.d10ng.applib.system.DatastoreUtils
import com.d10ng.applib.system.NetUtils
import com.d10ng.applib.system.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

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

        CoroutineScope(Dispatchers.IO).launch {
            val manager = NetUtils.instant(this@MainActivity).manager
            NetUtils.instant(this@MainActivity).networkCapabilitiesFlow.collect {
                println("isNetworkAvailable = ${isNetworkAvailable()}")
            }
        }
    }
}