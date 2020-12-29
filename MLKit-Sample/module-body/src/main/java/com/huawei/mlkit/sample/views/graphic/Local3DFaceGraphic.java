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

package com.huawei.mlkit.sample.views.graphic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.huawei.hms.mlsdk.common.MLPosition;
import com.huawei.hms.mlsdk.face.face3d.ML3DFace;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.ArrayList;

import java.util.List;

/**
 *  Local 3D Face Draw View
 *
 *  @since  2020-12-10
 */
public class Local3DFaceGraphic extends BaseGraphic {
    private static final String TAG = Local3DFaceGraphic.class.getSimpleName();
    private final GraphicOverlay overlay;

    private final Paint keypointPaint;

    private static float LINE_WIDTH;
    private List<MLPosition> face3dPoints;

    public static float dp2px(Context context, float dipValue) {
        return dipValue * context.getResources().getDisplayMetrics().density + 0.5f;
    }

    private volatile List<ML3DFace> faces;
    private Context mContext;
    private final Paint boxPaint;

    public Local3DFaceGraphic(GraphicOverlay overlay, List<ML3DFace> faces, Context context) {
        super(overlay);

        this.mContext = context;
        this.faces = faces;
        this.overlay = overlay;
        LINE_WIDTH = dp2px(this.mContext, 3);

        this.keypointPaint = new Paint();
        this.keypointPaint.setColor(Color.GREEN);
        this.keypointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.keypointPaint.setTextSize(dp2px(context, 2));

        this.boxPaint = new Paint();
        this.boxPaint.setColor(Color.GREEN);
        this.boxPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.boxPaint.setStrokeWidth(Local3DFaceGraphic.LINE_WIDTH);
    }

    @Override
    public void draw(Canvas canvas) {
        if (this.faces == null) {
            return;
        }

        for (int i = 0; i < this.faces.size(); i ++) {
            face3dPoints = faces.get(i).get3DKeyPoints(ML3DFace.LANDMARK_FIVE);
            float[] projectionMatrix = new float[ 4 * 4];
            float[] viewMatrix = new float[ 4 * 4];
            faces.get(i).get3DProjectionMatrix(projectionMatrix, 1, 10);
            faces.get(i).get3DViewMatrix(viewMatrix);

            int frameHeight = (int) unScaleX(overlay.getHeight());//Image Height
            int frameWidth = (int) unScaleY(overlay.getWidth());//Image Width

            float[] adaptMatrix = {frameWidth / 2, 0, frameWidth / 2, 0, -frameHeight / 2, frameHeight / 2, 0, 0, 1};
            List<MLPosition> face2dPoints = translateTo2D(face3dPoints, projectionMatrix, viewMatrix, adaptMatrix);

            StringBuilder sb = new StringBuilder();
            //Draw 2D Point
            Paint numPaint;
            numPaint = new Paint();
            numPaint.setColor(Color.RED);
            numPaint.setTextSize(frameHeight / 80);
            for (int j = 0; j < face2dPoints.size(); j++) {
                MLPosition point = face2dPoints.get(j);
                canvas.drawPoint(translateX(point.getX().floatValue()), translateY(point.getY().floatValue()), boxPaint);
                canvas.drawText("" + j, translateX(point.getX().floatValue()), translateY(point.getY().floatValue()), numPaint);
                sb.append(point.getX()+ " "+ point.getY()+"\n");

            }
        }

    }

    private List<MLPosition> translateTo2D(List<MLPosition> face3dPoints, float[] projectionMatrix, float[] viewMatrix, float[] adaptMatrix) {
        List<MLPosition> face2dPoints = new ArrayList<>();
        for (int i = 0; i < face3dPoints.size(); i++) {
            MLPosition curPoint = face3dPoints.get(i);
            float[] curVec = {curPoint.getX(), curPoint.getY(), curPoint.getZ(), 1};
            //1 V*Vec
            float[] temp1 = matrixMulti(viewMatrix, 4, 4, curVec);
            //2 P*(V*Vec)
            float[] temp2 = matrixMulti(projectionMatrix, 4, 4, temp1);
            //3 calculations x’ y'
            float[] temp3 = {temp2[0] / temp2[3], temp2[1] / temp2[3], 1};
            //4 calculations X Y coordinates
            float[] point = matrixMulti(adaptMatrix, 3, 3, temp3);
            face2dPoints.add(new MLPosition(point[0], point[1]));
        }
        return face2dPoints;
    }

    private float[] matrixMulti(float[] V, final int m, final int n, float[] vec) {
        float[] result = new float[n];
        for (int i = 0; i < n; i++) {
            float temp = 0;
            for (int j = 0; j < m; j++) {
                temp += V[i * m + j] * vec[j];
            }
            result[i] = temp;
        }
        return result;
    }

}