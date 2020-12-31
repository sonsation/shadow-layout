package com.sonsation.labs

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.sonsation.labs.stores.GradientStore
import com.sonsation.labs.stores.RadiusStore
import com.sonsation.labs.utils.*

class ShadowLayout : FrameLayout {

    private val gradientStore by lazy { GradientStore() }
    private val radiusStore by lazy { RadiusStore() }

    val gradientViewHelper by lazy { GradientViewHelper(context) }
    val backgroundViewHelper by lazy { BackgroundViewHelper(context) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(context, attributeSet, defStyleAttr)
    }

    private fun init(context: Context, attributeSet: AttributeSet, defStyle: Int) {
        initAttrsLayout(context, attributeSet, defStyle)
        setWillNotDraw(false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    private fun initAttrsLayout(context: Context, attributeSet: AttributeSet, defStyle: Int) {

        val a = context.obtainStyledAttributes(attributeSet, R.styleable.ShadowLayout, defStyle, 0)

        try {

            with (a) {
                radiusStore.updateRadius(getDimension(R.styleable.ShadowLayout_background_radius, 0f))
                radiusStore.updateRadius(
                    getDimension(R.styleable.ShadowLayout_background_radius, 0f),
                    getDimension(R.styleable.ShadowLayout_background_radius, 0f),
                    getDimension(R.styleable.ShadowLayout_background_radius, 0f),
                    getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                )

                gradientStore.enableGradient = getBoolean(R.styleable.ShadowLayout_inner_gradient_enable, false)
                gradientStore.gradientAngle = getInt(R.styleable.ShadowLayout_inner_gradient_angle, 0)
                gradientStore.gradientOffsetX =
                    getDimension(R.styleable.ShadowLayout_inner_gradient_offset_x, 0f)
                gradientStore.gradientOffsetY =
                    getDimension(R.styleable.ShadowLayout_inner_gradient_offset_y, 0f)
                gradientStore.gradientStartColor = getColor(
                    R.styleable.ShadowLayout_inner_gradient_start_color,
                    ViewHelper.NOT_SET_COLOR
                )
                gradientStore.gradientCenterColor = getColor(
                    R.styleable.ShadowLayout_inner_gradient_start_color,
                    ViewHelper.NOT_SET_COLOR
                )
                gradientStore.gradientEndColor = getColor(
                    R.styleable.ShadowLayout_inner_gradient_end_color,
                    ViewHelper.NOT_SET_COLOR
                )

                gradientStore.gradientMargin = getDimension(R.styleable.ShadowLayout_inner_gradient_margin, 0f)

                backgroundViewHelper.apply {
                    this.gradientStore = this@ShadowLayout.gradientStore
                    this.radiusStore = this@ShadowLayout.radiusStore
                    this.backgroundColor = getColor(R.styleable.ShadowLayout_background_color, ViewHelper.NOT_SET_COLOR)
                }
                gradientViewHelper.apply {
                    this.gradientStore = this@ShadowLayout.gradientStore
                    this.radiusStore = this@ShadowLayout.radiusStore
                }
            }
        } finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null)
            return

        backgroundViewHelper.updateCanvas(canvas)
        gradientViewHelper.updateCanvas(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updatePadding()
    }

    private fun updatePadding() {
        /*if (backgroundViewHelper.mStrokeWidth != 0f) {
            val padding = backgroundViewHelper.mStrokeWidth.toInt()
            setPadding(padding, padding, padding, padding)
        }*/
    }

    override fun setBackgroundColor(color: Int) {
        backgroundViewHelper.setBackgroundColor(color)
    }
}