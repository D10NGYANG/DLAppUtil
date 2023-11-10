package com.d10ng.dlapputil

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.d10ng.app.managers.ContactManager
import com.d10ng.app.managers.PermissionManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 初始化
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            lifecycleScope.launch {
                val res = ContactManager.pick()
                println("选择的联系人：$res")
            }
        }

        // 请求权限
        lifecycleScope.launch {
            val result = PermissionManager.request(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            println("权限请求结果：$result")

        }
    }
}