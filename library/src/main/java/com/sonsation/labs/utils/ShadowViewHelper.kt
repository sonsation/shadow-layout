package com.sonsation.labs.utils

import android.content.Context
import android.graphics.BlurMaskFilter
import com.sonsation.labs.stores.ShadowStore

class ShadowViewHelper(context: Context) : ViewHelper(context) {

    var shadowStore: ShadowStore? = null

    override fun draw() {

        if (shadowStore == null)
            return

        shadowStore?.let {
            val dx = it.shadowOffsetX
            val dy = it.shadowOffsetY
            color = it.shadowColor
            isAntiAlias = true
            maskFilter = BlurMaskFilter(it.shadowBlur, BlurMaskFilter.Blur.NORMAL)

            updateOffset(dx,dy, dx, dy)
        }

        super.draw()
    }
}