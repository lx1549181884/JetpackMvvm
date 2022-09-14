package com.rick.jetpackmvvm.commom

import androidx.recyclerview.widget.DiffUtil

class CommonDiffCallback<T : Diffable> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.diffKey == newItem.diffKey
    override fun areContentsTheSame(oldItem: T, newItem: T) = false
}