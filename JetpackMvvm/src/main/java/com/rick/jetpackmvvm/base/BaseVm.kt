package com.rick.jetpackmvvm.base

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rick.jetpackmvvm.commom.NoNullLiveData
import com.rick.jetpackmvvm.commom.NoNullValue

open class BaseVm : ViewModel() {
    enum class LoadState {
        LOADING, SUCCESS, FAIL
    }

    val statusBarBgColor = NoNullLiveData(object : NoNullValue<Int> {
        override fun get(): Int {
            return Color.TRANSPARENT
        }
    })

    val statusBarContentColor = MutableLiveData<Boolean>() // true黑 false白 null自动

    val statusBarVisible = NoNullLiveData(object : NoNullValue<Boolean> {
        override fun get(): Boolean {
            return true
        }
    })

    val navBarVisible = NoNullLiveData(object : NoNullValue<Boolean> {
        override fun get(): Boolean {
            return true
        }
    })

    val loadState = NoNullLiveData(object : NoNullValue<LoadState> {
        override fun get(): LoadState {
            return LoadState.LOADING
        }
    })
}