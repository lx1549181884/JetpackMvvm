package com.rick.jetpackmvvm.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.rick.jetpackmvvm.util.BindingUtil
import com.rick.jetpackmvvm.util.ViewModelUtil

abstract class BaseActivity<Binding : ViewDataBinding, Vm : ViewModel> : AppCompatActivity() {
    protected lateinit var binding: Binding
    protected lateinit var viewModel: Vm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.transparentStatusBar(this)
        binding = BindingUtil.createBinding(this, BaseActivity::class.java, 0)
        viewModel = ViewModelUtil.getViewModel(this, this, BaseActivity::class.java, 1)
        setContentView(binding.root)
        init(binding, viewModel)
        KeyboardUtils.fixAndroidBug5497(this)
    }

    protected abstract fun init(binding: Binding, vm: Vm)
}