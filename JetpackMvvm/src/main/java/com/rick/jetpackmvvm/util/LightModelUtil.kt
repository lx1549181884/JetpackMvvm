package com.rick.jetpackmvvm.util

import android.graphics.Color
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.KeyboardUtils
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.pow

/**
 * LightModel 工具
 */
object LightModelUtil {

    /**
     * 根据状态栏背景颜色，自动设置 LightModel
     * 使用该方法会沉浸状态栏，因为如此才能截屏至状态栏判断其色值亮度
     */
    fun autoLightModel(activity: ComponentActivity) {
        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            // 沉浸状态栏
            BarUtils.transparentStatusBar(activity)
            // 修复沉浸状态栏与软键盘冲突 bug
            KeyboardUtils.fixAndroidBug5497(activity)
            // 绘制监听器的添加与移除
            val onDrawListener = createOnDrawListener(activity)
            activity.window.decorView.viewTreeObserver.addOnDrawListener(onDrawListener)
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    activity.window.decorView.viewTreeObserver.removeOnDrawListener(onDrawListener)
                    activity.lifecycle.removeObserver(this)
                    super.onDestroy(owner)
                }
            })
        }
    }

    /**
     * 性能优化：单线程池，多次更新阻塞时只做最后一次更新
     */
    private val executor by lazy {
        object : ThreadPoolExecutor(
            1,
            1,
            0,
            TimeUnit.MILLISECONDS,
            ArrayBlockingQueue(1)
        ) {
            override fun execute(command: Runnable?) {
                queue.clear()
                super.execute(command)
            }
        }
    }

    /**
     * 创建绘制监听
     */
    private fun createOnDrawListener(activity: ComponentActivity) =
        ViewTreeObserver.OnDrawListener {
            executor.execute {
                try {
                    // 获取状态栏像素
                    val pixels = getStatusBarPixels(activity)
                    // 计算平均色值
                    val avgColor = getAvgColor(pixels)
                    // 判断是否为亮色
                    val isLight = isLightColor(avgColor)
                    activity.runOnUiThread {
                        // 设置 LightModel
                        if (!activity.isDestroyed) BarUtils.setStatusBarLightMode(activity, isLight)
                    }
                } catch (_: Exception) {
                }
            }
        }

    /**
     * 获取状态栏像素
     */
    private fun getStatusBarPixels(activity: ComponentActivity) = activity.window.decorView.let {
        it.isDrawingCacheEnabled = true
        it.buildDrawingCache()
        // 截屏
        val screenBitmap = it.getDrawingCache()
        val width = screenBitmap.width
        val height = BarUtils.getStatusBarHeight()
        val pixels = IntArray(width * height)
        // 获取状态栏区域像素
        screenBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        it.destroyDrawingCache()
        pixels
    }

    /**
     * 获取平均色值
     */
    private fun getAvgColor(pixels: IntArray): Int {
        var r = 0L
        var g = 0L
        var b = 0L
        pixels.forEach {
            r += Color.red(it)
            g += Color.green(it)
            b += Color.blue(it)
        }
        r /= pixels.size
        g /= pixels.size
        b /= pixels.size
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    /**
     * 是否为亮色
     */
    private fun isLightColor(@ColorInt color: Int) =
        (computeLuminance(color) + 0.05).pow(2.0) > 0.15

    /**
     * 颜色亮度
     */
    private fun computeLuminance(@ColorInt color: Int) =
        0.2126 * linearizeColorComponent(Color.red(color)) +
                0.7152 * linearizeColorComponent(Color.green(color)) +
                0.0722 * linearizeColorComponent(Color.blue(color))

    /**
     * 线性化颜色分量
     */
    private fun linearizeColorComponent(@IntRange(from = 0, to = 255) colorComponent: Int) =
        (colorComponent / 255.0).let {
            if (it <= 0.03928) it / 12.92 else ((it + 0.055) / 1.055).pow(2.4)
        }
}