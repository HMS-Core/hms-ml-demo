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

package com.huawei.mlkit.sample.activity.translate;

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
import android.widget.Toast;

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
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.activity.adapter.TranslateSpinnerAdapter;


import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RemoteTranslateActivity extends BaseActivity {
    private static final String TAG = "RemoteTranslateActivity";

    private static final ArrayList<String> LANG_CODE_LIST = new ArrayList<>(Arrays.asList(
            "ZH", "ZH-HK", "EN", "FR", "TH", "JA", "DE", "RU", "ES",
            "AR", "TR", "PT", "IT","PL","MS","SV","FI","NO","DA","KO",
            "VI", "ID", "CS", "HE", "EL", "HI", "TL", "SR", "RO", "MY",
            "KM","NL","ET","FA","LV","SK","TA","HU","BG","HR"));

    private static final ArrayList<String> SOURCE_LANGUAGE_CODE = new ArrayList<>();
    private static final ArrayList<String> DEST_LANGUAGE_CODE = new ArrayList<>();

    private static final ArrayList<String> LANGUAGE_LIST_EN = new ArrayList<>(Arrays.asList(
            "Chinese (Simplified)", "Chinese (Traditional)", "English", "French", "Thai", "Japanese",
            "German", "Russian", "Spanish", "Arabic", "Turkish", "Portuguese", "Italian","Polish",
            "Malaysian","Swedish","Finnish","Norwegian","Danish","Korean","Vietnamese", "Indonesian",
            "Czech", "Hebrew", "Greece", "Hindi", "Filipino", "Serbian", "Romanian", "Myanmar",
            "Khmer","Netherlands","Estonian","Persian","Latvian","Slovak","Tamil","Hungarian","Bulgarian","Croatian"));
    private static final ArrayList<String> LANGUAGE_LIST_ZH = new ArrayList<>(Arrays.asList(
            "中文简体", "中文繁体", "英文", "法语", "泰语", "日语", "德语", "俄语", "西班牙语",
            "阿拉伯语", "土耳其语", "葡萄牙语", "意大利语","波兰语","马来西亚语","瑞典语","芬兰语",
            "挪威语","丹麦语","韩语","越南语", "印尼语", "捷克语", "希伯来语", "希腊语", "印地语",
            "菲律宾语", "塞尔维亚语", "罗马尼亚语", "缅甸语", "高棉语","荷兰语","爱沙尼亚语","波斯语",
            "拉脱维亚语","斯洛伐克语","泰米尔语","匈牙利语","保加利亚语","克罗地亚语"));

    private static final ArrayList<String> SP_SOURCE_LIST = new ArrayList<>();
    private static final ArrayList<String> SP_SOURCE_LIST_EN = new ArrayList<>();

    private static final ArrayList<String> SP_DEST_LIST = new ArrayList<>();
    private static final ArrayList<String> SP_DEST_LIST_EN = new ArrayList<>();



    private Spinner spSourceType;
    private Spinner spDestType;
    private EditText etInputString;
    private TextView tvOutputString;
    private Button btrTranslator;
    private Button btrIdentification;
    private ImageButton btrSwitchLang;
    private TextView tvTime;

    private String srcLanguage = "Auto";
    private String dstLanguage = "EN";
    public static final String EN = "en";

    private View.OnClickListener listener;
    private TextWatcher textWatcher;
    private ArrayAdapter<String> spSourceAdapter;
    private ArrayAdapter<String> spDestAdapter;

    private MLRemoteTranslateSetting mlRemoteTranslateSetting;
    private MLRemoteTranslator mlRemoteTranslator;
    private MLRemoteLangDetectorSetting mlRemoteLangDetectorSetting;
    private MLRemoteLangDetector mlRemoteLangDetector;

    static {
        SOURCE_LANGUAGE_CODE.add("Auto");
        SOURCE_LANGUAGE_CODE.addAll(LANG_CODE_LIST);

        DEST_LANGUAGE_CODE.addAll(LANG_CODE_LIST);

        SP_SOURCE_LIST.add("自动检测");
        SP_SOURCE_LIST.addAll(LANGUAGE_LIST_ZH);
        SP_SOURCE_LIST_EN.add("Auto");
        SP_SOURCE_LIST_EN.addAll(LANGUAGE_LIST_EN);

        SP_DEST_LIST.addAll(LANGUAGE_LIST_ZH);
        SP_DEST_LIST_EN.addAll(LANGUAGE_LIST_EN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_remote_translate);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                RemoteTranslateActivity.this.autoUpdateSourceLanguage();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        this.createComponent();
        this.createSpinner();
        this.bindEventListener();
    }

    private void createSpinner() {
        if (this.isEngLanguage()) {
            this.spSourceAdapter = new TranslateSpinnerAdapter(this,
                    SOURCE_LANGUAGE_CODE, LANG_CODE_LIST,
                    android.R.layout.simple_spinner_dropdown_item, RemoteTranslateActivity.SP_SOURCE_LIST_EN);
            this.spDestAdapter = new TranslateSpinnerAdapter(this,
                    DEST_LANGUAGE_CODE, LANG_CODE_LIST,
                    android.R.layout.simple_spinner_dropdown_item, RemoteTranslateActivity.SP_DEST_LIST_EN);
        } else {
            this.spSourceAdapter = new TranslateSpinnerAdapter(this,
                    SOURCE_LANGUAGE_CODE, LANG_CODE_LIST,
                    android.R.layout.simple_spinner_dropdown_item, RemoteTranslateActivity.SP_SOURCE_LIST);
            this.spDestAdapter = new TranslateSpinnerAdapter(this,
                    DEST_LANGUAGE_CODE, LANG_CODE_LIST,
                    android.R.layout.simple_spinner_dropdown_item, RemoteTranslateActivity.SP_DEST_LIST);
        }

        this.spSourceAdapter.setDropDownViewResource(R.layout.translate_spinner_drop_item);
        this.spSourceType.setAdapter(this.spSourceAdapter);

        this.spDestAdapter.setDropDownViewResource(R.layout.translate_spinner_drop_item);
        this.spDestType.setAdapter(this.spDestAdapter);

        this.spSourceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RemoteTranslateActivity.this.srcLanguage = RemoteTranslateActivity.SOURCE_LANGUAGE_CODE.get(position);
                Log.i(RemoteTranslateActivity.TAG, "srcLanguage: " + RemoteTranslateActivity.this.srcLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        this.spDestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RemoteTranslateActivity.this.dstLanguage = RemoteTranslateActivity.DEST_LANGUAGE_CODE.get(position);
                Log.i(RemoteTranslateActivity.TAG, "dstLanguage: " + RemoteTranslateActivity.this.dstLanguage);
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
        if (code.equalsIgnoreCase(RemoteTranslateActivity.SOURCE_LANGUAGE_CODE.get(0))
                || code.equalsIgnoreCase(RemoteTranslateActivity.SP_SOURCE_LIST.get(0))) {
            this.dstLanguage = RemoteTranslateActivity.DEST_LANGUAGE_CODE.get(0);
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
        this.spSourceType = this.findViewById(R.id.spSourceType);
        this.spDestType = this.findViewById(R.id.spDestType);
        this.btrSwitchLang = this.findViewById(R.id.buttonSwitchLang);
    }

    private void bindEventListener() {
        this.etInputString.addTextChangedListener(textWatcher);
        this.listener = new MyListener();
        this.btrTranslator.setOnClickListener(this.listener);
        this.btrIdentification.setOnClickListener(this.listener);
        this.btrSwitchLang.setOnClickListener(this.listener);
        this.findViewById(R.id.back).setOnClickListener(this.listener);
        this.findViewById(R.id.delete_text).setOnClickListener(this.listener);
    }


    public boolean isEngLanguage() {
        Locale locale = Locale.getDefault();
        if (locale != null) {
            String strLan = locale.getLanguage();
            return strLan != null && RemoteTranslateActivity.EN.equals(strLan);
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
            Log.w(RemoteTranslateActivity.TAG, "updateOutputText: text is empty");
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RemoteTranslateActivity.this.tvOutputString.setText(text);
            }
        });
    }

    private void updateInputText(final String text) {
        if (text == null || text.isEmpty()) {
            Log.w(RemoteTranslateActivity.TAG, "updateInputText: text is empty");
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RemoteTranslateActivity.this.etInputString.setText(text);
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
                    RemoteTranslateActivity.this.finish();
                    break;
                case R.id.btn_translator:
                    RemoteTranslateActivity.this.doTranslate();
                    break;
                case R.id.btn_identification:
                    RemoteTranslateActivity.this.doLanguageRecognition();
                    break;
                case R.id.buttonSwitchLang:
                    RemoteTranslateActivity.this.doLanguageSwitch();
                    break;
                case R.id.delete_text:
                    RemoteTranslateActivity.this.etInputString.setText("");
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
                RemoteTranslateActivity.this.updateOutputText(text);
                RemoteTranslateActivity.this.updateTime(endTime - startTime);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                RemoteTranslateActivity.this.updateOutputText(e.getMessage());
                showToast(e.getMessage());
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
                RemoteTranslateActivity.this.updateSourceLanguage(langCode);
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
                    sb.append("Language=" + RemoteTranslateActivity.this.getEnLanguageName(langCode) + "(" + langCode + "), score=" + probability);
                    sb.append(".");
                }
                RemoteTranslateActivity.this.updateOutputText(sb.toString());
                RemoteTranslateActivity.this.updateTime(endTime - startTime);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                RemoteTranslateActivity.this.updateOutputText(e.getMessage());
            }
        });
        this.mlRemoteLangDetector.stop();
    }

    private String getLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < RemoteTranslateActivity.SOURCE_LANGUAGE_CODE.size(); i++) {
            if (code.equalsIgnoreCase(RemoteTranslateActivity.SOURCE_LANGUAGE_CODE.get(i))) {
                index = i;
                break;
            }
        }
        return this.spSourceAdapter.getItem(index);
    }

    private String getEnLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < RemoteTranslateActivity.LANG_CODE_LIST.size(); i++) {
            if (code.equalsIgnoreCase(RemoteTranslateActivity.LANG_CODE_LIST.get(i))) {
                index = i;
                return RemoteTranslateActivity.LANGUAGE_LIST_EN.get(index);
            }
        }
        return code;

    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
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
