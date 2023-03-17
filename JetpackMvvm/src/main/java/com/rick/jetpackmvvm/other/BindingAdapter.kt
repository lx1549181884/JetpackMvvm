package com.rick.jetpackmvvm.other

import android.text.InputType
import android.view.View
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ResourceUtils

/**
 * 常用的 BindingAdapter
 */
object BindingAdapter {

    /**
     * 是否显示
     */
    @JvmStatic
    @BindingAdapter("visible")
    fun visible(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * 是否选中
     */
    @JvmStatic
    @BindingAdapter("selected")
    fun selected(view: View, selected: Boolean) {
        view.isSelected = selected
    }

    /**
     * 是否可用
     */
    @JvmStatic
    @BindingAdapter("enable")
    fun enable(view: View, enable: Boolean) {
        view.isEnabled = enable
    }

    /**
     * 背景颜色
     */
    @JvmStatic
    @BindingAdapter("backgroundColor")
    fun backgroundColor(view: View, @ColorInt bgColor: Int) {
        view.setBackgroundColor(bgColor)
    }

    /**
     * 背景
     */
    @JvmStatic
    @BindingAdapter("background")
    fun background(view: View, @DrawableRes drawable: Int) {
        view.background = AppCompatResources.getDrawable(view.context, drawable)
    }

    /**
     * 周围图片
     */
    @JvmStatic
    @BindingAdapter(
        value = ["drawableStart", "drawableTop", "drawableEnd", "drawableBottom"],
        requireAll = false
    )
    fun drawable(
        view: TextView,
        @DrawableRes drawableStart: Int?,
        @DrawableRes drawableTop: Int?,
        @DrawableRes drawableEnd: Int?,
        @DrawableRes drawableBottom: Int?
    ) {
        val drawables = view.compoundDrawablesRelative
        drawableStart?.let { drawables[0] = ResourceUtils.getDrawable(it) }
        drawableTop?.let { drawables[1] = ResourceUtils.getDrawable(it) }
        drawableEnd?.let { drawables[2] = ResourceUtils.getDrawable(it) }
        drawableBottom?.let { drawables[3] = ResourceUtils.getDrawable(it) }
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            drawables[0],
            drawables[1],
            drawables[2],
            drawables[3]
        )
    }

    /**
     * List 和 Adapter
     */
    @JvmStatic
    @BindingAdapter(value = ["listAdapter", "list"], requireAll = false)
    fun listAdapter(
        view: RecyclerView,
        adapter: ListAdapter<*, *>?,
        list: List<Nothing>?
    ) {
        view.adapter = adapter
        adapter?.let {
            it.submitList(list)
            it.notifyDataSetChanged()
        }
    }

    /**
     * 是否可见的密码
     */
    @JvmStatic
    @BindingAdapter("pwdVisible")
    fun pwdVisible(view: EditText, visible: Boolean) {
        val selection = view.selectionStart // 获取光标位置
        view.inputType =
            InputType.TYPE_CLASS_TEXT or (if (visible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_TEXT_VARIATION_PASSWORD)
        view.setSelection(selection) // 恢复光标位置
    }

    /**
     * 动画
     */
    @JvmStatic
    @BindingAdapter(value = ["anim", "enableAnim"], requireAll = false)
    fun anim(view: View, anim: Animation? = null, enableAnim: Boolean = true) {
        if (anim != null && enableAnim) {
            view.startAnimation(anim)
        } else {
            view.clearAnimation()
        }
    }
}