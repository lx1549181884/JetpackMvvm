package com.rick.jetpackmvvm.commom

import androidx.paging.LoadState
import com.rick.jetpackmvvm.R
import com.rick.jetpackmvvm.base.BaseLoadStateAdapter
import com.rick.jetpackmvvm.databinding.LoadStateBinding

class CommonLoadStateAdapter(retry: Runnable?) : BaseLoadStateAdapter<LoadStateBinding>(retry) {
    override fun initItem(binding: LoadStateBinding, loadState: LoadState) {
        when (loadState) {
            is LoadState.Error -> {
                binding.tv.setText(R.string.load_error_click_retry)
            }
            is LoadState.Loading -> {
                binding.tv.setText(R.string.loading)
            }
            else -> {
                binding.tv.text = ""
            }
        }
    }
}