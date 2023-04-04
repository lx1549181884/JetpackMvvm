package com.rick.jetpackmvvmdemo.view;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.gyf.immersionbar.ImmersionBar;
import com.rick.jetpackmvvm.base.BaseDialog;
import com.rick.jetpackmvvm.base.BaseViewModel;
import com.rick.jetpackmvvmdemo.databinding.DialogLoadingBinding;

public class LoadingDialog extends BaseDialog<DialogLoadingBinding, BaseViewModel> {
    @Override
    protected void initView(@NonNull DialogLoadingBinding binding, @NonNull BaseViewModel viewModel) {
        Window window = requireDialog().getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
        attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(attributes);
        window.setDimAmount(0);
        ImmersionBar.with(this).init();
    }
}
