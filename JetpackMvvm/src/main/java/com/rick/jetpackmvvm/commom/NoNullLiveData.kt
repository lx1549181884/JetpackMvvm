package com.rick.jetpackmvvm.commom

import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData

class NoNullLiveData<T>(private val defaultValue: NoNullValue<T>) :
    MutableLiveData<T>(defaultValue.get()) {
    override fun setValue(value: T?) = super.setValue(value ?: defaultValue.get())

    @NonNull
    override fun getValue(): T = super.getValue() ?: defaultValue.get()
}