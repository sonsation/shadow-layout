package com.sonsation.library.effet

import android.graphics.*
import com.sonsation.library.utils.Util
import com.sonsation.library.utils.ViewHelper

class Background : Effect {

    override val paint by lazy { Paint() }
    override val path by lazy { Path() }
    private val strokePaint by lazy { Paint() }
    private val strokePath by lazy { Path() }

    override var offsetLeft = 0f
    override var offsetTop = 0f
    override var offsetRight = 0f
    override var offsetBottom = 0f

    override var alpha = 0f

    private var backgroundColor = ViewHelper.NOT_SET_COLOR
    private var strokeInfo: Stroke? = null
    private var shadowInfo: Shadow? = null

    fun init(strokeInfo: Stroke?, backgroundColor: Int) {
        this.strokeInfo = strokeInfo
        this.backgroundColor = backgroundColor

        updatePaint()
    }

    override fun updateOffset(left: Float, top: Float, right: Float, bottom: Float) {
        this.offsetLeft = left
        this.offsetTop = top
        this.offsetRight = right
        this.offsetBottom = bottom
    }

    override fun updatePaint() {

        if (strokeInfo != null && strokeInfo!!.isEnable) {
            strokePaint.apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = strokeInfo!!.strokeWidth
                color = strokeInfo!!.strokeColor

                if (Util.onSetAlphaFromColor(this@Background.alpha, strokeInfo!!.strokeColor)) {
                    alpha = Util.getIntAlpha(this@Background.alpha)
                }

                if (strokeInfo!!.gradient != null && strokeInfo!!.gradient!!.isEnable) {
                    shader = strokeInfo!!.gradient!!.getGradientShader()
                }
            }
        }

        paint.apply {
            isAntiAlias = true
            color = backgroundColor
            style = Paint.Style.FILL_AND_STROKE

            if (Util.onSetAlphaFromColor(this@Background.alpha, backgroundColor)) {
                alpha = Util.getIntAlpha(this@Background.alpha)
            }
        }
    }

    override fun updatePath(radiusInfo: Radius?) {

        val rect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)

        if (strokeInfo != null && strokeInfo!!.isEnable) {
            strokePath.apply {
                reset()

                if (radiusInfo == null) {
                    addRect(rect, Path.Direction.CW)
                } else {
                    addRoundRect(rect, radiusInfo.getRadiusArray(), Path.Direction.CW)
                }

                close()
            }
        }

        path.apply {
            reset()

            if (radiusInfo == null) {
                addRect(rect, Path.Direction.CW)
            } else {
                addRoundRect(rect, radiusInfo.getRadiusArray(), Path.Direction.CW)
            }

            close()
        }
    }

    override fun drawEffect(canvas: Canvas?) {

        if (strokeInfo != null && strokeInfo!!.isEnable) {
            canvas?.drawPath(strokePath, strokePaint)
        }

        canvas?.drawPath(path, paint)
    }

    override fun updateAlpha(alpha: Float) {
        this.alpha = alpha
        updatePaint()
    }

    fun setBackgroundColor(color: Int) {
        this.backgroundColor = color
        updatePaint()
    }

    fun updateStrokeWidth(strokeWidth: Float) {

        if (strokeInfo == null) {
            strokeInfo = Stroke()
        }

        strokeInfo!!.strokeWidth = strokeWidth
        updatePaint()
    }

    fun updateStrokeColor(color: Int) {
        if (strokeInfo == null) {
            strokeInfo = Stroke()
        }

        strokeInfo!!.strokeColor = color
        updatePaint()
    }
}