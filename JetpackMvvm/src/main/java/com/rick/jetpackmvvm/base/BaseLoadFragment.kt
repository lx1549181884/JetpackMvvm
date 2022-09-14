package com.rick.jetpackmvvm.base

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.rick.jetpackmvvm.commom.NoNullLiveData
import com.rick.jetpackmvvm.commom.NoNullValue

abstract class BaseLoadFragment<Binding : ViewDataBinding, Vm : ViewModel, Response> :
    BaseFragment<Binding, Vm>() {
    class LoadViewModel : ViewModel() {
        var loadState = NoNullLiveData(object : NoNullValue<LoadState> {
            override fun get(): LoadState {
                return LoadState.LOADING
            }
        })
    }

    enum class LoadState {
        LOADING, SUCCESS, FAIL
    }

    protected val loadViewModel: LoadViewModel by viewModels()
    override fun init(binding: Binding, viewModel: Vm) {
        init(binding, viewModel, loadViewModel)
        if (!(loadViewModel.loadState.value == LoadState.SUCCESS && onlyOnceLoadSuccess())) {
            load()
        }
    }

    protected abstract fun load()

    protected open fun onlyOnceLoadSuccess() = false

    protected abstract fun init(binding: Binding, viewModel: Vm, loadViewModel: LoadViewModel)
}