package com.d10ng.app.utils

import android.app.Application
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

/**
 * 封装Handler子类
 * > 解决handler内存泄漏问题
 *
 * @author D10NG
 * @date on 2019-09-28 11:11
 */

interface BaseHandlerCallBack {
    fun callBack(msg: Message)
}

class BaseHandler(c: AppCompatActivity, b: BaseHandlerCallBack) : Handler(c.mainLooper) {

    private val act: WeakReference<AppCompatActivity> = WeakReference(c)
    private val callBack: WeakReference<BaseHandlerCallBack> = WeakReference(b)

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val c = act.get()
        if (c != null) {
            callBack.get()?.callBack(msg)
        }
    }
}

class BaseApplicationHandler(c: Application, b: BaseHandlerCallBack) : Handler(c.mainLooper) {

    private val act: WeakReference<Application> = WeakReference(c)
    private val callBack: WeakReference<BaseHandlerCallBack> = WeakReference(b)

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val c = act.get()
        if (c != null) {
            callBack.get()?.callBack(msg)
        }
    }
}

class BaseFragmentHandler(f: Fragment, b: BaseHandlerCallBack) : Handler() {

    private val fragment: WeakReference<Fragment> = WeakReference(f)
    private val callBack: WeakReference<BaseHandlerCallBack> = WeakReference(b)

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val c = fragment.get()
        if (c != null) {
            callBack.get()?.callBack(msg)
        }
    }
}