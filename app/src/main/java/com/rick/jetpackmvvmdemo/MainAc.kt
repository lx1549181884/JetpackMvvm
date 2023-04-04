package com.rick.jetpackmvvmdemo

import com.rick.jetpackmvvm.base.BaseActivity
import com.rick.jetpackmvvmdemo.databinding.AcMainBinding
import com.rick.jetpackmvvmdemo.vm.AppVm

class MainAc : BaseActivity<AcMainBinding, AppVm>() {
    override fun init(binding: AcMainBinding, vm: AppVm) {
    }
}