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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzer;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerFactory;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerSetting;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;
import com.huawei.mlkit.sample.activity.adapter.skeleton.GridItem;
import com.huawei.mlkit.sample.activity.skeleton.HumanSkeletonActivity;
import com.huawei.mlkit.sample.activity.skeleton.TemplateActivity;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.mlkit.sample.views.graphic.HandKeypointGraphic;
import com.huawei.mlkit.sample.views.graphic.LocalSkeletonGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.util.List;

import static com.huawei.mlkit.sample.activity.skeleton.HumanSkeletonActivity.isOpenStatus;
import static com.huawei.mlkit.sample.activity.skeleton.TemplateActivity.getSelectedIndex;
import static com.huawei.mlkit.sample.activity.skeleton.TemplateActivity.getTemplateDataMap;

/**
 *  HandKeypointTransactor
 *
 * @since  2020-12-10
 */

public class HandKeypointTransactor extends BaseTransactor<List<MLHandKeypoints>> {
    private static final String TAG = "LocalSketlonTransactor";
    private static final int MAXHANDRESULTS = 2;

    private static MLHandKeypointAnalyzer analyzer;

    private Handler mHandler;

    public HandKeypointTransactor(MLHandKeypointAnalyzerSetting setting, Context context, Handler handler) {
        super(context);
        Log.i(TAG, "analyzer init");
        this.mHandler = handler;
        if (analyzer != null) {
            stop();
        }
        analyzer = MLHandKeypointAnalyzerFactory.getInstance().getHandKeypointAnalyzer(setting);
    }

    public HandKeypointTransactor() {
        MLHandKeypointAnalyzerSetting setting = new MLHandKeypointAnalyzerSetting
                .Factory()
                .setSceneType(MLHandKeypointAnalyzerSetting.TYPE_ALL)
                .setMaxHandResults(MAXHANDRESULTS)
                .create();
        this.analyzer = MLHandKeypointAnalyzerFactory.getInstance().getHandKeypointAnalyzer(setting);
    }

    @Override
    public void stop() {
        try {
            Log.i(TAG,   "analyzer stop.");
            this.analyzer.stop();
        } catch (Exception e) {
            Log.e(TAG, "Exception thrown while trying to close sketlon transactor: " + e.getMessage());
        }
    }

    @Override
    public Task<List<MLHandKeypoints>> detectInImage(MLFrame image) {
        return this.analyzer.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLHandKeypoints> mlHandKeypoints,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        if (mlHandKeypoints == null || mlHandKeypoints.isEmpty()) {
            return;
        }
		
        HandKeypointGraphic handKeypointGraphic = new HandKeypointGraphic(graphicOverlay, mlHandKeypoints);
        graphicOverlay.addGraphic(handKeypointGraphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "HandKeypoint failed: " + e.getMessage());
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }
}
