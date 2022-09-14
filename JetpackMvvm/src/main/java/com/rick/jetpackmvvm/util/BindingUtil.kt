package com.rick.jetpackmvvm.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ReflectUtils
import com.rick.jetpackmvvm.util.GenericUtil.getActualType

/**
 * ViewDataBinding 工具
 */
object BindingUtil {

    @JvmStatic
    fun <C : P, P : AppCompatActivity, B : ViewDataBinding> createBinding(
        activity: C,
        parent: Class<P>,
        @IntRange(from = 0) index: Int,
    ): B = createBinding(activity, activity, parent, index, activity.layoutInflater, null, false)

    @JvmStatic
    fun <C : P, P : Fragment, B : ViewDataBinding> createBinding(
        fragment: C,
        parent: Class<P>,
        @IntRange(from = 0) index: Int,
    ): B = createBinding(fragment.viewLifecycleOwner,
        fragment,
        parent,
        index,
        fragment.layoutInflater,
        null,
        false)

    @JvmStatic
    fun <C : P, P : Any, B : ViewDataBinding> createBinding(
        viewGroup: ViewGroup,
        child: C,
        parent: Class<P>,
        @IntRange(from = 0) index: Int,
    ): B = createBinding(null,
        child,
        parent,
        index,
        LayoutInflater.from(viewGroup.context),
        viewGroup,
        false)

    @JvmStatic
    fun <C : P, P : Any, B : ViewDataBinding> createBinding(
        owner: LifecycleOwner?,
        child: C,
        parent: Class<P>,
        @IntRange(from = 0) index: Int,
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        attachToRoot: Boolean,
    ): B = createBinding(getActualType<C, P, B>(child, parent, index),
        inflater,
        viewGroup,
        attachToRoot).apply { lifecycleOwner = owner }

    @JvmStatic
    fun <B : ViewDataBinding> createBinding(
        clazz: Class<B>,
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        attachToRoot: Boolean,
    ): B = ReflectUtils.reflect(clazz).method("inflate", inflater, viewGroup, attachToRoot).get()
}