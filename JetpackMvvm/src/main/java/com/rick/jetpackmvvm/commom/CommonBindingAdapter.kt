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
import com.rick.jetpackmvvm.commom.CommonExtend.getTextWatchers
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

    @JvmStatic
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
    @BindingAdapter(
        value = ["drawableStart", "drawableTop", "drawableEnd", "drawableBottom"],
        requireAll = false
    )
    fun drawableStart(
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
    fun anim(view: View, anim: Animation, enableAnim: Boolean = true) {
        if (enableAnim) {
            view.startAnimation(anim)
        } else {
            view.clearAnimation()
        }
    }

    /**
     * EditText 正小数
     * @param places 小数位数
     */
    @JvmStatic
    @BindingAdapter("numberDecimal")
    fun numberDecimal(view: EditText, places: Int) {
        view.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        view.getTextWatchers()?.iterator()?.let { iterator ->
            while (iterator.hasNext()) {
                iterator.next().apply {
                    if (this is NumDecTextWatcher) {
                        iterator.remove()
                    }
                }
            }
        }
        view.addTextChangedListener(NumDecTextWatcher(view, places))
    }
}