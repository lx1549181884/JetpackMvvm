package com.rick.jetpackmvvm.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.BarUtils

/**
 * 虚拟状态栏
 */
class VirtualStatusBar : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.height = BarUtils.getStatusBarHeight()
        super.setLayoutParams(params)
    }
}