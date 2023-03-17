package com.rick.jetpackmvvm.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.ViewDataBinding
import com.rick.jetpackmvvm.util.BindingUtil.createBinding

/**
 * 自定义 View 基类
 */
open abstract class BaseView<Binding : ViewDataBinding> : FrameLayout {

    protected lateinit var binding: Binding

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
        // 创建 ViewDataBinding
        binding = createBinding(
            null,
            BaseView::class.java,
            0,
            LayoutInflater.from(context),
            this,
            true
        )
        // 初始化
        init(context, attrs, binding)
    }

    /**
     * 初始化
     */
    abstract fun init(context: Context, attrs: AttributeSet?, binding: Binding)
}