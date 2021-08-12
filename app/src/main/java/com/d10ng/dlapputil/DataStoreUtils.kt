package com.d10ng.dlapputil

import android.content.Context
import com.d10ng.applib.system.dataStoreRead
import com.d10ng.applib.system.dataStoreWrite

suspend fun Context.readSaveName(): String {
    return dataStoreRead(SPF_SAVE_NAME)
}

suspend fun Context.writeSaveName(value: String) {
    dataStoreWrite(SPF_SAVE_NAME, value)
}