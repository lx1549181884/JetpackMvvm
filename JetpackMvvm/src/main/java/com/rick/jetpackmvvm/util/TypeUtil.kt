package com.rick.jetpackmvvm.util

import androidx.annotation.IntRange
import com.blankj.utilcode.util.LogUtils
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 类型工具
 */
object TypeUtil {

    /**
     * 获取父类泛型的具体类
     *
     * @param C 子类
     * @param P 父类
     * @param index 泛型索引
     */
    @JvmStatic
    fun <C : P, P : Any, T> C.getClass(
        parent: Class<P>,
        @IntRange(from = 0) index: Int
    ): Class<T> {
        // 获取第一个节点
        var node: Node? = Node(this, parent, index, true)
        while (true) {
            // 若没有节点，则抛出异常
            if (node == null) throw RuntimeException("not found class")
            // 如果是具体类型，则返回；否则继续查找下个节点
            if (node.type is Class<*>) return node.type as Class<T> else node = node.next()
        }
    }

    /**
     * 节点
     */
    private class Node(
        var obj: Any, // 子类实例
        var clazz: Class<*>,  // 具体类型
        index: Int, // 参数化类型索引
        isCurrentOrLastIndex: Boolean // 是当前类或父类参数化类型索引。因为首个节点比较特殊，用的是当前索引，需要区分。
    ) {
        var type: Type // 参数化类型

        init {
            type = if (isCurrentOrLastIndex) {
                clazz.typeParameters[index] // 根据当前类索引生成
            } else {
                (clazz.genericSuperclass as ParameterizedType).actualTypeArguments[index] // 根据父类索引生成
            }
            LogUtils.d("节点 class=${clazz.simpleName} type=$type\n")
        }

        /**
         * 获取下一个节点
         */
        fun next(
            obj: Any = this.obj,
            clazz: Class<*> = this.clazz,
            type: Type = this.type
        ): Node? {
            // 若参数化类型是具体类型
            if (type is Class<*>) return null
            /**
             * 查找子类
             */
            // 从类声明的参数化类型中查找
            clazz.typeParameters.forEachIndexed { index, typeParameter ->
                // 若有相同名称参数化类型，表示子类重声明了泛型
                if (type == typeParameter) {
                    // 返回子类节点
                    return Node(obj, getSubClass(obj, clazz), index, false)
                }
            }
            /**
             * 查找外部类
             */
            // 通过 Class 遍历 Field 查找外部类引用变量
            obj.javaClass.declaredFields.forEach { field ->
                field.isAccessible = true
                // 外部类引用变量名为"this$0"开头
                if (field.name.startsWith("this$0")) return try {
                    // 用外部类的实例与类型继续获取节点
                    field[obj]?.let { next(it, field.type, type) }
                } catch (e: IllegalAccessException) {
                    null
                }
            }
            return null
        }

        /**
         * 获取父类下一级子类
         */
        private fun getSubClass(child: Any, parent: Class<*>): Class<*> {
            var clazz: Class<*> = child.javaClass
            while (true) {
                if (clazz.superclass == null || clazz.superclass.isAssignableFrom(parent)) break
                clazz = clazz.superclass
            }
            return clazz;
        }
    }
}
