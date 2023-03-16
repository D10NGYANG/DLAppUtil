package com.d10ng.app.status

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import java.util.*

/**
 * 键盘弹出和收回的监听⌚️
 * @Author: D10NG
 * @Time: 2021/1/6 9:51 上午
 */
class SoftKeyBroadManager constructor(
    private val activityRootView: View,
    isSoftKeyboardOpened: Boolean = false
) : OnGlobalLayoutListener {

    /** 接口 */
    interface SoftKeyboardStateListener {
        fun onSoftKeyboardOpened(keyboardHeightInPx: Int)
        fun onSoftKeyboardClosed()
    }

    /** 监听器 */
    private val listeners: MutableList<SoftKeyboardStateListener> = LinkedList<SoftKeyboardStateListener>()

    private var lastSoftKeyboardHeightInPx = 0
    private var isSoftKeyboardOpened: Boolean

    init {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        val r = Rect()
        activityRootView.getWindowVisibleDisplayFrame(r)
        val heightDiff: Int = activityRootView.rootView.height - (r.bottom - r.top)
        if (!isSoftKeyboardOpened && heightDiff > 500) {
            // 如果高度超过500 键盘可能被打开
            isSoftKeyboardOpened = true
            notifyOnSoftKeyboardOpened(heightDiff)
        } else if (isSoftKeyboardOpened && heightDiff < 500) {
            isSoftKeyboardOpened = false
            notifyOnSoftKeyboardClosed()
        }
    }

    /**
     * 添加监听器
     * @param listener SoftKeyboardStateListener
     */
    fun addSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        listeners.add(listener)
    }

    /**
     * 移除监听器
     * @param listener SoftKeyboardStateListener
     */
    fun removeSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        listeners.remove(listener)
    }

    /**
     * 通知输入法弹出
     * @param keyboardHeightInPx Int 屏幕位移PX
     */
    private fun notifyOnSoftKeyboardOpened(keyboardHeightInPx: Int) {
        lastSoftKeyboardHeightInPx = keyboardHeightInPx
        for (listener in listeners) {
            listener.onSoftKeyboardOpened(keyboardHeightInPx)
        }
    }

    /** 通知输入法收起 */
    private fun notifyOnSoftKeyboardClosed() {
        for (listener in listeners) {
            listener.onSoftKeyboardClosed()
        }
    }

    /** 移除监听 */
    fun destroy() {
        activityRootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }
}