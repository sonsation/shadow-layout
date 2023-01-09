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
        clipChildren = false
        clipToPadding = false
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {

        if (radius == null) {
            return
        }

        val rect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())

        path.apply {
            reset()
            addRoundRect(rect, radius!!.getRadiusArray(), Path.Direction.CW)
            close()
        }

        canvas?.clipPath(path)

        super.onDraw(canvas)
    }

    fun setRadius(radius: Radius) {
        this.radius = radius
        invalidate()
    }

    fun setStrokePadding(padding: Int) {
        setPadding(padding, padding, padding, padding)
    }
}