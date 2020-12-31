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

    override fun draw() {

        /*if (enableStroke) {

            if (mStrokeWidth > (canvas!!.width / 2) || mStrokeWidth > (canvas!!.height / 2)) {
                mStrokeWidth = dpToPx(1f)
            }

            offsetLeft = 0f + mStrokeWidth / 2
            offsetRight = canvas!!.width.toFloat() - mStrokeWidth / 2
            offsetTop = 0f
            offsetBottom = canvas!!.height.toFloat()
        }*/

        super.draw()

        /*if (enableStroke) {

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
        }*/
    }

    override fun updateCanvas(canvas: Canvas?) {
        super.updateCanvas(canvas)
        draw()
    }
}