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
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LabelUtils {
    private static final String TAG = "CustModelActivity";

    private static final int PRINT_LENGTH = 10;

    public static ArrayList<String> readLabels(Context context, String assetFileName) {
        ArrayList<String> result = new ArrayList<>();
        InputStream is = null;
        try {
            is = context.getAssets().open(assetFileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8")); // Construct a BufferedReader class to read files.
            String readString;
            while ((readString = br.readLine()) != null) {
                result.add(readString);
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
        return result;
    }

    public static Map processResult(List<String> labelList, float[] probabilities) {
        Map<String, Float> localResult = new HashMap<>();
        ValueComparator compare = new ValueComparator(localResult);
        for (int i = 0; i < probabilities.length; i++) {
            localResult.put(labelList.get(i), probabilities[i]);
        }
        TreeMap<String, Float> result = new TreeMap<>(compare);
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
}
