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

import android.graphics.Bitmap;
import android.graphics.Canvas;

//ground
public class Ground {
    private Bitmap image;
    private int firstX;
    private int secondX;
    private int y;
    private int groundWidth;
    private int level;

    public Ground(Bitmap bitmap, int width, int height, int level) {
        this.image = bitmap;
        this.groundWidth = width;
        firstX = 0;
        secondX = width;
        y = height;
        this.level = level;

    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, firstX, y, null);
        canvas.drawBitmap(image, secondX, y, null);
    }


    public void step() {
        firstX -= level;
        secondX -= level;
        if (firstX <= -groundWidth) {
            firstX = groundWidth;
        }
        if (secondX <= -groundWidth) {
            secondX = groundWidth;
        }
    }
}
