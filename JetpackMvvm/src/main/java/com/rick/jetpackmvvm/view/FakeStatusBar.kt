package com.rick.jetpackmvvm.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.res.getBooleanOrThrow
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.rick.jetpackmvvm.R

class FakeStatusBar : View {

    private var bgColor = Color.TRANSPARENT
    private var contentColor: Boolean? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FakeStatusBar)
        try {
            setContentColor(typedArray.getBooleanOrThrow(R.styleable.FakeStatusBar_statusBarContentColor))
        } catch (_: Exception) {
        }
        typedArray.recycle()
        refreshContentColor()
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.height = BarUtils.getStatusBarHeight()
        super.setLayoutParams(params)
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        this.bgColor = color
        refreshContentColor()

    }

    fun setContentColor(isBlackOrWhiteOrAuto: Boolean?) {
        contentColor = isBlackOrWhiteOrAuto
        refreshContentColor()
    }

    private fun refreshContentColor() {
        ActivityUtils.getActivityByContext(context)?.let {
            val lightModel =
                contentColor ?: (bgColor == Color.TRANSPARENT || getLight(bgColor) > 0xFF * 0.6)
            BarUtils.setStatusBarLightMode(it, lightModel)
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

    companion object {
        @JvmStatic
        @BindingAdapter("statusBarContentColor")
        fun statusBarContentColor(fakeStatusBar: FakeStatusBar, isBlackOrWhite: Boolean?) {
            fakeStatusBar.setContentColor(isBlackOrWhite)
        }
    }
}