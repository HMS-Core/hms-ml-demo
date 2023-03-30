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
import android.util.Log;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ModelOperator
 *
 * @since 2020-12-10
 */
public abstract class ModelOperator implements ResultProcessor {

    private static final String TAG = "ModelOperator";

    private static final int BITMAP_SIZE = 224;

    protected String modelName;

    protected String modelFullName;

    protected String modelLabelFile;

    protected int mBitmapSize = BITMAP_SIZE;

    protected int batchNum = 0;

    /**
     * getInputType
     *
     * @return Input type
     */
    protected abstract int getInputType();

    /**
     * getInputType
     *
     * @return Output type
     */
    protected abstract int getOutputType();

    /**
     * getOutputType
     *
     * @param bmp Bitmap
     * @return return Bitmap
     */

    protected abstract Object getInput(Bitmap bmp);

    /**
     * getInput
     *
     * @return return
     */
    protected abstract int[] getInputShape();

    /**
     * getInputShape
     *
     * @return list
     */

    protected abstract ArrayList<int[]> getOutputShapeList();

    protected WeakReference<Context> weakContext;

    protected ArrayList<String> mLabels = new ArrayList<>();

    public int printLength = 3;

    public void setPrintLength(int printLength) {
        this.printLength = printLength;
    }

    public ModelOperator(Context context) {
        this.weakContext = new WeakReference<>(context);
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelFullName() {
        return modelFullName;
    }

    public String getModelLabelFile() {
        return modelLabelFile;
    }

    public int getmBitmapSize() {
        return mBitmapSize;
    }

    protected Bitmap processBitMap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, mBitmapSize, mBitmapSize, false);
    }

    private boolean dumpBitmapInfo(Bitmap bitmap) {
        if (bitmap == null) {
            return true;
        }
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        Log.e(TAG, "bitmap width is " + width + " height " + height);
        return false;
    }

    public Object bitmapTransInput(Bitmap bitmap) {
        return getInput(processBitMap(bitmap));
    }

    protected void readLabels(String assetFileName) {
        InputStream is = null;
        try {
            is = weakContext.get().getAssets().open(assetFileName);
            // Construct a BufferedReader class to read files.
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String readString;
            while ((readString = br.readLine()) != null) {
                mLabels.add(readString);
            }
            br.close();
        } catch (IOException error) {
            Log.e(TAG, "Asset file doesn't exist: " + error.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException error) {
                    Log.e(TAG, "close failed: " + error.getMessage());
                }
            }
        }
    }

    public static Map processResult(List<String> labelList, float[] probabilities) {
        Log.e("===>", labelList.size() + ":" + probabilities.length);
        Map<String, Float> localResult = new HashMap<>();
        ValueComparator compare = new ValueComparator(localResult);
        for (int i = 0; i < probabilities.length; i++) {
            localResult.put(labelList.get(i), probabilities[i]);
        }
        TreeMap<String, Float> result = new TreeMap<>(compare);
        result.putAll(localResult);
        return result;
    }


    public static Map processResult(List<String> labelList, byte[] probabilities) {
        Log.e("===>", labelList.size() + ":" + probabilities.length);
        Map<String, Byte> localResult = new HashMap<>();
        ValueByteComparator compare = new ValueByteComparator(localResult);
        for (int i = 0; i < probabilities.length; i++) {
            localResult.put(labelList.get(i), probabilities[i]);
        }
        TreeMap<String, Byte> result = new TreeMap<>(compare);
        result.putAll(localResult);
        return result;
    }

    private static class ValueComparator implements Comparator<String> {
        Map<String, Float> base;

        ValueComparator(Map<String, Float> base) {
            this.base = base;
        }

        @Override
        public int compare(String o1, String o2) {
            if (base.get(o1) >= base.get(o2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private static class ValueByteComparator implements Comparator<String> {
        Map<String, Byte> base;

        ValueByteComparator(Map<String, Byte> base) {
            this.base = base;
        }

        @Override
        public int compare(String o1, String o2) {
            if (base.get(o1) >= base.get(o2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
