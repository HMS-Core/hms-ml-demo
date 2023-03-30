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

package com.huawei.mlkit.sample.camera.graphic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.huawei.mlkit.sample.camera.GraphicOverlay;
import com.huawei.mlkit.sample.entity.Recognition;

import java.text.DecimalFormat;

public class CustmodelObjectGraphic extends BaseGraphic {


    private final Paint boxPaint;
    private final Paint textPaint;

    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;
    private Recognition result;
    private DecimalFormat df;


    public CustmodelObjectGraphic(GraphicOverlay overlay, Recognition result) {
        super(overlay);
        this.result = result;
        this.boxPaint = new Paint();
        this.boxPaint.setColor(Color.WHITE);
        this.boxPaint.setStyle(Style.STROKE);
        this.boxPaint.setStrokeWidth(CustmodelObjectGraphic.STROKE_WIDTH);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(CustmodelObjectGraphic.TEXT_SIZE);
        df = new DecimalFormat("0.00%");
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = result.getLocation();
        float cornerSize = Math.min(rect.width(), rect.height()) / 8.0f;
        canvas.drawRoundRect(rect, cornerSize, cornerSize, boxPaint);
        canvas.drawText(result.getTitle(), rect.left + cornerSize, rect.bottom, this.textPaint);
        canvas.drawText(df.format(result.getConfidence()), rect.left + cornerSize, rect.top, this.textPaint);
    }
}

