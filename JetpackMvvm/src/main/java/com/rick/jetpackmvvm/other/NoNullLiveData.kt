package com.rick.jetpackmvvm.other

import androidx.lifecycle.MutableLiveData

/**
 * 值不为 null 的 LiveData
 */
class NoNullLiveData<T : Any>(private val defValue: T) : MutableLiveData<T>(defValue) {
    override fun setValue(value: T?) = super.setValue(value ?: defValue)

    override fun getValue(): T = super.getValue() ?: defValue
}