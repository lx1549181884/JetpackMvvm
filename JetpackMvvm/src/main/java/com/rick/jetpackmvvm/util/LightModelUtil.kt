package com.rick.jetpackmvvm.util

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.WindowInsetsController
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.blankj.utilcode.util.BarUtils
import kotlin.math.pow

/**
 * LightModel 工具
 */
object LightModelUtil {

    /**
     * 设置 LightModel
     */
    fun setLightModel(activity: Activity, @ColorInt color: Int) {
        setLightModel(activity, isLightColor(color))
    }

    /**
     * 设置 LightModel
     */
    fun setLightModel(activity: Activity, isLight: Boolean) {
        if (!activity.isDestroyed) {
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