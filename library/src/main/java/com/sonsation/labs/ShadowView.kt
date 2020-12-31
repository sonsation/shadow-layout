package com.sonsation.labs

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.sonsation.labs.utils.BackgroundViewHelper
import com.sonsation.labs.stores.ShadowStore
import com.sonsation.labs.utils.ShadowViewHelper

class ShadowView : View {

    private val shadowStore by lazy { ShadowStore() }
    private val shadowViewHelper by lazy { ShadowViewHelper(context) }
    private var backgroundViewHelper: BackgroundViewHelper? = null

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
        initAttrsView(context, attributeSet, defStyle)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }

        val shadowLayout = rootView as? ShadowLayout ?: return

        backgroundViewHelper = shadowLayout.backgroundViewHelper
    }

    private fun initAttrsView(context: Context, attributeSet: AttributeSet, defStyle: Int) {

        val a = context.obtainStyledAttributes(attributeSet, R.styleable.ShadowView, defStyle, 0)

        try {
            shadowStore.shadowEnable = a.getBoolean(R.styleable.ShadowView_shadow_enable, false)
            shadowStore.shadowColor = a.getColor(
                R.styleable.ShadowView_shadow_color,
                -1
            )
            shadowStore.shadowOffsetX = a.getDimension(R.styleable.ShadowView_shadow_offset_x, 0f)
            shadowStore.shadowOffsetY = a.getDimension(R.styleable.ShadowView_shadow_offset_y, 0f)
            shadowStore.shadowBlur = a.getDimension(R.styleable.ShadowView_shadow_blur, 0f)

            shadowViewHelper.shadowStore = shadowStore
        } finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}