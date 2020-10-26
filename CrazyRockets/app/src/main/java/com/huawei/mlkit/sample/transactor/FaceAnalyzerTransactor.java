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

package com.huawei.mlkit.sample.transactor;

import android.util.SparseArray;

import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceKeyPoint;
import com.huawei.mlkit.sample.views.GameGraphic;

import static com.huawei.hms.mlsdk.face.MLFaceKeyPoint.TYPE_TIP_OF_NOSE;

public class FaceAnalyzerTransactor implements MLAnalyzer.MLTransactor<MLFace> {
    private GameGraphic gameGraphic;

    public FaceAnalyzerTransactor(GameGraphic gameGraphic) {
        this.gameGraphic = gameGraphic;
    }

    @Override
    public void transactResult(MLAnalyzer.Result<MLFace> results) {
        SparseArray<MLFace> items = results.getAnalyseList();
        if (items.size() <= 0) {
            return;
        }
        MLFaceKeyPoint centerY = items.get(0).getFaceKeyPoint(TYPE_TIP_OF_NOSE);
        gameGraphic.setOffset(centerY.getCoordinatePoint().y);
        gameGraphic.invalidate();

    }

    @Override
    public void destroy() {

    }
}
