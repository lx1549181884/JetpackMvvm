package com.rick.jetpackmvvm.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.rick.jetpackmvvm.util.BindingUtil.createBinding
import com.rick.jetpackmvvm.util.LightModelUtil
import com.rick.jetpackmvvm.util.ViewModelUtil.getViewModel

/**
 * Activity 基类
 */
abstract class BaseActivity<B : ViewDataBinding, Vm : ViewModel> : AppCompatActivity() {
    protected lateinit var binding: B
    protected lateinit var viewModel: Vm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 创建 ViewDataBinding
        binding = createBinding(BaseActivity::class.java, 0)
        // 获取 ViewModel
        viewModel = getViewModel(BaseActivity::class.java, 1)
        setContentView(binding.root)
        // LightModel 自动化
        LightModelUtil.setAutoLightModel(this)
        // 初始化
        init(binding, viewModel)
    }

    /**
     * 初始化
     */
    protected abstract fun init(binding: B, vm: Vm)
}