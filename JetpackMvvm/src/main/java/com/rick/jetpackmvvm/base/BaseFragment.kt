package com.rick.jetpackmvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.NavHostFragment
import com.blankj.utilcode.util.BarUtils
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
            this@BaseFragment,
            BaseFragment::class.java,
            0,
            layoutInflater,
            baseBinding.content,
            true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelUtil.getViewModel(this, this, BaseFragment::class.java, 1)
        baseBinding.vm = viewModel
        baseBinding.fr = this
        BarUtils.setNavBarVisibility(requireActivity(), viewModel.navBarVisible.value)
        if (!loadOnlyOnce()) {
            viewModel.loadState.value = BaseVm.LoadState.LOADING
        }
        if (viewModel.loadState.value != BaseVm.LoadState.SUCCESS) {
            if (load()) {
                viewModel.loadState.value = BaseVm.LoadState.SUCCESS
            }
        }
        listenerKeys()?.let {
            it.forEach { key ->
                parentFragmentManager.setFragmentResultListener(key, viewLifecycleOwner, this)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!onBackPressed()) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )
        init(binding, viewModel)
    }

    protected open fun listenerKeys(): Array<String>? = null

    override fun onFragmentResult(requestKey: String, result: Bundle) {}

    protected abstract fun init(binding: Binding, vm: Vm)

    protected open fun onBackPressed(): Boolean = false

    open fun load() = true

    open fun back() = NavHostFragment.findNavController(this).navigateUp()

    open fun loadOnlyOnce() = true
}