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
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints;
import com.huawei.mlkit.sample.views.GameGraphic;

public class HandKeypointTransactor implements MLAnalyzer.MLTransactor<MLHandKeypoints> {
    private GameGraphic gameGraphic;

    public HandKeypointTransactor(GameGraphic gameGraphic) {
        this.gameGraphic = gameGraphic;
    }

    @Override
    public void transactResult(MLAnalyzer.Result<MLHandKeypoints> results) {
        SparseArray<MLHandKeypoints> analyseList = results.getAnalyseList();
        if (analyseList == null || analyseList.size() <= 0) {
            return;
        }
        int centerY = analyseList.get(0).getRect().centerY();
        gameGraphic.setOffset(centerY);
        gameGraphic.invalidate();
    }

    @Override
    public void destroy() {

    }
}
