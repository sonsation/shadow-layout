package com.sonsation.library.effet

import android.graphics.*
import com.sonsation.library.utils.ViewHelper

class Gradient : Effect {

    override val paint by lazy { Paint() }
    override val path by lazy { Path() }

    override var offsetLeft = 0f
    override var offsetTop = 0f
    override var offsetRight = 0f
    override var offsetBottom = 0f

    override var alpha = 0f

    val isEnable: Boolean
        get() = ((gradientStartColor != ViewHelper.NOT_SET_COLOR && gradientEndColor != ViewHelper.NOT_SET_COLOR)
                || (gradientArray != null && gradientArray?.isNotEmpty() == true)) && gradientAngle != -1
    private var gradientStartColor = ViewHelper.NOT_SET_COLOR
    private var gradientCenterColor = ViewHelper.NOT_SET_COLOR
    private var gradientEndColor = ViewHelper.NOT_SET_COLOR
    private var gradientAngle = 0
    private var gradientOffsetX = 0f
    private var gradientOffsetY = 0f
    private var gradientArray: IntArray? = null
    private var gradientPositions: FloatArray? = null
    private var localMatrix: Matrix? = null

    fun init(
        angle: Int,
        startColor: Int,
        centerColor: Int,
        endColor: Int,
        offsetX: Float,
        offsetY: Float,
        gradientArray: IntArray?,
        gradientPositions: FloatArray?
    ) {
        this.gradientAngle = angle
        this.gradientStartColor = startColor
        this.gradientCenterColor = centerColor
        this.gradientEndColor = endColor
        this.gradientOffsetX = offsetX
        this.gradientOffsetY = offsetY
        this.gradientArray = gradientArray
        this.gradientPositions = gradientPositions

        updatePaint()
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
            shader = getGradientShader()

            if (localMatrix != null) {
                shader.setLocalMatrix(localMatrix)
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
                val height = (offsetBottom - offsetTop).toInt()
                addRoundRect(rect, radiusInfo.getRadiusArray(height), Path.Direction.CW)
            }

            close()
        }
    }

    override fun drawEffect(canvas: Canvas?) {

        if (!isEnable)
            return

        canvas?.drawPath(path, paint)
    }

    override fun updateAlpha(alpha: Float) {

    }

    fun getGradientShader(): LinearGradient {

        val colors = if (gradientArray != null && gradientArray?.isNotEmpty() == true) {
            gradientArray!!
        } else {
            if (gradientCenterColor == ViewHelper.NOT_SET_COLOR) {
                intArrayOf(gradientStartColor, gradientEndColor)
            } else {
                intArrayOf(gradientStartColor, gradientCenterColor, gradientEndColor)
            }
        }

        var realAngle = 0

        if (gradientAngle > 0) {
            val trueAngle = gradientAngle % 360
            realAngle = trueAngle + 360
        }

        val trueAngle = realAngle % 360

        val width = offsetRight - offsetLeft
        val height = offsetBottom - offsetTop

        return when (trueAngle / 45) {
            0 -> {
                val x = offsetRight + gradientOffsetX
                LinearGradient(x, 0f, offsetLeft, 0f, colors, gradientPositions, Shader.TileMode.CLAMP)
            }
            1 -> {
                val x = offsetRight + gradientOffsetX
                val y = offsetTop + gradientOffsetY
                LinearGradient(x, offsetTop, offsetLeft, y, colors, gradientPositions, Shader.TileMode.CLAMP)
            }
            2 -> {
                val y = offsetTop + gradientOffsetY
                LinearGradient(0f, y, 0f, offsetBottom, colors, gradientPositions, Shader.TileMode.CLAMP)
            }
            3 -> {
                val x = width + gradientOffsetX
                val y = (height * 2) + gradientOffsetY
                LinearGradient(0f, y, x, offsetBottom, colors, gradientPositions, Shader.TileMode.CLAMP)
            }
            4 -> {
                val y = offsetBottom + gradientOffsetY
                LinearGradient(0f, y, 0f, 0f, colors, gradientPositions, Shader.TileMode.CLAMP)
            }
            5 -> {
                val x = offsetRight + gradientOffsetX
                val y = offsetTop + gradientOffsetY
                LinearGradient(0f, y, x, offsetTop, colors, gradientPositions, Shader.TileMode.CLAMP)
            }
            6 -> {
                val x = offsetTop + gradientOffsetX
                LinearGradient(x, 0f, offsetRight, 0f, colors, gradientPositions, Shader.TileMode.CLAMP)
            }
            else -> {
                val x = offsetRight + gradientOffsetX
                val y = offsetTop + gradientOffsetY
                LinearGradient(0f, y, x, offsetBottom, colors, gradientPositions, Shader.TileMode.CLAMP)
            }
        }
    }

    fun updateGradientColor(startColor: Int, centerColor: Int, endColor: Int) {
        this.gradientStartColor = startColor
        this.gradientCenterColor = centerColor
        this.gradientEndColor = endColor

        updatePaint()
    }

    fun updateGradientColor(startColor: Int, endColor: Int) {
        this.updateGradientColor(startColor, ViewHelper.NOT_SET_COLOR, endColor)
    }

    fun updateGradientAngle(angle: Int) {
        this.gradientAngle = angle
        updatePaint()
    }

    fun updateGradientOffsetX(offset: Float) {
        this.gradientOffsetX = offset
        updatePaint()
    }

    fun updateGradientOffsetY(offset: Float) {
        this.gradientOffsetY = offset
        updatePaint()
    }

    fun updateLocalMatrix(matrix: Matrix?) {
        this.localMatrix = matrix
        updatePaint()
    }
}