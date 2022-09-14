package com.rick.jetpackmvvm.util

import androidx.annotation.IntRange
import java.lang.reflect.ParameterizedType

/**
 * 泛型工具
 */
object GenericUtil {
    @JvmStatic
    fun <C : P, P : Any, T> getActualType(
        child: C,
        parent: Class<P>,
        @IntRange(from = 0) index: Int,
    ): Class<T> {
        var tempIndex = index
        var tempTypeName: String
        mutableListOf<Class<*>>().apply {
            addAll(getExtendClasses(child, parent))
            addAll(getOuterClasses(child))
        }.forEach {
            with(it.genericSuperclass) {
                if (this is ParameterizedType) with(actualTypeArguments[tempIndex]) {
                    if (this is Class<*>) return this as Class<T> else {
                        tempTypeName = this.toString()
                        it.typeParameters.forEachIndexed { index, type ->
                            if (type.toString() == tempTypeName) {
                                tempIndex = index
                                return@forEachIndexed
                            }
                        }
                    }
                }
            }
        }
        throw RuntimeException("couldn't find actual type")
    }

    private fun <C : P, P : Any> getExtendClasses(
        child: C,
        parent: Class<P>,
    ) = mutableListOf<Class<out P>>().apply {
        var clazz = child.javaClass as Class<out P>?
        while (true) {
            clazz = clazz?.let {
                add(0, it)
                it.superclass as Class<out P>?
            }
            if (clazz == null || clazz == parent || !parent.isAssignableFrom(clazz)) break
        }
    }

    private fun getOuterClasses(obj: Any) = mutableListOf<Class<*>>().apply {
        var tempObj: Any? = obj
        while (true) {
            tempObj = tempObj?.let { getOuter<Any>(it)?.apply { add(this.javaClass) } } ?: break
        }
    }

    private fun <T> getOuter(obj: Any): T? {
        with(obj.javaClass) {
            if (isMemberClass || isAnonymousClass) for (field in fields) with(field) {
                if (name.startsWith("this$") && type == enclosingClass) return get(obj) as T
            }
        }
        return null
    }
}