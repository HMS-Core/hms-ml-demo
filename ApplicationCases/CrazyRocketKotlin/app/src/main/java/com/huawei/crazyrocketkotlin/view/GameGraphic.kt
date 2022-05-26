package com.huawei.crazyrocketkotlin.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.huawei.crazyrocketkotlin.beans.World

class GameGraphic : View {
    private var world: World? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context,attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,attrs, defStyleAttr)

    fun initData(
        context: Context,
        relativeLayout: RelativeLayout,
        score: TextView,
        level: Int,
        magnification: Float
    ) {
        world = World(context, relativeLayout, score, level, magnification)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        world?.draw(canvas)
        invalidate()
    }

    fun startGame() {
        world?.startGame()
    }

    fun setOffset(offset: Float) {
        world?.setOffset(offset)
    }
}