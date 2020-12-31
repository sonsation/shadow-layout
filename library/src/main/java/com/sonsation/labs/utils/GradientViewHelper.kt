package com.sonsation.labs.utils

import android.content.Context
import android.graphics.*

class GradientViewHelper(context: Context) : ViewHelper(context) {

    override fun draw() {

        if (gradientStore == null)
            return

        gradientStore?.let {
            if (!it.enableGradient)
                return

            if (it.gradientMargin != 0f) {
                updateOffset(it.gradientMargin, -it.gradientMargin, it.gradientMargin, -it.gradientMargin)
            }

            shader = it.getGradientShader(offsetLeft, offsetTop, offsetRight, offsetBottom)
        }

        super.draw()
    }
}