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

package com.huawei.mlkit.sample.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.huawei.hms.mlsdk.custom.MLModelDataType;
import com.huawei.hms.mlsdk.custom.MLModelOutputs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HebingModel extends ModelOperator {
    private static final int BITMAP_SIZE = 224;

    public HebingModel(Context context) {
        super(context);
        modelName = "model_hebing_3branch";
        modelFullName = modelName + ".ms";
        modelLabelFile = "";
        mBitmapSize = BITMAP_SIZE;
    }

    @Override
    protected int getInputType() {
        return MLModelDataType.FLOAT32;
    }

    @Override
    protected int getOutputType() {
        return MLModelDataType.FLOAT32;
    }

    @Override
    protected int[] getInputShape() {
        return new int[]{1, BITMAP_SIZE, BITMAP_SIZE, 3};
    }

    @Override
    protected Object getInput(Bitmap inputBitmap) {
        final float[][][][] input = new float[1][mBitmapSize][mBitmapSize][3];
        for (int i = 0; i < mBitmapSize; i++) {
            for (int j = 0; j < mBitmapSize; j++) {
                int pixel = inputBitmap.getPixel(i, j);
                input[batchNum][j][i][2] = ((Color.red(pixel) / 255f - 0.485f)) / 0.229f;
                input[batchNum][j][i][1] = ((Color.green(pixel) / 255f - 0.456f)) / 0.224f;
                input[batchNum][j][i][0] = ((Color.blue(pixel) / 255f - 0.406f)) / 0.225f;
            }
        }
        return input;
    }

    @Override
    protected ArrayList<int[]> getOutputShapeList() {
        ArrayList<int[]> outputShapeList = new ArrayList<>();
        int[] outputShape1 = new int[] {1, 410};
        int[] outputShape2 = new int[] {1, 113};
        int[] outputShape3 = new int[] {1, 1};
        outputShapeList.add(outputShape1);
        outputShapeList.add(outputShape2);
        outputShapeList.add(outputShape3);
        return outputShapeList;
    }

    @Override
    public String[][] resultPostProcess(MLModelOutputs output) {
        List<String> labelList = Arrays.asList(LabelConstant.LABEL_TABLE);
        float[][] result = output.getOutput(0); // index
        float[] probabilities = result[0];
        TreeMap<String, Float> sequenceResult = (TreeMap<String, Float>) processResult(labelList, probabilities);

        int total = 0;
        String[][] lastReuslt = new String[printLength][2];
        DecimalFormat df = new DecimalFormat("0.00%");
        for (Map.Entry<String, Float> entry : sequenceResult.entrySet()) {
            if (total == printLength || entry.getValue() <= 0) {
                break;
            }
            lastReuslt[total][0] = entry.getKey();
            lastReuslt[total][1] = df.format(entry.getValue());

            total++;
        }
        return lastReuslt;
    }
}
