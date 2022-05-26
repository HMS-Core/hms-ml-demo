package com.huawei.crazyrocketkotlin.camera

import android.content.Context
import android.view.ViewGroup
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.huawei.crazyrocketkotlin.R
import com.huawei.crazyrocketkotlin.util.CrazyRocketSurfaceHolder
import com.huawei.crazyrocketkotlin.view.overlay.GraphicOverlay
import com.huawei.hms.common.size.Size
import com.huawei.hms.mlsdk.common.LensEngine
import java.io.IOException
import kotlin.math.max
import kotlin.math.min


class LensEnginePreview(var mContext: Context, attrs: AttributeSet) : ViewGroup(mContext, attrs) {

    private val TAG = "LensEnginePreview"

    private var mSurfaceView: SurfaceView? = null
    private var mStartRequested = false
    private var mSurfaceAvailable = false

    private var mLensEngine: LensEngine? = null
    private val mOverlay: GraphicOverlay? = null

    init {
        mContext = context
        mStartRequested = false
        mSurfaceAvailable = false
        mSurfaceView = SurfaceView(context)
        mSurfaceView!!.holder.addCallback(object : CrazyRocketSurfaceHolder() {

            override fun surfaceCreated(holder: SurfaceHolder) {
                super.surfaceCreated(holder)
                mSurfaceAvailable = true
                try {
                    startIfReady()
                } catch (e: IOException) {
                    Log.e(TAG, mContext.getString(R.string.not_start_camera), e)
                }
            }
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                super.surfaceDestroyed(holder)
                mSurfaceAvailable = false
            }
        })
        this.addView(mSurfaceView)
    }

    @Throws(IOException::class)
    fun start(lensEngine: LensEngine?) {
        if (lensEngine == null) {
            stop()
        }
        mLensEngine = lensEngine
        if (mLensEngine != null) {
            mStartRequested = true
            startIfReady()
        }
    }

    fun stop() {
        mLensEngine?.close()
    }

    @Throws(IOException::class)
    private fun startIfReady() {
        if (mStartRequested && mSurfaceAvailable) {
            mLensEngine?.run(mSurfaceView!!.holder)

            mOverlay?.let {
                val size: Size = mLensEngine?.displayDimension!!
                val min = min(size.width, size.height)
                val max = max(size.width, size.height)
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mLensEngine?.lensType?.let { it1 -> it.setCameraInfo(min, max, it1) }
                } else {
                    mLensEngine?.lensType?.let { it1 -> it.setCameraInfo(max, min, it1) }
                }
                it.clear()
            }
            mStartRequested = false
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var previewWidth = 320
        var previewHeight = 240

        this.mLensEngine?.let {
            val size: Size = this.mLensEngine!!.displayDimension
            previewWidth = size.width
            previewHeight = size.height
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val tmp = previewWidth
            previewWidth = previewHeight
            previewHeight = tmp
        }

        val viewWidth = right - left
        val viewHeight = bottom - top

        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()


        // To fill the view with the camera preview, while also preserving the correct aspect ratio,
        // it is usually necessary to slightly oversize the child and to crop off portions along one
        // of the dimensions. We scale up based on the dimension requiring the most correction, and
        // compute a crop offset for the other dimension.
        if (widthRatio > heightRatio) {
            childWidth = viewWidth
            childHeight = (previewHeight.toFloat() * widthRatio).toInt()
            childYOffset = (childHeight - viewHeight) / 2
        } else {
            childWidth = (previewWidth.toFloat() * heightRatio).toInt()
            childHeight = viewHeight
            childXOffset = (childWidth - viewWidth) / 2
        }

        for (i in 0 until this.childCount) {
            // One dimension will be cropped. We shift child over or up by this offset and adjust
            // the size to maintain the proper aspect ratio.
            getChildAt(i).layout(-1 * childXOffset, -1 * childYOffset,
                childWidth - childXOffset, childHeight - childYOffset)
        }

        try {
            this.startIfReady()
        } catch (e: IOException) {
            Log.e(TAG, mContext.getString(R.string.not_start_camera), e)
        }
    }
}