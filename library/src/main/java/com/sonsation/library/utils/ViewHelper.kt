package com.sonsation.library.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import com.sonsation.library.effet.*
import java.lang.NumberFormatException

class ViewHelper(private val context: Context) {

    var canvas: Canvas? = null
    var radiusInfo: Radius? = null
    var strokeInfo: Stroke? = null

    companion object {
        const val NOT_SET_COLOR = -101
        const val STROKE_SHADOW = "stroke"
        const val FILL_SHADOW = "fill"
    }

    fun updateCanvas(canvas: Canvas?) {
        this.canvas = canvas
    }

    fun drawEffect(effect: Effect) {

        if (canvas == null) {
            return
        }

        effect.updatePath(radiusInfo)
        effect.drawEffect(canvas)
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

            if (splitArray.size != 4) {
                return null
            }

            var blurSize: Float
            var offsetX: Float
            var offsetY: Float
            var color: Int

            try {
                blurSize = splitArray[0].toFloat().toPx()
                offsetX = splitArray[1].toFloat().toPx()
                offsetY = splitArray[2].toFloat().toPx()
                color = Color.parseColor(splitArray[3])
            } catch (e: NumberFormatException) {
                blurSize = 0f
                offsetX = 0f
                offsetY = 0f
                color = Color.WHITE
            }

            val shadow = Shadow().apply {
                init(isBackground, blurSize, offsetX, offsetY, color)
            }

            list.add(shadow)
        }

        return list
    }

    fun Float.toPx(): Float {
        return context.resources.displayMetrics.density * this
    }
}