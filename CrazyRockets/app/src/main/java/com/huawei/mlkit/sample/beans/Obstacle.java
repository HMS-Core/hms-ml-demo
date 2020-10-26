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
import android.graphics.Canvas;

import com.huawei.mlkit.sample.utils.SizeUtils;

import java.util.Random;

//Obstacle
public class Obstacle {
    private Bitmap obstacleImage;
    public int x;
    public int y;
    public int obstacleWidth;
    public int obstacleHeight;
    public int gap;
    private int distance;
    private Random random = new Random();

    private int randomNumber;
    private int level;


    public Obstacle(Context context, Bitmap bitmap, int gap, int distance, int randomNumber, int n, int level) {
        obstacleImage = bitmap;
        this.gap = gap;
        this.distance = distance;
        this.randomNumber = randomNumber;
        obstacleWidth = obstacleImage.getWidth();
        obstacleHeight = obstacleImage.getHeight();
        x = n * distance + obstacleWidth / 2;
        y = gap / 2 + random.nextInt(randomNumber) + SizeUtils.dp2Px(context, 80);
        this.level = level;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(obstacleImage, x - obstacleWidth / 2, y - obstacleHeight / 2, null);
    }

    public void step() {
        x -= level;
        if (x <= -obstacleWidth / 2) {
            x = 2 * distance + obstacleWidth / 2;
            y = gap / 2 + random.nextInt(randomNumber);
        }
    }
}
