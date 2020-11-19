/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.mlkit.sample.transactor;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;

import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.mlkit.sample.utils.SkeletonUtils;
import com.huawei.mlkit.sample.views.graphic.SkeletonGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.ArrayList;
import java.util.List;

public class SkeletonTransactor implements MLAnalyzer.MLTransactor<MLSkeleton> {
    private static final String TAG = "SkeletonTransactor";
    private MLSkeletonAnalyzer analyzer;
    private GraphicOverlay graphicOverlay;
    private LensEngine lensEngine;
    private Activity activity;
    private List<MLSkeleton> templateList;
    private int zeroCount = 0;


    public SkeletonTransactor(MLSkeletonAnalyzer analyzer, GraphicOverlay overlay, LensEngine lensEngine, Activity activity) {
        this.analyzer = analyzer;
        this.graphicOverlay = overlay;
        this.lensEngine = lensEngine;
        this.activity = activity;
        templateList = SkeletonUtils.getTemplateData();
    }

    @Override
    public void transactResult(MLAnalyzer.Result<MLSkeleton> results) {
        Log.e(TAG, "detect success");
        graphicOverlay.clear();

        SparseArray<MLSkeleton> items = results.getAnalyseList();

        List<MLSkeleton> resultsList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            resultsList.add(items.valueAt(i));
        }

        if (resultsList.size() <= 0) {
            return;
        }
        float similarity = 0.8f;

        SkeletonGraphic skeletonGraphic = new SkeletonGraphic(graphicOverlay, resultsList);
        graphicOverlay.addGraphic(skeletonGraphic);
        graphicOverlay.postInvalidate();

        float result = analyzer.caluteSimilarity(resultsList, templateList);

        if (result >= similarity) {
            if (zeroCount > 0) {
                return;
            } else {
                zeroCount = 0;
            }
            zeroCount++;
        } else {
            zeroCount = 0;
            return;
        }

        lensEngine.photograph(null, new LensEngine.PhotographListener() {
            @Override
            public void takenPhotograph(byte[] bytes) {
                SkeletonUtils.takePictureListener.picture(bytes);
                if (activity != null) {
                    activity.finish();
                }
            }
        });
    }

    @Override
    public void destroy() {
        Log.e(TAG, "detect fail");
    }
}
