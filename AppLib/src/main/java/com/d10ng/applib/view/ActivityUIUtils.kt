package com.d10ng.applib.view

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * 设置透明状态栏
 * @receiver Activity
 */
fun Activity.makeStatusBarTransparent() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        return
    }
    val window: Window = this.window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val option: Int = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}

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
 * 设置状态栏字体颜色
 * @receiver Activity
 * @param isBlack Boolean true:黑色，false:白色
 */
fun Activity.setStatusBarFontColor(isBlack: Boolean = true) {
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    }*/
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.decorView.systemUiVisibility = if (isBlack)
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        else View.SYSTEM_UI_FLAG_VISIBLE
    }
}