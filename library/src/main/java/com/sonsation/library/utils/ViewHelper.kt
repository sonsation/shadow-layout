package com.sonsation.library.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import com.sonsation.library.effet.*
import java.lang.NumberFormatException

class ViewHelper(private val context: Context) {

    var radiusInfo: Radius? = null
    var strokeInfo: Stroke? = null

    companion object {
        const val NOT_SET_COLOR = -101
        const val STROKE_SHADOW = "stroke"
        const val FILL_SHADOW = "fill"
    }

    fun Canvas.drawEffect(effect: Effect) {
        effect.updatePath(radiusInfo)
        effect.drawEffect(this)
    }

    fun updateOffset(effect: Effect, width: Int, height: Int) {

        when (effect) {
            is Shadow -> {

                if (effect.isBackgroundShadow) {

                    val dx = effect.getShadowOffsetX()
                    val dy = effect.getShadowOffsetY()

                    effect.updateOffset(dx, dy, width + dx, height + dy)
                } else {

                    val dx = effect.getShadowOffsetX() + effect.getShadowBlurSize()
                    val dy = effect.getShadowOffsetY() + effect.getShadowBlurSize()

                    val right = if (dx == 0f) {
                        width.toFloat()
                    } else {
                        width.toFloat() - dx
                    }

                    val bottom = if (dy == 0f) {
                        height.toFloat() - dy
                    } else {
                        height.toFloat()
                    }

                    effect.updateOffset(dx, dy, right, bottom)
                }
            }
            is Background -> {
                effect.updateOffset(0f, 0f, width.toFloat(), height.toFloat())
            }
            is Gradient -> {
                effect.updateOffset(0f, 0f, width.toFloat(), height.toFloat())
                effect.updatePaint()
            }
        }
    }

    fun parseGradientArray(arrays: String?): List<Int>? {

        if (arrays.isNullOrEmpty())
            return null

        val list = mutableListOf<Int>()

        val split = arrays.split(",").map {
            val text = it
            text.trim()
        }


        if (split.isEmpty())
            return null

        split.map { it.trim() }

        split.forEach {
            list.add(Color.parseColor(it))
        }

        return list
    }

    fun parseGradientPositions(arrays: String?): List<Float>? {

        if (arrays.isNullOrEmpty())
            return null

        val list = mutableListOf<Float>()

        val split = arrays.split(",").map {
            val text = it
            text.trim()
        }


        if (split.isEmpty())
            return null

        split.map { it.trim() }

        split.forEach {
            list.add(it.toFloat())
        }

        return list
    }

    fun parseShadowArray(isBackground: Boolean, arrays: String?): List<Shadow>? {

        if (arrays.isNullOrEmpty())
            return null

        val list = mutableListOf<Shadow>()
        val split = arrays.split("},").map {
            var text = it
            text = text.replace("{", "")
            text = text.replace("}", "")
            text.trim()
        }

        if (split.isEmpty())
            return null

        split.map { it.trim() }

        split.forEach {

            val splitArray = it.split(",")

            val shadow = Shadow().apply {
                if (splitArray.size == 4) {
                    try {
                        val blurSize = splitArray[0].toFloat().toPx()
                        val offsetX = splitArray[1].toFloat().toPx()
                        val offsetY = splitArray[2].toFloat().toPx()
                        val color = Color.parseColor(splitArray[3])

                        init(isBackground, blurSize, offsetX, offsetY, 0f, color)
                    } catch (e: NumberFormatException) {
                        init(isBackground, 0f, 0f, 0f, 0f, Color.WHITE)
                    }
                } else {
                    try {
                        val blurSize = splitArray[0].toFloat().toPx()
                        val offsetX = splitArray[1].toFloat().toPx()
                        val offsetY = splitArray[2].toFloat().toPx()
                        val spread = splitArray[3].toFloat().toPx()
                        val color = Color.parseColor(splitArray[4])
                        init(isBackground, blurSize, offsetX, offsetY, spread, color)
                    } catch (e: NumberFormatException) {
                        init(isBackground, 0f, 0f, 0f, 0f, Color.WHITE)
                    }
                }
            }

            list.add(shadow)
        }

        return list
    }

    fun Float.toPx(): Float {
        return context.resources.displayMetrics.density * this
    }
}