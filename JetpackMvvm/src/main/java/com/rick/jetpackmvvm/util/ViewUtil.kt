package com.rick.jetpackmvvm.util

import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils

/**
 * 控件工具
 */
object ViewUtil {
    /**
     * 给 RecyclerView 添加 Divider
     */
    @JvmStatic
    fun addRecyclerViewDivider(
        recyclerView: RecyclerView,
        @ColorInt color: Int,
        @Dimension(unit = Dimension.DP) space: Int,
    ) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val orientation = layoutManager.orientation
            val decoration = DividerItemDecoration(recyclerView.context, orientation)
            val drawable = GradientDrawable()
            drawable.setColor(color)
            val spacePx = ConvertUtils.dp2px(space.toFloat())
            drawable.setSize(spacePx, spacePx)
            decoration.setDrawable(drawable)
            recyclerView.addItemDecoration(decoration)
        }
    }
}