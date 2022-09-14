package com.rick.jetpackmvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModel
import com.rick.jetpackmvvm.util.BindingUtil
import com.rick.jetpackmvvm.util.ViewModelUtil

abstract class BaseFragment<Binding : ViewDataBinding, Vm : ViewModel> : Fragment(),
    FragmentResultListener {
    protected lateinit var binding: Binding
    protected lateinit var viewModel: Vm

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BindingUtil.createBinding(this, BaseFragment::class.java, 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelUtil.getViewModel(this, this, BaseFragment::class.java, 1)
        init(binding, viewModel)
        listenerKeys()?.let {
            it.forEach { key ->
                parentFragmentManager.setFragmentResultListener(key, viewLifecycleOwner, this)
            }
        }
    }

    protected open fun listenerKeys(): Array<String>? = null

    override fun onFragmentResult(requestKey: String, result: Bundle) {}

    protected abstract fun init(binding: Binding, viewModel: Vm)
}