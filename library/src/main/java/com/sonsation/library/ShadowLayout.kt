package com.sonsation.library

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout

class ShadowLayout : FrameLayout {

    private var mRadius = 0f
    private var mTopLeftRadius = 0f
    private var mTopRightRadius = 0f
    private var mBottomLeftRadius = 0f
    private var mBottomRightRadius = 0f
    private var mBackgroundStrokeWidth = 0f
    private var mBackgroundColor = -101
    private var mBackgroundStrokeColor = -101

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

    private var enableGradient = false
    private var mInnerGradientStartColor = -101
    private var mInnerGradientCenterColor = -101
    private var mInnerGradientEndColor = -101
    private var mInnerGradientAngle = 0
    private var mInnerGradientOffsetX = 0f
    private var mInnerGradientOffsetY = 0f

    private val backgroundPaint by lazy {
        Paint()
    }
    private val strokePaint by lazy {
        Paint()
    }
    private val innerGradientPaint by lazy {
        Paint()
    }
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
            //default background settings
            mRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)

            if (mRadius == 0f) {
                mBottomLeftRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mBottomRightRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mTopLeftRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
                mTopRightRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
            }

            mBackgroundColor = a.getColor(
                R.styleable.ShadowLayout_background_color,
                -1
            )

            //stroke settings
            mBackgroundStrokeColor = a.getColor(
                R.styleable.ShadowLayout_background_stroke_color,
                -101
            )
            mBackgroundStrokeWidth =
                a.getDimension(R.styleable.ShadowLayout_background_stroke_width, 0f)

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
            enableGradient = a.getBoolean(R.styleable.ShadowLayout_inner_gradient_enable, false)
            mInnerGradientAngle = a.getInt(R.styleable.ShadowLayout_inner_gradient_angle, 0)
            mInnerGradientOffsetX =
                a.getDimension(R.styleable.ShadowLayout_inner_gradient_offset_x, 0f)
            mInnerGradientOffsetY =
                a.getDimension(R.styleable.ShadowLayout_inner_gradient_offset_y, 0f)
            mInnerGradientStartColor = a.getColor(
                R.styleable.ShadowLayout_inner_gradient_start_color,
                -101
            )
            mInnerGradientCenterColor = a.getColor(
                R.styleable.ShadowLayout_inner_gradient_start_color,
                -101
            )
            mInnerGradientEndColor = a.getColor(
                R.styleable.ShadowLayout_inner_gradient_end_color,
                -101
            )
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

        drawBackground(canvas)
        drawInnerGradient(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updatePadding()
    }

    private fun updatePadding() {
        if (mBackgroundStrokeWidth != 0f) {
            val padding = mBackgroundStrokeWidth.toInt()
            setPadding(padding, padding, padding, padding)
        }
    }

    private fun drawBackground(canvas: Canvas?) {

        if (canvas == null || canvas.height == 0 || canvas.width == 0)
            return

        backgroundPaint.apply {
            isAntiAlias = true

            color = if (!enableGradient) {
                mBackgroundColor
            } else {
                Color.TRANSPARENT
            }
        }

        val backgroundPath = Path().apply {

            val offsetLeft = 0f + mBackgroundStrokeWidth / 2
            val offsetRight = canvas.width.toFloat() - mBackgroundStrokeWidth / 2
            val offsetTop = 0f
            val offsetBottom = canvas.height.toFloat()

            val backgroundRect = RectF(offsetLeft, offsetTop, offsetRight, offsetBottom)
            addRoundRect(backgroundRect, getRadiusArray(), Path.Direction.CW)
        }

        canvas.drawPath(backgroundPath, backgroundPaint)

        if (mBackgroundStrokeWidth != 0f && mBackgroundStrokeColor != -101) {

            if (mBackgroundStrokeWidth > (canvas.width / 2) || mBackgroundStrokeWidth > (canvas.height / 2)) {
                mBackgroundStrokeWidth = dpToPx(1f)
            }

            strokePaint.apply {
                isAntiAlias = true
                color = mBackgroundStrokeColor
                strokeWidth = mBackgroundStrokeWidth
                strokeCap = Paint.Cap.ROUND
                style = Paint.Style.STROKE
            }

            val strokePath = Path().apply {

                val strokeMargin = mBackgroundStrokeWidth / 2
                val offsetRight = canvas.width.toFloat() - strokeMargin
                val offsetBottom = canvas.height.toFloat() - strokeMargin

                val strokeRect = RectF(strokeMargin, strokeMargin, offsetRight, offsetBottom)

                addRoundRect(strokeRect, getRadiusArray(), Path.Direction.CW)
            }
            canvas.drawPath(strokePath, strokePaint)
        }
    }

    private fun drawInnerGradient(canvas: Canvas?) {

        if (canvas == null || canvas.height == 0 || canvas.width == 0)
            return

        if (!enableGradient)
            return

        val strokeMargin = mBackgroundStrokeWidth / 2
        val offsetRight = if (canvas.width - strokeMargin < 0) {
            0f
        } else {
            canvas.width - strokeMargin
        }
        val offsetBottom = if (canvas.height - strokeMargin < 0) {
            0f
        } else {
            canvas.height - strokeMargin
        }

        innerGradientPaint.apply {
            isAntiAlias = true
            shader = getGradientShader(strokeMargin, strokeMargin, offsetRight, offsetBottom)
        }

        val path = Path().apply {
            val innerGradientRect = RectF(strokeMargin, strokeMargin, offsetRight, offsetBottom)

            addRoundRect(innerGradientRect, getRadiusArray(), Path.Direction.CW)
        }

        canvas.drawPath(path, innerGradientPaint)
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

    private fun dpToPx(dp: Float): Float {
        return resources.displayMetrics.density * dp
    }

    fun setRadius(radius: Float) {
        this.mRadius = radius
        postInvalidate()
    }

    fun setTopLeftRadius(radius: Float) {
        this.mTopLeftRadius = radius
        postInvalidate()
    }

    fun setTopRightRadius(radius: Float) {
        this.mTopRightRadius = radius
        postInvalidate()
    }

    fun setBottomLeftRadius(radius: Float) {
        this.mBottomLeftRadius = radius
        postInvalidate()
    }

    fun setBottomRightRadius(radius: Float) {
        this.mBottomRightRadius = radius
        postInvalidate()
    }

    fun setStrokeWidth(width: Float) {
        this.mBackgroundStrokeWidth = width
        postInvalidate()
    }

    override fun setBackgroundColor(color: Int) {
        this.mBackgroundColor = color
        postInvalidate()
    }

    fun setBackgroundStrokeColor(color: Int) {
        this.mBackgroundStrokeColor = color
        postInvalidate()
    }

    fun setBackgroundStrokeWidth(width: Float) {
        this.mBackgroundStrokeWidth = width
        postInvalidate()
    }
}