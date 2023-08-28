package com.rick.jetpackmvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.BarUtils
import com.rick.jetpackmvvm.databinding.FragmentBaseBinding
import com.rick.jetpackmvvm.util.BindingUtil.createBinding
import com.rick.jetpackmvvm.util.ViewModelUtil.getViewModel

/**
 * Fragment 基类
 */
abstract class BaseFragment<B : ViewDataBinding, Vm : BaseViewModel> : Fragment(),
    FragmentResultListener {
    private lateinit var outBinding: FragmentBaseBinding // 外层布局
    protected lateinit var binding: B // 里层布局
    protected lateinit var vm: Vm
    protected var isLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = // 创建外层布局
        createBinding(
            viewLifecycleOwner,
            FragmentBaseBinding::class.java,
            inflater,
            null,
            false
        ).apply {
            // 设置外层布局
            outBinding = this
            this@BaseFragment.vm = getViewModel(BaseFragment::class.java, 1)
            vm = this@BaseFragment.vm
        }.root.apply {
            // 创建里层布局
            binding = createBinding(
                viewLifecycleOwner,
                BaseFragment::class.java,
                0,
                layoutInflater,
                outBinding.content,
                true
            )
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 导航栏
        BarUtils.setNavBarVisibility(requireActivity(), vm.navBarVisible.value)
        // 回退键
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
        // 监听 fragment 返回结果
        listenRequestKeys()?.let { key ->
            key.forEach {
                parentFragmentManager.setFragmentResultListener(it, viewLifecycleOwner, this)
            }
        }
        // 初始化
        initView(binding, vm)
        // 加载数据
        if (!isLoaded) {
            loadData()
            isLoaded = true
        }
    }

    /**
     * 返回
     */
    open fun back() = findNavController().navigateUp()

    /**
     * 回退键事件
     * @return true拦截 false不拦截
     */
    open fun onBackPressed(): Boolean = false

    /**
     * 监听 fragment 请求 key
     */
    protected open fun listenRequestKeys(): Array<String>? = null

    /**
     * fragment 请求结果回调
     */
    override fun onFragmentResult(requestKey: String, result: Bundle) {}

    /**
     * 初始化界面
     * 在一个 fragment 生命周期内，可能多次创建界面，如回退至当前页面时
     */
    protected abstract fun initView(binding: B, vm: Vm)

    /**
     * 销毁界面回调
     * 在一个 fragment 生命周期内，可能多次销毁界面，如被下一个页面完全遮盖时
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * 加载数据
     * 在一个 fragment 生命周期内，只自动调用一次
     */
    open fun loadData() {}
}