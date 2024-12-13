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

        outlinePaint.apply {
            isAntiAlias = true

            if (strokeInfo?.isEnable == true) {
                style = Paint.Style.STROKE
                color = strokeInfo!!.strokeColor
                strokeWidth = strokeInfo!!.strokeWidth
            } else {
                style = Paint.Style.FILL
            }

            if (Util.onSetAlphaFromColor(this@Background.alpha, strokeInfo!!.strokeColor)) {
                alpha = Util.getIntAlpha(this@Background.alpha)
            }

            if (strokeInfo?.gradient?.isEnable == true) {
                shader = strokeInfo?.gradient?.getGradientShader()
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

        val viewRect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)

        outlinePath.apply {
            reset()

            if (radiusInfo == null) {
                addRect(viewRect, Path.Direction.CW)
            } else {
                val height = viewRect.height()
                addRoundRect(viewRect, radiusInfo.getRadiusArray(height), Path.Direction.CW)
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

    fun getStrokeInfo(): Stroke? {
        return null
    }
}