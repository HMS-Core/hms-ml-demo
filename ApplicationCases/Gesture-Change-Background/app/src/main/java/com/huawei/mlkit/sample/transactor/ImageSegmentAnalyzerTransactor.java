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

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.SparseArray;

import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.mlkit.sample.views.overlay.CameraImageGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

public class ImageSegmentAnalyzerTransactor implements MLAnalyzer.MLTransactor<MLImageSegmentation> {
    private GraphicOverlay mOverlay;
    private boolean isFront = true;

    public ImageSegmentAnalyzerTransactor(GraphicOverlay mOverlay) {
        this.mOverlay = mOverlay;
    }

    @Override
    public void transactResult(MLAnalyzer.Result<MLImageSegmentation> result) {
        SparseArray<MLImageSegmentation> imageSegmentationResult = result.getAnalyseList();
        Bitmap bitmap = imageSegmentationResult.valueAt(0).getForeground();
        if (isFront) {
            bitmap = convert(bitmap);
        }
        mOverlay.clear();
        CameraImageGraphic cameraImageGraphic = new CameraImageGraphic(mOverlay, bitmap);
        mOverlay.addGraphic(cameraImageGraphic);
        mOverlay.postInvalidate();
    }

    @Override
    public void destroy() {

    }

    private Bitmap convert(Bitmap bitmap) {
        Matrix m = new Matrix();
        m.setScale(-1, 1);
        Bitmap reverseBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        return reverseBitmap;
    }

    public void setFront(boolean isFront) {
        this.isFront = isFront;
    }

}
