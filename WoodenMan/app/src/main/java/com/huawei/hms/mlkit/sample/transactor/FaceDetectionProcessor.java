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

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlkit.sample.camera.CameraImageGraphic;
import com.huawei.hms.mlkit.sample.camera.FaceGraphic;
import com.huawei.hms.mlkit.sample.camera.FrameMetadata;
import com.huawei.hms.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.common.MLPosition;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.huawei.hms.mlsdk.face.MLFaceShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceDetectionProcessor extends BaseFaceTransactor<List<MLFace>> {

    private static final String TAG = "FaceDetectionProcessor";

    private final MLFaceAnalyzer detector;

    private long start;

    private Handler mHandler;

    public static MLFace mlFace;

    public FaceDetectionProcessor(Handler handler, MLFaceAnalyzerSetting options) {
        detector = MLAnalyzerFactory.getInstance().getFaceAnalyzer(options);
        this.mHandler = handler;
    }

    @Override
    public void stop() {
        detector.destroy();
    }

    protected Task<List<MLFace>> detectInImage(MLFrame frame) {
        start = System.currentTimeMillis();
        return detector.asyncAnalyseFrame(frame);
    }

    @Override
    protected void onSuccess(Bitmap originalCameraImage, List<MLFace> faces, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        Log.d(TAG, "detectInImage duration=" + (System.currentTimeMillis() - start));
        graphicOverlay.clear();
        mlFace = null;
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }
        List<Float> xx = new ArrayList<>();
        List<Float> yy = new ArrayList<>();

        Map<Integer, Float> map = new HashMap<Integer, Float>();

        for (int i = 0; i < faces.size(); ++i) {
            MLFace face = faces.get(i);



            if (face.getFaceShapeList() == null) {
                return;
            }
            xx.clear();
            yy.clear();
            for (MLFaceShape contour : face.getFaceShapeList()) {
                if (contour == null) {
                    continue;
                }
                List<MLPosition> points;
                if (contour.getFaceShapeType() == MLFaceShape.TYPE_FACE) {
                    points = contour.getPoints();

                    for (int j = 0; j < points.size(); j++) {
                        MLPosition point = points.get(j);
                        xx.add(point.getX());
                        yy.add(point.getY());
                    }
                }
            }

            Float min_X = Collections.min(xx);
            Float max_X = Collections.max(xx);
            Float min_Y = Collections.min(yy);
            Float max_Y = Collections.max(yy);
            float width_X = max_X - min_X;
            float width_Y = max_Y - min_Y;
            float area_XY = width_X * width_Y;
            map.put(i, area_XY);
        }
        List<Float> area_list = new ArrayList<Float>(map.values());
        if (!area_list.isEmpty()) {
            Float area_max = Collections.max(area_list);
            int key_area_max = 0;
            for (Map.Entry<Integer, Float> entry : map.entrySet()) {
                if (area_max.equals(entry.getValue())) {
                    key_area_max = entry.getKey();
                }
            }
            mlFace = faces.get(key_area_max);

            int cameraFacing =
                    frameMetadata != null ? frameMetadata.getCameraFacing() :
                            Camera.CameraInfo.CAMERA_FACING_BACK;
            FaceGraphic faceGraphic = new FaceGraphic(graphicOverlay, mlFace, cameraFacing, null/*overlayBitmap*/);
            graphicOverlay.addGraphic(faceGraphic);
            graphicOverlay.postInvalidate();
        }

    }

    protected SparseArray<MLFace> syncDetectInImage(MLFrame frame) {
        return detector.analyseFrame(frame);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed " + e);
    }
}
