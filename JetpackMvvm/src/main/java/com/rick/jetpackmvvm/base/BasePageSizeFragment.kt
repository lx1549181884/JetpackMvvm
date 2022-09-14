package com.rick.jetpackmvvm.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.rick.jetpackmvvm.commom.Diffable

abstract class BasePageSizeFragment<Bean : Diffable, ItemBinding : ViewDataBinding, Vm : ViewModel> :
    BasePagingFragment<Bean, ItemBinding, Vm>() {

    override val pager: Pager<Int, Bean>
        get() = Pager(PagingConfig(pageSize, 1, true, pageSize), null, pageSizeSource)

    protected abstract val pageSize: Int
    protected abstract val pageSizeSource: () -> PagingSource<Int, Bean>
}