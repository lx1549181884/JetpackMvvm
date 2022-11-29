package com.rick.jetpackmvvm.view

import android.content.Context
import android.graphics.Color
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

    private var color = Color.TRANSPARENT

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        this.color = color
        ActivityUtils.getActivityByContext(context)?.let {
            BarUtils.setStatusBarLightMode(
                it,
                color == Color.TRANSPARENT || getLight(color) > 0xFF * 0.6
            )
            refreshVisibility()
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

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l, t, r, b)
        refreshVisibility()
    }

    private fun refreshVisibility() {
        visibility = IntArray(2).apply { getLocationOnScreen(this) }[1].let {
            if (it == 0 && color != Color.TRANSPARENT) {
                VISIBLE
            } else {
                GONE
            }
        }
    }
}