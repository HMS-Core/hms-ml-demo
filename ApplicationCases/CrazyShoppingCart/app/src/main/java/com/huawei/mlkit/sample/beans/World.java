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
import android.graphics.Canvas;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.mlkit.sample.R;

public class World {
    private Context context;
    private int screenWidth;
    private int screenHeight;
    private boolean gameStart = false;

    private ShoopCart shoopCart;
    private Goods goods1;
    private Goods goods2;
    private Goods goods3;
    private Goods goods4;

    private float magnification = 1;

    private TextView topScore;
    private TextView score;
    private LinearLayout gameover;

    private String scoreStr;

    private int scoreNum = 0;

    public World(Context context, TextView topScore, TextView score, LinearLayout gameover) {
        this.context = context;
        this.topScore = topScore;
        this.score = score;
        this.gameover = gameover;
        scoreStr = context.getString(R.string.score);

        topScore.setText(scoreStr + scoreNum);
        score.setText(scoreNum + "");

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        start();
    }

    private void start() {
        scoreNum = 0;
        topScore.setText(scoreStr + scoreNum);
        score.setText(scoreNum + "");
        shoopCart = new ShoopCart(context, screenWidth, screenHeight);
        goods1 = new Goods(context, 0, screenWidth, screenHeight);
        goods2 = new Goods(context, 1, screenWidth, screenHeight);
        goods3 = new Goods(context, 2, screenWidth, screenHeight);
        goods4 = new Goods(context, 3, screenWidth, screenHeight);
    }

    public void draw(Canvas canvas) {
        if (gameStart) {
            shoopCart.draw(canvas);
            goods1.draw(canvas);
            goods2.draw(canvas);
            goods3.draw(canvas);
            goods4.draw(canvas);

            goods1.step();
            goods2.step();
            goods3.step();
            goods4.step();

            if (goods1.hit(shoopCart)) {
                scoreNum++;
                topScore.setText(scoreStr + scoreNum);
                score.setText(scoreNum + "");
            }
            if (goods2.hit(shoopCart)) {
                scoreNum++;
                topScore.setText(scoreStr + scoreNum);
                score.setText(scoreNum + "");
            }
            if (goods3.hit(shoopCart)) {
                scoreNum++;
                topScore.setText(scoreStr + scoreNum);
                score.setText(scoreNum + "");
            }
            if (goods4.hit(shoopCart)) {
                scoreNum++;
                topScore.setText(scoreStr + scoreNum);
                score.setText(scoreNum + "");
            }
        }
    }

    public void startGame() {
        start();
        gameStart = true;
    }

    public void gameOver() {
        gameStart = false;
        gameover.setVisibility(View.VISIBLE);
    }

    public void setOffset(float offset) {
        if (gameStart) {
            shoopCart.setOffset(screenWidth - offset * magnification);
        }
    }

    public void setMagnification(float magnification) {
        this.magnification = magnification;
    }

    public void setSpeed(int speed) {
        if (goods1 != null) {
            goods1.setSpeed(speed);
        }
        if (goods2 != null) {
            goods2.setSpeed(speed);
        }
        if (goods3 != null) {
            goods3.setSpeed(speed);
        }
        if (goods4 != null) {
            goods4.setSpeed(speed);
        }
    }
}
