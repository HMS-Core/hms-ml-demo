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

package com.huawei.mlkit.sample.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.huawei.mlkit.sample.beans.World;

public class GameGraphic extends View {
    private World world;

    public GameGraphic(Context context) {
        super(context);
    }

    public GameGraphic(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameGraphic(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initData(Context context,  TextView topScore, TextView score, LinearLayout gameover) {
        world = new World(context, topScore,score,gameover);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        world.draw(canvas);
        invalidate();
    }

    public void startGame() {
        world.startGame();
    }

    public void gameOver() {
        world.gameOver();
    }

    public void setOffset(float offset) {
        world.setOffset(offset);
    }

    public void setMagnification(float magnification) {
       world.setMagnification(magnification);
    }

    public void setSpeed(int speed){
        world.setSpeed(speed);
    }
}
