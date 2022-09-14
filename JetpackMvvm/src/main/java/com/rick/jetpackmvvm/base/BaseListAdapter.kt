package com.rick.jetpackmvvm.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.rick.jetpackmvvm.commom.CommonViewHolder
import com.rick.jetpackmvvm.util.BindingUtil

abstract class BaseListAdapter<Bean : Any, Binding : ViewDataBinding>(diffCallback: DiffUtil.ItemCallback<Bean>) :
    ListAdapter<Bean, CommonViewHolder<Binding>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommonViewHolder<Binding>(BindingUtil.createBinding(parent,
            this,
            BaseListAdapter::class.java,
            1))

    override fun onBindViewHolder(holder: CommonViewHolder<Binding>, position: Int) {
        initItem(position, getItem(position), holder.binding)
        holder.binding.executePendingBindings()
    }

    protected abstract fun initItem(position: Int, bean: Bean, binding: Binding)
}