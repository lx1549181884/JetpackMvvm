package com.rick.jetpackmvvm.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.liveData
import com.rick.jetpackmvvm.commom.CommonLoadStateAdapter
import com.rick.jetpackmvvm.commom.Diffable
import com.rick.jetpackmvvm.databinding.FragmentPagingBinding

abstract class BasePagingFragment<Bean : Diffable, ItemBinding : ViewDataBinding, Vm : ViewModel> :
    BaseFragment<FragmentPagingBinding, Vm>() {
    protected var adapter: PagingAdapter? = null
    override fun init(binding: FragmentPagingBinding, viewModel: Vm) {
        adapter = PagingAdapter().apply {
            binding.refresh.setOnRefreshListener { refresh() }
            addLoadStateListener { binding.refresh.isRefreshing = it.refresh is LoadState.Loading }
            binding.adapter = withLoadStateFooter(CommonLoadStateAdapter(this::retry))
            pager.liveData.observe(viewLifecycleOwner) { submitData(lifecycle, it) }
        }
    }

    inner class PagingAdapter : BasePagingAdapter<Bean, ItemBinding>() {
        override fun initItem(binding: ItemBinding, bean: Bean, position: Int) {
            this@BasePagingFragment.initItem(binding, bean, position)
        }
    }

    fun refresh() {
        adapter?.refresh()
    }

    protected abstract fun initItem(binding: ItemBinding, bean: Bean, position: Int)
    protected abstract val pager: Pager<Int, Bean>
}