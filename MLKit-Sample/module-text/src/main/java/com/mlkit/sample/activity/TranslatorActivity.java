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

package com.mlkit.sample.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.langdetect.MLDetectedLang;
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory;
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetector;
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetectorSetting;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.mlkit.sample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class TranslatorActivity extends BaseActivity {
    private static final String TAG = "TranslatorActivity";
    private static final String[] SOURCE_LANGUAGE_CODE = new String[]{"Auto",
            "ZH", "EN", "FR", "TH", "JA", "DE", "RU", "ES",
            "AR", "TR", "PT", "IT"};
    private static final String[] DEST_LANGUAGE_CODE = new String[]{
            "ZH", "EN", "FR", "TH", "JA", "DE", "RU", "ES",
            "AR", "TR", "PT", "IT"};
    private static final List<String> SP_SOURCE_LIST = new ArrayList<>(Arrays.asList("自动检测",
            "中文", "英文", "法语", "泰语", "日语", "德语", "俄语", "西班牙语",
            "阿拉伯语", "土耳其语", "葡萄牙语", "意大利语"));
    private static final List<String> SP_SOURCE_LIST_EN = new ArrayList<>(Arrays.asList("Auto",
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian"));
    private static final List<String> SP_DEST_LIST = new ArrayList<>(Arrays.asList(
            "中文", "英文", "法语", "泰语", "日语", "德语", "俄语", "西班牙语",
            "阿拉伯语", "土耳其语", "葡萄牙语", "意大利语"));
    private static final List<String> SP_DEST_LIST_EN = new ArrayList<>(Arrays.asList(
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian"));
    private static final List<String> CODE_LIST = new ArrayList<>(Arrays.asList(
            "zh", "en", "fr", "th", "ja", "de", "ru", "es",
            "ar", "tr", "pt", "it", "ro"));
    private static final List<String> LANGUAGE_LIST = new ArrayList<>(Arrays.asList(
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian", "Romanian"));

    private Spinner spSourceType;
    private Spinner spDestType;
    private EditText etInputString;
    private TextView tvOutputString;
    private Button btrTranslator;
    private Button btrIdentification;
    private ImageButton btrSwitchLang;
    private TextView tvTime;
    private TextView tvInputLen;
    private TextView tvOutputLen;

    private String srcLanguage = "Auto";
    private String dstLanguage = "EN";
    public static final String EN = "en";

    private View.OnClickListener listener;

    private ArrayAdapter<String> spSourceAdapter;
    private ArrayAdapter<String> spDestAdapter;

    private MLRemoteTranslateSetting mlRemoteTranslateSetting;
    private MLRemoteTranslator mlRemoteTranslator;
    private MLRemoteLangDetectorSetting mlRemoteLangDetectorSetting;
    private MLRemoteLangDetector mlRemoteLangDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_translate);
        this.createComponent();
        this.createSpinner();
        this.bindEventListener();
    }

    private void createSpinner() {
        if (this.isEngLanguage()) {
            this.spSourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, TranslatorActivity.SP_SOURCE_LIST_EN);
            this.spDestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, TranslatorActivity.SP_DEST_LIST_EN);
        } else {
            this.spSourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, TranslatorActivity.SP_SOURCE_LIST);
            this.spDestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, TranslatorActivity.SP_DEST_LIST);
        }

        this.spSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spSourceType.setAdapter(this.spSourceAdapter);

        this.spDestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spDestType.setAdapter(this.spDestAdapter);

        this.spSourceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TranslatorActivity.this.srcLanguage = TranslatorActivity.SOURCE_LANGUAGE_CODE[position];
                Log.i(TranslatorActivity.TAG, "srcLanguage: " + TranslatorActivity.this.srcLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        this.spDestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TranslatorActivity.this.dstLanguage = TranslatorActivity.DEST_LANGUAGE_CODE[position];
                Log.i(TranslatorActivity.TAG, "dstLanguage: " + TranslatorActivity.this.dstLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateSourceLanguage(String code) {
        int count = this.spSourceAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (this.getLanguageName(code).equalsIgnoreCase(this.spSourceAdapter.getItem(i))) {
                this.spSourceType.setSelection(i, true);
                return;
            }
        }
        this.spSourceType.setSelection(0, true);
    }

    private void updateDestLanguage(String code) {
        if (code.equalsIgnoreCase(TranslatorActivity.SOURCE_LANGUAGE_CODE[0]) || code.equalsIgnoreCase(TranslatorActivity.SP_SOURCE_LIST.get(0))) {
            this.dstLanguage = TranslatorActivity.DEST_LANGUAGE_CODE[0];
            return;
        }
        int count = this.spDestAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (this.getLanguageName(code).equalsIgnoreCase(this.spDestAdapter.getItem(i))) {
                this.spDestType.setSelection(i, true);
                return;
            }
        }
        this.spDestType.setSelection(0, true);
    }

    private void createComponent() {
        this.etInputString = this.findViewById(R.id.et_input);
        this.tvOutputString = this.findViewById(R.id.tv_output);
        this.btrTranslator = this.findViewById(R.id.btn_translator);
        this.btrIdentification = this.findViewById(R.id.btn_identification);
        this.tvTime = this.findViewById(R.id.tv_time);
        this.tvInputLen = this.findViewById(R.id.tv_src_len);
        this.tvOutputLen = this.findViewById(R.id.tv_dst_len);
        this.spSourceType = this.findViewById(R.id.spSourceType);
        this.spDestType = this.findViewById(R.id.spDestType);
        this.btrSwitchLang = this.findViewById(R.id.buttonSwitchLang);
        this.updateLength(this.tvInputLen, this.etInputString.getText().length());
    }

    private void bindEventListener() {
        this.etInputString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                TranslatorActivity.this.updateLength(TranslatorActivity.this.tvInputLen, str.length());
                TranslatorActivity.this.autoUpdateSourceLanguage();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        this.listener = new MyListener();
        this.btrTranslator.setOnClickListener(this.listener);
        this.btrIdentification.setOnClickListener(this.listener);
        this.btrSwitchLang.setOnClickListener(this.listener);
        this.findViewById(R.id.back).setOnClickListener(this.listener);
    }

    public boolean isEngLanguage() {
        Locale locale = Locale.getDefault();
        if (locale != null) {
            String strLan = locale.getLanguage();
            return strLan != null && TranslatorActivity.EN.equals(strLan);
        }
        return false;
    }

    private void updateLength(TextView view, int length) {
        view.setText(String.format(Locale.ENGLISH, "%d words", length));
    }

    /**
     * Update output text content.
     *
     * @param text Source text.
     */
    private void updateOutputText(final String text) {
        if (text == null || text.isEmpty()) {
            Log.w(TranslatorActivity.TAG, "updateOutputText: text is empty");
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TranslatorActivity.this.tvOutputString.setText(text);
                TranslatorActivity.this.updateLength(TranslatorActivity.this.tvOutputLen, text.length());
            }
        });
    }

    private void updateInputText(final String text) {
        if (text == null || text.isEmpty()) {
            Log.w(TranslatorActivity.TAG, "updateInputText: text is empty");
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TranslatorActivity.this.etInputString.setText(text);
                TranslatorActivity.this.updateLength(TranslatorActivity.this.tvInputLen, text.length());
            }
        });
    }

    /**
     * Get the contents of the input text box.
     *
     * @return string
     */
    private String getInputText() {
        return this.etInputString.getText().toString();
    }

    private String getSourceType() {
        return this.srcLanguage;
    }

    private String getDestType() {
        return this.dstLanguage;
    }

    public class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    TranslatorActivity.this.finish();
                    break;
                case R.id.btn_translator:
                    TranslatorActivity.this.doTranslate();
                    break;
                case R.id.btn_identification:
                    TranslatorActivity.this.doLanguageRecognition();
                    break;
                case R.id.buttonSwitchLang:
                    TranslatorActivity.this.doLanguageSwitch();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateTime(long time) {
        this.tvTime.setText(time + " ms");
    }


    private void doLanguageSwitch() {
        String str = this.srcLanguage;
        this.srcLanguage = this.dstLanguage;
        this.dstLanguage = str;
        this.updateSourceLanguage(this.srcLanguage);
        this.updateDestLanguage(this.dstLanguage);
        String inputStr = this.tvOutputString.getText().toString();
        String outputStr = this.etInputString.getText().toString();
        this.updateInputText(inputStr);
        this.updateOutputText(outputStr);

    }

    private void doTranslate() {
        // Translating, get data, and update output boxes.
        String sourceText = this.getInputText();
        String sourceLang = this.getSourceType();
        String targetLang = this.getDestType();

        this.mlRemoteTranslateSetting = new MLRemoteTranslateSetting.Factory()
                .setSourceLangCode(sourceLang)
                .setTargetLangCode(targetLang)
                .create();
        this.mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(this.mlRemoteTranslateSetting);
        final long startTime = System.currentTimeMillis();
        Task<String> task = this.mlRemoteTranslator.asyncTranslate(sourceText);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                long endTime = System.currentTimeMillis();
                TranslatorActivity.this.updateOutputText(text);
                TranslatorActivity.this.updateTime(endTime - startTime);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                TranslatorActivity.this.updateOutputText(e.getMessage());
            }
        });

        this.autoUpdateSourceLanguage();
    }

    private void autoUpdateSourceLanguage() {
        this.mlRemoteLangDetectorSetting = new MLRemoteLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        this.mlRemoteLangDetector = MLLangDetectorFactory.getInstance().getRemoteLangDetector(this.mlRemoteLangDetectorSetting);
        Task<List<MLDetectedLang>> probabilityDetectTask = this.mlRemoteLangDetector.probabilityDetect(this.getInputText());
        probabilityDetectTask.addOnSuccessListener(new OnSuccessListener<List<MLDetectedLang>>() {
            @Override
            public void onSuccess(List<MLDetectedLang> result) {
                MLDetectedLang recognizedLang = result.get(0);
                String langCode = recognizedLang.getLangCode();
                TranslatorActivity.this.updateSourceLanguage(langCode);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private void doLanguageRecognition() {
        this.mlRemoteLangDetectorSetting = new MLRemoteLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        this.mlRemoteLangDetector = MLLangDetectorFactory.getInstance().getRemoteLangDetector(this.mlRemoteLangDetectorSetting);
        Task<List<MLDetectedLang>> probabilityDetectTask = this.mlRemoteLangDetector.probabilityDetect(this.getInputText());
        final long startTime = System.currentTimeMillis();
        probabilityDetectTask.addOnSuccessListener(new OnSuccessListener<List<MLDetectedLang>>() {
            @Override
            public void onSuccess(List<MLDetectedLang> result) {
                long endTime = System.currentTimeMillis();
                StringBuilder sb = new StringBuilder();
                for (MLDetectedLang recognizedLang : result) {
                    String langCode = recognizedLang.getLangCode();
                    float probability = recognizedLang.getProbability();
                    sb.append("Language=" + TranslatorActivity.this.getEnLanguageName(langCode) + "(" + langCode + "), score=" + probability);
                    sb.append(".");
                }
                TranslatorActivity.this.updateOutputText(sb.toString());
                TranslatorActivity.this.updateTime(endTime - startTime);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                TranslatorActivity.this.updateOutputText(e.getMessage());
            }
        });
        this.mlRemoteLangDetector.stop();
    }

    private String getLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < TranslatorActivity.SOURCE_LANGUAGE_CODE.length; i++) {
            if (code.equalsIgnoreCase(TranslatorActivity.SOURCE_LANGUAGE_CODE[i])) {
                index = i;
                break;
            }
        }
        return this.spSourceAdapter.getItem(index);
    }

    private String getEnLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < TranslatorActivity.CODE_LIST.size(); i++) {
            if (code.equalsIgnoreCase(TranslatorActivity.CODE_LIST.get(i))) {
                index = i;
                return TranslatorActivity.LANGUAGE_LIST.get(index);
            }
        }
        return code;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mlRemoteTranslator != null) {
            this.mlRemoteTranslator.stop();
        }
        if (this.mlRemoteLangDetector != null) {
            this.mlRemoteLangDetector.stop();
        }
    }
}
