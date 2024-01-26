# ActivityUIUtils

活动界面工具类

## ① 锁定屏幕方向

```kotlin
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
fun Activity.lockScreenOrientation(isVertical: Boolean = true)
```

```kotlin
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 锁定屏幕方向
        lockScreenOrientation()
    }
}
```

## ② 状态栏设置

```kotlin
/**
 * 状态栏设置
 * @receiver Activity
 * @param fullScreen Boolean 是否全屏，沉浸式状态栏
 * @param color Int 状态栏颜色
 * @param darkText Boolean 状态栏字体颜色是否为黑色
 */
fun Activity.setStatusBar(fullScreen: Boolean = true, color: Int = 0, darkText: Boolean = true)
```

```kotlin
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置状态栏颜色透明
        setStatusBar()
    }
}
```