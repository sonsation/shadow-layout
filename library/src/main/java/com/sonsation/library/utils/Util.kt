package com.sonsation.library.utils

import com.sonsation.library.model.Color
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

object Util {

    fun parseIntColor(color: Int): Color {
        val alpha = android.graphics.Color.alpha(color)
        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)

        return Color(alpha, red, green, blue)
    }

    fun floatAlphaToIntAlpha(alpha: Float): Int {
        return max(0, min(255, floor(alpha * 256.0).toInt()))
    }

    fun intAlphaToFloatAlpha(alpha: Int): Float {
        return 0f
    }
}