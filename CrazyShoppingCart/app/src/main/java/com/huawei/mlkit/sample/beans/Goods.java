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

import java.util.Random;

public class Goods {
    private int[] goods = {R.drawable.hat, R.drawable.coat, R.drawable.babyclothes, R.drawable.slipper,
            R.drawable.basketball, R.drawable.banana, R.drawable.shoes};
    private Bitmap[] images;
    private Bitmap image;
    private int x;
    private int y;

    private int screenWidth;
    private int screenHeight;
    private int goodsWidth;
    private int goodsHeight;

    private int distance;

    private int speed = 6;

    private Random random = new Random();

    public Goods(Context context, int n, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        images = new Bitmap[goods.length];
        for (int i = 0; i < images.length; i++) {
            images[i] = BitmapFactory.decodeResource(context.getResources(), goods[i]);
        }
        image = images[0];
        goodsWidth = image.getWidth();
        goodsHeight = image.getHeight();

        distance = screenHeight / 4;

        x = goodsWidth / 2 + random.nextInt(screenWidth - goodsWidth);
        y = -goodsHeight / 2 - distance * n;

        image = images[random.nextInt(goods.length)];
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x - goodsWidth / 2, y - goodsHeight / 2, null);
    }

    public void step() {
        y += speed;
        if (y >= screenHeight + goodsHeight / 2) {
            image = images[random.nextInt(goods.length)];
            x = goodsWidth / 2 + random.nextInt(screenWidth - goodsWidth);
            y = -goodsHeight / 2;
        }
    }

    public boolean hit(ShoopCart shoopCart) {
        if ((x + goodsWidth / 2) > (shoopCart.x - shoopCart.cartWidth / 2) && (x - goodsWidth / 2) < (shoopCart.x + shoopCart.cartWidth)) {
            if ((y + goodsHeight / 2) > (shoopCart.y - shoopCart.cartHeight / 2) && (y - goodsHeight / 2 < shoopCart.y + shoopCart.cartHeight / 2)) {
                image = images[random.nextInt(goods.length)];
                x = goodsWidth / 2 + random.nextInt(screenWidth - goodsWidth);
                y = -goodsHeight / 2;
                return true;
            }
        }
        return false;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
