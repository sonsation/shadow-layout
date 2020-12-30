package com.sonsation.labs.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.sonsation.labs.R

class BackgroundViewHelper(context: Context) : ViewHelper(context) {

    private val mStrokePaint by lazy { Paint() }
    private val mStrokePath by lazy { Path() }

    override fun parseTypedArray(typedArray: TypedArray) {

        with (typedArray) {

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

            mStrokeColor = getColor(
                R.styleable.ShadowLayout_background_stroke_color,
                -101
            )

            mStrokeWidth =
                getDimension(R.styleable.ShadowLayout_background_stroke_width, 0f)
        }
    }

    override fun draw() {

        if (canvas == null)
            return

        if (enableStroke) {

            if (mStrokeWidth > (canvas!!.width / 2) || mStrokeWidth > (canvas!!.height / 2)) {
                mStrokeWidth = dpToPx(1f)
            }

            offsetLeft = 0f + mStrokeWidth / 2
            offsetRight = canvas!!.width.toFloat() - mStrokeWidth / 2
            offsetTop = 0f
            offsetBottom = canvas!!.height.toFloat()
        }

        super.draw()

        if (enableStroke) {

            mStrokePaint.apply {
                isAntiAlias = true
                color = mStrokeColor
                strokeWidth = mStrokeWidth
                strokeCap = Cap.ROUND
                style = Style.STROKE
            }

            mStrokePath.apply {

                reset()

                val strokeMargin = mStrokeWidth / 2
                val offsetRight = canvas!!.width.toFloat() - strokeMargin
                val offsetBottom = canvas!!.height.toFloat() - strokeMargin

                val strokeRect = RectF(strokeMargin, strokeMargin, offsetRight, offsetBottom)

                addRoundRect(strokeRect, getRadiusArray(), Path.Direction.CW)

                close()
            }

            canvas?.drawPath(mStrokePath, mStrokePaint)
        }
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

    fun updateStrokeWidth(width: Float) {
        mStrokeWidth = width
        draw()
    }

    fun updateStrokeColor(color: Int) {
        mStrokeColor = color
        draw()
    }

    fun updateBackgroundColor(color: Int) {
        this.color = color
        draw()
    }
}