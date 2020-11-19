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

package com.huawei.mlkit.sample.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzer;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerFactory;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerSetting;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.mlkit.sample.views.graphic.HandKeypointGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.List;

public class HandKeypointTransactor extends BaseTransactor<List<MLHandKeypoints>> {

    private static final String TAG = "HandKeypoint";
    private static final int MAXHANDRESULTS = 2;

    private final MLHandKeypointAnalyzer analyzer;

    private long start;
    private Context mContext;

    public HandKeypointTransactor(Context context) {
        this.mContext = context;
        MLHandKeypointAnalyzerSetting setting = new MLHandKeypointAnalyzerSetting
                .Factory()
                .setSceneType(MLHandKeypointAnalyzerSetting.TYPE_ALL)
                .setMaxHandResults(MAXHANDRESULTS)
                .create();
        this.analyzer = MLHandKeypointAnalyzerFactory.getInstance().getHandKeypointAnalyzer(setting);
    }

    @Override
    public void stop() {
        if (this.analyzer != null){
            this.analyzer.stop();
        }
    }

    @Override
    protected Task<List<MLHandKeypoints>> detectInImage(MLFrame image) {
        start = System.currentTimeMillis();
        return this.analyzer.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLHandKeypoints> results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d(TAG, "hand detect time end to end:" + (System.currentTimeMillis() - start));
        // For synchronous display, if surfaceTexture is used, you need to draw the preview image. If surfaceView is used asynchronously, you do not need to draw the preview image.
        // In this step, you can choose whether to add the data based on the setting of synchronous or asynchronous display.
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        HandKeypointGraphic graphic =
                new HandKeypointGraphic(graphicOverlay, mContext, results);
        graphicOverlay.addGraphic(graphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "HandKeypoint failed: " + e.getMessage());
    }
}
