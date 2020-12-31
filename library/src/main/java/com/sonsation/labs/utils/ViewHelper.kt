package com.sonsation.labs.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.sonsation.labs.stores.GradientStore
import com.sonsation.labs.stores.RadiusStore

abstract class ViewHelper(private val context: Context) : Paint() {

    var canvas: Canvas? = null
    private val path by lazy { Path() }
    var gradientStore: GradientStore? = null
    var radiusStore:  RadiusStore? = null
    var backgroundColor = NOT_SET_COLOR

    var offsetTop = 0f
    var offsetBottom = 0f
    var offsetLeft = 0f
    var offsetRight = 0f

    init {
        isAntiAlias = true
    }

    companion object {
        const val NOT_SET_COLOR = -101
        const val FIRST_SHADOW = 0
        const val SECOND_SHADOW = 1
        const val NOT_SET_SHADOW_TYPE = -1
    }

    open fun draw() {

        if (canvas == null)
            return

        updatePath()

        canvas?.drawPath(path, this)
    }

    fun updatePath() {

        if (canvas == null)
            return

        if (radiusStore == null)
            return

        val roundRect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)

        path.apply {
            reset()
            addRoundRect(roundRect, radiusStore!!.getRadiusArray(), Path.Direction.CW)
            close()
        }
    }

    fun updateOffset(left: Float, top: Float, right: Float, bottom: Float) {

        if (canvas == null)
            return

        offsetLeft = 0f + left
        offsetRight = canvas!!.width.toFloat() + right
        offsetTop = 0f + top
        offsetBottom = canvas!!.height.toFloat() + bottom
    }

    open fun updateRadius(radius: Float) {
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

    @JvmName("setBackgroundColorMethod")
    fun setBackgroundColor(color: Int) {
        this.backgroundColor = color
        draw()
    }

    fun dpToPx(dp: Float): Float {
        return context.resources.displayMetrics.density * dp
    }
}