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

    private val viewHelper by lazy { ViewHelper(context) }
    private val background by lazy { Background() }
    private val gradient by lazy { Gradient() }
    private val backgroundShadows by lazy { mutableListOf<Shadow>() }
    private val foregroundShadows by lazy { mutableListOf<Shadow>() }
    private val customEffects by lazy { mutableListOf<Effect>() }

    private val effects: List<Effect>
        get() = listOf<Effect>(
            *backgroundShadows.toTypedArray(),
            background,
            *foregroundShadows.toTypedArray(),
            gradient,
            *customEffects.toTypedArray())

    private var clipOutLine = false

    private var isInit = false
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

            clipOutLine = a.getBoolean(R.styleable.ShadowLayout_clipToOutline, false)
            defaultAlpha = a.getFloat(R.styleable.ShadowLayout_android_alpha, 1f)

            //default background settings
            val backgroundColor = if (a.hasValue(R.styleable.ShadowLayout_background_color)) {
                a.getColor(
                    R.styleable.ShadowLayout_background_color,
                    Color.parseColor("#ffffffff")
                )
            } else {
                a.getColor(
                    R.styleable.ShadowLayout_android_background,
                    Color.parseColor("#ffffffff")
                )
            }

            viewHelper.strokeInfo = Stroke().apply {
                strokeColor =
                    a.getColor(R.styleable.ShadowLayout_stroke_color, ViewHelper.NOT_SET_COLOR)
                strokeWidth = a.getDimension(R.styleable.ShadowLayout_stroke_width, 0f)

                gradient = Gradient().apply {

                    val gradientStartColor = a.getColor(
                        R.styleable.ShadowLayout_stroke_gradient_start_color,
                        ViewHelper.NOT_SET_COLOR
                    )
                    val gradientCenterColor = a.getColor(
                        R.styleable.ShadowLayout_stroke_gradient_center_color,
                        ViewHelper.NOT_SET_COLOR
                    )
                    val gradientEndColor = a.getColor(
                        R.styleable.ShadowLayout_stroke_gradient_end_color,
                        ViewHelper.NOT_SET_COLOR
                    )
                    val gradientOffsetX =
                        a.getDimension(R.styleable.ShadowLayout_stroke_gradient_offset_x, 0f)
                    val gradientOffsetY =
                        a.getDimension(R.styleable.ShadowLayout_stroke_gradient_offset_y, 0f)
                    val gradientAngle = a.getInt(R.styleable.ShadowLayout_stroke_gradient_angle, -1)

                    val gradients =
                        viewHelper.parseGradientArray(a.getString(R.styleable.ShadowLayout_gradient_array))
                    val gradientPositions =
                        viewHelper.parseGradientPositions(a.getString(R.styleable.ShadowLayout_gradient_positions))

                    init(
                        gradientAngle,
                        gradientStartColor,
                        gradientCenterColor,
                        gradientEndColor,
                        gradientOffsetX,
                        gradientOffsetY,
                        gradients?.toIntArray(),
                        gradientPositions?.toFloatArray()
                    )
                }
            }

            background.init(viewHelper.strokeInfo, backgroundColor)
            background.updateAlpha(defaultAlpha)

            viewHelper.radiusInfo = Radius().apply {
                radius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)

                if (radius == 0f) {
                    topLeftRadius =
                        a.getDimension(R.styleable.ShadowLayout_background_top_left_radius, 0f)
                    topRightRadius =
                        a.getDimension(R.styleable.ShadowLayout_background_top_right_radius, 0f)
                    bottomLeftRadius =
                        a.getDimension(R.styleable.ShadowLayout_background_bottom_left_radius, 0f)
                    bottomRightRadius =
                        a.getDimension(R.styleable.ShadowLayout_background_bottom_right_radius, 0f)
                }

                smoothCorner = a.getBoolean(R.styleable.ShadowLayout_smooth_corner, false)
                radiusWeight = a.getFloat(R.styleable.ShadowLayout_background_radius_weight, 1f)
            }

            backgroundShadows.apply {

                val shadow = Shadow().apply {
                    val shadowColor = a.getColor(
                        R.styleable.ShadowLayout_shadow_color,
                        ViewHelper.NOT_SET_COLOR
                    )
                    val shadowOffsetX = a.getDimension(R.styleable.ShadowLayout_shadow_offset_x, 0f)
                    val shadowOffsetY = a.getDimension(R.styleable.ShadowLayout_shadow_offset_y, 0f)
                    val shadowBlurSize = a.getDimension(R.styleable.ShadowLayout_shadow_blur, 0f)
                    val shadowSpread = a.getDimension(R.styleable.ShadowLayout_shadow_spread, 0f)

                    init(
                        true,
                        shadowBlurSize,
                        shadowOffsetX,
                        shadowOffsetY,
                        shadowSpread,
                        shadowColor
                    )

                    updateAlpha(defaultAlpha)
                }

                add(shadow)

                val shadows = viewHelper.parseShadowArray(
                    true,
                    a.getString(R.styleable.ShadowLayout_shadow_array)
                )

                if (shadows != null) {
                    shadows.forEach {
                        it.updateAlpha(defaultAlpha)
                    }
                    addAll(shadows)
                }
            }

            foregroundShadows.apply {

                val shadow = Shadow().apply {
                    val shadowColor = a.getColor(
                        R.styleable.ShadowLayout_inner_shadow_color,
                        ViewHelper.NOT_SET_COLOR
                    )
                    val shadowOffsetX =
                        a.getDimension(R.styleable.ShadowLayout_inner_shadow_offset_x, 0f)
                    val shadowOffsetY =
                        a.getDimension(R.styleable.ShadowLayout_inner_shadow_offset_y, 0f)
                    val shadowBlurSize =
                        a.getDimension(R.styleable.ShadowLayout_inner_shadow_blur, 0f)
                    val shadowSpread =
                        a.getDimension(R.styleable.ShadowLayout_inner_shadow_spread, 0f)

                    init(
                        false,
                        shadowBlurSize,
                        shadowOffsetX,
                        shadowOffsetY,
                        shadowSpread,
                        shadowColor
                    )

                    val shadowType = a.getString(R.styleable.ShadowLayout_inner_shadow_type)

                    if (!shadowType.isNullOrEmpty()) {
                        setShadowType(shadowType)
                    }

                    updateAlpha(defaultAlpha)
                }

                add(shadow)

                val shadows = viewHelper.parseShadowArray(
                    false,
                    a.getString(R.styleable.ShadowLayout_inner_shadow_array)
                )

                if (shadows != null) {
                    shadows.forEach {
                        it.updateAlpha(defaultAlpha)
                    }
                    addAll(shadows)
                }
            }

            //gradient Settings
            val gradientStartColor = a.getColor(
                R.styleable.ShadowLayout_gradient_start_color,
                ViewHelper.NOT_SET_COLOR
            )
            val gradientCenterColor = a.getColor(
                R.styleable.ShadowLayout_gradient_center_color,
                ViewHelper.NOT_SET_COLOR
            )
            val gradientEndColor = a.getColor(
                R.styleable.ShadowLayout_gradient_end_color,
                ViewHelper.NOT_SET_COLOR
            )
            val gradientOffsetX = a.getDimension(R.styleable.ShadowLayout_gradient_offset_x, 0f)
            val gradientOffsetY = a.getDimension(R.styleable.ShadowLayout_gradient_offset_y, 0f)
            val gradientAngle = a.getInt(R.styleable.ShadowLayout_gradient_angle, -1)

            val gradients =
                viewHelper.parseGradientArray(a.getString(R.styleable.ShadowLayout_gradient_array))
            val gradientPositions =
                viewHelper.parseGradientPositions(a.getString(R.styleable.ShadowLayout_gradient_positions))

            gradient.init(
                gradientAngle,
                gradientStartColor,
                gradientCenterColor,
                gradientEndColor,
                gradientOffsetX,
                gradientOffsetY,
                gradients?.toIntArray(),
                gradientPositions?.toFloatArray()
            )
        } finally {
            a.recycle()
            isInit = true
        }
    }

    override fun dispatchDraw(canvas: Canvas) {

        with(viewHelper) {
            effects.forEach {
                updateOffset(it, canvas.width, canvas.height)
                canvas.drawEffect(it)
            }
        }

        if (clipOutLine) {
            canvas.clipPath(background.path)
        }

        super.dispatchDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        updatePadding()

        val width = abs(right - left)
        val height = abs(bottom - top)

        with(viewHelper) {
            effects.forEach {
                updateOffset(it, width, height)
            }
        }

        for (i in 0 until childCount) {
            getChildAt(i)?.alpha = defaultAlpha
        }
    }

    override fun setAlpha(alpha: Float) {

        if (!isInit)
            return

        defaultAlpha = alpha

        effects.forEach {
            it.updateAlpha(alpha)
        }

        invalidate()

        for (i in 0 until childCount) {
            getChildAt(i)?.alpha = alpha
        }
    }

    override fun getAlpha(): Float {
        return defaultAlpha
    }

    private fun updatePadding() {

    }

    fun updateBackgroundColor(color: Int) {
        background.setBackgroundColor(color)
        invalidate()
    }

    fun updateRadius(radius: Float) {

        if (viewHelper.radiusInfo == null) {
            viewHelper.radiusInfo = Radius()
        }

        viewHelper.radiusInfo!!.updateRadius(radius)
        invalidate()
    }

    fun updateRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        if (viewHelper.radiusInfo == null) {
            viewHelper.radiusInfo = Radius()
        }

        viewHelper.radiusInfo!!.updateRadius(topLeft, topRight, bottomLeft, bottomRight)
        invalidate()
    }

    fun addBackgroundShadow(blurSize: Float, offsetX: Float, offsetY: Float, shadowColor: Int) {
        val shadow = Shadow().apply {
            init(true, blurSize, offsetX, offsetY, 0f, shadowColor)
        }
        backgroundShadows.add(shadow)
        invalidate()
    }

    fun addBackgroundShadow(
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        spread: Float,
        shadowColor: Int
    ) {
        val shadow = Shadow().apply {
            init(true, blurSize, offsetX, offsetY, spread, shadowColor)
        }
        backgroundShadows.add(shadow)
        invalidate()
    }

    fun removeBackgroundShadowLast() {
        backgroundShadows.removeLastOrNull()
        invalidate()
    }

    fun removeBackgroundShadowFirst() {
        backgroundShadows.removeFirstOrNull()
        invalidate()
    }

    fun removeAllBackgroundShadows() {
        backgroundShadows.clear()
        invalidate()
    }

    fun removeBackgroundShadow(position: Int) {
        backgroundShadows.removeAt(position)
        invalidate()
    }

    fun updateBackgroundShadow(position: Int, shadow: Shadow) {
        backgroundShadows[position] = shadow
        invalidate()
    }

    fun updateBackgroundShadow(
        position: Int,
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        color: Int
    ) {
        backgroundShadows[position].init(true, blurSize, offsetX, offsetY, 0f, color)
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
        backgroundShadows[position].init(true, blurSize, offsetX, offsetY, spread, color)
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

    fun getBackgroundShadowCount(): Int {
        return backgroundShadows.size
    }

    fun addForegroundShadow(blurSize: Float, shadowColor: Int) {
        val shadow = Shadow().apply {
            init(false, blurSize, 0f, 0f, 0f, shadowColor)
        }
        foregroundShadows.add(shadow)
        invalidate()
    }

    fun addForegroundShadow(
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        spread: Float,
        shadowColor: Int
    ) {
        val shadow = Shadow().apply {
            init(false, blurSize, offsetX, offsetY, spread, shadowColor)
        }
        foregroundShadows.add(shadow)
        invalidate()
    }

    fun removeForegroundShadowLast() {
        foregroundShadows.removeLastOrNull()
        invalidate()
    }

    fun removeForegroundShadowFirst() {
        foregroundShadows.removeFirstOrNull()
        invalidate()
    }

    fun removeAllForegroundShadows() {
        foregroundShadows.clear()
        invalidate()
    }

    fun removeForegroundShadow(position: Int) {
        foregroundShadows.removeAt(position)
        invalidate()
    }

    fun updateForegroundShadow(position: Int, shadow: Shadow) {
        foregroundShadows[position] = shadow
        invalidate()
    }

    fun updateForegroundShadow(
        position: Int,
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        color: Int
    ) {
        foregroundShadows[position].init(false, blurSize, offsetX, offsetY, 0f, color)
        invalidate()
    }

    fun updateForegroundShadow(
        position: Int,
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        spread: Float,
        color: Int
    ) {
        foregroundShadows[position].init(false, blurSize, offsetX, offsetY, spread, color)
        invalidate()
    }

    fun updateForegroundShadow(shadow: Shadow) {
        updateForegroundShadow(0, shadow)
    }

    fun updateForegroundShadow(blurSize: Float, offsetX: Float, offsetY: Float, color: Int) {
        updateForegroundShadow(0, blurSize, offsetX, offsetY, color)
    }

    fun updateForegroundShadow(
        blurSize: Float,
        offsetX: Float,
        offsetY: Float,
        spread: Float,
        color: Int
    ) {
        updateForegroundShadow(0, blurSize, offsetX, offsetY, spread, color)
    }

    fun getForegroundShadowCount(): Int {
        return foregroundShadows.size
    }

    fun updateStrokeWidth(strokeWidth: Float) {
        background.updateStrokeWidth(strokeWidth)
        invalidate()
    }

    fun updateStrokeColor(color: Int) {
        background.updateStrokeColor(color)
        invalidate()
    }

    fun updateGradientColor(startColor: Int, centerColor: Int, endColor: Int) {
        gradient.updateGradientColor(startColor, centerColor, endColor)
        invalidate()
    }

    fun updateGradientColor(startColor: Int, endColor: Int) {
        gradient.updateGradientColor(startColor, endColor)
        invalidate()
    }

    fun updateGradientAngle(angle: Int) {
        gradient.updateGradientAngle(angle)
        invalidate()
    }

    fun updateLocalMatrix(matrix: Matrix?) {
        gradient.updateLocalMatrix(matrix)
        invalidate()
    }

    fun updateGradientOffsetX(offset: Float) {
        gradient.updateGradientOffsetX(offset)
        invalidate()
    }

    fun updateGradientOffsetY(offset: Float) {
        gradient.updateGradientOffsetY(offset)
        invalidate()
    }

    fun updateSmoothCornerStatus(enable: Boolean) {

        if (viewHelper.radiusInfo == null) {
            return
        }

        viewHelper.radiusInfo!!.smoothCorner = enable

        invalidate()
    }

    fun getGradientInfo(): Gradient {
        return gradient
    }

    fun getRadiusInfo(): Radius? {
        return viewHelper.radiusInfo
    }

    fun getStrokeInfo(): Stroke? {
        return viewHelper.strokeInfo
    }

    fun addCustomEffect(effect: Effect) {
        customEffects.add(effect)
        invalidate()
    }

    fun removeAllCustomEffects() {
        customEffects.clear()
        invalidate()
    }

    fun removeCustomEffect(index: Int) {
        customEffects.removeAt(index)
        invalidate()
    }

    fun updateCustomEffect(index: Int, effect: Effect) {
        customEffects[index] = effect
        invalidate()
    }
}