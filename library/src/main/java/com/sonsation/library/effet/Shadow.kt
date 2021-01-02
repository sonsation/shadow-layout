package com.sonsation.library.effet

import android.graphics.*
import com.sonsation.library.utils.ViewHelper

class Shadow : Effect {

    override val paint by lazy { Paint() }
    override val path by lazy { Path() }

    override var offsetLeft = 0f
    override var offsetTop = 0f
    override var offsetRight = 0f
    override var offsetBottom = 0f

    var isBackgroundShadow = true
    private val isEnable: Boolean
        get() = blurSize != 0f && shadowColor != ViewHelper.NOT_SET_COLOR
    private var blurSize = 0f
    private var shadowColor = ViewHelper.NOT_SET_COLOR
    private var shadowOffsetX = 0f
    private var shadowOffsetY = 0f
    private var shadowType = ViewHelper.FILL_SHADOW

    fun init(isBackground: Boolean, blurSize: Float, shadowOffsetX: Float, shadowOffsetY: Float, shadowColor: Int) {
        this.isBackgroundShadow = isBackground
        this.blurSize = blurSize
        this.shadowOffsetX = shadowOffsetX
        this.shadowOffsetY = shadowOffsetY
        this.shadowColor = shadowColor

        updatePaint()
    }

    override fun drawEffect(canvas: Canvas?) {

        if (!isEnable)
            return

        canvas?.drawPath(path, paint)
    }

    override fun updateOffset(left: Float, top: Float, right: Float, bottom: Float) {
        this.offsetLeft = left
        this.offsetTop = top
        this.offsetRight = right
        this.offsetBottom = bottom
    }

    override fun updatePaint() {

        paint.apply {
            isAntiAlias = true
            color = shadowColor

            style = if (shadowType == ViewHelper.FILL_SHADOW) {
                Paint.Style.FILL_AND_STROKE
            } else {
                Paint.Style.STROKE
            }

            if (blurSize != 0f) {
                maskFilter = if (isBackgroundShadow) {
                    BlurMaskFilter(blurSize, BlurMaskFilter.Blur.NORMAL)
                } else {
                    BlurMaskFilter(blurSize, BlurMaskFilter.Blur.INNER)
                }
            }
        }
    }

    override fun updatePath(radiusInfo: Radius?) {

        val rect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)

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

    fun getShadowColor(): Int = shadowColor
    fun getShadowBlurSize(): Float = blurSize
    fun getShadowOffsetX(): Float = shadowOffsetX
    fun getShadowOffsetY(): Float = shadowOffsetY

    fun updateShadowColor(color: Int) {
        this.shadowColor = color
        updatePaint()
    }

    fun updateShadowOffsetX(offset: Float) {
        this.shadowOffsetX = offset
    }

    fun updateShadowOffsetY(offset: Float) {
        this.shadowOffsetY = offset
    }

    fun setShadowType(type: String) {
        this.shadowType = type
        updatePaint()
    }
}