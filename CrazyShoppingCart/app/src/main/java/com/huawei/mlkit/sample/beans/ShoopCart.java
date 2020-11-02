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

public class ShoopCart {
    public float x;
    public float y;
    private Bitmap shoopCart;
    private int screenWidth;
    public int cartWidth;
    public int cartHeight;

    public ShoopCart(Context context, int screenWidth, int screenHeight) {
        shoopCart = BitmapFactory.decodeResource(context.getResources(), R.drawable.shoppingcart);
        this.screenWidth = screenWidth;
        cartWidth = shoopCart.getWidth();
        cartHeight = shoopCart.getHeight();
        x = screenWidth / 2;
        y = screenHeight / 2 + cartHeight;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(shoopCart, x - cartWidth / 2, y - cartHeight / 2, null);
    }

    public void setOffset(float offset) {
        if (offset > (screenWidth - cartWidth)) {
            x = screenWidth - cartWidth / 2;
        } else if (offset < cartWidth) {
            x = cartWidth / 2;
        } else {
            x = offset;
        }
    }
}
