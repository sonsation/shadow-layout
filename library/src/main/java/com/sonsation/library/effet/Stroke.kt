package com.sonsation.library.effet

import com.sonsation.library.utils.ViewHelper

class Stroke {

    val isEnable: Boolean
        get() = strokeWidth != 0f && strokeColor != ViewHelper.NOT_SET_COLOR
    var strokeWidth: Float = 0f
    var strokeColor: Int = ViewHelper.NOT_SET_COLOR
    var gradient: Gradient? = null
}