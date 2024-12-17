package com.sonsation.library.model

import android.graphics.RectF

data class Offset(
    var left: Float = 0f,
    var top: Float = 0f,
    var right: Float = 0f,
    var bottom: Float = 0f
) : RectF(left, top, right, bottom)
