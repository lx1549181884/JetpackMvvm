package com.rick.jetpackmvvm.other

import androidx.lifecycle.MutableLiveData

fun MutableLiveData<*>.refresh() {
    value = value
}