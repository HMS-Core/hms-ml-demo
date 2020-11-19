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
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;

import static android.content.ContentValues.TAG;

public class TextTranslation {

    private MLRemoteTranslator translator;
    private String dstLanguage;
    private String srcLanguage;
    private String translateText = "";

    private EditText mEdText;
    private TranslationCallback callback;

    public void setCallback(TranslationCallback callback) {
        this.callback = callback;
    }

    public TextTranslation(String dstLanguage, String srcLanguage, EditText mEd_text) {
        this.dstLanguage = dstLanguage;
        this.srcLanguage = srcLanguage;
        this.mEdText = mEd_text;
    }

    public void createRemoteTranslator() {
        MLRemoteTranslateSetting setting = new MLRemoteTranslateSetting
                .Factory()
                .setSourceLangCode(srcLanguage)
                .setTargetLangCode(dstLanguage)
                .create();
        this.translator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
        String mText = mEdText.getText().toString().trim();
        Task<String> task = translator.asyncTranslate(mText);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                if (text != null) {
                    translateText = text;
                    if (callback != null) {
                        callback.onSuccess(translateText);
                    }
                } else {
                    if (callback != null) {
                        callback.onError();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (callback != null) {
                    callback.onError();
                }
            }
        });
    }

    public interface TranslationCallback {
        void onSuccess(String translateText);

        void onError();
    }
}
