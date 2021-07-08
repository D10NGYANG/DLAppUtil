package com.d10ng.dlapputil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.d10ng.applib.system.getSimInfoList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(getSimInfoList().toString())
    }
}