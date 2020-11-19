/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.mlkit.sample.photoreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

public class TextRecognition {
    private static int TEXT_LENGTH = 4999;
    private static final int INIT_VALUE = 0;
    private Context mContext;
    private Bitmap mBitmap;
    private String mSourceText;
    private EditText mEdText;
    private MLTextAnalyzer textAnalyzer;

    public MLTextAnalyzer getTextAnalyzer() {
        return textAnalyzer;
    }

    public TextRecognition(Context context, Bitmap originBitmap, String sourceText, EditText mEd_text) {
        this.mContext = context;
        this.mBitmap = originBitmap;
        this.mSourceText = sourceText;
        this.mEdText = mEd_text;

        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .setLanguage("zh")
                .create();

        this.textAnalyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);
    }


    public void startTextAnalyzer() {
        if (this.isChosen(this.mBitmap)) {
            MLFrame mlFrame = new MLFrame.Creator().setBitmap(this.mBitmap).create();
            Task<MLText> task = this.textAnalyzer.asyncAnalyseFrame(mlFrame);
            task.addOnSuccessListener(new OnSuccessListener<MLText>() {

                @Override
                public void onSuccess(MLText mlText) {
                    if (mlText != null) {
                        mSourceText = mlText.getStringValue();
                        if (mSourceText.length() > TEXT_LENGTH) {
                            mEdText.setText(mSourceText.substring(INIT_VALUE, TEXT_LENGTH));
                        } else {
                            mEdText.setText(mSourceText);
                        }
                    } else {
                        displayFailure();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Transacting logic for segment failure.
                    displayFailure();
                    return;
                }
            });
        } else {
            Toast.makeText(mContext, R.string.please_select_picture, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private boolean isChosen(Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        } else {
            return true;
        }
    }

    private void displayFailure() {
        Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
    }
}
