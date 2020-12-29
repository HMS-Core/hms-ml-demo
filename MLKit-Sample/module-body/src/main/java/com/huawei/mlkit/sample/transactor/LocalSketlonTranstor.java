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
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;
import com.huawei.mlkit.sample.activity.skeleton.HumanSkeletonActivity;
import com.huawei.mlkit.sample.activity.skeleton.TemplateActivity;
import com.huawei.mlkit.sample.activity.adapter.skeleton.GridItem;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.mlkit.sample.views.graphic.LocalSkeletonGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.util.List;

import static com.huawei.mlkit.sample.activity.skeleton.HumanSkeletonActivity.isOpenStatus;

import static com.huawei.mlkit.sample.activity.skeleton.TemplateActivity.*;
import static com.huawei.mlkit.sample.activity.skeleton.TemplateActivity.getSelectedIndex;

/**
 *  SketlonTranstor
 *
 * @since  2020-12-10
 */

public class LocalSketlonTranstor extends BaseTransactor<List<MLSkeleton>> {
    private static final String TAG = "LocalSketlonTransactor";

    private static MLSkeletonAnalyzer analyzer;

    private Handler mHandler;

    public LocalSketlonTranstor(MLSkeletonAnalyzerSetting setting, Context context, Handler handler) {
        super(context);
        Log.i(TAG, "analyzer init");
        this.mHandler = handler;
        if (analyzer != null) {
            stop();
        }
        analyzer = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer(setting);
    }

    @Override
    public void stop() {
        try {
            Log.i(TAG,   "analyzer stop.");
            this.analyzer.stop();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close sketlon transactor: " + e.getMessage());
        }
    }

    @Override
    public Task<List<MLSkeleton>> detectInImage(MLFrame image) {
        return this.analyzer.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLSkeleton> MLSkeletons,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d(TAG, "Total MLSkeletons graphicOverlay start");
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        if (MLSkeletons == null || MLSkeletons.isEmpty()) {
            return;
        }
        Log.d(TAG, "Total MLSkeletons hmsMLLocalFaceGraphic start");
        LocalSkeletonGraphic hmsMLLocalSkeletonGraphic = new LocalSkeletonGraphic(graphicOverlay, MLSkeletons);
        graphicOverlay.addGraphic(hmsMLLocalSkeletonGraphic);
        graphicOverlay.postInvalidate();
        Log.d(TAG, "Total MLSkeletons graphicOverlay end");

        if (!isOpenStatus()) {
            return;
        }
        if (mHandler == null) {
            return;
        }
        compareSimilarity(MLSkeletons);
    }

    private void compareSimilarity(List<MLSkeleton> skeletons) {
        // Select template from templates.
        int index = 0;
        if (getSelectedIndex() == -1) {
            index = 0;
        } else {
            index = getSelectedIndex();
        }
        GridItem gridItem = getTemplateDataMap().get(TemplateActivity.getKey() + index);

        // Calculate similarity of two skeletons.
        List<MLSkeleton> templateList = gridItem.getSkeletonList();
        if (templateList == null) {
            return;
        }
        float result = analyzer.caluteSimilarity(skeletons, templateList);
        if (result < 0f || result > 1f) {
            return;
        }
        Log.i(TAG, "similarity : " + result);

        // Send msg to handle for display.
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putFloat(HumanSkeletonActivity.SIMILARITY, result);
        msg.setData(bundle);
        msg.what = HumanSkeletonActivity.UPDATE_VIEW;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Skeleton detection failed: " + e.getMessage());
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }
}
