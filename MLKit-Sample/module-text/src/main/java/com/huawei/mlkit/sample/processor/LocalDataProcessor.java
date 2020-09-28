/**
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

package com.huawei.mlkit.sample.processor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.List;

public class LocalDataProcessor {
    private float previewWidth;
    private float previewHeight;
    private float widthScaleValue = 1.0f;
    private float heightScaleValue = 1.0f;

    boolean isLandScape=false;
    private Integer maxWidthOfImage;
    private Integer maxHeightOfImage;

    public void setLandScape(boolean landScape) {
        this.isLandScape = landScape;
    }

    public void setCameraInfo(GraphicOverlay graphicOverlay, Canvas canvas, float width, float height) {
        this.previewWidth = width*graphicOverlay.getWidthScaleValue();
        this.previewHeight = height*graphicOverlay.getHeightScaleValue();
        if ((this.previewWidth != 0) && (this.previewHeight != 0)) {
            this.widthScaleValue = (float) canvas.getWidth() / this.previewWidth;
            this.heightScaleValue = (float) canvas.getHeight() / this.previewHeight;
        }
    }

    public void drawHmsMLVisionText(Canvas canvas, List<MLText.Block> blocks){
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.WHITE);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4.0f);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(45.0f);

        for (int i = 0; i < blocks.size(); i++) {
            List<MLText.TextLine> lines = blocks.get(i).getContents();
            for (int j = 0; j < lines.size(); j++) {
                // Display by line, without displaying empty lines.
                if (lines.get(j).getStringValue() != null && lines.get(j).getStringValue().trim().length() != 0) {
                    this.drawText(rectPaint,textPaint,canvas,lines.get(j));
                }
            }
        }
    }

    private void drawText(Paint rectPaint,Paint textPaint,Canvas canvas, MLText.Base text){
        Point[] points = text.getVertexes();
        if (points != null && points.length == 4) {
            for (int i = 0; i < points.length; i++) {
                points[i].x = (int) this.scaleX(points[i].x);
                points[i].y = (int) this.scaleY(points[i].y);
            }
            float[] pts = {points[0].x, points[0].y, points[1].x, points[1].y,
                    points[1].x, points[1].y, points[2].x, points[2].y,
                    points[2].x, points[2].y, points[3].x, points[3].y,
                    points[3].x, points[3].y, points[0].x, points[0].y};
            float averageHeight = ((points[3].y - points[0].y) + (points[2].y - points[1].y)) / 2.0f;
            float textSize = averageHeight * 0.7f;
            float offset = averageHeight / 4;
            textPaint.setTextSize(textSize);
            canvas.drawLines(pts, rectPaint);
            Path path = new Path();
            path.moveTo(points[3].x, points[3].y - offset);
            path.lineTo(points[2].x, points[2].y - offset);
            canvas.drawLines(pts, rectPaint);
            canvas.drawTextOnPath(text.getStringValue(), path, 0, 0, textPaint);
        }
    }

    public float scaleX(float x) {
        return x * this.widthScaleValue;
    }

    public float scaleY(float y) {
        return y * this.heightScaleValue;
    }

    public Integer getMaxWidthOfImage(FrameMetadata frameMetadata) {
        if (this.maxWidthOfImage == null) {
            if (this.isLandScape) {
                this.maxWidthOfImage = frameMetadata.getHeight();
            } else {
                this.maxWidthOfImage = frameMetadata.getWidth();
            }
        }
        return this.maxWidthOfImage;
    }

    public Integer getMaxHeightOfImage(FrameMetadata frameMetadata) {
        if (this.maxHeightOfImage == null) {
            if (this.isLandScape) {
                this.maxHeightOfImage = frameMetadata.getWidth();
            } else {
                this.maxHeightOfImage = frameMetadata.getHeight();
            }
        }
        return this.maxHeightOfImage;
    }
}
