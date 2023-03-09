package com.rick.jetpackmvvm.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.IntRange
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ReflectUtils
import com.rick.jetpackmvvm.util.TypeUtil.getClass

/**
 * ViewDataBinding 工具
 */
object BindingUtil {

    /**
     * 实例化 BaseActivity 的 ViewDataBinding 泛型，并绑定生命周期
     */
    @JvmStatic
    fun <C : P, P : ComponentActivity, B : ViewDataBinding> C.createBinding(
        base: Class<P>,
        @IntRange(from = 0) index: Int
    ): B = createBinding(this, base, index, layoutInflater, null, false)

    /**
     * 实例化 BaseFragment 的 ViewDataBinding 泛型，并绑定生命周期
     */
    @JvmStatic
    fun <C : P, P : Fragment, B : ViewDataBinding> C.createBinding(
        base: Class<P>,
        @IntRange(from = 0) index: Int
    ): B = createBinding(viewLifecycleOwner, base, index, layoutInflater, null, false)

    /**
     * 实例化 BaseView 的 ViewDataBinding 泛型
     */
    @JvmStatic
    fun <C : P, P : ViewGroup, B : ViewDataBinding> C.createBinding(
        base: Class<P>,
        @IntRange(from = 0) index: Int
    ): B = createBinding(null, base, index, LayoutInflater.from(context), this, true)

    /**
     * 实例化 base 的 ViewDataBinding 泛型
     */
    @JvmStatic
    fun <C : P, P : Any, B : ViewDataBinding> C.createBinding(
        base: Class<P>,
        @IntRange(from = 0) index: Int,
        viewGroup: ViewGroup
    ): B =
        createBinding(null, base, index, LayoutInflater.from(viewGroup.context), viewGroup, false)

    /**
     * 实例化 base 的 ViewDataBinding 泛型，并绑定生命周期
     */
    @JvmStatic
    fun <C : P, P : Any, B : ViewDataBinding> C.createBinding(
        owner: LifecycleOwner?,
        base: Class<P>,
        @IntRange(from = 0) index: Int,
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        attachToRoot: Boolean
    ): B = createBinding(owner, getClass(base, index), inflater, viewGroup, attachToRoot)

    /**
     * 创建 ViewDataBinding 实例，并绑定生命周期
     */
    @JvmStatic
    fun <B : ViewDataBinding> createBinding(
        owner: LifecycleOwner?,
        clazz: Class<B>,
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        attachToRoot: Boolean
    ): B = ReflectUtils.reflect(clazz).method("inflate", inflater, viewGroup, attachToRoot).get<B>()
        .apply { lifecycleOwner = owner }
}