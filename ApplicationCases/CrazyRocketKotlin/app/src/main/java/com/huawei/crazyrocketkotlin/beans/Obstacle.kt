package com.huawei.crazyrocketkotlin.beans

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import com.huawei.crazyrocketkotlin.util.SizeUtils
import java.util.*

class Obstacle(
    context: Context,
    bitmap: Bitmap,
    gap: Int,
    distance: Int,
    randomNumber: Int,
    n: Int,
    level: Int
) {

    private var obstacleImage: Bitmap? = null
    var x = 0
    var y = 0
    var obstacleWidth = 0
    var obstacleHeight = 0
    var gap = 0
    private var distance = 0
    private val random = Random()

    private var randomNumber = 0
    private var level = 0

    init {
        obstacleImage = bitmap
        this.gap = gap
        this.distance = distance
        this.randomNumber = randomNumber

        obstacleImage?.let {
            obstacleWidth = it.width
            obstacleHeight = it.height
        }

        x = n * distance + obstacleWidth / 2
        y = gap / 2 + random.nextInt(randomNumber) + SizeUtils.dp2Px(context, 80F)
        this.level = level
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(
            obstacleImage!!,
            (x - obstacleWidth / 2).toFloat(),
            (y - obstacleHeight / 2).toFloat(),
            null
        )
    }


    fun step() {
        x -= level
        if (x <= -obstacleWidth / 2) {
            x = 2 * distance + obstacleWidth / 2
            y = gap / 2 + random.nextInt(randomNumber)
        }
    }

}