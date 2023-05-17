package com.rick.jetpackmvvm.util

import android.graphics.Color
import android.os.Build
import android.view.ViewTreeObserver
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.BarUtils
import com.gyf.immersionbar.ImmersionBar
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.pow

/**
 * LightModel 工具
 */
object LightModelUtil {

    private val onDrawListeners = hashMapOf<ComponentActivity, ViewTreeObserver.OnDrawListener>()
    private val lifecycleObservers = hashMapOf<ComponentActivity, LifecycleObserver>()

    /**
     * 根据状态栏背景颜色，自动设置 LightModel
     * 使用该方法会沉浸状态栏，因为如此才能截屏至状态栏判断其色值亮度
     */
    fun setAutoLightModel(activity: ComponentActivity) {
        cancelAutoLightModel(activity)
        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            // 沉浸状态栏，并解决软键盘与底部输入框冲突问题
            ImmersionBar.with(activity)
                .keyboardEnable(true)
                .navigationBarColorInt(Color.WHITE)
                .autoNavigationBarDarkModeEnable(true)
                .init()
            // 绘制监听器的添加与移除
            val onDrawListener = createOnDrawListener(activity)
            activity.window.decorView.viewTreeObserver.addOnDrawListener(onDrawListener)
            onDrawListeners[activity] = onDrawListener
            val lifecycleObserver = object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    cancelAutoLightModel(activity)
                    super.onDestroy(owner)
                }
            }
            activity.lifecycle.addObserver(lifecycleObserver)
            lifecycleObservers[activity] = lifecycleObserver
        }
    }

    /**
     * 取消自动设置 LightModel
     */
    fun cancelAutoLightModel(activity: ComponentActivity) {
        onDrawListeners[activity]?.let {
            activity.window.decorView.viewTreeObserver.removeOnDrawListener(it)
            onDrawListeners.remove(activity)
        }
        lifecycleObservers[activity]?.let {
            activity.lifecycle.removeObserver(it)
            lifecycleObservers.remove(activity)
        }
    }

    /**
     * 设置 LightModel
     */
    fun setLightModel(activity: ComponentActivity, isLight: Boolean) {
        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            if (BarUtils.isStatusBarLightMode(activity) != isLight) {
                BarUtils.setStatusBarLightMode(activity, isLight)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.decorView.windowInsetsController?.let { controller ->
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS.let {
                        val current = controller.systemBarsAppearance and it
                        val target = if (isLight) it else 0
                        if (current != target) {
                            controller.setSystemBarsAppearance(
                                controller.systemBarsAppearance xor it,
                                it
                            )
                        }
                    }
                }
            }
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
                    if (!onDrawListeners.containsKey(activity)) return@execute
                    // 获取状态栏像素
                    val pixels = getStatusBarPixels(activity)
                    // 计算平均色值
                    val avgColor = getAvgColor(pixels)
                    // 判断是否为亮色
                    val isLight = isLightColor(avgColor)
                    // 设置 LightModel
                    activity.runOnUiThread {
                        setLightModel(activity, isLight)
                    }
                    Thread.sleep(100)
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