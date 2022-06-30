package com.huawei.crazyrocketkotlin.beans

import android.graphics.Bitmap
import android.graphics.Canvas

//ground
class Ground(bitmap: Bitmap, width: Int, height: Int, level: Int) {

    private var image: Bitmap? = null
    private var firstX = 0
    private var secondX = 0
    private var y = 0
    private var groundWidth = 0
    private var level = 0

    init {
        image = bitmap
        groundWidth = width
        firstX = 0
        secondX = width
        y = height
        this.level = level
    }

    fun draw(canvas: Canvas) {
        image?.let {
            canvas.drawBitmap(it, firstX.toFloat(), y.toFloat(), null)
            canvas.drawBitmap(it, secondX.toFloat(), y.toFloat(), null)
        }
    }

    fun step() {
        firstX -= level
        secondX -= level
        if (firstX <= -groundWidth) {
            firstX = groundWidth
        }
        if (secondX <= -groundWidth) {
            secondX = groundWidth
        }
    }
}