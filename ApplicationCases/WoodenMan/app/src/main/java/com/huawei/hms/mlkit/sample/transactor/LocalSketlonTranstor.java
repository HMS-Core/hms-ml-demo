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
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlkit.sample.camera.FrameMetadata;
import com.huawei.hms.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.hms.mlkit.sample.views.graphic.LocalSkeletonGraphic;
import com.huawei.hms.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class LocalSketlonTranstor extends BaseTransactor<List<MLSkeleton>> {
    private static final String TAG = "LocalSketlonTransactor";

    private  final MLSkeletonAnalyzer detector;
    private int zeroCount = 0;
    private Handler mHandler;

    private FrameMetadata mFrameMetadata;

    private ByteBuffer latestImage;

    public LocalSketlonTranstor(MLSkeletonAnalyzerSetting setting, Context context) {
        super(context);
        this.detector = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer(setting);
    }

    public LocalSketlonTranstor(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
        MLSkeletonAnalyzerSetting setting = new MLSkeletonAnalyzerSetting.Factory()
                .create();
        detector = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer(setting);
    }

    @Override
    public void stop() {
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close sketlon transactor: " + e.getMessage());
        }
    }

    public MLSkeletonAnalyzer getDetector() {
        return detector;
    }

    public ByteBuffer getProcessingImage() {
        return latestImage;
    }

    public FrameMetadata getFrameMetadata() {
        return mFrameMetadata;
    }

    @Override
    public Task<List<MLSkeleton>> detectInImage(MLFrame image) {
        latestImage = image.getByteBuffer();
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLSkeleton> MLSkeletons,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        mFrameMetadata = frameMetadata;
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        if (MLSkeletons == null || MLSkeletons.isEmpty()) {
            return;
        }
        LocalSkeletonGraphic hmsMLLocalSkeletonGraphic = new LocalSkeletonGraphic(graphicOverlay, MLSkeletons);
        graphicOverlay.addGraphic(hmsMLLocalSkeletonGraphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed: " + e.getMessage());
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }
}
