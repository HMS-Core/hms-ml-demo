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
import android.graphics.RectF;

import com.huawei.hms.mlsdk.custom.MLModelDataType;
import com.huawei.hms.mlsdk.custom.MLModelOutputs;
import com.huawei.mlkit.sample.entity.Recognition;

import java.util.ArrayList;

public class ObjectTfModel extends ModelOperator {

    private static final int BITMAP_SIZE = 300;

    private static final int NUM_DETECTIONS = 10;

    private static final float IMAGE_MEAN = 128.0f;

    private static final float IMAGE_STD = 128.0f;

    public ObjectTfModel(Context context) {
        super(context);

        modelName = "detect";
        modelFullName = modelName + ".ms";
        modelLabelFile = "labelmap.txt";
        mBitmapSize = BITMAP_SIZE;
        printLength = NUM_DETECTIONS;
    }

    @Override
    protected int getInputType() {
        return MLModelDataType.BYTE;
    }

    @Override
    protected int getOutputType() {
        return MLModelDataType.FLOAT32;
    }

    @Override
    protected Object getInput(Bitmap inputBitmap) {
        final byte[][][][] input = new byte[1][BITMAP_SIZE][BITMAP_SIZE][3];
        for (int i = 0; i < BITMAP_SIZE; i++) {
            for (int j = 0; j < BITMAP_SIZE; j++) {
                int pixel = inputBitmap.getPixel(i, j);
                input[batchNum][j][i][0] = (byte)Color.red(pixel);
                input[batchNum][j][i][1] = (byte)Color.red(pixel);
                input[batchNum][j][i][2] = (byte)Color.red(pixel);
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
        int[] outputShape1 = new int[] {1, NUM_DETECTIONS, 4};
        int[] outputShape2 = new int[] {1, NUM_DETECTIONS};
        int[] outputShape3 = new int[] {1, NUM_DETECTIONS};
        int[] outputShape4 = new int[] {1};
        outputShapeList.add(outputShape1);
        outputShapeList.add(outputShape2);
        outputShapeList.add(outputShape3);
        outputShapeList.add(outputShape4);
        return outputShapeList;
    }

    @Override
    public ArrayList<Recognition> resultPostProcess(MLModelOutputs output) {
        float[][][] outputLocations = output.getOutput(0);
        float[][] outputClasses = output.getOutput(1);
        float[][] outputScores = output.getOutput(2);
        float[] numDetections = output.getOutput(3);
        int numDetectionsOutput = Math.min(NUM_DETECTIONS, (int) numDetections[0]);
        final ArrayList<Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
        ArrayList<String> labelList = LabelUtils.readLabels(weakContext.get(), modelLabelFile);
        for (int i = 0; i < numDetectionsOutput; ++i) {
            final RectF detection =
                    new RectF(outputLocations[0][i][1] * mBitmapSize, outputLocations[0][i][0] * mBitmapSize,
                            outputLocations[0][i][3] * mBitmapSize, outputLocations[0][i][2] * mBitmapSize);
            // SSD Mobilenet V1 Model assumes class 0 is background class
            // in label file and class labels start from 1 to number_of_classes+1,
            // while outputClasses correspond to class index from 0 to number_of_classes
            int labelOffset = 1;
            recognitions.add(new Recognition("" + i, labelList.get((int) outputClasses[0][i] + labelOffset),
                    outputScores[0][i], detection));
        }
        // to be updated.
        return recognitions;
    }
}
