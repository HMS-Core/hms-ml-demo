/**
 *  Copyright 2018 Google LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  2020.2.21-Changed name from CameraSource to LensEngine, and adjusted the architecture, except for the classes: start and stop
 *                  Huawei Technologies Co., Ltd.
 */

package com.huawei.hms.mlkit.sample.camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.huawei.hms.mlkit.sample.views.graphic.BaseGraphic;
import com.huawei.hms.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.common.MLPosition;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceShape;

import java.util.List;

public class FaceGraphic extends BaseGraphic {
    private static final float BOX_STROKE_WIDTH = 6.0f;

    private final Paint boxPaint;
    private final Paint textPaint;

    private volatile MLFace hmsMLFace;


    public FaceGraphic(GraphicOverlay overlay, MLFace face, int facing, Bitmap overlayBitmap) {
        super(overlay);

        hmsMLFace = face;

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT);

        boxPaint = new Paint();
        boxPaint.setColor(Color.WHITE);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    @Override
    public void draw(Canvas canvas) {
        MLFace mLFace = hmsMLFace;
        if (mLFace == null) {
            return;
        }

        if (mLFace.getFaceShapeList() == null) {
            return;
        }

        for (MLFaceShape contour : mLFace.getFaceShapeList()) {
            if (contour == null) {
                continue;
            }
            List<MLPosition> points;
            if (contour.getFaceShapeType() == MLFaceShape.TYPE_FACE) {
                points = contour.getPoints();

                for (int i = 0; i < points.size(); i++) {
                    MLPosition point = points.get(i);
                    canvas.drawPoint(translateX(point.getX().floatValue()), translateY(point.getY().floatValue()), boxPaint);
                    if (i != (points.size() - 1)) {
                        MLPosition next = points.get(i + 1);
                        if (point != null && point.getX() != null && point.getY() != null) {
                            if (i % 3 == 0) {
                                canvas.drawText(i + 1 + "", translateX(point.getX().floatValue()), translateY(point.getY().floatValue()), textPaint);
                            }
                        }
                    }
                }
            }

        }
    }

}