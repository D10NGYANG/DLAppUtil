package com.d10ng.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.provider.Settings
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.d10ng.app.managers.ActivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

/**
 * 手持机机身按键监听服务
 * @Author d10ng
 * @Date 2023/11/10 14:27
 */
class PhysicalButtonAccessibilityService : AccessibilityService() {
    companion object {
        var instant: WeakReference<PhysicalButtonAccessibilityService?> = WeakReference(null)

        /**
         * 是否开启了无障碍服务
         * @return Boolean
         */
        fun isEnable(): Boolean {
            return instant.get() != null
        }

        /**
         * 开启无障碍服务
         */
        fun start() {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            ActivityManager.top().value?.startActivity(intent)
        }

        // 长按时间
        private const val LONG_PRESS_TIME = 2500L

        // 单击按键事件
        private val clickEvent = MutableSharedFlow<Int>()

        // 长按按键事件
        private val longPressEvent = MutableSharedFlow<Int>()

        // 按键开始按的时间
        private val downTimeMap = mutableMapOf<Int, Long>()

        private fun onKeyDown(keyCode: Int) {
            downTimeMap[keyCode] = System.currentTimeMillis()
            CoroutineScope(Dispatchers.IO).launch {
                delay(LONG_PRESS_TIME)
                val downTime = downTimeMap[keyCode]
                if (downTime != null && System.currentTimeMillis() - downTime >= LONG_PRESS_TIME) {
                    downTimeMap.remove(keyCode)
                    longPressEvent.emit(keyCode)
                }
            }
        }

        private fun onKeyUp(keyCode: Int) {
            val downTime = downTimeMap[keyCode]
            if (downTime != null) {
                downTimeMap.remove(keyCode)
                runBlocking { clickEvent.emit(keyCode) }
            }
        }

        fun getClickEvent() = clickEvent.asSharedFlow()
        fun getLongPressEvent() = longPressEvent.asSharedFlow()
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        event ?: return super.onKeyEvent(null)
        when (event.action) {
            KeyEvent.ACTION_DOWN -> onKeyDown(event.keyCode)
            KeyEvent.ACTION_UP -> onKeyUp(event.keyCode)
        }
        return super.onKeyEvent(event)
    }

    override fun onCreate() {
        super.onCreate()
        instant = WeakReference(this)
    }

    override fun onDestroy() {
        instant = WeakReference(null)
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
    }
}