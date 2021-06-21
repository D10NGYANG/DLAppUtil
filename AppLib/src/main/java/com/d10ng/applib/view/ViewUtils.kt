package com.d10ng.applib.view

import android.content.res.Resources
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * 获取状态栏高度
 * @return Int
 */
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
fun View.fitStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        return
    }
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