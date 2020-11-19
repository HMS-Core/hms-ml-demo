/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.mlkit.sample.beans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.utils.SizeUtils;

public class World {
    private Context context;

    private boolean gameStart;
    private int score;
    private boolean gameOver;

    private Rockets rockets;
    private Ground ground;
    private Obstacle obstacle1;
    private Obstacle obstacle2;

    private Paint paint;
    private Paint linearPaint = new Paint();
    private int screenWidth;
    private int screenHeight;

    private Bitmap groundBitmap;
    private int groundWidth;
    private int groundHeight;

    private Bitmap obstacleBitmap;

    private int gap;
    private int distance;
    private int randomNumber;

    private RelativeLayout gameover;
    private TextView scoreText;
    private int level;
    private float magnification = 1;

    private String scoreStr;


    private long fitstTime = 0;
    private long lastTime = 0;
    private int[] color;


    public World(Context context, RelativeLayout gameover, TextView score, int level, float magnification) {
        this.context = context;
        this.gameover = gameover;
        this.scoreText = score;
        this.level = level;
        this.magnification = magnification;
        scoreStr = context.getString(R.string.score);
        color = new int[]{context.getResources().getColor(R.color.startcolor), context.getResources().getColor(R.color.endcolor)};

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();

        groundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ground);
        obstacleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle);

        groundWidth = groundBitmap.getWidth();
        groundHeight = screenHeight - groundBitmap.getHeight() / 3 * 2;

        gap = SizeUtils.dp2Px(context, 150);
        randomNumber = screenHeight - groundBitmap.getHeight() / 3 * 2 - gap - SizeUtils.dp2Px(context, 80);
        distance = screenWidth;

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        paint.setTextSize(45);
        paint.setFakeBoldText(true);

        LinearGradient linearGradient = new LinearGradient(0, 0, 0, screenHeight / 5, color, new float[]{0, 1.0f}, LinearGradient.TileMode.CLAMP);
        linearPaint.setShader(linearGradient);

        start();
    }

    public void start() {
        gameStart = false;
        gameOver = false;
        ground = new Ground(groundBitmap, groundWidth, groundHeight, level);
        rockets = new Rockets(context, screenWidth, groundHeight);
        obstacle1 = new Obstacle(context, obstacleBitmap, gap, distance, randomNumber, 1, level);
        obstacle2 = new Obstacle(context, obstacleBitmap, gap, distance, randomNumber, 2, level);
        score = 0;
    }

    public void draw(Canvas canvas) {
        obstacle1.draw(canvas);
        obstacle2.draw(canvas);

        canvas.drawRect(0, 0, screenWidth, screenHeight / 5, linearPaint);

        ground.draw(canvas);
        canvas.drawText(scoreStr + score, screenWidth / 10, screenHeight / 8, paint);
        if (gameOver) {
            gameover.setVisibility(View.VISIBLE);
            scoreText.setText(score + "");
            return;
        }

        if (gameStart && !gameOver) {
            ground.step();
            rockets.draw(canvas);

            obstacle1.step();
            obstacle2.step();

            if (rockets.pass(obstacle1, obstacle2, level)) {
                lastTime = System.currentTimeMillis();
                if (lastTime - fitstTime >= 1500) {
                    score++;
                }
                fitstTime = lastTime;
            }
            if (rockets.hit(obstacle1, obstacle2)) {
                gameStart = false;
                gameOver = true;
            }
        }
        if (!gameOver) rockets.fly();

    }


    public void startGame() {
        start();
        gameStart = true;
    }

    public void setOffset(float offset) {
        if (gameStart) {
            rockets.setOffset(offset * magnification);
        }
    }
}
