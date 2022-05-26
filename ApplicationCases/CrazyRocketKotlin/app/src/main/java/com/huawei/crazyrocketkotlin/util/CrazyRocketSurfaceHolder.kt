package com.huawei.crazyrocketkotlin.util

import android.view.SurfaceHolder

open class CrazyRocketSurfaceHolder: SurfaceHolder.Callback {
    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}
}
