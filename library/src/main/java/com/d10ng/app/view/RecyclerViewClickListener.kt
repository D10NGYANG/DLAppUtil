package com.d10ng.app.view

import android.view.View

/**
 * 列表点击接口
 *
 * @author D10NG
 * @date on 2020/8/19 8:32 AM
 */
interface RecyclerViewClickListener {
    /**
     * 点击控件
     * @param view 控件
     * @param position 列表位置
     * @param data 携带数据
     */
    fun click(view: View, position: Int, data: Any?)

    /**
     * 点击控件
     * @param id 控件ID或单纯的标记
     * @param position 列表位置
     * @param data 携带数据
     */
    fun click(id: Int, position: Int, data: Any?)
}

class RecyclerViewClick: RecyclerViewClickListener {

    private lateinit var listener: (view: View, position: Int, data: Any?) -> Unit

    fun onClick(listener: (view: View, position: Int, data: Any?) -> Unit) {
        this.listener = listener
    }

    override fun click(view: View, position: Int, data: Any?) {
        this.listener.invoke(view, position, data)
    }

    private lateinit var listener2: (id: Int, position: Int, data: Any?) -> Unit

    fun onClickId(listener: (id: Int, position: Int, data: Any?) -> Unit) {
        this.listener2 = listener
    }

    override fun click(id: Int, position: Int, data: Any?) {
        this.listener2.invoke(id, position, data)
    }
}