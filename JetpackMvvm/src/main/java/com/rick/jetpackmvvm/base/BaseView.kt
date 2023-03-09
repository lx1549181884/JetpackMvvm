package com.rick.jetpackmvvm.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.ViewDataBinding
import com.rick.jetpackmvvm.util.BindingUtil.createBinding

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
        binding = createBinding(
            null,
            BaseView::class.java,
            0,
            LayoutInflater.from(context),
            this,
            true
        )
        init(context, attrs, binding)
    }

    abstract fun init(context: Context, attrs: AttributeSet?, binding: Binding)
}