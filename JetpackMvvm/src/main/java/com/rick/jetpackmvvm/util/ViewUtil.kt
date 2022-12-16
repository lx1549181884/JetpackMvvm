package com.rick.jetpackmvvm.util

import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils

/**
 * 控件工具
 */
object ViewUtil {

    @JvmStatic
    fun addRecyclerViewDivider(
        recyclerView: RecyclerView,
        orientation: Int,
        @ColorInt color: Int,
        @Dimension(unit = Dimension.DP) spaceDp: Int,
    ) {
        val decoration = DividerItemDecoration(recyclerView.context, orientation)
        val drawable = GradientDrawable()
        drawable.setColor(color)
        val spacePx = ConvertUtils.dp2px(spaceDp.toFloat())
        drawable.setSize(spacePx, spacePx)
        decoration.setDrawable(drawable)
        recyclerView.addItemDecoration(decoration)
    }
}