package com.example.sampleapp.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import com.example.sampleapp.R
import com.sonsation.labs.utils.ViewHelper

class GradientViewHelper(context: Context) : ViewHelper(context) {

    private var enableGradient = false
    private var mInnerGradientStartColor = -101
    private var mInnerGradientCenterColor = -101
    private var mInnerGradientEndColor = -101
    private var mInnerGradientAngle = 0
    private var mInnerGradientOffsetX = 0f
    private var mInnerGradientOffsetY = 0f
    private var mInnerGradientMargin = 0f

    override fun parseTypedArray(typedArray: TypedArray) {

        with(typedArray) {

            mRadius = getDimension(R.styleable.ShadowLayout_background_radius, 0f)

            if (mRadius == 0f) {
                mBottomLeftRadius = getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mBottomRightRadius = getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mTopLeftRadius = getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mTopRightRadius = getDimension(R.styleable.ShadowLayout_background_radius, 0f)
            }

            color = getColor(
                R.styleable.ShadowLayout_background_color,
                -1
            )

            mStrokeWidth =
                getDimension(R.styleable.ShadowLayout_background_stroke_width, 0f)

            enableGradient = getBoolean(R.styleable.ShadowLayout_inner_gradient_enable, false)
            mInnerGradientAngle = getInt(R.styleable.ShadowLayout_inner_gradient_angle, 0)
            mInnerGradientOffsetX =
                getDimension(R.styleable.ShadowLayout_inner_gradient_offset_x, 0f)
            mInnerGradientOffsetY =
                getDimension(R.styleable.ShadowLayout_inner_gradient_offset_y, 0f)
            mInnerGradientStartColor = getColor(
                R.styleable.ShadowLayout_inner_gradient_start_color,
                NOT_SET_COLOR
            )
            mInnerGradientCenterColor = getColor(
                R.styleable.ShadowLayout_inner_gradient_start_color,
                NOT_SET_COLOR
            )
            mInnerGradientEndColor = getColor(
                R.styleable.ShadowLayout_inner_gradient_end_color,
                NOT_SET_COLOR
            )

            mInnerGradientMargin = getDimension(R.styleable.ShadowLayout_inner_gradient_margin, 0f)
        }
    }

    override fun draw() {

        if (!enableGradient)
            return

        if (canvas == null)
            return

        if (mInnerGradientMargin != 0f) {
            offsetLeft = 0f + mInnerGradientMargin
            offsetRight = canvas!!.width.toFloat() - mInnerGradientMargin
            offsetTop = 0f + mInnerGradientMargin
            offsetBottom = canvas!!.height.toFloat() - mInnerGradientMargin
        }

        shader = getGradientShader(offsetLeft, offsetTop, offsetRight, offsetBottom)

        super.draw()
    }

    override fun updateCanvas(canvas: Canvas?) {
        super.updateCanvas(canvas)
        draw()
    }

    override fun updateRadius(radius: Float) {
        super.updateRadius(radius)
        draw()
    }

    override fun updateRadius(
        topLeftRadius: Float,
        topRightRadius: Float,
        bottomLeftRadius: Float,
        bottomRightRadius: Float
    ) {
        super.updateRadius(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius)
        draw()
    }

    private fun getGradientShader(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ): LinearGradient {

        val colors = if (mInnerGradientCenterColor == -1) {
            intArrayOf(mInnerGradientStartColor, mInnerGradientEndColor)
        } else {
            intArrayOf(mInnerGradientStartColor, mInnerGradientCenterColor, mInnerGradientEndColor)
        }

        if (mInnerGradientAngle < 0) {
            val trueAngle = mInnerGradientAngle % 360
            mInnerGradientAngle = trueAngle + 360
        }

        val trueAngle = mInnerGradientAngle % 360

        val width = right - left
        val height = bottom - top

        return when (trueAngle / 45) {
            0 -> {
                val x = right + mInnerGradientOffsetX
                LinearGradient(x, 0f, left, 0f, colors, null, Shader.TileMode.CLAMP)
            }
            1 -> {
                val x = right + mInnerGradientOffsetX
                val y = top + mInnerGradientOffsetY
                LinearGradient(x, top, left, y, colors, null, Shader.TileMode.CLAMP)
            }
            2 -> {
                val y = top + mInnerGradientOffsetY
                LinearGradient(0f, y, 0f, bottom, colors, null, Shader.TileMode.CLAMP)
            }
            3 -> {
                val x = width + mInnerGradientOffsetX
                val y = (height * 2) + mInnerGradientOffsetY
                LinearGradient(0f, y, x, bottom, colors, null, Shader.TileMode.CLAMP)
            }
            4 -> {
                val y = bottom + mInnerGradientOffsetY
                LinearGradient(0f, y, 0f, 0f, colors, null, Shader.TileMode.CLAMP)
            }
            5 -> {
                val x = right + mInnerGradientOffsetX
                val y = top + mInnerGradientOffsetY
                LinearGradient(0f, y, x, top, colors, null, Shader.TileMode.CLAMP)
            }
            6 -> {
                val x = top + mInnerGradientOffsetX
                LinearGradient(x, 0f, right, 0f, colors, null, Shader.TileMode.CLAMP)
            }
            else -> {
                val x = right + mInnerGradientOffsetX
                val y = top + mInnerGradientOffsetY
                LinearGradient(0f, y, x, bottom, colors, null, Shader.TileMode.CLAMP)
            }
        }
    }

    fun updateStrokeWidth(width: Float) {
        mStrokeWidth = width
        draw()
    }
}