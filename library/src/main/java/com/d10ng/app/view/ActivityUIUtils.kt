package com.d10ng.app.view

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.core.view.WindowCompat

/**
 * 锁定屏幕方向
 * - 除了在Activity中设置当前方法以外还需要，在主题中设置以下内容
 * <resources>
 *     <style name="AppTheme" parent="Theme.MaterialComponents.Light.NoActionBar">
 *         <!-- 锁定布局在发生以下改变时，不重置状态 -->
 *         <item name="android:configChanges">orientation|keyboardHidden|screenSize|locale</item>
 *     </style>
 * </resources>
 * - 还需要在AndroidManifest.xml中为您的activity设置以下内容
 * <activity
 *     android:name=".XActivity"
 *     android:screenOrientation="locked" />
 * @receiver Activity
 * @param isVertical Boolean 是否为竖向
 */
fun Activity.lockScreenOrientation(isVertical: Boolean = true) {
    val orientation = if (isVertical) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    if (requestedOrientation != orientation) {
        requestedOrientation = orientation
    }
}

/**
 * 状态栏设置
 * @receiver Activity
 * @param fullScreen Boolean 是否全屏，沉浸式状态栏
 * @param color Int 状态栏颜色
 * @param darkText Boolean 状态栏字体颜色是否为黑色
 */
fun Activity.setStatusBar(fullScreen: Boolean = true, color: Int = 0, darkText: Boolean = true) {
    // 沉浸式状态栏
    WindowCompat.setDecorFitsSystemWindows(window, !fullScreen)
    // 设置状态栏颜色
    window.statusBarColor = color
    // 设置状态栏字体颜色
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
        darkText
}