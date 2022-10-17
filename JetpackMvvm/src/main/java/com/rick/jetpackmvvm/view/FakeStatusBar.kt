package com.rick.jetpackmvvm.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils

class FakeStatusBar : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.height = BarUtils.getStatusBarHeight()
        super.setLayoutParams(params)
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        ActivityUtils.getTopActivity()?.let {
            BarUtils.setStatusBarLightMode(it, getLight(color) > 0xFF * 0.7)
        }
    }

    private fun getLight(@ColorInt color: Int): Double {
        val colorLong =
            if (color >= 0) color.toLong() else Int.MAX_VALUE + 1L + color - Int.MIN_VALUE
        val r = colorLong / 0x10000L and 0xFF
        val g = colorLong / 0x100L and 0xFF
        val b = colorLong and 0xFF
        return r * 0.3 + g * 0.59 + b * 0.11
    }
}