package com.huawei.crazyrocketkotlin.view.overlay

import android.graphics.Canvas

abstract class BaseGraphic(overlay: GraphicOverlay) {

    private var graphicOverlay: GraphicOverlay? = null

    init {
        this.graphicOverlay = overlay
    }
    abstract fun draw(canvas: Canvas?)

    open fun scaleX(x: Float): Float {
        return x * graphicOverlay!!.getWidthScaleValue()
    }

    open fun scaleY(y: Float): Float {
        return y * graphicOverlay!!.getHeightScaleValue()
    }

}