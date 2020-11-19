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

import com.huawei.mlkit.sample.R;

//Rockets
public class Rockets {
    private Bitmap image;
    private int index = 0;
    private float x;
    private float y;
    private Bitmap[] images;
    private int[] rockets = {R.drawable.rocket0, R.drawable.rocket1, R.drawable.rocket2};

    private int rocketWidth;
    private int rocketHeight;

    public Rockets(Context context, int width, int height) {
        x = width / 2;
        y = height / 2;
        images = new Bitmap[3];
        for (int i = 0; i < images.length; i++) {
            images[i] = BitmapFactory.decodeResource(context.getResources(), rockets[i]);
        }
        image = images[0];
        rocketWidth = image.getWidth();
        rocketHeight = image.getHeight();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x - rocketWidth / 2, y - rocketHeight / 2, null);
    }

    public void fly() {
        index++;
        image = images[(index / 8) % images.length];
    }


    public void setOffset(float offset) {
        y = offset;
    }

    public boolean pass(Obstacle col1, Obstacle col2, int level) {
        if (level % 2 != 0) {
            level += 1;
        }
        return (x >= col1.x - level / 2 && x < col1.x + level / 2) || (x >= col2.x - level / 2 && x < col2.x + level / 2);//判断鸟是否通过柱子1或柱子2 ，通过加一分

    }

    public boolean hit(Obstacle column1, Obstacle column2) {
        return hit(column1) || hit(column2);
    }

    private boolean hit(Obstacle col) {
        if (x > col.x - col.obstacleWidth / 2 - rocketWidth / 2 + 80 && x < col.x + col.obstacleWidth / 2 + rocketWidth / 2 - 80) {
            if (y > col.y - col.gap / 2 + rocketHeight / 2 && y < col.y + col.gap / 2 - rocketHeight / 2) {
                return false;
            }
            return true;
        }
        return false;
    }
}
