package com.rick.jetpackmvvm.base

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.rick.jetpackmvvm.other.NoNullLiveData

/**
 * ViewModel 基类
 */
open class BaseViewModel : ViewModel() {
    // 状态栏颜色
    val statusBarColor = NoNullLiveData(Color.TRANSPARENT)

    // 显示状态栏
    val statusBarVisible = NoNullLiveData(true)

    // 显示导航栏
    val navBarVisible = NoNullLiveData(true)
}