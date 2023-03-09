package com.rick.jetpackmvvm.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.rick.jetpackmvvm.util.BindingUtil.createBinding
import com.rick.jetpackmvvm.util.ViewModelUtil.getViewModel


abstract class BaseActivity<Binding : ViewDataBinding, Vm : ViewModel> : ComponentActivity() {
    protected lateinit var binding: Binding
    protected lateinit var viewModel: Vm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.transparentStatusBar(this)
        binding = createBinding(BaseActivity::class.java, 0)
        viewModel = getViewModel(BaseActivity::class.java, 1)
        setContentView(binding.root)
        init(binding, viewModel)
        KeyboardUtils.fixAndroidBug5497(this)
    }

    protected abstract fun init(binding: Binding, vm: Vm)
}