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

package com.huawei.mlkit.sample.processor.gcr.homecard;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.mlkit.sample.activity.entity.BlockItem;
import com.huawei.mlkit.sample.activity.entity.GeneralCardResult;
import com.huawei.mlkit.sample.processor.gcr.GeneralCardProcessor;
import com.huawei.mlkit.sample.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Post processing plug-in of Mainland Travel Permit for Hong Kong、Macao and Taiwan residents
 *
 * @since 2020-03-12
 */
public class HomeCardProcessor implements GeneralCardProcessor {
    private static final String TAG = "HomeCardProcessor";

    private final MLText text;

    public HomeCardProcessor(MLText text) {
        this.text = text;
    }

    @Override
    public GeneralCardResult getResult() {
        List<MLText.Block> blocks = text.getBlocks();
        if (blocks.isEmpty()) {
            Log.i(TAG, "Result blocks is empty");
            return null;
        }

        ArrayList<BlockItem> homeOriginItems = homeGetOriginItems(blocks);

        String valid = "";
        String number = "";
        boolean numberFlag = false;
        boolean validFlag = false;

        for (BlockItem item : homeOriginItems) {
            String homeTempStr = item.text;

            if (!validFlag) {
                String homeResult = tryGetValidDate(homeTempStr);
                if (!homeResult.isEmpty()) {
                    valid = homeResult;
                    validFlag = true;
                }
            }

            if (!numberFlag) {
                String homeResult = tryGetCardNumber(homeTempStr);
                if (!homeResult.isEmpty()) {
                    number = homeResult;
                    numberFlag = true;
                }
            }
        }

        Log.i(TAG, "valid: " + valid);
        Log.i(TAG, "number: " + number);

        return new GeneralCardResult(valid, number);
    }

    private ArrayList<BlockItem> homeGetOriginItems(List<MLText.Block> blocks) {
        ArrayList<BlockItem> originItems = new ArrayList<>();

        for (MLText.Block block : blocks) {
            // Add in behavior units
            List<MLText.TextLine> lines = block.getContents();
            for (MLText.TextLine line : lines) {
                String homeText = line.getStringValue();
                homeText = StringUtils.filterString(homeText, "[^a-zA-Z0-9\\.\\-,<\\(\\)\\s]");
                Log.d(TAG, "text: " + homeText);
                Point[] points = line.getVertexes();
                Rect rect = new Rect(points[0].x, points[0].y, points[2].x, points[2].y);
                BlockItem item = new BlockItem(homeText, rect);
                originItems.add(item);
            }
        }
        return originItems;
    }

    private String tryGetValidDate(String originStr) {
        return StringUtils.getCorrectValidDate(originStr);
    }

    private String tryGetCardNumber(String originStr) {
        return StringUtils.getHomeCardNumber(originStr);
    }
}