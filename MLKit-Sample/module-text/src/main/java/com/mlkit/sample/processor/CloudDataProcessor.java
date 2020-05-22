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

package com.mlkit.sample.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.document.MLDocument;
import com.huawei.hms.mlsdk.text.MLText;
import com.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.List;

public class CloudDataProcessor {

    private GraphicOverlay mGraphicOverlay;

    private Bitmap mBitmapCopyForTap;

    private MLText mHmsMLVisionText;

    public void setGraphicOverlay (GraphicOverlay graphicOverlay) {
        this.mGraphicOverlay = graphicOverlay;
    }

    public void setBitmap(Bitmap bitmapCopyForTap) {
        this.mBitmapCopyForTap = bitmapCopyForTap;
    }

    public void setText(MLText text) {
        this.mHmsMLVisionText = text;
    }

    public void drawView(Canvas canvas, boolean flag) {
        Paint rectPaint;
        Paint textPaint;
        rectPaint = new Paint();
        rectPaint.setColor(Color.parseColor("#5CACEE"));
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(2);
        rectPaint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        if (flag) {
            textPaint.setAlpha(255);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setAlpha(130); //
            canvas.drawRect(0, 0, this.mBitmapCopyForTap.getWidth(), this.mBitmapCopyForTap.getHeight(), paint);
        } else {
            textPaint.setAlpha(0);
        }
        this.drawRectAndText(canvas, rectPaint, textPaint);
    }

    private void drawRectAndText(Canvas canvas, Paint rectPaint, Paint textPaint) {
        List<MLText.Block> blocks = this.mHmsMLVisionText.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<MLText.TextLine> lines = blocks.get(i).getContents();
            for (int j = 0; j < lines.size(); j++) {
                List<MLText.Word> elements = lines.get(j).getContents();
                for (int l = 0; l < elements.size(); l++) {
                    MLText.Word element = elements.get(l);
                    Point[] points = element.getVertexes();
                    if (points == null) {
                        return;
                    }
                    float[] linePoints = this.getDrawLineAllPoints(points);
                    float[] ptsTexts = this.getTextPoints(points);
                    float size = (ptsTexts[ptsTexts.length - 1] - ptsTexts[1]) * 0.85f;
                    textPaint.setTextSize(size);
                    Path path = new Path();
                    this.drawViewText(ptsTexts, path);
                    canvas.drawLines(linePoints, rectPaint);
                    canvas.drawTextOnPath(element.getStringValue(), path, 0, 0, textPaint);
                }
            }
        }
    }

    private float[] getTextPoints(Point[] points) {
        float[] ptsTexts = new float[points.length * 2];
        for (int m = 0; m < points.length; m++) {
            points[m].x = (int) this.translateX(points[m].x, this.mGraphicOverlay);
            points[m].y = (int) this.translateY(points[m].y, this.mGraphicOverlay);
            int tt = m * 2;
            ptsTexts[tt++] = points[m].x;
            ptsTexts[tt++] = points[m].y;
        }
        return ptsTexts;
    }


    private float[] getDrawLineAllPoints(Point[] points) {
        float[] pts = new float[points.length * 4];
        for (int i = 0; i < points.length; i++) {
            points[i].x = (int) this.translateX(points[i].x, this.mGraphicOverlay);
            points[i].y = (int) this.translateY(points[i].y, this.mGraphicOverlay);
            if (i == 0) {
                pts[0] = points[0].x;
                pts[1] = points[0].y;
                pts[pts.length - 2] = points[0].x;
                pts[pts.length - 1] = points[0].y;
            } else {
                int j = i * 4 - 2;
                pts[j++] = points[i].x;
                pts[j++] = points[i].y;
                pts[j++] = points[i].x;
                pts[j++] = points[i].y;
            }
        }
        return pts;
    }

    private void drawViewText(float[] texts, Path path) {
        float x1 = texts[texts.length - 2];
        float y1 = texts[texts.length - 1];
        float offset = (texts[texts.length - 1] - texts[1]) / 4;
        path.moveTo(x1, y1 - offset);
        for (int i = 2; i <= texts.length / 4; i++) {
            float x2 = texts[texts.length - 2 * i];
            float y2 = texts[texts.length - 2 * i + 1];
            if (i != texts.length / 4) {
                path.lineTo(x2, y2 - offset);
            } else {
                path.lineTo(x2 + 300, y2 - offset);
            }
        }
    }

    private float scaleX(float x, GraphicOverlay graphicOverlay) {
        return x * graphicOverlay.getWidthScaleValue();
    }

    private float scaleY(float y, GraphicOverlay graphicOverlay) {
        return y * graphicOverlay.getHeightScaleValue();
    }

    private float translateX(float x, GraphicOverlay graphicOverlay) {
        if (graphicOverlay.getCameraFacing() == LensEngine.FRONT_LENS) {
            return graphicOverlay.getWidth() - this.scaleX(x, graphicOverlay);
        } else {
            return this.scaleX(x, graphicOverlay);
        }
    }

    private float translateY(float y, GraphicOverlay graphicOverlay) {
        return this.scaleY(y, graphicOverlay);
    }

    public void drawCloudDocText(MLDocument text, Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#EE6A50"));

        List<MLDocument.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<MLDocument.Section> paragraphs = blocks.get(i).getSections();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<MLDocument.Word> words = paragraphs.get(j).getWordList();
                for (int l = 0; l < words.size(); l++) {
                    List<MLDocument.Character> symbols = words.get(l).getCharacterList();
                    for (int m = 0; m < symbols.size(); m++) {
                        Rect rect = symbols.get(m).getBorder();
                        textPaint.setTextSize(rect.height() * 0.75f);
                        canvas.drawText(symbols.get(m).getStringValue(), rect.left, rect.bottom, textPaint);
                    }
                }
            }
        }
    }
}
