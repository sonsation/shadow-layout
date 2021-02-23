package com.sonsation.library

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.sonsation.library.effet.*
import com.sonsation.library.utils.Util
import com.sonsation.library.utils.ViewHelper

class ShadowLayout : FrameLayout {

    private val viewHelper by lazy { ViewHelper(context) }
    private val background by lazy { Background() }
    private val gradient by lazy { Gradient() }

    private val backgroundShadowList by lazy { mutableListOf<Shadow>() }
    private val foregroundShadowList by lazy { mutableListOf<Shadow>() }

    private var isInit = false

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

            val alpha = a.getFloat(R.styleable.ShadowLayout_android_alpha, 1f)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                clipToOutline = a.getBoolean(R.styleable.ShadowLayout_clipToOutline, false)
            }

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
                strokeColor = a.getColor(R.styleable.ShadowLayout_stroke_color, ViewHelper.NOT_SET_COLOR)
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
                    val gradientOffsetX = a.getDimension(R.styleable.ShadowLayout_stroke_gradient_offset_x, 0f)
                    val gradientOffsetY = a.getDimension(R.styleable.ShadowLayout_stroke_gradient_offset_y, 0f)
                    val gradientAngle = a.getInt(R.styleable.ShadowLayout_stroke_gradient_angle, -1)

                    init(gradientAngle, gradientStartColor, gradientCenterColor, gradientEndColor,
                            gradientOffsetX, gradientOffsetY)
                }
            }

            background.init(viewHelper.strokeInfo, alpha, backgroundColor)

            viewHelper.radiusInfo = Radius().apply {
                radius = a.getDimension(R.styleable.ShadowLayout_background_radius, 0f)

                if (radius == 0f) {
                    topLeftRadius = a.getDimension(R.styleable.ShadowLayout_background_top_left_radius, 0f)
                    topRightRadius = a.getDimension(R.styleable.ShadowLayout_background_top_right_radius, 0f)
                    bottomLeftRadius = a.getDimension(R.styleable.ShadowLayout_background_bottom_left_radius, 0f)
                    bottomRightRadius = a.getDimension(R.styleable.ShadowLayout_background_bottom_right_radius, 0f)
                }
            }

            backgroundShadowList.apply {

                val shadow = Shadow().apply {
                    val shadowColor = a.getColor(
                            R.styleable.ShadowLayout_shadow_color,
                            ViewHelper.NOT_SET_COLOR
                    )
                    val shadowOffsetX = a.getDimension(R.styleable.ShadowLayout_shadow_offset_x, 0f)
                    val shadowOffsetY = a.getDimension(R.styleable.ShadowLayout_shadow_offset_y, 0f)
                    val shadowBlurSize = a.getDimension(R.styleable.ShadowLayout_shadow_blur, 0f)

                    init(true, alpha, shadowBlurSize, shadowOffsetX, shadowOffsetY, shadowColor)
                }

                add(shadow)

                val shadows = viewHelper.parseShadowArray(true, alpha, a.getString(R.styleable.ShadowLayout_shadow_array))

                if (shadows != null) {
                    addAll(shadows)
                }
            }

            foregroundShadowList.apply {

                val shadow = Shadow().apply {
                    val shadowColor = a.getColor(
                            R.styleable.ShadowLayout_inner_shadow_color,
                            ViewHelper.NOT_SET_COLOR
                    )
                    val shadowBlurSize = a.getDimension(R.styleable.ShadowLayout_inner_shadow_blur, 0f)

                    init(false, alpha, shadowBlurSize, 0f, 0f, shadowColor)

                    val strokeType = a.getString(R.styleable.ShadowLayout_inner_shadow_type)

                    if (!strokeType.isNullOrEmpty()) {
                        setShadowType(strokeType)
                    }
                }

                add(shadow)

                val shadows = viewHelper.parseShadowArray(false, alpha, a.getString(R.styleable.ShadowLayout_inner_shadow_array))

                if (shadows != null) {
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

            gradient.init(gradientAngle, gradientStartColor, gradientCenterColor, gradientEndColor,
                    gradientOffsetX, gradientOffsetY)

        } finally {
            a.recycle()
            isInit = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null)
            return

        viewHelper.updateCanvas(canvas)

        backgroundShadowList.forEach {
            viewHelper.drawEffect(it)
        }

        viewHelper.drawEffect(background)
        viewHelper.drawEffect(gradient)

        foregroundShadowList.forEach {
            viewHelper.drawEffect(it)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        updatePadding()
    }

    private fun updatePadding() {

    }

    fun updateBackgroundColor(color: Int) {
        background.setBackgroundColor(color)
        postInvalidate()
    }

    fun updateRadius(radius: Float) {

        if (viewHelper.radiusInfo == null) {
            viewHelper.radiusInfo = Radius()
        }

        viewHelper.radiusInfo!!.updateRadius(radius)
        postInvalidate()
    }

    fun updateRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        if (viewHelper.radiusInfo == null) {
            viewHelper.radiusInfo = Radius()
        }

        viewHelper.radiusInfo!!.updateRadius(topLeft, topRight, bottomLeft, bottomRight)
        postInvalidate()
    }

    fun addBackgroundShadow(blurSize: Float, offsetX: Float, offsetY: Float, shadowColor: Int) {
        val shadow = Shadow().apply {
            init(true, 1f, blurSize, offsetX, offsetY, shadowColor)
        }
        backgroundShadowList.add(shadow)
        postInvalidate()
    }

    fun removeBackgroundShadowLast() {
        backgroundShadowList.removeLastOrNull()
        postInvalidate()
    }

    fun removeBackgroundShadowFirst() {
        backgroundShadowList.removeFirstOrNull()
        postInvalidate()
    }

    fun removeAllBackgroundShadows() {
        backgroundShadowList.clear()
        postInvalidate()
    }

    fun removeBackgroundShadow(position: Int) {
        backgroundShadowList.removeAt(position)
        postInvalidate()
    }

    fun updateBackgroundShadow(position: Int, shadow: Shadow) {
        backgroundShadowList[position] = shadow
        postInvalidate()
    }

    fun updateBackgroundShadow(position: Int, blurSize: Float, offsetX: Float, offsetY: Float, color: Int) {
        backgroundShadowList[position].init(true, Util.intAlphaToFloatAlpha(backgroundShadowList[position].alpha), blurSize, offsetX, offsetY, color)
        postInvalidate()
    }

    fun updateBackgroundShadow(shadow: Shadow) {
        updateBackgroundShadow(0, shadow)
    }

    fun updateBackgroundShadow(blurSize: Float, offsetX: Float, offsetY: Float, color: Int) {
        updateBackgroundShadow(0, blurSize, offsetX, offsetY, color)
    }

    fun getBackgroundShadowCount(): Int {
        return backgroundShadowList.size
    }

    fun addForegroundShadow(blurSize: Float, shadowColor: Int) {
        val shadow = Shadow().apply {
            init(true, 1f, blurSize, 0f, 0f, shadowColor)
        }
        backgroundShadowList.add(shadow)
        postInvalidate()
    }

    fun removeForegroundShadowLast() {
        backgroundShadowList.removeLastOrNull()
        postInvalidate()
    }

    fun removeForegroundShadowFirst() {
        backgroundShadowList.removeFirstOrNull()
        postInvalidate()
    }

    fun removeAllForegroundShadows() {
        backgroundShadowList.clear()
        postInvalidate()
    }

    fun removeForegroundShadow(position: Int) {
        backgroundShadowList.removeAt(position)
        postInvalidate()
    }

    fun updateForegroundShadow(position: Int, shadow: Shadow) {
        backgroundShadowList[position] = shadow.apply {
            updateShadowOffsetX(0f)
            updateShadowOffsetY(0f)
        }
        postInvalidate()
    }

    fun updateForegroundShadow(position: Int, blurSize: Float, color: Int) {
        backgroundShadowList[position].init(false, Util.intAlphaToFloatAlpha(backgroundShadowList[position].alpha), blurSize, 0f, 0f, color)
        postInvalidate()
    }

    fun updateForegroundShadow(shadow: Shadow) {
        shadow.apply {
            updateShadowOffsetX(0f)
            updateShadowOffsetY(0f)
        }
        updateForegroundShadow(0, shadow)
    }

    fun updateForegroundShadow(blurSize: Float, color: Int) {
        updateForegroundShadow(0, blurSize, color)
    }

    fun getForegroundShadowCount(): Int {
        return backgroundShadowList.size
    }

    fun updateStrokeWidth(strokeWidth: Float) {
        background.updateStrokeWidth(strokeWidth)
        postInvalidate()
    }

    fun updateStrokeColor(color: Int) {
        background.updateStrokeColor(color)
        postInvalidate()
    }

    fun updateGradientColor(startColor: Int, centerColor: Int, endColor: Int) {
        gradient.updateGradientColor(startColor, centerColor, endColor)
        postInvalidate()
    }

    fun updateGradientColor(startColor: Int, endColor: Int) {
        gradient.updateGradientColor(startColor, endColor)
        postInvalidate()
    }

    fun updateGradientAngle(angle: Int) {
        gradient.updateGradientAngle(angle)
        postInvalidate()
    }

    fun updateGradientOffsetX(offset: Float) {
        gradient.updateGradientOffsetX(offset)
        postInvalidate()
    }

    fun updateGradientOffsetY(offset: Float) {
        gradient.updateGradientOffsetY(offset)
        postInvalidate()
    }

    override fun setBackgroundColor(color: Int) {
        updateBackgroundColor(color)
    }

    override fun setAlpha(alpha: Float) {

        if (!isInit)
            return

        backgroundShadowList.forEach {
            it.updateAlpha(alpha)
        }
        background.updateAlpha(alpha)
        postInvalidate()
    }
}