package com.rick.jetpackmvvm.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.rick.jetpackmvvm.util.LightModelUtil

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

    private var color = Color.WHITE

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        this.color = color
        setLightModel()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setLightModel()
        visibility = visibility
    }

    private fun setLightModel() {
        ActivityUtils.getActivityByContext(context)?.let {
            LightModelUtil.setLightModel(it, color)
        }
    }

    override fun setVisibility(visibility: Int) {
        val top = intArrayOf(0, 0).apply { getLocationInWindow(this) }[1]
        super.setVisibility(if (top > 0) GONE else visibility)
    }
}