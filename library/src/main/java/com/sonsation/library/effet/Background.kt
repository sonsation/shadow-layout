package com.sonsation.library.effet

import android.graphics.*
import com.sonsation.library.utils.Util
import com.sonsation.library.utils.ViewHelper

class Background : Effect {

    override val paint by lazy { Paint() }
    override val path by lazy { Path() }

    private val outlinePaint by lazy { Paint() }
    val outlinePath by lazy { Path() }

    override var offsetLeft = 0f
    override var offsetTop = 0f
    override var offsetRight = 0f
    override var offsetBottom = 0f

    override var alpha = 0f

    private var backgroundColor = ViewHelper.NOT_SET_COLOR

    private var strokeInfo: Stroke? = null
    private var strokeGradient: Gradient? = null
    private var gradient: Gradient? = null

    fun init(backgroundColor: Int, strokeInfo: Stroke?, strokeGradient: Gradient?, gradient: Gradient?) {
        this.backgroundColor = backgroundColor
        this.strokeInfo = strokeInfo
        this.strokeGradient = strokeGradient
        this.gradient = gradient

        updatePaint()
    }

    override fun updateOffset(left: Float, top: Float, right: Float, bottom: Float) {
        this.offsetLeft = left
        this.offsetTop = top
        this.offsetRight = right
        this.offsetBottom = bottom
    }

    override fun updatePaint() {

        with (outlinePaint) {

            isAntiAlias = true

            if (strokeInfo?.isEnable == true) {
                style = Paint.Style.STROKE
                color = strokeInfo!!.strokeColor
                strokeWidth = strokeInfo!!.strokeWidth

                shader = if (strokeGradient?.isEnable == true) {
                    strokeGradient?.getGradientShader(offsetLeft, offsetTop, offsetRight, offsetBottom)
                } else {
                    null
                }
            } else {
                color = backgroundColor
                strokeWidth = 0f
                style = Paint.Style.FILL

                shader = if (gradient?.isEnable == true) {
                    gradient?.getGradientShader(offsetLeft, offsetTop, offsetRight, offsetBottom)
                } else {
                    null
                }
            }

            if (Util.onSetAlphaFromColor(this@Background.alpha, strokeInfo!!.strokeColor)) {
                alpha = Util.getIntAlpha(this@Background.alpha)
            }
        }

        with (paint) {
            isAntiAlias = true
            color = backgroundColor
            style = Paint.Style.FILL_AND_STROKE

            shader = if (strokeInfo?.isEnable == true && gradient?.isEnable == true) {
                gradient?.getGradientShader(offsetLeft, offsetTop, offsetRight, offsetBottom)
            } else {
                null
            }

            if (Util.onSetAlphaFromColor(this@Background.alpha, backgroundColor)) {
                alpha = Util.getIntAlpha(this@Background.alpha)
            }
        }
    }

    override fun updatePath(radiusInfo: Radius?) {

        val viewRect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)
        val strokeWidth = (strokeInfo?.takeIf { it.isEnable }?.strokeWidth ?: 0f).div(2f)
        val strokeRect = RectF(offsetLeft + strokeWidth, offsetTop + strokeWidth, offsetRight - strokeWidth, offsetBottom - strokeWidth)

        outlinePath.apply {
            reset()

            if (radiusInfo == null) {
                addRect(strokeRect, Path.Direction.CW)
            } else {
                val height = strokeRect.height()
                addRoundRect(strokeRect, radiusInfo.getRadiusArray(height), Path.Direction.CW)
            }

            close()
        }

        path.apply {
            reset()
            addRect(viewRect, Path.Direction.CW)
            close()
        }
    }

    override fun drawEffect(canvas: Canvas?) {
        canvas?.save()
        canvas?.clipPath(outlinePath)
        canvas?.drawPath(path, paint)
        canvas?.restore()
        canvas?.drawPath(outlinePath, outlinePaint)
    }

    override fun updateAlpha(alpha: Float) {
        this.alpha = alpha
    }

    fun setBackgroundColor(color: Int) {
        this.backgroundColor = color
    }
}