package com.rick.jetpackmvvm.util

import androidx.annotation.IntRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.rick.jetpackmvvm.util.GenericUtil.getActualType

/**
 * ViewModel 工具
 */
object ViewModelUtil {
    @JvmStatic
    fun <C : P, P : Any, V : ViewModel> getViewModel(
        owner: ViewModelStoreOwner,
        child: C,
        parent: Class<P>,
        @IntRange(from = 0) index: Int,
    ): V = getViewModel(owner, getActualType(child, parent, index))

    @JvmStatic
    fun <V : ViewModel> getViewModel(owner: ViewModelStoreOwner, clazz: Class<V>): V =
        ViewModelProvider(owner)[clazz]
}