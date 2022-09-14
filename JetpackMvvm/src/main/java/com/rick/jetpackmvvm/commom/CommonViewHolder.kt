package com.rick.jetpackmvvm.commom

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class CommonViewHolder<T : ViewDataBinding>(var binding: T) : RecyclerView.ViewHolder(binding.root)