package com.rick.jetpackmvvm.commom

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ResourceUtils
import com.rick.jetpackmvvm.util.ViewUtil.addRecyclerViewDivider

/**
 * 通用的 BindingAdapter
 */
object CommonBindingAdapter {
    @JvmStatic
    @BindingAdapter("visible")
    fun visible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("selected")
    fun selected(view: View, selected: Boolean) {
        view.isSelected = selected
    }

    @JvmStatic
    @BindingAdapter("enable")
    fun enable(view: View, enable: Boolean) {
        view.isEnabled = enable
    }

    @BindingAdapter("backgroundColor")
    fun backgroundColor(view: View, @ColorInt bgColor: Int) {
        view.setBackgroundColor(bgColor)
    }

    @JvmStatic
    @BindingAdapter(value = ["onClick"], requireAll = false)
    fun onClick(view: View?, clickListener: View.OnClickListener?) {
        ClickUtils.applySingleDebouncing(view, clickListener)
    }

    @JvmStatic
    @SuppressLint("UseCompatLoadingForDrawables")
    @BindingAdapter("background")
    fun background(view: View, @DrawableRes drawable: Int) {
        view.background = view.context.getDrawable(drawable)
    }

    @JvmStatic
    @BindingAdapter("drawableStart")
    fun drawableStart(view: TextView, @DrawableRes drawable: Int) {
        val drawables = view.compoundDrawablesRelative
        drawables[0] = ResourceUtils.getDrawable(drawable)
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            drawables[0],
            drawables[1],
            drawables[2],
            drawables[3]
        )
    }

    /**
     * RecyclerView Adapter
     */
    @JvmStatic
    @BindingAdapter("recyclerViewAdapter")
    fun recyclerViewAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        view.adapter = adapter
    }

    /**
     * RecyclerView List
     */
    @JvmStatic
    @BindingAdapter("recyclerViewList")
    fun recyclerViewList(view: RecyclerView, list: List<Nothing>?) {
        val adapter = view.adapter
        if (adapter is ListAdapter<*, *>) {
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * RecyclerView Divider
     */
    @JvmStatic
    @BindingAdapter(value = ["recyclerViewDividerColor", "recyclerViewDividerSpace"])
    fun recyclerViewDivider(
        view: RecyclerView?,
        @ColorInt color: Int,
        @Dimension(unit = Dimension.DP) space: Int,
    ) {
        addRecyclerViewDivider(view!!, color, space)
    }

    /**
     * 是否可见的密码
     */
    @JvmStatic
    @BindingAdapter("pwdVisible")
    fun pwdVisible(view: EditText, visible: Boolean) {
        val inputType = view.inputType // 原先的inputType
        val selection = view.selectionStart // 获取光标位置
        view.inputType =
            if (visible) inputType xor InputType.TYPE_TEXT_VARIATION_PASSWORD else inputType or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setSelection(selection) // 恢复光标位置
    }

    /**
     * 动画
     */
    @JvmStatic
    @BindingAdapter(value = ["anim", "enableAnim"], requireAll = false)
    fun anim(view: View, anim: Animation, enableAnim: Boolean = true) {
        if (enableAnim) {
            view.startAnimation(anim)
        } else {
            view.clearAnimation()
        }
    }
}