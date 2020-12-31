package com.sonsation.labs.stores

class RadiusStore {

    var radius = 0f
    var topLeftRadius = 0f
    var topRightRadius = 0f
    var bottomLeftRadius = 0f
    var bottomRightRadius = 0f

    fun updateRadius(radius: Float) {
        this.radius = radius
    }

    fun updateRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        this.topLeftRadius = topLeft
        this.topRightRadius = topRight
        this.bottomLeftRadius = bottomLeft
        this.bottomRightRadius = bottomRight
    }

    fun getRadiusArray(): FloatArray {
        return if (radius != 0f) {
            floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        } else {
            floatArrayOf(
                topLeftRadius,
                topLeftRadius,
                topRightRadius,
                topRightRadius,
                bottomLeftRadius,
                bottomLeftRadius,
                bottomRightRadius,
                bottomRightRadius
            )
        }
    }
}