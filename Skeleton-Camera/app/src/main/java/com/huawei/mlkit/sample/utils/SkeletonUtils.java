/*
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

package com.huawei.mlkit.sample.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.SurfaceHolder;

import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.skeleton.MLJoint;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.huawei.mlkit.sample.transactor.SkeletonTransactor;
import com.huawei.mlkit.sample.transactor.TakePictureListener;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkeletonUtils {
    private static final String TAG = "SkeletonUtils";
    private static MLSkeletonAnalyzer analyzer = null;
    private static LensEngine lensEngine;
    private static List<MLSkeleton> templateList;
    private static Bitmap bitmap;
    public static TakePictureListener takePictureListener;

    public static void setTakePictureListener(TakePictureListener takePicture) {
        takePictureListener = takePicture;
    }

    public static void createMLSkeletonAnalyzer() {
        analyzer = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer();
    }


    public static void initLensEngie(Context context) {
        lensEngine = new LensEngine.Creator(context, analyzer)
                .setLensType(LensEngine.BACK_LENS)
                .applyDisplayDimension(1280, 720)
                .applyFps(20.0f)
                .enableAutomaticFocus(true)
                .create();
    }

    public static void setTransactor(GraphicOverlay overlay, Activity activity) {
        analyzer.setTransactor(new SkeletonTransactor(analyzer, overlay, lensEngine, activity));
    }

    public static void runLensEngine(SurfaceHolder holder) {
        try {
            lensEngine.run(holder);
        } catch (IOException e) {
            Log.e(TAG, "e=" + e.getMessage());
        }
    }

    public static void closeLensEngine() {
        if (lensEngine != null) {
            lensEngine.close();
        }
    }

    public static void releaseLensEngine() {
        if (lensEngine != null) {
            lensEngine.release();
        }
    }

    public static void releaseAnalyzer() {
        if (analyzer != null) {
            try {
                analyzer.stop();
            } catch (IOException e) {
                Log.e(TAG, "e=" + e.getMessage());
            }
        }
    }

    public static void setTemplateData(float[][] SKELETON_DATA) {
        List<MLJoint> mlJointList = new ArrayList<>();
        for (int i = 0; i < SKELETON_DATA.length; i++) {
            MLJoint mlJoint = new MLJoint(SKELETON_DATA[i][0], SKELETON_DATA[i][1], (int) SKELETON_DATA[i][2], SKELETON_DATA[i][3]);
            mlJointList.add(mlJoint);
        }

        templateList = new ArrayList<>();
        templateList.add(new MLSkeleton(mlJointList));
    }

    public static List<MLSkeleton> getTemplateData() {
        return templateList;
    }

    public static void setBitmap(Context context, int resId) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }
}
