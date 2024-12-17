package com.sonsation.library

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.sonsation.library.effet.*
import com.sonsation.library.utils.ViewHelper
import kotlin.math.abs

class ShadowLayout : FrameLayout {

    private val outlineRect by lazy {
        RectF()
    }

    private val outlinePaint by lazy {
        Paint()
    }

    private val outlinePath by lazy {
        Path()
    }

    private val backgroundPaint by lazy {
        Paint()
    }

    private val backgroundPath by lazy {
        Path()
    }

    private var backgroundColor = ViewHelper.NOT_SET_COLOR
    private var backgroundBlur = 0f
    private var backgroundBlurType = BlurMaskFilter.Blur.NORMAL

    private var radius: Radius? = null
    private var stroke: Stroke? = null
    private var gradient: Gradient? = null
    private var strokeGradient: Gradient? = null
    private val shadows by lazy {
        mutableListOf<Shadow>()
    }

    private var clipOutLine = true
    private var isInitialized = false
    private var defaultAlpha = 0f

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

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

    private fun init(context: Context, attributeSet: AttributeSet?, defStyle: Int) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }

        if (attributeSet == null) {
            return
        }
        
        initAttrsLayout(context, attributeSet, defStyle)
    }

    private fun initAttrsLayout(context: Context, attributeSet: AttributeSet, defStyle: Int) {

        val a = context.obtainStyledAttributes(attributeSet, R.styleable.ShadowLayout, defStyle, 0)

        try {

            clipOutLine = a.getBoolean(R.styleable.ShadowLayout_clipToOutline, true)
            defaultAlpha = a.getFloat(R.styleable.ShadowLayout_android_alpha, 1f)

            stroke = Stroke(
                strokeColor =
                a.getColor(R.styleable.ShadowLayout_stroke_color, ViewHelper.NOT_SET_COLOR),
                strokeWidth = a.getDimension(R.styleable.ShadowLayout_stroke_width, 0f)
            ).apply {
                this.blurType = BlurMaskFilter.Blur.entries.find {
                    it.ordinal == a.getInteger(R.styleable.ShadowLayout_stroke_blur_type, BlurMaskFilter.Blur.NORMAL.ordinal)
                } ?: BlurMaskFilter.Blur.NORMAL
                this.blur = a.getDimension(R.styleable.ShadowLayout_stroke_blur, 0f)
            }

            val allRadius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)
            val radiusHalf = a.getBoolean(R.styleable.ShadowLayout_background_radius_half, false)
            val radiusWeight = a.getFloat(R.styleable.ShadowLayout_background_radius_weight, 1f)

            radius = if (allRadius == 0f) {
                val topLeftRadius =
                    a.getDimension(R.styleable.ShadowLayout_background_top_left_radius, 0f)
                val topRightRadius =
                    a.getDimension(R.styleable.ShadowLayout_background_top_right_radius, 0f)
                val bottomLeftRadius =
                    a.getDimension(R.styleable.ShadowLayout_background_bottom_left_radius, 0f)
                val bottomRightRadius =
                    a.getDimension(R.styleable.ShadowLayout_background_bottom_right_radius, 0f)

                Radius(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius).apply {
                    this.radiusHalf = radiusHalf
                    this.radiusWeight = radiusWeight
                }
            } else {
                Radius(allRadius).apply {
                    this.radiusHalf = radiusHalf
                    this.radiusWeight = radiusWeight
                }
            }

            gradient = Gradient(
                gradientStartColor = a.getColor(
                    R.styleable.ShadowLayout_gradient_start_color,
                    ViewHelper.NOT_SET_COLOR
                ),
                gradientCenterColor = a.getColor(
                    R.styleable.ShadowLayout_gradient_center_color,
                    ViewHelper.NOT_SET_COLOR
                ),
                gradientEndColor = a.getColor(
                    R.styleable.ShadowLayout_gradient_end_color,
                    ViewHelper.NOT_SET_COLOR
                ),
                gradientAngle = a.getInt(R.styleable.ShadowLayout_gradient_angle, -1),
                gradientOffsetX = a.getDimension(R.styleable.ShadowLayout_gradient_offset_x, 0f),
                gradientOffsetY = a.getDimension(R.styleable.ShadowLayout_gradient_offset_y, 0f),
                gradientArray = ViewHelper.parseGradientArray(a.getString(R.styleable.ShadowLayout_gradient_array))
                    ?.toIntArray(),
                gradientPositions = ViewHelper.parseGradientPositions(a.getString(R.styleable.ShadowLayout_gradient_positions))
                    ?.toFloatArray()
            )

            strokeGradient = Gradient(
                gradientStartColor = a.getColor(
                    R.styleable.ShadowLayout_stroke_gradient_start_color,
                    ViewHelper.NOT_SET_COLOR
                ),
                gradientCenterColor = a.getColor(
                    R.styleable.ShadowLayout_stroke_gradient_center_color,
                    ViewHelper.NOT_SET_COLOR
                ),
                gradientEndColor = a.getColor(
                    R.styleable.ShadowLayout_stroke_gradient_end_color,
                    ViewHelper.NOT_SET_COLOR
                ),
                gradientAngle = a.getInt(R.styleable.ShadowLayout_stroke_gradient_angle, -1),
                gradientOffsetX = a.getDimension(
                    R.styleable.ShadowLayout_stroke_gradient_offset_x,
                    0f
                ),
                gradientOffsetY = a.getDimension(
                    R.styleable.ShadowLayout_stroke_gradient_offset_y,
                    0f
                ),
                gradientArray = ViewHelper.parseGradientArray(a.getString(R.styleable.ShadowLayout_gradient_array))
                    ?.toIntArray(),
                gradientPositions = ViewHelper.parseGradientPositions(a.getString(R.styleable.ShadowLayout_gradient_positions))
                    ?.toFloatArray()
            )

            backgroundColor = if (a.hasValue(R.styleable.ShadowLayout_background_color)) {
                a.getColor(
                    R.styleable.ShadowLayout_background_color,
                    Color.parseColor("#ffffffff")
                )
            } else {
                Color.parseColor("#ffffffff")
            }

            backgroundBlur = a.getDimension(R.styleable.ShadowLayout_background_blur, 0f)

            backgroundBlurType = BlurMaskFilter.Blur.entries.find {
                it.ordinal == a.getInteger(R.styleable.ShadowLayout_background_blur_type, BlurMaskFilter.Blur.NORMAL.ordinal)
            } ?: BlurMaskFilter.Blur.NORMAL

            val shadow = Shadow(
                blurSize = a.getDimension(R.styleable.ShadowLayout_shadow_blur, 0f),
                shadowColor = a.getColor(R.styleable.ShadowLayout_shadow_color, ViewHelper.NOT_SET_COLOR),
                shadowOffsetX = a.getDimension(R.styleable.ShadowLayout_shadow_offset_x, 0f),
                shadowOffsetY = a.getDimension(R.styleable.ShadowLayout_shadow_offset_y, 0f),
                shadowSpread = a.getDimension(R.styleable.ShadowLayout_shadow_spread, 0f)
            )

            shadows.add(shadow)

            val shadows = ViewHelper.parseShadowArray(context, a.getString(R.styleable.ShadowLayout_shadow_array))

            if (!shadows.isNullOrEmpty()) {
                this.shadows.addAll(shadows)
            }
        } finally {
            a.recycle()
            isInitialized = true
        }
    }

    override fun dispatchDraw(canvas: Canvas) {

        val offset = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())

        setOutlineAndBackground(offset)

        shadows.forEach { shadow ->
            shadow.updatePath(outlineRect, radius)
            shadow.updatePaint(defaultAlpha)

            if (shadow.isEnable) {
                canvas.drawPath(shadow.path, shadow.paint)
            }
        }

        canvas.save()
        canvas.clipPath(outlinePath)
        canvas.drawPath(backgroundPath, backgroundPaint)
        canvas.restore()
        canvas.drawPath(outlinePath, outlinePaint)

        if (clipOutLine) {
            canvas.clipPath(outlinePath)
        }

        super.dispatchDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val offset = RectF(0f, 0f, abs(right - left).toFloat(), abs(bottom - top).toFloat())

        setOutlineAndBackground(offset)

        shadows.forEach { shadow ->
            shadow.updatePath(outlineRect, radius)
            shadow.updatePaint(defaultAlpha)
        }

        for (i in 0 until childCount) {
            getChildAt(i)?.alpha = defaultAlpha
        }
    }

    override fun setAlpha(alpha: Float) {

        if (!isInitialized) {
            return
        }

        defaultAlpha = alpha

        invalidate()

        for (i in 0 until childCount) {
            getChildAt(i)?.alpha = alpha
        }
    }

    override fun getAlpha(): Float {
        return defaultAlpha
    }

    fun updateBackgroundColor(color: Int) {
        backgroundColor = color
        invalidate()
    }

    fun updateRadius(radius: Float) {
        this.radius?.updateRadius(radius)
        invalidate()
    }

    fun updateRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        this.radius?.updateRadius(topLeft, topRight, bottomLeft, bottomRight)
        invalidate()
    }

    fun addBackgroundShadow(blurSize: Float, offsetX: Float, offsetY: Float, shadowColor: Int) {
        val shadow = Shadow(blurSize, shadowColor, offsetX, offsetY, 0f)
        shadows.add(shadow)
        invalidate()
    }

    fun addBackgroundShadow(
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        spread: Float,
        shadowColor: Int
    ) {
        val shadow = Shadow(blurSize, shadowColor, offsetX, offsetY, spread)
        shadows.add(shadow)
        invalidate()
    }

    fun removeBackgroundShadowLast() {
        shadows.removeLastOrNull()
        invalidate()
    }

    fun removeBackgroundShadowFirst() {
        shadows.removeFirstOrNull()
        invalidate()
    }

    fun removeAllBackgroundShadows() {
        shadows.clear()
        invalidate()
    }

    fun removeBackgroundShadow(position: Int) {
        shadows.removeAt(position)
        invalidate()
    }

    fun updateBackgroundShadow(position: Int, shadow: Shadow) {
        shadows[position] = shadow
        invalidate()
    }

    fun updateBackgroundShadow(
        position: Int,
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        color: Int
    ) {
        shadows[position].apply {
            this.blurSize = blurSize
            this.shadowColor = color
            this.shadowOffsetX = offsetX
            this.shadowOffsetY = offsetY
        }
        invalidate()
    }

    fun updateBackgroundShadow(
        position: Int,
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        spread: Float,
        color: Int
    ) {
        shadows[position].apply {
            this.blurSize = blurSize
            this.shadowColor = color
            this.shadowOffsetX = offsetX
            this.shadowOffsetY = offsetY
            this.shadowSpread = spread
        }
        invalidate()
    }

    fun updateBackgroundShadow(shadow: Shadow) {
        updateBackgroundShadow(0, shadow)
    }

    fun updateBackgroundShadow(blurSize: Float, offsetX: Float, offsetY: Float, color: Int) {
        updateBackgroundShadow(0, blurSize, offsetX, offsetY, color)
    }

    fun updateBackgroundShadow(
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        spread: Float,
        color: Int
    ) {
        updateBackgroundShadow(0, blurSize, offsetX, offsetY, spread, color)
    }

    fun updateStrokeWidth(strokeWidth: Float) {
        stroke?.updateStrokeWidth(strokeWidth)
        invalidate()
    }

    fun updateStrokeColor(color: Int) {
        stroke?.updateStrokeColor(color)
        invalidate()
    }

    fun updateGradientColor(startColor: Int, centerColor: Int, endColor: Int) {
        this.gradient?.updateGradientColor(startColor, centerColor, endColor)
        invalidate()
    }

    fun updateGradientColor(startColor: Int, endColor: Int) {
        this.gradient?.updateGradientColor(startColor, endColor)
        invalidate()
    }

    fun updateGradientAngle(angle: Int) {
        this.gradient?.updateGradientAngle(angle)
        invalidate()
    }

    fun updateLocalMatrix(matrix: Matrix?) {
        this.gradient?.updateLocalMatrix(matrix)
        invalidate()
    }

    fun updateGradientOffsetX(offset: Float) {
        this.gradient?.updateGradientOffsetX(offset)
        invalidate()
    }

    fun updateGradientOffsetY(offset: Float) {
        this.gradient?.updateGradientOffsetY(offset)
        invalidate()
    }

    fun updateStrokeGradientColor(startColor: Int, centerColor: Int, endColor: Int) {
        this.strokeGradient?.updateGradientColor(startColor, centerColor, endColor)
        invalidate()
    }

    fun updateStrokeGradientColor(startColor: Int, endColor: Int) {
        this.strokeGradient?.updateGradientColor(startColor, endColor)
        invalidate()
    }

    fun updateStrokeGradientAngle(angle: Int) {
        this.strokeGradient?.updateGradientAngle(angle)
        invalidate()
    }

    fun updateStrokeLocalMatrix(matrix: Matrix?) {
        this.strokeGradient?.updateLocalMatrix(matrix)
        invalidate()
    }

    fun updateStrokeGradientOffsetX(offset: Float) {
        this.strokeGradient?.updateGradientOffsetX(offset)
        invalidate()
    }

    fun updateStrokeGradientOffsetY(offset: Float) {
        this.strokeGradient?.updateGradientOffsetY(offset)
        invalidate()
    }

    fun updateBackgroundRadiusHalf(enable: Boolean) {
        this.radius?.radiusHalf = enable
        invalidate()
    }

    fun updateBackgroundBlur(blur: Float) {
        this.backgroundBlur = blur
        invalidate()
    }

    fun updateBackgroundBlurType(blurType: BlurMaskFilter.Blur) {
        this.backgroundBlurType = blurType
        invalidate()
    }

    fun updateStrokeBlur(blur: Float) {
        this.stroke?.blur = blur
        invalidate()
    }

    fun updateStrokeBlurType(blurType: BlurMaskFilter.Blur) {
        this.stroke?.blurType = blurType
        invalidate()
    }

    fun getGradientInfo(): Gradient? {
        return this.gradient
    }

    fun getRadiusInfo(): Radius? {
        return this.radius
    }

    fun getStrokeInfo(): Stroke? {
        return this.stroke
    }

    fun setOutlineAndBackground(offset: RectF) {

        val calStrokeWidth = stroke?.takeIf { it.isEnable }?.strokeWidth?.div(2f) ?: 0f

        this.outlineRect.set(
            RectF(
                offset.left - calStrokeWidth,
                offset.top - calStrokeWidth,
                offset.right + calStrokeWidth,
                offset.bottom + calStrokeWidth
            )
        )

        with(outlinePaint) {

            isAntiAlias = true

            if (stroke?.isEnable == true) {
                style = Paint.Style.STROKE
                color = stroke!!.strokeColor
                outlinePaint.strokeWidth = stroke!!.strokeWidth
                shader = if (strokeGradient?.isEnable == true) {
                    strokeGradient?.getGradientShader(
                        outlineRect.left,
                        outlineRect.top,
                        outlineRect.right,
                        outlineRect.bottom
                    )
                } else {
                    null
                }

                if (stroke!!.blur != 0f) {
                    maskFilter = BlurMaskFilter(stroke!!.blur, stroke!!.blurType)
                } else {
                    maskFilter = null
                }

                if (ViewHelper.onSetAlphaFromColor(defaultAlpha, stroke!!.strokeColor)) {
                    alpha = ViewHelper.getIntAlpha(defaultAlpha)
                }
            } else {
                color = backgroundColor
                strokeWidth = 0f
                style = Paint.Style.FILL

                shader = if (gradient?.isEnable == true) {
                    gradient?.getGradientShader(
                        offset.left,
                        offset.top,
                        offset.right,
                        offset.bottom
                    )
                } else {
                    null
                }

                if (ViewHelper.onSetAlphaFromColor(defaultAlpha, backgroundColor)) {
                    alpha = ViewHelper.getIntAlpha(defaultAlpha)
                }

                if (backgroundBlur != 0f) {
                    maskFilter = BlurMaskFilter(backgroundBlur, backgroundBlurType)
                } else {
                    maskFilter = null
                }
            }
        }

        with(backgroundPaint) {
            isAntiAlias = true
            color = backgroundColor
            style = Paint.Style.FILL

            shader = if (gradient?.isEnable == true) {
                gradient?.getGradientShader(
                    offset.left + calStrokeWidth,
                    offset.top + calStrokeWidth,
                    offset.right - calStrokeWidth,
                    offset.bottom - calStrokeWidth
                )
            } else {
                null
            }

            if (backgroundBlur != 0f) {
                maskFilter = BlurMaskFilter(backgroundBlur, backgroundBlurType)
            } else {
                maskFilter = null
            }

            if (ViewHelper.onSetAlphaFromColor(defaultAlpha, backgroundColor)) {
                alpha = ViewHelper.getIntAlpha(defaultAlpha)
            }
        }

        outlinePath.apply {
            reset()

            if (radius == null) {
                addRect(offset, Path.Direction.CW)
            } else {
                val height = offset.height()
                addRoundRect(offset, radius!!.getRadiusArray(height), Path.Direction.CW)
            }

            close()
        }

        backgroundPath.apply {
            reset()
            addRect(offset, Path.Direction.CW)
            close()
        }
    }
}