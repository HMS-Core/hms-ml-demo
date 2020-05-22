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
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mlkit.sample.views.graphic.CameraImageGraphic;
import com.mlkit.sample.views.graphic.LocalObjectGraphic;
import com.mlkit.sample.camera.FrameMetadata;
import com.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.objects.MLObject;
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzer;
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzerSetting;

import java.io.IOException;
import java.util.List;

public class LocalObjectTransactor extends BaseTransactor<List<MLObject>> {

    private static final String TAG = LocalObjectTransactor.class.getSimpleName();

    private final MLObjectAnalyzer detector;

    public LocalObjectTransactor(MLObjectAnalyzerSetting options) {
        this.detector = MLAnalyzerFactory.getInstance().getLocalObjectAnalyzer(options);
    }

    @Override
    public void stop() {
        super.stop();
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(LocalObjectTransactor.TAG, "Exception thrown while trying to close object transactor: " + e.getMessage());
        }
    }

    @Override
    protected Task<List<MLObject>> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLObject> results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();

        for (MLObject object : results) {
            LocalObjectGraphic objectGraphic = new LocalObjectGraphic(graphicOverlay, object);
            graphicOverlay.addGraphic(objectGraphic);
        }
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(LocalObjectTransactor.TAG, "Object detection failed: " + e.getMessage());
    }
}
