package com.d10ng.dlapputil

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.d10ng.applib.app.checkPermissionWithBool
import com.d10ng.applib.system.getSimInfoList
import com.d10ng.applib.system.getSubscriptionInfoList
import com.d10ng.applib.system.toSimInfo
import com.d10ng.applib.system.toSimInfoList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
            if (checkPermissionWithBool(Manifest.permission.READ_PHONE_STATE)) {
                if (getSubscriptionInfoList().isNotEmpty()) {
                    println("test-0:${getSubscriptionInfoList()[0].toSimInfo()}")
                    println("test-1:${getSubscriptionInfoList().toSimInfoList()[0]}")
                }
            }
        }
    }
}