package com.rick.jetpackmvvm.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.rick.jetpackmvvm.other.ViewBindingHolder
import com.rick.jetpackmvvm.util.BindingUtil.createBinding

/**
 * ListAdapter 基类
 */
abstract class BaseListAdapter<Bean : Any, Binding : ViewDataBinding>(diffCallback: DiffUtil.ItemCallback<Bean>?) :
    ListAdapter<Bean, ViewBindingHolder<Binding>>(diffCallback ?: object :
        DiffUtil.ItemCallback<Bean>() {
        // 默认 DiffUtil.ItemCallback
        override fun areItemsTheSame(oldItem: Bean, newItem: Bean) = false

        override fun areContentsTheSame(oldItem: Bean, newItem: Bean) = false
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        // 创建 ViewBindingHolder
        ViewBindingHolder<Binding>(createBinding(BaseListAdapter::class.java, 1, parent))

    override fun onBindViewHolder(holder: ViewBindingHolder<Binding>, position: Int) {
        // 初始化条目
        initItem(position, getItem(position), holder.binding)
        // 即时绑定
        holder.binding.executePendingBindings()
    }

    /**
     * 初始化条目
     */
    protected abstract fun initItem(position: Int, bean: Bean, binding: Binding)
}