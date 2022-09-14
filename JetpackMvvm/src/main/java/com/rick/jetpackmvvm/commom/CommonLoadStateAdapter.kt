package com.rick.jetpackmvvm.commom

import androidx.paging.LoadState
import com.rick.jetpackmvvm.base.BaseLoadStateAdapter
import com.rick.jetpackmvvm.databinding.LoadStateBinding

class CommonLoadStateAdapter(retry: Runnable?) : BaseLoadStateAdapter<LoadStateBinding>(retry) {
    override fun initItem(binding: LoadStateBinding, loadState: LoadState) {
        when (loadState) {
            is LoadState.Error -> {
                binding.tv.text = "加载错误，点击重试"
            }
            is LoadState.Loading -> {
                binding.tv.text = "加载中..."
            }
            else -> {
                binding.tv.text = ""
            }
        }
    }
}