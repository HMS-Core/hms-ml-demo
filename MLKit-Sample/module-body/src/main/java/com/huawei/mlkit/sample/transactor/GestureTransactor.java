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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.gesture.MLGesture;
import com.huawei.hms.mlsdk.gesture.MLGestureAnalyzer;
import com.huawei.hms.mlsdk.gesture.MLGestureAnalyzerFactory;
import com.huawei.hms.mlsdk.gesture.MLGestureAnalyzerSetting;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.mlkit.sample.views.graphic.GestureGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import java.util.List;

public class GestureTransactor extends BaseTransactor<List<MLGesture>> {

    private static final String TAG = "GestureTransactor";

    private final MLGestureAnalyzer analyzer;

    private long start;
    private Context mContext;

    public GestureTransactor(Context context) {
        this.mContext = context;
        MLGestureAnalyzerSetting setting = new MLGestureAnalyzerSetting
                .Factory()
                .create();
        this.analyzer = MLGestureAnalyzerFactory.getInstance().getGestureAnalyzer(setting);
    }

    @Override
    public void stop() {
        this.analyzer.stop();
    }

    @Override
    protected Task<List<MLGesture>> detectInImage(MLFrame image) {
        start = System.currentTimeMillis();
        return this.analyzer.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLGesture> results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d(TAG,"gesture detect time end to end:" + (System.currentTimeMillis() - start));
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        GestureGraphic graphic = new GestureGraphic(graphicOverlay, mContext, results);
        graphicOverlay.addGraphic(graphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Gesture failed: " + e.getMessage());
    }
}
