/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.mlkit.sample.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CropBitMap {
    private final int targetHeight;

    private final int targetWidth;

    private final Bitmap output;

    public CropBitMap(int targetHeight, int targetWidth) {
        this.targetHeight = targetHeight;
        this.targetWidth = targetWidth;
        output = Bitmap.createBitmap(this.targetWidth, this.targetHeight, Bitmap.Config.ARGB_8888);
    }

    public Bitmap getCropBitmap(Bitmap input) {
        int srcL;
        int srcR;
        int srcT;
        int srcB;
        int dstL;
        int dstR;
        int dstT;
        int dstB;
        int w = input.getWidth();
        int h = input.getHeight();
        if (targetWidth > w) { // padding
            srcL = 0;
            srcR = w;
            dstL = (targetWidth - w) / 2;
            dstR = dstL + w;
        } else { // cropping
            dstL = 0;
            dstR = targetWidth;
            srcL = (w - targetWidth) / 2;
            srcR = srcL + targetWidth;
        }
        if (targetHeight > h) { // padding
            srcT = 0;
            srcB = h;
            dstT = (targetHeight - h) / 2;
            dstB = dstT + h;
        } else { // cropping
            dstT = 0;
            dstB = targetHeight;
            srcT = (h - targetHeight) / 2;
            srcB = srcT + targetHeight;
        }
        Rect src = new Rect(srcL, srcT, srcR, srcB);
        Rect dst = new Rect(dstL, dstT, dstR, dstB);
        new Canvas(output).drawBitmap(input, src, dst, null);
        return output;

    }
}
