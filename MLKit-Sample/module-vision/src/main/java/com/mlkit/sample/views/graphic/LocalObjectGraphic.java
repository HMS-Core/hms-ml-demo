/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mlkit.sample.views.graphic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.huawei.hms.mlsdk.objects.MLObject;
import com.mlkit.sample.views.overlay.GraphicOverlay;

public class LocalObjectGraphic extends BaseGraphic {

    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final MLObject object;
    private final Paint boxPaint;
    private final Paint textPaint;

    public LocalObjectGraphic(GraphicOverlay overlay, MLObject object) {
        super(overlay);
        this.object = object;
        this.boxPaint = new Paint();
        this.boxPaint.setColor(Color.WHITE);
        this.boxPaint.setStyle(Style.STROKE);
        this.boxPaint.setStrokeWidth(LocalObjectGraphic.STROKE_WIDTH);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(LocalObjectGraphic.TEXT_SIZE);
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = new RectF(this.object.getBorder());
        rect.left = this.translateX(rect.left);
        rect.top = this.translateY(rect.top);
        rect.right = this.translateX(rect.right);
        rect.bottom = this.translateY(rect.bottom);
        canvas.drawRect(rect, this.boxPaint);

        canvas.drawText(LocalObjectGraphic.getCategoryName(this.object.getTypeIdentity()), rect.left, rect.bottom, this.textPaint);
        canvas.drawText("trackingId: " + this.object.getTracingIdentity(), rect.left, rect.top, this.textPaint);
        if (this.object.getTypePossibility() != null) {
            canvas.drawText("confidence: " + this.object.getTypePossibility(), rect.right, rect.bottom, this.textPaint);
        }
    }

    private static String getCategoryName(int category) {
        switch (category) {
            case MLObject.TYPE_OTHER:
                return "Unknown";
            case MLObject.TYPE_FURNITURE:
                return "Home good";
            case MLObject.TYPE_GOODS:
                return "Fashion good";
            case MLObject.TYPE_PLACE:
                return "Place";
            case MLObject.TYPE_PLANT:
                return "Plant";
            case MLObject.TYPE_FOOD:
                return "Food";
            case MLObject.TYPE_FACE:
                return "Face";
            default: // fall out
        }
        return "";
    }
}

