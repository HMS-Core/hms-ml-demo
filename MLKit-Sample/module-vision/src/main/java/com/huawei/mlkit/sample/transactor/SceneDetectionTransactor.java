/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.mlkit.sample.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.scd.MLSceneDetection;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzer;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzerFactory;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzerSetting;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.mlkit.sample.views.graphic.SceneDetectionGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SceneDetectionTransactor extends BaseTransactor<List<MLSceneDetection>> {

    private static final String TAG = "scdTransactor";

    private final MLSceneDetectionAnalyzer analyzer;
    private MLSceneDetectionAnalyzerSetting setting;

    private Context mContext;
    private float confidence;

    public SceneDetectionTransactor(Context context, float confidence) {
        this.mContext = context;
        this.confidence = confidence;
        //  Creating an Analyzer
        setting = new MLSceneDetectionAnalyzerSetting.Factory().setConfidence(confidence/100).create();
        this.analyzer = MLSceneDetectionAnalyzerFactory.getInstance().getSceneDetectionAnalyzer(setting);
    }

    public SceneDetectionTransactor(Context context) {
        this.mContext = context;
        //  Creating an Analyzer
        this.analyzer = MLSceneDetectionAnalyzerFactory.getInstance().getSceneDetectionAnalyzer();
    }

    @Override
    public void stop() {
        this.analyzer.stop();
    }

    @Override
    protected Task<List<MLSceneDetection>> detectInImage(MLFrame image) {
        return this.analyzer.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLSceneDetection> results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();

        // For synchronous display, if surfaceTexture is used, the preview image needs to be drawn.
        // If surfaceView is used asynchronously, the preview image does not need to be drawn.
        // This step can be added or not based on the setting of synchronous or asynchronous display.
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        // Create a layer on the camera preview image and draw the result.
        SceneDetectionGraphic graphic =
                new SceneDetectionGraphic(graphicOverlay, mContext, results, confidence);
        graphicOverlay.addGraphic(graphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "XXX failed: " + e.getMessage());
    }
}
