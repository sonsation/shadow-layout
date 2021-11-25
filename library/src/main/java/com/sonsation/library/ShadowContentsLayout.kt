package com.sonsation.library

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import com.sonsation.library.effet.Radius


class ShadowContentsLayout : FrameLayout {

    private var radius: Radius? = null
    private val path by lazy { Path() }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    init {
        clipChildren = true
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (radius != null) {
            return
        }

        val rect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())

        path.apply {
            reset()
            addRoundRect(rect, radius!!.getRadiusArray(), Path.Direction.CW)
            close()
        }

        canvas?.clipPath(path)
    }

    fun setRadius(radius: Radius) {
        this.radius = radius
        postInvalidate()
    }
}