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

package com.mlkit.sample.transactor;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.mlkit.sample.util.Constant;
import com.mlkit.sample.views.graphic.RemoteLandmarkGraphic;
import com.mlkit.sample.camera.FrameMetadata;
import com.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzer;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzerSetting;

import java.io.IOException;
import java.util.List;

public class RemoteLandmarkTransactor extends BaseTransactor<List<MLRemoteLandmark>> {

    private static final String TAG = "LandmarkTransactor";

    private final MLRemoteLandmarkAnalyzer detector;

    private Handler handler;

    public RemoteLandmarkTransactor(Handler handler) {
        super();
        this.detector = MLAnalyzerFactory.getInstance().getRemoteLandmarkAnalyzer();
        this.handler = handler;
    }

    public RemoteLandmarkTransactor(MLRemoteLandmarkAnalyzerSetting options) {
        super();
        this.detector = MLAnalyzerFactory.getInstance().getRemoteLandmarkAnalyzer(options);
    }

    @Override
    public void stop() {
        super.stop();
        try {
            this.detector.close();
        } catch (IOException e) {
            Log.e(RemoteLandmarkTransactor.TAG, "Exception thrown while trying to close remote landmark transactor: " + e.getMessage());
        }
    }

    @Override
    protected Task<List<MLRemoteLandmark>> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            Bitmap originalCameraImage,
            List<MLRemoteLandmark> results,
            FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        this.handler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
        if (results != null && !results.isEmpty()) {
            graphicOverlay.clear();
            for (MLRemoteLandmark landmark : results) {
                RemoteLandmarkGraphic landmarkGraphic = new RemoteLandmarkGraphic(graphicOverlay, landmark);
                graphicOverlay.addGraphic(landmarkGraphic);
            }
            graphicOverlay.postInvalidate();
        }
    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(RemoteLandmarkTransactor.TAG, "Remote landmark detection failed: " + e.getMessage());
        this.handler.sendEmptyMessage(Constant.GET_DATA_FAILED);
    }
}
