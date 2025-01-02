package com.sonsation.library.model

data class Padding(
    var start: Int,
    var top: Int,
    var end: Int,
    var bottom: Int
) {

    fun setPadding(padding: Int) {
        this.start = padding
        this.top = padding
        this.end = padding
        this.bottom = padding
    }

    fun setPadding(start: Int, top: Int, end: Int, bottom: Int) {
        this.start = start
        this.top = top
        this.end = end
        this.bottom = bottom
    }
}