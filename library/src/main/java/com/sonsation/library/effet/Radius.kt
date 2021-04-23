package com.sonsation.library.effet

class Radius {

    var radius = 0f
    var topLeftRadius = 0f
    var topRightRadius = 0f
    var bottomLeftRadius = 0f
    var bottomRightRadius = 0f
    var smoothCorner = false

    fun updateRadius(radius: Float) {
        this.radius = radius
    }

    fun updateRadius(tl: Float, tr: Float, bl: Float, br: Float) {
        this.topLeftRadius = tl
        this.topRightRadius = tr
        this.bottomLeftRadius = bl
        this.bottomRightRadius = br
    }

    fun setSmoothCorner(height: Int) {
        if (radius != 0f) {
            radius = height.toFloat() / 2f
        } else {

            if (topLeftRadius != 0f) {
                topLeftRadius = height.toFloat() / 2f
            }

            if (topRightRadius != 0f) {
                topRightRadius = height.toFloat() / 2f
            }

            if (bottomLeftRadius != 0f) {
                bottomLeftRadius = height.toFloat() / 2f
            }

            if (bottomRightRadius != 0f) {
                bottomRightRadius = height.toFloat() / 2f
            }
        }
    }

    fun getRadiusArray(): FloatArray {

        if (radius != 0f) {
            return floatArrayOf(
                    radius,
                    radius,
                    radius,
                    radius,
                    radius,
                    radius,
                    radius,
                    radius
            )
        }

        return floatArrayOf(
                topLeftRadius,
                topLeftRadius,
                topRightRadius,
                topRightRadius,
                bottomRightRadius,
                bottomRightRadius,
                bottomLeftRadius,
                bottomLeftRadius
        )
    }
}