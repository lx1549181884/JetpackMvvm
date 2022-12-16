package com.rick.jetpackmvvm.base

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.rick.jetpackmvvm.commom.NoNullLiveData
import com.rick.jetpackmvvm.commom.NoNullValue

open class BaseVm : ViewModel() {
    enum class LoadState {
        LOADING, SUCCESS, FAIL
    }

    val statusBarColor = NoNullLiveData(object : NoNullValue<Int> {
        override fun get(): Int {
            return Color.TRANSPARENT
        }
    })

    val loadState = NoNullLiveData(object : NoNullValue<LoadState> {
        override fun get(): LoadState {
            return LoadState.LOADING
        }
    })
}