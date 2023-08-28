package com.rick.jetpackmvvm.other

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.refresh(action: ((t: T?) -> Unit)) {
    action(value)
    value = value
}