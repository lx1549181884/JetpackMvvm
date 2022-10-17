package com.rick.jetpackmvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.rick.jetpackmvvm.databinding.FrBaseBinding
import com.rick.jetpackmvvm.util.BindingUtil
import com.rick.jetpackmvvm.util.ViewModelUtil

abstract class BaseFragment<Binding : ViewDataBinding, Vm : BaseVm> : Fragment(),
    FragmentResultListener {
    private lateinit var baseBinding: FrBaseBinding
    protected lateinit var binding: Binding
    protected lateinit var viewModel: Vm

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BindingUtil.createBinding(
        viewLifecycleOwner,
        FrBaseBinding::class.java,
        inflater,
        null,
        false
    ).apply {
        baseBinding = this
    }.root.apply {
        binding = BindingUtil.createBinding(
            viewLifecycleOwner,
            this as ViewGroup,
            this@BaseFragment,
            BaseFragment::class.java,
            0,
            true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelUtil.getViewModel(this, this, BaseFragment::class.java, 1)
        baseBinding.vm = viewModel
        init(binding, viewModel)
        listenerKeys()?.let {
            it.forEach { key ->
                parentFragmentManager.setFragmentResultListener(key, viewLifecycleOwner, this)
            }
        }

        try {
            javaClass.getDeclaredMethod("onBackPressed")
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        this@BaseFragment.onBackPressed()
                    }
                })
        } catch (e: Exception) {
        }
    }

    protected open fun listenerKeys(): Array<String>? = null

    override fun onFragmentResult(requestKey: String, result: Bundle) {}

    protected abstract fun init(binding: Binding, viewModel: Vm)

    protected open fun onBackPressed() {}
}