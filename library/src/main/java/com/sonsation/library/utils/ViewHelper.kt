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

    fun dpToPx(dp: Float): Float {
        return context.resources.displayMetrics.density * dp
    }

    fun updateCanvas(canvas: Canvas?) {
        this.canvas = canvas
    }

    fun drawEffect(effect: Effect) {

        if (canvas == null)
            return

        updateOffset(effect)
        effect.updatePath(radiusInfo)
        effect.drawEffect(canvas)
    }

    private fun updateOffset(effect: Effect) {

        when (effect) {
            is Shadow -> {

                if (effect.isBackgroundShadow) {

                    val dx = effect.getShadowOffsetX()
                    val dy = effect.getShadowOffsetY()

                    effect.updateOffset(dx, dy, canvas!!.width + dx, canvas!!.height + dy)
                } else {

                    val dx = effect.getShadowOffsetX() + effect.getShadowBlurSize()
                    val dy = effect.getShadowOffsetY() + effect.getShadowBlurSize()

                    val right = if (dx == 0f) {
                        canvas!!.width.toFloat()
                    } else {
                        canvas!!.width.toFloat() - dx
                    }

                    val bottom = if (dy == 0f) {
                        canvas!!.height.toFloat() - dy
                    } else {
                        canvas!!.height.toFloat()
                    }

                    effect.updateOffset(dx, dy, right, bottom)
                }
            }
            is Background -> {
                effect.updateOffset(0f, 0f, canvas!!.width.toFloat(), canvas!!.height.toFloat())
            }
            is Gradient -> {
                effect.updateOffset(0f, 0f, canvas!!.width.toFloat(), canvas!!.height.toFloat())
                effect.updatePaint()
            }
        }
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
            if (splitArray.size != 4)
                return null

            var blurSize: Float
            var offsetX: Float
            var offsetY: Float
            var color: Int

            try {
                blurSize = dpToPx(splitArray[0].toFloat())
                offsetX = dpToPx(splitArray[1].toFloat())
                offsetY = dpToPx(splitArray[2].toFloat())
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
}