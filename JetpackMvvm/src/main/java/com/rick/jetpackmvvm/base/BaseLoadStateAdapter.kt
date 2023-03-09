package com.rick.jetpackmvvm.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.rick.jetpackmvvm.commom.CommonViewHolder
import com.rick.jetpackmvvm.util.BindingUtil.createBinding

abstract class BaseLoadStateAdapter<T : ViewDataBinding>(private val retry: Runnable?) :
    LoadStateAdapter<CommonViewHolder<T>>() {
    override fun onBindViewHolder(holder: CommonViewHolder<T>, loadState: LoadState) {
        initItem(holder.binding, loadState)
        holder.binding.root.setOnClickListener { if (loadState is LoadState.Error) retry?.run() }
        holder.binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        CommonViewHolder<T>(createBinding(BaseLoadStateAdapter::class.java, 0, parent))

    protected abstract fun initItem(binding: T, loadState: LoadState)
}