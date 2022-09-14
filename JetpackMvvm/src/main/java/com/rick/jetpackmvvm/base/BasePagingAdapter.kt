package com.rick.jetpackmvvm.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import com.rick.jetpackmvvm.commom.CommonDiffCallback
import com.rick.jetpackmvvm.commom.CommonViewHolder
import com.rick.jetpackmvvm.commom.Diffable
import com.rick.jetpackmvvm.util.BindingUtil

abstract class BasePagingAdapter<Bean : Diffable, Binding : ViewDataBinding> :
    PagingDataAdapter<Bean, CommonViewHolder<Binding>>(CommonDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommonViewHolder<Binding>(BindingUtil.createBinding(
            parent,
            this,
            BasePagingAdapter::class.java,
            1))

    override fun onBindViewHolder(holder: CommonViewHolder<Binding>, position: Int) {
        initItem(holder.binding, getItem(position)!!, position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun initItem(binding: Binding, bean: Bean, position: Int)
}