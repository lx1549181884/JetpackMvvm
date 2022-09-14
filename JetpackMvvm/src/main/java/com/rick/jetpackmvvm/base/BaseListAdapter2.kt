package com.rick.jetpackmvvm.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.rick.jetpackmvvm.commom.Diffable

abstract class BaseListAdapter2<Bean : Diffable, Binding : ViewDataBinding> :
    BaseListAdapter<Bean, Binding>(object : DiffUtil.ItemCallback<Bean>() {
        override fun areItemsTheSame(oldItem: Bean, newItem: Bean) =
            oldItem.diffKey == newItem.diffKey

        override fun areContentsTheSame(oldItem: Bean, newItem: Bean) = false
    })