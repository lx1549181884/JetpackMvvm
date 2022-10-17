package com.rick.jetpackmvvm.base

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseVm : ViewModel() {
    val statusBarColor = MutableLiveData(Color.TRANSPARENT)
}