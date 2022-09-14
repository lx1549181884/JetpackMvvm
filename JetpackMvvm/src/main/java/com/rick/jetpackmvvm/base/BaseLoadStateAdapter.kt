package com.rick.jetpackmvvm.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.rick.jetpackmvvm.commom.CommonViewHolder
import com.rick.jetpackmvvm.util.BindingUtil

abstract class BaseLoadStateAdapter<T : ViewDataBinding>(private val retry: Runnable?) :
    LoadStateAdapter<CommonViewHolder<T>>() {
    override fun onBindViewHolder(holder: CommonViewHolder<T>, loadState: LoadState) {
        initItem(holder.binding, loadState)
        holder.binding.root.setOnClickListener { if (loadState is LoadState.Error) retry?.run() }
        holder.binding.executePendingBindings()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        loadState: LoadState,
    ) = CommonViewHolder<T>(BindingUtil.createBinding(viewGroup,
        this,
        BaseLoadStateAdapter::class.java,
        0))

    protected abstract fun initItem(binding: T, loadState: LoadState)
}