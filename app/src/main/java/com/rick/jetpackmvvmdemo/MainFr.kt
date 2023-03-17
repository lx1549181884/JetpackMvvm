package com.rick.jetpackmvvmdemo

import com.blankj.utilcode.util.ColorUtils
import com.rick.jetpackmvvm.base.BaseFragment
import com.rick.jetpackmvvmdemo.databinding.FrMainBinding
import com.rick.jetpackmvvmdemo.vm.AppVm

class MainFr : BaseFragment<FrMainBinding, AppVm>() {
    override fun initView(binding: FrMainBinding, vm: AppVm) {
        binding.root.setOnClickListener {
            val randomColor = ColorUtils.getRandomColor(false)
            binding.text.text = ColorUtils.int2ArgbString(randomColor)
            vm.statusBarColor.value = randomColor
        }
    }
}