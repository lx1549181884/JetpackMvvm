package com.rick.jetpackmvvm.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.rick.jetpackmvvm.util.BindingUtil.createBinding
import com.rick.jetpackmvvm.util.ViewModelUtil.getViewModel

/**
 * Dialog 基类
 */
abstract class BaseDialog<Binding : ViewDataBinding, Vm : ViewModel> : AppCompatDialogFragment() {
    protected lateinit var binding: Binding
    protected lateinit var vm: Vm

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 创建 ViewDataBinding
        binding = createBinding(BaseDialog::class.java, 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 内容区域背景透明
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 获取 ViewModel
        vm = getViewModel(this, BaseDialog::class.java, 1)
        // 初始化界面
        initView(binding, vm)
    }

    /**
     * 初始化界面
     */
    protected abstract fun initView(binding: Binding, viewModel: Vm)
}