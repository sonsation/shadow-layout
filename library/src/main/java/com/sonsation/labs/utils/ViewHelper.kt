package com.sonsation.labs.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

abstract class ViewHelper(private val context: Context) : Paint() {

    var canvas: Canvas? = null
    var mRadius = 0f
    var mTopLeftRadius = 0f
    var mTopRightRadius = 0f
    var mBottomLeftRadius = 0f
    var mBottomRightRadius = 0f
    private val path by lazy { Path() }

    var enableStroke: Boolean = false
        get() = mStrokeWidth != 0f && mStrokeColor != NOT_SET_COLOR
    var mStrokeWidth = 0f
    var mStrokeColor = NOT_SET_COLOR

    var offsetTop = 0f
    var offsetBottom = 0f
    var offsetLeft = 0f
    var offsetRight = 0f

    init {
        isAntiAlias = true
    }

    companion object {
        const val NOT_SET_COLOR = -101
    }

    abstract fun parseTypedArray(typedArray: TypedArray)

    open fun draw() {

        if (canvas == null)
            return

        updatePath()

        canvas?.drawPath(path, this)
    }

    fun updatePath() {

        if (canvas == null)
            return

        val roundRect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)

        path.apply {
            reset()
            addRoundRect(roundRect, getRadiusArray(), Path.Direction.CW)
            close()
        }
    }

    open fun getRadiusArray(): FloatArray {
        return if (mRadius != 0f) {
            floatArrayOf(mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius, mRadius)
        } else {
            floatArrayOf(
                mTopLeftRadius,
                mTopLeftRadius,
                mTopRightRadius,
                mTopRightRadius,
                mBottomLeftRadius,
                mBottomLeftRadius,
                mBottomRightRadius,
                mBottomRightRadius
            )
        }
    }

    open fun updateRadius(radius: Float) {
        mRadius = radius
        draw()
    }

    open fun updateRadius(
        topLeftRadius: Float,
        topRightRadius: Float,
        bottomLeftRadius: Float,
        bottomRightRadius: Float
    ) {
        mTopLeftRadius = topLeftRadius
        mTopRightRadius = topRightRadius
        mBottomLeftRadius = bottomLeftRadius
        mBottomRightRadius = bottomRightRadius
        draw()
    }

    open fun updateCanvas(canvas: Canvas?) {

        if (canvas == null)
            return

        this.canvas = canvas

        offsetLeft = 0f
        offsetRight = canvas.width.toFloat()
        offsetTop = 0f
        offsetBottom = canvas.height.toFloat()

        draw()
    }

    fun dpToPx(dp: Float): Float {
        return context.resources.displayMetrics.density * dp
    }
}