package com.huawei.crazyrocketkotlin.beans

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.huawei.crazyrocketkotlin.R
import com.huawei.crazyrocketkotlin.util.SizeUtils

class World (var context : Context ,gameover : RelativeLayout,score:TextView,level: Int,magnification: Float) {

    private var gameStart = false
    private var score = 0
    private var gameOver = false

    private var rockets: Rockets? = null
    private var ground: Ground? = null
    private var obstacle1: Obstacle? = null
    private var obstacle2: Obstacle? = null

    private var paint: Paint? = null
    private val linearPaint = Paint()
    private var screenWidth = 0
    private var screenHeight = 0

    private var groundBitmap: Bitmap? = null
    private var groundWidth = 0
    private var groundHeight = 0

    private var obstacleBitmap: Bitmap? = null

    private var gap = 0
    private var distance = 0
    private var randomNumber = 0

    private var gameover: RelativeLayout? = null
    private var scoreText: TextView? = null
    private var level = 0
    private var magnification = 1f

    private var scoreStr: String? = null

    private var firstTime: Long = 0
    private var lastTime: Long = 0

    private var color = intArrayOf()


    init {
        this.gameover = gameover
        this.level = level
        this.magnification = magnification
        scoreText = score
        scoreStr = context.getString(R.string.score)

        color = intArrayOf(ContextCompat.getColor(context,R.color.startcolor), ContextCompat.getColor(context,R.color.endcolor))

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        screenWidth = wm.defaultDisplay.width
        screenHeight = wm.defaultDisplay.height

        groundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ground)
        obstacleBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.obstacle)

        groundBitmap?.let {
            groundWidth = it.width
            groundHeight = screenHeight - it.height / 3 * 2
        }

        gap = SizeUtils.dp2Px(context, 150F)
        randomNumber = screenHeight - groundBitmap!!.height / 3 * 2 - gap - SizeUtils.dp2Px(context, 80F)
        distance = screenWidth

        paint = Paint()
        paint?.apply {
            this.style = Paint.Style.FILL_AND_STROKE
            this.color = Color.WHITE
            this.textSize = 45f
            this.isFakeBoldText = true
        }


        val linearGradient = LinearGradient(0F, 0F, 0F, screenHeight / 5F, color, floatArrayOf(0f, 1.0f),
            Shader.TileMode.CLAMP
        )
        linearPaint.shader = linearGradient

        start()

    }

    fun start() {
        gameStart = false
        gameOver = false
        ground = groundBitmap?.let { Ground(it, groundWidth, groundHeight, level) }
        rockets = Rockets(context, screenWidth, groundHeight)

        obstacleBitmap?.let {
            obstacle1 = Obstacle(context, it, gap, distance, randomNumber, 1, level)
            obstacle2 = Obstacle(context, it, gap, distance, randomNumber, 2, level)
        }

        score = 0
    }

    @SuppressLint("SetTextI18n")
    fun draw(canvas: Canvas) {
        obstacle1?.draw(canvas)
        obstacle2?.draw(canvas)

        canvas.drawRect(0f, 0f, screenWidth.toFloat(), (screenHeight / 5).toFloat(), linearPaint)

        ground!!.draw(canvas)
        canvas.drawText(scoreStr + score, (screenWidth / 10).toFloat(), (screenHeight / 8).toFloat(),
            paint!!
        )
        if (gameOver) {
            gameover?.visibility = View.VISIBLE
            scoreText!!.text = score.toString() + ""
            return
        }

        if (gameStart && !gameOver) {
            ground?.step()
            rockets?.draw(canvas)
            obstacle1?.step()
            obstacle2?.step()

            if (rockets!!.pass(obstacle1!!, obstacle2!!, level)) {
                lastTime = System.currentTimeMillis()
                if (lastTime - firstTime >= 1500) {
                    score++
                }
                firstTime = lastTime
            }
            if (rockets!!.hit(obstacle1, obstacle2)) {
                gameStart = false
                gameOver = true
            }
        }

        if (!gameOver) rockets?.fly()


    }

    fun startGame() {
        start()
        gameStart = true
    }

    fun setOffset(offset: Float) {
        if (gameStart) {
            rockets?.setOffset(offset * magnification)
        }
    }
}