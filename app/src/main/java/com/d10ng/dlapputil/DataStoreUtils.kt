package com.d10ng.dlapputil

import android.content.Context
import com.d10ng.applib.system.dataStoreRead
import com.d10ng.applib.system.dataStoreWrite
import com.d10ng.text.string.decrypt
import com.d10ng.text.string.encrypt

suspend fun Context.readSaveName(): String {
    return dataStoreRead<String>(SPF_SAVE_NAME).decrypt()
}

suspend fun Context.writeSaveName(value: String) {
    dataStoreWrite(SPF_SAVE_NAME, value.encrypt())
}