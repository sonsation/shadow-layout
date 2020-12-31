package com.sonsation.labs.stores

import android.graphics.LinearGradient
import android.graphics.Shader
import com.sonsation.labs.utils.ViewHelper

class GradientStore {

    var enableGradient = false
    var gradientStartColor = ViewHelper.NOT_SET_COLOR
    var gradientCenterColor = ViewHelper.NOT_SET_COLOR
    var gradientEndColor = ViewHelper.NOT_SET_COLOR
    var gradientAngle = 0
    var gradientOffsetX = 0f
    var gradientOffsetY = 0f
    var gradientMargin = 0f

    fun getGradientShader(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ): LinearGradient {

        val colors = if (gradientCenterColor == -1) {
            intArrayOf(gradientStartColor, gradientEndColor)
        } else {
            intArrayOf(gradientStartColor, gradientCenterColor, gradientEndColor)
        }

        if (gradientAngle < 0) {
            val trueAngle = gradientAngle % 360
            gradientAngle = trueAngle + 360
        }

        val trueAngle = gradientAngle % 360

        val width = right - left
        val height = bottom - top

        return when (trueAngle / 45) {
            0 -> {
                val x = right + gradientOffsetX
                LinearGradient(x, 0f, left, 0f, colors, null, Shader.TileMode.CLAMP)
            }
            1 -> {
                val x = right + gradientOffsetX
                val y = top + gradientOffsetY
                LinearGradient(x, top, left, y, colors, null, Shader.TileMode.CLAMP)
            }
            2 -> {
                val y = top + gradientOffsetY
                LinearGradient(0f, y, 0f, bottom, colors, null, Shader.TileMode.CLAMP)
            }
            3 -> {
                val x = width + gradientOffsetX
                val y = (height * 2) + gradientOffsetY
                LinearGradient(0f, y, x, bottom, colors, null, Shader.TileMode.CLAMP)
            }
            4 -> {
                val y = bottom + gradientOffsetY
                LinearGradient(0f, y, 0f, 0f, colors, null, Shader.TileMode.CLAMP)
            }
            5 -> {
                val x = right + gradientOffsetX
                val y = top + gradientOffsetY
                LinearGradient(0f, y, x, top, colors, null, Shader.TileMode.CLAMP)
            }
            6 -> {
                val x = top + gradientOffsetX
                LinearGradient(x, 0f, right, 0f, colors, null, Shader.TileMode.CLAMP)
            }
            else -> {
                val x = right + gradientOffsetX
                val y = top + gradientOffsetY
                LinearGradient(0f, y, x, bottom, colors, null, Shader.TileMode.CLAMP)
            }
        }
    }
}