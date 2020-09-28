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

package com.huawei.hms.mlkit.sample.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlkit.sample.activity.ModelGameStartOneActivity;
import com.huawei.hms.mlkit.sample.activity.TemplateActivity;
import com.huawei.hms.mlkit.sample.camera.FrameMetadata;
import com.huawei.hms.mlkit.sample.views.CustomDialog;
import com.huawei.hms.mlkit.sample.views.GridItem;
import com.huawei.hms.mlkit.sample.views.SkeletonGraphic;
import com.huawei.hms.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.hms.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.common.MLPosition;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceShape;
import com.huawei.hms.mlsdk.skeleton.MLJoint;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelGameOneProcessor extends BaseTransactor<List<MLSkeleton>> {
    private static final String TAG = "ModelGameProcessor";
    public static final int UPDATE_SCORES_VIEW = 1010;

    private MLSkeletonAnalyzer detector;

    private Handler mHandler;

    private int zeroCount = 0;

    public ModelGameOneProcessor(Handler handler, Context context) {
        super(context);
        MLSkeletonAnalyzerSetting setting = new MLSkeletonAnalyzerSetting.Factory()
                .create();
        detector = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer(setting);
        this.mHandler = handler;
    }

    @Override
    public void stop() {
        super.stop();
        try {
            detector.stop();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close cloud image labeler!", e);
        }
    }

    public MLSkeletonAnalyzer getDetector() {
        return detector;
    }

    @Override
    public Task<List<MLSkeleton>> detectInImage(MLFrame frame) {
        return detector.asyncAnalyseFrame(frame);
    }

    @Override
    protected void onSuccess(Bitmap originalCameraImage, List<MLSkeleton> skeletons, FrameMetadata frameMetadata,
                             GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        if (skeletons.isEmpty()) {
            return;
        }

        if (ModelGameStartOneActivity.isStartStatus()  && mHandler != null) {

            MLFace mlFace = FaceDetectionProcessor.mlFace;

            List<Float> xx = new ArrayList<>();
            List<Float> yy = new ArrayList<>();

            if (mlFace != null) {
                if (mlFace.getFaceShapeList() == null) {
                    return;
                }
                xx.clear();
                yy.clear();
                for (MLFaceShape contour : mlFace.getFaceShapeList()) {
                    if (contour == null) {
                        continue;
                    }
                    List<MLPosition> points;
                    if (contour.getFaceShapeType() == MLFaceShape.TYPE_FACE) {
                        points = contour.getPoints();
                        Log.i(TAG, "isPoint TYPE_FACE value is : " + points);
                        for (int j = 0; j < points.size(); j++) {
                            MLPosition point = points.get(j);
                            xx.add(point.getX());
                            yy.add(point.getY());
                        }
                    }
                }

            }

            MLJoint mlJoint = null;
            Map<Integer, MLJoint> map = new HashMap<>();
            boolean isPoint = false;
            List<MLSkeleton> skeletons_list = new ArrayList<>();
            graphicOverlay.postInvalidate();
            for (int i = 0; i < skeletons.size(); i++) {
                mlJoint = skeletons.get(i).getJointPoint(MLJoint.TYPE_HEAD_TOP);
                Log.i(TAG, "isPoint TYPE_HEAD_TOP value is : " + mlJoint.getPointX());
                Log.i(TAG, "isPoint TYPE_HEAD_TOP value is : " + mlJoint.getPointY());
                mlJoint.getPointX();
                mlJoint.getPointY();
                map.put(i, mlJoint);
                isPoint = isPointInPolygon(mlJoint.getPointX(), mlJoint.getPointY(), xx, yy);
                Log.i(TAG, "isPoint value is : " + isPoint);
            }

            if (isPoint) {
                int key_area_max = 0;
                for (Map.Entry<Integer, MLJoint> entry : map.entrySet()) {
                    if (mlJoint == entry.getValue()) {
                        key_area_max = entry.getKey();
                    }
                }
                skeletons_list.add(skeletons.get(key_area_max));
                SkeletonGraphic skeletonGraphic = new SkeletonGraphic(graphicOverlay, skeletons_list);
                graphicOverlay.addGraphic(skeletonGraphic);
                compareSimilarity(skeletons_list);
            }

        }
    }

    public boolean isIntersect(double px1, double py1, double px2, double py2, double px3, double py3, double px4, double py4) {
        boolean flag = false;
        double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
        if (d != 0) {
            double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3)) / d;
            double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1)) / d;
            if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean isPointOnLine(double px0, double py0, double px1, double py1, double px2, double py2) {
        boolean flag = false;
        double ESP = 1e-9;
        if ((Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP) && ((px0 - px1) * (px0 - px2) <= 0)
                && ((py0 - py1) * (py0 - py2) <= 0)) {
            flag = true;
        }
        return flag;
    }

    public double Multiply(double px0, double py0, double px1, double py1, double px2, double py2) {
        return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));
    }

    public boolean isPointInPolygon(double px, double py, List<Float> polygonXA, List<Float> polygonYA) {
        boolean isInside = false;
        double ESP = 1e-9;
        int count = 0;
        double linePoint1x;
        double linePoint1y;
        double linePoint2x = 180;
        double linePoint2y;
        linePoint1x = px;
        linePoint1y = py;
        linePoint2y = py;

        for (int i = 0; i < polygonXA.size() - 1; i++) {
            double cx1 = polygonXA.get(i);
            double cy1 = polygonYA.get(i);
            double cx2 = polygonXA.get(i + 1);
            double cy2 = polygonYA.get(i + 1);
            if (isPointOnLine(px, py, cx1, cy1, cx2, cy2)) {
                return true;
            }
            if (Math.abs(cy2 - cy1) < ESP) {
                continue;
            }
            if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                if (cy1 > cy2)
                    count++;
            } else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                if (cy2 > cy1)
                    count++;
            } else if (isIntersect(cx1, cy1, cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y)) {
                count++;
            }
        }
        if (count % 2 == 1) {
            isInside = true;
        }
        return isInside;
    }

    private void compareSimilarity(List<MLSkeleton> skeletons) {
        float similarity = 0f;
        int index = 0;
        if (TemplateActivity.getSelectedIndex() == -1) {
            index = 0;
        } else {
            index = TemplateActivity.getSelectedIndex();
        }
        GridItem gridItem = TemplateActivity.getTemplateDataMap().get("key" + index);

        List<MLSkeleton> templateList = gridItem.getSkeletonList();

        if (templateList == null) {
            return;
        }

        float result = detector.caluteSimilarity(skeletons, templateList);
        if (result > similarity) {
            similarity = result;
        }
        Log.d(TAG, "similarity : " + similarity);

        if (similarity == 0) {
            zeroCount++;
            if (zeroCount == 1) {
                return;
            } else {
                zeroCount = 0;
            }
        } else {
            zeroCount = 0;
        }
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putFloat("similarity", similarity);
        msg.setData(bundle);
        msg.what = ModelGameStartOneActivity.UPDATE_SIMILARITY_VIEW;
        mHandler.sendMessage(msg);
        Log.d(TAG, "CustomDialog.sThresholdValue:" + CustomDialog.getThresholdValue());
        if (similarity > CustomDialog.getThresholdValue()) {
            mHandler.sendEmptyMessage(ModelGameStartOneActivity.AUTO_START);
        }
    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "skeleton detection failed " + e);
    }
}