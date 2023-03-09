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

abstract class BaseDialog<Binding : ViewDataBinding, Vm : ViewModel> : AppCompatDialogFragment() {
    protected lateinit var binding: Binding
    protected lateinit var viewModel: Vm

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = createBinding(BaseDialog::class.java, 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        viewModel = getViewModel(this, BaseDialog::class.java, 1)
        init(binding, viewModel)
    }

    protected abstract fun init(binding: Binding, viewModel: Vm)
}