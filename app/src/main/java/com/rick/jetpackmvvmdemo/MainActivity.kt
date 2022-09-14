package com.rick.jetpackmvvmdemo

import com.rick.jetpackmvvm.base.BaseActivity
import com.rick.jetpackmvvmdemo.databinding.ActivityMainBinding
import com.rick.jetpackmvvmdemo.vm.MainVm

class MainActivity : BaseActivity<ActivityMainBinding, MainVm>() {
    override fun init(binding: ActivityMainBinding, vm: MainVm) {
    }
}