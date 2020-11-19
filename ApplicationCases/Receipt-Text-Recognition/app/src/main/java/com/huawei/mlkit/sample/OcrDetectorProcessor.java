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
package com.huawei.mlkit.sample;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.text.MLText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class OcrDetectorProcessor implements MLAnalyzer.MLTransactor<MLText.Block> {
    private Activity activity;
    private Pattern pattern;
    private Matcher match;
    private String title;
    private int regexpnumber;

    public OcrDetectorProcessor(Activity activity, String regexp, String title, int regexpnumber) {
        this.activity = activity;
        pattern = Pattern.compile(regexp);
        this.title = title;
        this.regexpnumber = regexpnumber;
    }

    @Override
    public void transactResult(MLAnalyzer.Result<MLText.Block> results) {
        SparseArray<MLText.Block> items = results.getAnalyseList();
        for (int i = 0; i < items.size(); i++) {
            String str = items.get(i).getStringValue().replace(" ", "");
            String result = str;
            if (title == null) {
                continue;
            }
            if (!result.contains(title)) {
                continue;
            }
            int number = title.length();
            if (number > 0) {
                result = str.substring(number, number + regexpnumber);
            }

            match = pattern.matcher(result);
            if (match.matches()) {
                Intent intent = new Intent();
                intent.putExtra("result", result);
                activity.setResult(RESULT_OK, intent);
                activity.finish();
            }
        }
    }

    @Override
    public void destroy() {
        Log.e("TAG", "destroy");
    }
}
