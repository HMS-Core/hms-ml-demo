package com.huawei.crazyrocketkotlin.view.overlay

import android.content.Context
import android.graphics.Canvas
import android.hardware.Camera
import android.util.AttributeSet
import android.view.View

class GraphicOverlay @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private val lock = Any()
    private var previewWidth = 0
    private var previewHeight = 0
    private var widthScaleValue = 1.0f
    private var heightScaleValue = 1.0f
    private var cameraFacing: Int = Camera.CameraInfo.CAMERA_FACING_BACK
    private val graphics: MutableList<BaseGraphic> = ArrayList<BaseGraphic>()


    fun clear() {
        synchronized(this.lock) { this.graphics.clear() }
        this.postInvalidate()
    }

    fun setCameraInfo(width: Int, height: Int, facing: Int) {
        synchronized(lock) {
            previewWidth = width
            previewHeight = height
            cameraFacing = facing
        }
        this.postInvalidate()
    }

    fun getWidthScaleValue(): Float {
        return widthScaleValue
    }

    fun getHeightScaleValue(): Float {
        return heightScaleValue
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            if (previewWidth != 0 && previewHeight != 0) {
                widthScaleValue = width.toFloat() / previewWidth.toFloat()
                heightScaleValue = height.toFloat() / previewHeight.toFloat()
            }
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }

}