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
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory;
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetector;
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting;
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslateSetting;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslator;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslatorModel;
import com.mlkit.sample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;


public class LocalTranslateActivity extends BaseActivity {
    private static final String TAG = "LocalTranslateActivity";
    private static final String[] SOURCE_LANGUAGE_CODE = new String[]{"Auto", "ZH", "EN"};
    private static final String[] DEST_LANGUAGE_CODE = new String[]{"ZH", "EN"};
    private static final List<String> SP_SOURCE_LIST = new ArrayList<>(Arrays.asList("自动检测", "中文", "英文"));
    private static final List<String> SP_SOURCE_LIST_EN = new ArrayList<>(Arrays.asList("Auto", "Chinese", "English"));
    private static final List<String> SP_DEST_LIST = new ArrayList<>(Arrays.asList("中文", "英文"));
    private static final List<String> SP_DEST_LIST_EN = new ArrayList<>(Arrays.asList("Chinese", "English"));
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
    private Button btnDownloadSrc;
    private Button btnDownloadDest;
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
    private MLLocalModelManager manager;

    private String bestResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_local_translate);
        this.createComponent();
        this.createSpinner();
        this.bindEventListener();
        this.manager = MLLocalModelManager.getInstance();
    }

    private void createSpinner() {
        if (this.isEngLanguage()) {
            this.spSourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, LocalTranslateActivity.SP_SOURCE_LIST_EN);
            this.spDestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, LocalTranslateActivity.SP_DEST_LIST_EN);
        } else {
            this.spSourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, LocalTranslateActivity.SP_SOURCE_LIST);
            this.spDestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, LocalTranslateActivity.SP_DEST_LIST);
        }

        this.spSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spSourceType.setAdapter(this.spSourceAdapter);

        this.spDestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spDestType.setAdapter(this.spDestAdapter);

        this.spSourceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocalTranslateActivity.this.srcLanguage = LocalTranslateActivity.SOURCE_LANGUAGE_CODE[position];
                Log.i(LocalTranslateActivity.TAG, "srcLanguage: " + LocalTranslateActivity.this.srcLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        this.spDestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocalTranslateActivity.this.dstLanguage = LocalTranslateActivity.DEST_LANGUAGE_CODE[position];
                Log.i(LocalTranslateActivity.TAG, "dstLanguage: " + LocalTranslateActivity.this.dstLanguage);
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
        if (code.equalsIgnoreCase(LocalTranslateActivity.SOURCE_LANGUAGE_CODE[0]) || code.equalsIgnoreCase(LocalTranslateActivity.SP_SOURCE_LIST.get(0))) {
            this.dstLanguage = LocalTranslateActivity.DEST_LANGUAGE_CODE[0];
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
        this.btnDownloadSrc = this.findViewById(R.id.downloadSource);
        this.btnDownloadDest = this.findViewById(R.id.downloadDest);
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
                LocalTranslateActivity.this.updateLength(LocalTranslateActivity.this.tvInputLen, str.length());
                // todo
               // LocalTranslateActivity.this.autoUpdateSourceLanguage();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        this.listener = new MyListener();
        this.btrTranslator.setOnClickListener(this.listener);
        this.btrIdentification.setOnClickListener(this.listener);
        this.btrSwitchLang.setOnClickListener(this.listener);
        this.btnDownloadSrc.setOnClickListener(this.listener);
        this.btnDownloadDest.setOnClickListener(this.listener);
        this.findViewById(R.id.back).setOnClickListener(this.listener);
    }

    public boolean isEngLanguage() {
        Locale locale = Locale.getDefault();
        if (locale != null) {
            String strLan = locale.getLanguage();
            return strLan != null && LocalTranslateActivity.EN.equals(strLan);
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
            Log.w(LocalTranslateActivity.TAG, "updateOutputText: text is empty");
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalTranslateActivity.this.tvOutputString.setText(text);
                LocalTranslateActivity.this.updateLength(LocalTranslateActivity.this.tvOutputLen, text.length());
            }
        });
    }

    private void updateInputText(final String text) {
        if (text == null || text.isEmpty()) {
            Log.w(LocalTranslateActivity.TAG, "updateInputText: text is empty");
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalTranslateActivity.this.etInputString.setText(text);
                LocalTranslateActivity.this.updateLength(LocalTranslateActivity.this.tvInputLen, text.length());
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
                    LocalTranslateActivity.this.finish();
                    break;
                case R.id.btn_translator:
                    LocalTranslateActivity.this.translate();
                    break;
                case R.id.btn_identification:
                    LocalTranslateActivity.this.firstLangDetect();
                    break;
                case R.id.buttonSwitchLang:
                    LocalTranslateActivity.this.doLanguageSwitch();
                    break;
                case R.id.downloadSource:
                    downloadModel(getSourceType());
                    break;
                case R.id.downloadDest:
                    downloadModel(getDestType());
                    break;
                default:
                    break;
            }
        }
    }

    private void updateTime(long time) {
        this.tvTime.setText(time + " ms");
    }


    private void downloadModel(final String languageCode) {
        MLLocalTranslatorModel model = new MLLocalTranslatorModel.Factory(languageCode).create();
        MLModelDownloadStrategy request = new MLModelDownloadStrategy.Factory()
                .needWifi()
                .create();

        manager.downloadModel(model, request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showToast("DownloadModel Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "downloadModel failed: " + e.getMessage());
                showToast("DownloadModel Failed");
            }
        });
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

    private void translate() {
        String sourceText = this.getInputText();
        String sourceLanguage = this.getSourceType();
        String targetLanguage = this.getDestType();

        final CountDownLatch latch = new CountDownLatch(1);
        // Auto detect language
        if (sourceLanguage.equalsIgnoreCase("AUTO")) {
            detectLanguage(sourceText, latch);
        } else {
            latch.countDown();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String sourceLang = sourceLanguage;
                if (bestResult != null) {
                    sourceLang = bestResult.toUpperCase(Locale.ENGLISH);
                    bestResult = null;
                }

                // Create local translator
                MLLocalTranslateSetting setting = new MLLocalTranslateSetting.Factory()
                        .setSourceLangCode(sourceLang)
                        .setTargetLangCode(targetLanguage)
                        .create();
                final MLLocalTranslator translator = MLTranslatorFactory.getInstance().getLocalTranslator(setting);
                translateImpl(translator, sourceText);
            }
        }).start();
    }

    private void detectLanguage(final String input, final CountDownLatch latch) {
        // Create local detector
        MLLangDetectorFactory factory = MLLangDetectorFactory.getInstance();
        MLLocalLangDetectorSetting setting = new MLLocalLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        final MLLocalLangDetector localLangDetector = factory.getLocalLangDetector(setting);

        localLangDetector.firstBestDetect(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "detectLanguage: " + s);
                latch.countDown();
                bestResult = s;
                updateSourceLanguage(s);
                localLangDetector.stop();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "LocalLangDetector firstBestDetect failed: " + e.getMessage());
            }
        });
    }
    private void translateImpl(final MLLocalTranslator translator, String input) {
        final long startTime = System.currentTimeMillis();
        translator.asyncTranslate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                long endTime = System.currentTimeMillis();
                updateTime(endTime - startTime);
                updateOutputText(s);
                translator.stop();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Translate failed: " + e.getMessage());
                showToast("Translate Failed");
            }
        });
    }

    private void firstLangDetect() {
        String input = getInputText();
        MLLangDetectorFactory factory = MLLangDetectorFactory.getInstance();
        MLLocalLangDetectorSetting setting = new MLLocalLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        final MLLocalLangDetector localLangDetector = factory.getLocalLangDetector(setting);
        bestLangDetectImpl(localLangDetector, input);
    }

    private void bestLangDetectImpl(final MLLocalLangDetector detector, String input) {
        final long startTime = System.currentTimeMillis();
        detector.firstBestDetect(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "BestLangDetect success: " + s);
                String result = "Language=" + getEnLanguageName(s);
                long endTime = System.currentTimeMillis();
                updateTime(endTime - startTime);
                updateOutputText(result);
                detector.stop();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "BestLangDetect failed: " + e.getMessage());
                showToast("BestLangDetect Failed");
            }
        });
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

    private String getLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < LocalTranslateActivity.SOURCE_LANGUAGE_CODE.length; i++) {
            if (code.equalsIgnoreCase(LocalTranslateActivity.SOURCE_LANGUAGE_CODE[i])) {
                index = i;
                break;
            }
        }
        return this.spSourceAdapter.getItem(index);
    }

    private String getEnLanguageName(String code) {
        int index;
        for (int i = 0; i < LocalTranslateActivity.CODE_LIST.size(); i++) {
            if (code.equalsIgnoreCase(LocalTranslateActivity.CODE_LIST.get(i))) {
                index = i;
                return LocalTranslateActivity.LANGUAGE_LIST.get(index);
            }
        }
        return code;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
