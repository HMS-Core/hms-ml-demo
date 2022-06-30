package com.huawei.crazyrocketkotlin.beans

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.huawei.crazyrocketkotlin.R

//Rockets
class Rockets (context: Context, width: Int, height: Int) {
    private var image: Bitmap? = null
    private var index = 0
    private var x = 0f
    private var y = 0f
    private var images: Array<Bitmap?>
    private val rockets = intArrayOf(R.drawable.rocket0, R.drawable.rocket1, R.drawable.rocket2)

    private var rocketWidth = 0
    private var rocketHeight = 0

    init {
        x = (width / 2).toFloat()
        y = (height / 2).toFloat()
        images = arrayOfNulls(3)
        for (i in images.indices) {
            images[i] = BitmapFactory.decodeResource(context.resources, rockets[i])
        }
        image = images[0]
        image?.let {
            rocketWidth = it.width
            rocketHeight = it.height
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image!!, x - rocketWidth / 2, y - rocketHeight / 2, null)
    }

    fun fly() {
        index++
        image = images[index / 8 % images.size]
    }

    fun setOffset(offset: Float) {
        y = offset
    }

    fun pass(col1: Obstacle, col2: Obstacle, level: Int): Boolean {
        var mLevel = level
        if (level % 2 != 0) {
            mLevel += 1
        }
        return x >= col1.x - mLevel / 2 && x < col1.x + mLevel / 2 || x >= col2.x - mLevel / 2 && x < col2.x + mLevel / 2
    }

    fun hit(column1: Obstacle?, column2: Obstacle?): Boolean {
        return hit(column1!!) || hit(column2!!)
    }

    private fun hit(col: Obstacle): Boolean {
        return if (x > col.x - col.obstacleWidth / 2 - rocketWidth / 2 + 80 && x < col.x + col.obstacleWidth / 2 + rocketWidth / 2 - 80) {
            !(y > col.y - col.gap / 2 + rocketHeight / 2 && y < col.y + col.gap / 2 - rocketHeight / 2)
        } else false
    }


}