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
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.huawei.hms.mlsdk.common.MLPosition;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceKeyPoint;
import com.huawei.hms.mlsdk.face.MLFaceShape;
import com.huawei.mlkit.sample.util.CommonUtils;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalFaceGraphic extends BaseGraphic {
    private static final String TAG = LocalFaceGraphic.class.getSimpleName();
    private final GraphicOverlay overlay;

    private final Paint keypointPaint;

    private final Paint facePaint;
    private final Paint eyePaint;
    private final Paint eyebrowPaint;
    private final Paint lipPaint;
    private final Paint nosePaint;
    private final Paint noseBasePaint;
    private final Paint textPaint;
    private final Paint faceFeaturePaintText;
    private final Paint faceFeaturePaint;
    private final Paint borderPaint;

    private volatile List<MLFace> faces;
    private Context mContext;

    public LocalFaceGraphic(GraphicOverlay overlay, List<MLFace> faces, Context context) {
        super(overlay);

        this.mContext = context;
        this.faces = faces;
        this.overlay = overlay;

        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(CommonUtils.dp2px(this.mContext, 6));
        this.textPaint.setTypeface(Typeface.DEFAULT);

        this.faceFeaturePaintText = new Paint();
        this.faceFeaturePaintText.setColor(Color.WHITE);
        this.faceFeaturePaintText.setTextSize(CommonUtils.dp2px(this.mContext, 11));
        this.faceFeaturePaintText.setTypeface(Typeface.DEFAULT);

        this.faceFeaturePaint = new Paint();
        this.faceFeaturePaint.setColor(this.faceFeaturePaintText.getColor());
        this.faceFeaturePaint.setStyle(Paint.Style.STROKE);
        this.faceFeaturePaint.setStrokeWidth(CommonUtils.dp2px(this.mContext, 2));

        float lineWidth = CommonUtils.dp2px(this.mContext, 1);
        this.facePaint = new Paint();
        this.facePaint.setColor(Color.parseColor("#ffcc66"));
        this.facePaint.setStyle(Paint.Style.STROKE);
        this.facePaint.setStrokeWidth(lineWidth);

        this.keypointPaint = new Paint();
        this.keypointPaint.setColor(Color.RED);
        this.keypointPaint.setStyle(Paint.Style.FILL);
        this.keypointPaint.setStrokeWidth(CommonUtils.dp2px(this.mContext, 2));

        this.eyePaint = new Paint();
        this.eyePaint.setColor(Color.parseColor("#00ccff"));
        this.eyePaint.setStyle(Paint.Style.STROKE);
        this.eyePaint.setStrokeWidth(lineWidth);

        this.eyebrowPaint = new Paint();
        this.eyebrowPaint.setColor(Color.parseColor("#006666"));
        this.eyebrowPaint.setStyle(Paint.Style.STROKE);
        this.eyebrowPaint.setStrokeWidth(lineWidth);

        this.nosePaint = new Paint();
        this.nosePaint.setColor(Color.parseColor("#ffff00"));
        this.nosePaint.setStyle(Paint.Style.STROKE);
        this.nosePaint.setStrokeWidth(lineWidth);

        this.noseBasePaint = new Paint();
        this.noseBasePaint.setColor(Color.parseColor("#ff6699"));
        this.noseBasePaint.setStyle(Paint.Style.STROKE);
        this.noseBasePaint.setStrokeWidth(lineWidth);

        this.lipPaint = new Paint();
        this.lipPaint.setColor(Color.parseColor("#990000"));
        this.lipPaint.setStyle(Paint.Style.STROKE);
        this.lipPaint.setStrokeWidth(lineWidth);

        this.borderPaint = new Paint();
        this.borderPaint.setColor(Color.parseColor("#ffcc66"));
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(lineWidth);
    }

    public List<String> sortHashMap(HashMap<String, Float> map) {
        Set<Map.Entry<String, Float>> entey = map.entrySet();
        List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(entey);
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                if (o2.getValue() - o1.getValue() >= 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        List<String> emotions = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            emotions.add(list.get(i).getKey());
        }
        return emotions;
    }

    @Override
    public void draw(Canvas canvas) {
        if (this.faces == null || this.faces.size() == 0) {
            return;
        }
        // Draw rect of face.
        for (MLFace mlFace : this.faces) {
            canvas.drawRect(translateRect(mlFace.getBorder()), borderPaint);
        }
        // Draw points of face.
        paintKeyPoint(this.faces.get(0), canvas);
        // Draw shape on the detected face (except the first one).
        for (int i = 1; i < this.faces.size(); i ++) {
            paintFaceShape(this.faces.get(i), canvas);
        }
        // Draw features of the first face.
        paintFeatures(this.faces.get(0), canvas);
    }

    /**
     * Draw shape of the input face.
     * @param face   Face information you want to draw.
     * @param canvas canvas
     */
    private void paintFaceShape(MLFace face, Canvas canvas) {
        if (face == null) {
            return;
        }
        List<MLPosition> allPoints = face.getAllPoints();
        for (MLPosition mlPosition : allPoints) {
            if (mlPosition != null) {
                canvas.drawPoint(this.translateX(mlPosition.getX().floatValue()), this.translateY(mlPosition.getY().floatValue()), this.facePaint);
            }
        }
    }

    /**
     * Draw all the features of the input faces.
     *
     * @param face   Face information you want to draw.
     * @param canvas canvas.
     */
    private void paintFeatures(MLFace face, Canvas canvas) {
        float start = this.overlay.getWidth() / 4.0f;
        float x = start;
        float width = this.overlay.getWidth() / 3.0f;
        float y;
        if (this.isLandScape()) {
            y = (CommonUtils.dp2px(this.mContext, this.overlay.getHeight() / 8.0f)) < 130 ? 130 : (CommonUtils.dp2px(this.mContext, this.overlay.getHeight() / 8.0f));
        } else {
            y = (CommonUtils.dp2px(this.mContext, this.overlay.getHeight() / 16.0f)) < 340.0 ? 340 : (CommonUtils.dp2px(this.mContext, this.overlay.getHeight() / 16.0f));
            if (this.overlay.getHeight() > 2500) {
                y = CommonUtils.dp2px(this.mContext, this.overlay.getHeight() / 10.0f);
            }
        }
        Log.i(TAG, x + "," + y + "; height" + this.overlay.getHeight() + ",width" + this.overlay.getWidth());
        float space = CommonUtils.dp2px(this.mContext, 12);
        HashMap<String, Float> emotions = new HashMap<>();
        emotions.put("Smiling", face.possibilityOfSmiling());
        emotions.put("Neutral", face.getEmotions().getNeutralProbability());
        emotions.put("Angry", face.getEmotions().getAngryProbability());
        emotions.put("Fear", face.getEmotions().getFearProbability());
        emotions.put("Sad", face.getEmotions().getSadProbability());
        emotions.put("Disgust", face.getEmotions().getDisgustProbability());
        emotions.put("Surprise", face.getEmotions().getSurpriseProbability());
        List<String> result = this.sortHashMap(emotions);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        canvas.drawText("Glass Probability: " + decimalFormat.format(face.getFeatures().getSunGlassProbability()), x, y, this.faceFeaturePaintText);
        x = x + width;
        String sex = (face.getFeatures().getSexProbability() > 0.5f) ? "Female" : "Male";
        canvas.drawText("Gender: " + sex, x, y, this.faceFeaturePaintText);
        y = y - space;
        x = start;
        canvas.drawText("EulerAngleY: " + decimalFormat.format(face.getRotationAngleY()), x, y, this.faceFeaturePaintText);
        x = x + width;
        canvas.drawText("EulerAngleX: " + decimalFormat.format(face.getRotationAngleX()), x, y, this.faceFeaturePaintText);
        y = y - space;
        x = start;
        canvas.drawText("EulerAngleZ: " + decimalFormat.format(face.getRotationAngleZ()), x, y, this.faceFeaturePaintText);
        x = x + width;
        canvas.drawText("Emotion: " + result.get(0), x, y, this.faceFeaturePaintText);
        x = start;
        y = y - space;
        canvas.drawText("Hat Probability: " + decimalFormat.format(face.getFeatures().getHatProbability()), x, y, this.faceFeaturePaintText);
        x = x + width;
        canvas.drawText("Age: " + face.getFeatures().getAge(), x, y, this.faceFeaturePaintText);
        y = y - space;
        x = start;
        canvas.drawText("Moustache Probability: " + decimalFormat.format(face.getFeatures().getMoustacheProbability()), x, y, this.faceFeaturePaintText);
        y = y - space;
        canvas.drawText("Right eye open Probability: " + decimalFormat.format(face.opennessOfRightEye()), x, y, this.faceFeaturePaintText);
        y = y - space;
        canvas.drawText("Left eye open Probability: " + decimalFormat.format(face.opennessOfLeftEye()), x, y, this.faceFeaturePaintText);
    }

    /**
     * Draw keypoints of the input faces.
     *
     * @param face  Face information you want to draw.
     * @param canvas canvas
     */
    private void paintKeyPoint(MLFace face, Canvas canvas) {
        for (MLFaceShape contour : face.getFaceShapeList()) {
            if (contour == null) {
                continue;
            }
            List<MLPosition> points = contour.getPoints();
            for (int i = 0; i < points.size(); i++) {
                MLPosition point = points.get(i);
                if (point == null) {
                    continue;
                }
                canvas.drawPoint(this.translateX(point.getX().floatValue()), this.translateY(point.getY().floatValue()), this.faceFeaturePaint);
                if (i != (points.size() - 1)) {
                    MLPosition next = points.get(i + 1);
                    if (point.getX() != null && point.getY() != null) {
                        canvas.drawLines(new float[]{this.translateX(point.getX().floatValue()), this.translateY(point.getY().floatValue()),
                                this.translateX(next.getX().floatValue()), this.translateY(next.getY().floatValue())}, this.getPaint(contour));
                        if (i % 3 == 0) {
                            canvas.drawText(i + 1 + "", this.translateX(point.getX().floatValue()), this.translateY(point.getY().floatValue()), this.textPaint);
                        }
                    }
                }
            }
        }
        for (MLFaceKeyPoint keypoint : face.getFaceKeyPoints()) {
            if (keypoint != null) {
                MLPosition point = keypoint.getPoint();
                canvas.drawCircle(
                        this.translateX(point.getX()),
                        this.translateY(point.getY()),
                        CommonUtils.dp2px(this.mContext, 3), this.keypointPaint);
            }
        }
    }

    private boolean isLandScape() {
        Configuration configuration = this.mContext.getResources().getConfiguration(); // Get the configuration information.
        int ori = configuration.orientation; // Get screen orientation.
        return ori == Configuration.ORIENTATION_LANDSCAPE;
    }

    private Paint getPaint(MLFaceShape contour) {
        switch (contour.getFaceShapeType()) {
            case MLFaceShape.TYPE_LEFT_EYE:
            case MLFaceShape.TYPE_RIGHT_EYE:
                return this.eyePaint;

            case MLFaceShape.TYPE_BOTTOM_OF_LEFT_EYEBROW:
            case MLFaceShape.TYPE_BOTTOM_OF_RIGHT_EYEBROW:
            case MLFaceShape.TYPE_TOP_OF_LEFT_EYEBROW:
            case MLFaceShape.TYPE_TOP_OF_RIGHT_EYEBROW:
                return this.eyebrowPaint;

            case MLFaceShape.TYPE_BOTTOM_OF_LOWER_LIP:
            case MLFaceShape.TYPE_TOP_OF_LOWER_LIP:
            case MLFaceShape.TYPE_BOTTOM_OF_UPPER_LIP:
            case MLFaceShape.TYPE_TOP_OF_UPPER_LIP:
                return this.lipPaint;

            case MLFaceShape.TYPE_BOTTOM_OF_NOSE:
                return this.noseBasePaint;

            case MLFaceShape.TYPE_BRIDGE_OF_NOSE:
                return this.nosePaint;

            default:
                return this.facePaint;
        }
    }
}