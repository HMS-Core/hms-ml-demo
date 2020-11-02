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

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzer;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerFactory;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.transactor.HandKeypointTransactor;
import com.huawei.mlkit.sample.views.GameGraphic;

import java.io.IOException;
import java.util.List;

public class GameUtils {
    private static final String TAG = "FaceUtils";
    private static MLHandKeypointAnalyzer analyzer;
    private static LensEngine lensEngine;
    static int width = 0;
    static int height = 0;

    public static void createHandAnalyze() {
        analyzer = MLHandKeypointAnalyzerFactory.getInstance().getHandKeypointAnalyzer();
    }

    public static void setHandTransactor(GameGraphic gameGraphic) {
        analyzer.setTransactor(new HandKeypointTransactor(gameGraphic));
    }

    public static float getMagnification(Context context) {
        int magnification = 1;
        Camera camera = Camera.open(1);
        List<Camera.Size> supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        for (int i = supportedPreviewSizes.size() - 1; i >= 0; i--) {
            width = supportedPreviewSizes.get(i).width;
            height = supportedPreviewSizes.get(i).height;
            if (width >= 300 && height >= 300) {
                break;
            }
        }

        camera.release();
        magnification = supportedPreviewSizes.get(0).height / height;
        return magnification;
    }

    public static void initLensEngie(Context context) {
        lensEngine = new LensEngine.Creator(context, analyzer)
                .setLensType(LensEngine.FRONT_LENS)
                .applyDisplayDimension(width, height)
                .applyFps(30.0f)
                .enableAutomaticFocus(true)
                .create();
    }

    public static void startLensEngine(LensEnginePreview preview) {
        if (lensEngine != null) {
            try {
                preview.start(lensEngine);
            } catch (IOException e) {
                Log.e(TAG, "Failed to start lens engine.", e);
                lensEngine.release();
                lensEngine = null;
            }
        }
    }

    public static void stopPreview(LensEnginePreview mPreview) {
        if(lensEngine!=null){
            mPreview.stop();
        }
    }

    public static void releaseAnalyze() {
        if (lensEngine != null) {
            lensEngine.release();
        }
        if (analyzer != null) {
            analyzer.destroy();
        }
    }

    private static Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
}
