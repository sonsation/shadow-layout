package com.sonsation.library.effet

import com.sonsation.library.utils.ViewHelper

class Radius {

    var radius = 0f
    var topLeftRadius = 0f
    var topRightRadius = 0f
    var bottomLeftRadius = 0f
    var bottomRightRadius = 0f
    var smoothCorner = false
    var radiusWeight = 1f

    fun updateRadius(radius: Float) {
        this.radius = radius
    }

    fun updateRadius(tl: Float, tr: Float, bl: Float, br: Float) {
        this.topLeftRadius = tl
        this.topRightRadius = tr
        this.bottomLeftRadius = bl
        this.bottomRightRadius = br
    }

    fun getRadiusArray(height: Float): FloatArray {

        if (radius != 0f) {

            val targetRadius = if (smoothCorner) {
                height.div(2f)
            } else {
                radius * radiusWeight
            }

            return floatArrayOf(
                    targetRadius,
                    targetRadius,
                    targetRadius,
                    targetRadius,
                    targetRadius,
                    targetRadius,
                    targetRadius,
                    targetRadius
            )
        }

        val targetTopLeftRadius = if (smoothCorner) {
            height.div(2f)
        } else {
            topLeftRadius * radiusWeight
        }
        val targetTopRightRadius = if (smoothCorner) {
            height.div(2f)
        } else {
            topRightRadius * radiusWeight
        }
        val targetBottomLeftRadius = if (smoothCorner) {
            height.div(2f)
        } else {
            bottomLeftRadius * radiusWeight
        }
        val targetBottomRightRadius = if (smoothCorner) {
            height.div(2f)
        } else {
            bottomRightRadius * radiusWeight
        }

        return floatArrayOf(
                targetTopLeftRadius,
                targetTopLeftRadius,
                targetTopRightRadius,
                targetTopRightRadius,
                targetBottomRightRadius,
                targetBottomRightRadius,
                targetBottomLeftRadius,
                targetBottomLeftRadius
        )
    }

    fun getRadiusArray(): FloatArray {

        if (radius != 0f) {

            val targetRadius = radius * radiusWeight

            return floatArrayOf(
                targetRadius,
                targetRadius,
                targetRadius,
                targetRadius,
                targetRadius,
                targetRadius,
                targetRadius,
                targetRadius
            )
        }

        val targetTopLeftRadius = topLeftRadius * radiusWeight
        val targetTopRightRadius = topRightRadius * radiusWeight
        val targetBottomLeftRadius = bottomLeftRadius * radiusWeight
        val targetBottomRightRadius = bottomRightRadius * radiusWeight

        return floatArrayOf(
            targetTopLeftRadius,
            targetTopLeftRadius,
            targetTopRightRadius,
            targetTopRightRadius,
            targetBottomRightRadius,
            targetBottomRightRadius,
            targetBottomLeftRadius,
            targetBottomLeftRadius
        )
    }
}