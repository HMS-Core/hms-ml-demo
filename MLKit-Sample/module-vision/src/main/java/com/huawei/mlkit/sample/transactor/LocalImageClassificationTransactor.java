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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.huawei.hmf.tasks.Task;
import com.huawei.mlkit.sample.views.graphic.LocalImageClassificationGraphic;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.classification.MLImageClassification;
import com.huawei.hms.mlsdk.classification.MLImageClassificationAnalyzer;

import java.io.IOException;
import java.util.List;

public class LocalImageClassificationTransactor extends BaseTransactor<List<MLImageClassification>> {

    private static final String TAG = "ClassificationTrans";

    private final MLImageClassificationAnalyzer detector;

    private Context mContext;

    public LocalImageClassificationTransactor(Context context) {
        this.mContext = context;
        this.detector = MLAnalyzerFactory.getInstance().getLocalImageClassificationAnalyzer();
    }

    @Override
    public void stop() {
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(LocalImageClassificationTransactor.TAG,
                    "Exception thrown while trying to close image classification transactor: " + e.getMessage());
        }
    }

    @Override
    protected Task<List<MLImageClassification>> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLImageClassification> classifications,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();

        LocalImageClassificationGraphic hmsMLLocalImageClassificationGraphic =
                new LocalImageClassificationGraphic(graphicOverlay, mContext, classifications);
        graphicOverlay.addGraphic(hmsMLLocalImageClassificationGraphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(LocalImageClassificationTransactor.TAG, "Image classification failed: " + e.getMessage());
    }
}
