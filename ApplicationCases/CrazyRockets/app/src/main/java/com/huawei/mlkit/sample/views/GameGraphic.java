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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.mlkit.sample.beans.World;

import androidx.annotation.Nullable;

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


    public void initData(Context context, RelativeLayout relaytivelayout, TextView score, int level, float magnification) {

        world = new World(context, relaytivelayout, score, level, magnification);
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

    public void setOffset(float offset) {
        world.setOffset(offset);
    }
}
