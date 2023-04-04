package com.rick.jetpackmvvm.util

import androidx.annotation.IntRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rick.jetpackmvvm.util.TypeUtil.getClass

/**
 * ViewModel 工具
 */
object ViewModelUtil {

    /**
     * 获取 ViewModel
     */
    @JvmStatic
    fun <C : P, P : ViewModelStoreOwner, V : ViewModel> C.getViewModel(
        parent: Class<P>,
        @IntRange(from = 0) index: Int
    ): V = getViewModel(this, getClass(parent, index))

    /**
     * 获取 ViewModel
     */
    @JvmStatic
    fun <C : P, P : Any, V : ViewModel> C.getViewModel(
        owner: ViewModelStoreOwner,
        parent: Class<P>,
        @IntRange(from = 0) index: Int
    ): V = getViewModel(owner, getClass(parent, index))

    /**
     * 获取 ViewModel
     */
    @JvmStatic
    fun <V : ViewModel> getViewModel(owner: ViewModelStoreOwner, clazz: Class<V>): V =
        ViewModelProvider(owner)[clazz]
}