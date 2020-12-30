package com.sonsation.labs

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.example.sampleapp.utils.GradientViewHelper
import com.sonsation.labs.utils.BackgroundViewHelper

class ShadowLayout : FrameLayout {

    private var mRadius = 0f
    private var mTopLeftRadius = 0f
    private var mTopRightRadius = 0f
    private var mBottomLeftRadius = 0f
    private var mBottomRightRadius = 0f

    private var mFirstShadowEnable = false
    private var mFirstShadowColor = -101
    private var mFirstShadowOffsetX = 0f
    private var mFirstShadowOffsetY = 0f
    private var mFirstShadowBlur = 0f

    private var mSecondShadowEnable = false
    private var mSecondShadowColor = -1
    private var mSecondShadowOffsetX = 0f
    private var mSecondShadowOffsetY = 0f
    private var mSecondShadowBlur = 0f

    private val gradientViewHelper by lazy { GradientViewHelper(context) }
    private val backgroundViewHelper by lazy { BackgroundViewHelper(context) }
    private val shadowPaint by lazy {
        Paint()
    }

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

            backgroundViewHelper.parseTypedArray(a)
            gradientViewHelper.parseTypedArray(a)

            //default background settings
            mRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)

            if (mRadius == 0f) {
                mBottomLeftRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mBottomRightRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mTopLeftRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mTopRightRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
            }

            //first shadow settings
            mFirstShadowEnable = a.getBoolean(R.styleable.ShadowLayout_first_shadow_enable, false)
            mFirstShadowColor = a.getColor(
                R.styleable.ShadowLayout_first_shadow_color,
                -1
            )
            mFirstShadowOffsetX = a.getDimension(R.styleable.ShadowLayout_first_shadow_offset_x, 0f)
            mFirstShadowOffsetY = a.getDimension(R.styleable.ShadowLayout_first_shadow_offset_y, 0f)
            mFirstShadowBlur = a.getDimension(R.styleable.ShadowLayout_first_shadow_blur, 0f)

            //second shadow settings
            mSecondShadowEnable = a.getBoolean(R.styleable.ShadowLayout_second_shadow_enable, false)
            mSecondShadowColor = a.getColor(
                R.styleable.ShadowLayout_second_shadow_color,
                -101
            )
            mSecondShadowOffsetX =
                a.getDimension(R.styleable.ShadowLayout_second_shadow_offset_x, 0f)
            mSecondShadowOffsetY =
                a.getDimension(R.styleable.ShadowLayout_second_shadow_offset_y, 0f)
            mSecondShadowBlur = a.getDimension(R.styleable.ShadowLayout_second_shadow_blur, 0f)

            //inner gradient settings

        } finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null)
            return

        if (mSecondShadowEnable) {
            drawBackgroundShadow(
                canvas,
                mSecondShadowOffsetX,
                mSecondShadowOffsetY,
                mSecondShadowBlur,
                mSecondShadowColor
            )
        }

        if (mFirstShadowEnable) {
            drawBackgroundShadow(
                canvas,
                mFirstShadowOffsetX,
                mFirstShadowOffsetY,
                mFirstShadowBlur,
                mFirstShadowColor
            )
        }

        backgroundViewHelper.updateCanvas(canvas)
        gradientViewHelper.updateCanvas(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updatePadding()
    }

    private fun updatePadding() {
        if (backgroundViewHelper.mStrokeWidth != 0f) {
            val padding = backgroundViewHelper.mStrokeWidth.toInt()
            setPadding(padding, padding, padding, padding)
        }
    }

    private fun drawBackgroundShadow(
        canvas: Canvas?,
        dx: Float,
        dy: Float,
        blurSize: Float,
        shadowColor: Int
    ) {

        if (canvas == null || canvas.height == 0 || canvas.width == 0)
            return

        shadowPaint.apply {
            color = shadowColor
            isAntiAlias = true
            maskFilter = BlurMaskFilter(blurSize, BlurMaskFilter.Blur.NORMAL)
        }

        val path = Path().apply {

            val offsetLeft = dx
            val offsetRight = canvas.width.toFloat() + dx
            val offsetTop = dy
            val offsetBottom = canvas.height.toFloat() + dy

            val shadowRect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)
            addRoundRect(shadowRect, getRadiusArray(), Path.Direction.CW)
        }

        canvas.drawPath(path, shadowPaint)
    }

    private fun getRadiusArray(): FloatArray {

        if (mRadius != 0f) {
            return floatArrayOf(
                mRadius,
                mRadius,
                mRadius,
                mRadius,
                mRadius,
                mRadius,
                mRadius,
                mRadius
            )
        }

        return floatArrayOf(
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