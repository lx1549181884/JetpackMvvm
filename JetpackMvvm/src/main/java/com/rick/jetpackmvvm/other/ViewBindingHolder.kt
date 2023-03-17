package com.rick.jetpackmvvm.other

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * 使用 ViewBinding 的 Holder
 */
class ViewBindingHolder<T : ViewDataBinding>(var binding: T) : RecyclerView.ViewHolder(binding.root)