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
import java.util.Map;
import java.util.TreeMap;

public class LabelTfModel extends ModelOperator {

    private static final int BITMAP_SIZE = 224;

    private static final float IMAGE_MEAN = 127.0f;

    private static final float IMAGE_STD = 128.0f;


    public LabelTfModel(Context context) {
        super(context);
        modelName = "mobilenet_v1_1_0_224_quant";
        modelFullName = modelName + ".ms";
        modelLabelFile = "labels_mobilenet_quant_v1_224.txt";
        mBitmapSize = BITMAP_SIZE;

        readLabels(modelLabelFile);
    }

    @Override
    protected int getInputType() {
        return MLModelDataType.BYTE;
    }

    @Override
    protected int getOutputType() {
        return MLModelDataType.BYTE;
    }

    @Override
    protected Object getInput(Bitmap inputBitmap) {
        final byte[][][][] input = new byte[1][BITMAP_SIZE][BITMAP_SIZE][3];
        for (int i = 0; i < BITMAP_SIZE; i++) {
            for (int j = 0; j < BITMAP_SIZE; j++) {
                int pixel = inputBitmap.getPixel(i, j);
                input[batchNum][i][j][0] = (byte) Color.red(pixel);
                input[batchNum][i][j][1] = (byte) Color.green(pixel);
                input[batchNum][i][j][2] = (byte) Color.blue(pixel);
            }
        }
        return input;
    }

    @Override
    protected int[] getInputShape() {
        return new int[] {1, BITMAP_SIZE, BITMAP_SIZE, 3};
    }

    @Override
    protected ArrayList<int[]> getOutputShapeList() {
        ArrayList<int[]> outputShapeList = new ArrayList<>();
        int[] outputShape = new int[] {1, 1001};
        outputShapeList.add(outputShape);
        return outputShapeList;
    }


    @Override
    public String[][] resultPostProcess(MLModelOutputs output) {
        byte[][] result = output.getOutput(0); // index
        byte[] probabilities = result[0];
        float[] pro =  new float[probabilities.length];
        for (int i = 0; i < pro.length; i++) {
            pro[i] = probabilities[i]/255f;
        }
        TreeMap<String, Float> sequenceResult = (TreeMap<String, Float>) processResult(mLabels, pro);


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
