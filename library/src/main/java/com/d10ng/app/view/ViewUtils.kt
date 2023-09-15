package com.d10ng.app.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * 获取状态栏高度
 * @return Int
 */
@SuppressLint("InternalInsetResource", "DiscouragedApi")
@Deprecated("jetpack compose已经存在更合适的方法，当前方法已弃用")
fun getStatusBarHeight(): Int {
    val resources = Resources.getSystem()
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * 解决RecyclerView与SwipeRefreshLayout下拉动作的冲突
 * @param srLayout SwipeRefreshLayout
 */
fun RecyclerView.fixSwipeRefreshClash(srLayout: SwipeRefreshLayout) {
    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            // 当前能完全显示的第一个item的位置
            val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            // 需要去到顶部才能支持下拉刷新
            srLayout.isEnabled = firstCompletelyVisibleItemPosition <= 0
            if (firstCompletelyVisibleItemPosition > 0) srLayout.isRefreshing = false
        }
    }
    this.addOnScrollListener(scrollListener)
}

/**
 * 将顶部view增加高度为状态栏高度的paddingTop
 * @receiver View
 */
@Deprecated("jetpack compose已经存在更合适的方法，当前方法已弃用")
fun View.fitStatusBar() {
    this.post {
        this.layoutParams.height = this.height + getStatusBarHeight()
        this.setPadding(
            this.paddingLeft,
            this.paddingTop + getStatusBarHeight(),
            this.paddingRight,
            this.paddingBottom
        )
    }
}

/**
 * (x,y)是否在view的区域内
 * @receiver View
 * @param x Int
 * @param y Int
 * @return Boolean
 */
fun View.isInView(x: Int, y: Int): Boolean {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    val left = location[0]
    val top = location[1]
    val right = left + this.measuredWidth
    val bottom = top + this.measuredHeight

    return y in top..bottom && x >= left && x <= right
}

/**
 * 隐藏软键盘
 * @param view View
 * @return [Boolean] true:成功; false:失败;
 */
@Deprecated("jetpack compose已经存在更合适的方法，当前方法已弃用")
fun Context.hideKeyboard(view: View): Boolean {
    val im = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return im.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}