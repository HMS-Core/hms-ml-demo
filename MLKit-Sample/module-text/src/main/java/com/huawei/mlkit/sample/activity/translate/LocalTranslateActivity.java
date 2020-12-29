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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.ml.language.common.utils.LanguageCodeUtil;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory;
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetector;
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting;
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadListener;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy;
import com.huawei.hms.mlsdk.model.download.MLRemoteModel;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslateSetting;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslator;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslatorModel;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.activity.adapter.TranslateDownloadAdapter;
import com.huawei.mlkit.sample.activity.adapter.TranslateSpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


public class LocalTranslateActivity extends BaseActivity {
    private static final String TAG = "LocalTranslateActivity";

    private static final ArrayList<String> LANG_CODE_LIST = new ArrayList<>(Arrays.asList(
            "ZH", "ZH-HK", "EN", "ES", "DE", "RU", "FR", "IT", "PT", "TH", "AR", "TR", "JA",
            "DA", "PL", "FI", "KO", "SV", "VI", "MS", "NO", "ID", "CS", "HE", "EL", "HI", "TL",
            "SR", "RO","TA","HU","NL","FA","SK","ET","LV","KM"));

    private static final ArrayList<String> SOURCE_LANGUAGE_CODE = new ArrayList<>();
    private static final ArrayList<String> DEST_LANGUAGE_CODE = new ArrayList<>();

    private static final ArrayList<String> SP_SOURCE_LIST = new ArrayList<>();
    private static final ArrayList<String> SP_SOURCE_LIST_EN = new ArrayList<>();

    private static final List<String> LANGUAGE_LIST_EN = new ArrayList<>(Arrays.asList(
            "Chinese (Simplified)", "Chinese (Traditional)", "English", "Spanish", "German", "Russian", "French", "Italian",
            "Portuguese", "Thai", "Arabic", "Turkish", "Japanese", "Danish", "Polish",
            "Finnish", "Korean", "Swedish", "Vietnamese", "Malaysian", "Norwegian",
            "Indonesian", "Czech", "Hebrew", " Greek", "Hindi", "Filipino", "Serbian",
            "Romanian","Tamil","Hungarian","Netherlands","Persian","Slovak","Estonian",
            "Latvian","Khmer"));

    private static final List<String> LANGUAGE_LIST_ZH = new ArrayList<>(Arrays.asList(
            "中文简体", "中文繁体", "英文", "西班牙语", "德语", "俄语", "法语", "意大利", "葡萄牙", "泰语",
            "阿拉伯", "土耳其", "日语", "丹麦语", "波兰语", "芬兰语", "韩语", "瑞典语", "越南语",
            "马来西亚语", "挪威语", "印尼语", "捷克语", "希伯来语", "希腊语", "印地语",
            "菲律宾语", "塞尔维亚语", "罗马尼亚语","泰米尔语","匈牙利语","荷兰语","波斯语",
            "斯洛伐克语","爱沙尼亚语","拉脱维亚语","高棉语"));

    private static final List<String> SP_DEST_LIST = new ArrayList<>();
    private static final List<String> SP_DEST_LIST_EN = new ArrayList<>();

    private static final List<String> DOWNLOAD_LANG_LIST = new ArrayList<>();
    private static final List<String> DOWNLOAD_LANG_LIST_EN = new ArrayList<>();

    private static final List<String> DEMO_LIST = new ArrayList<>(Arrays.asList("Machine learning", "机器学习", "機器學習", "Machine learning",
            "Aprendizaje de la máquina", "Maschinelles Lernen", "Машинное обучение", "Apprentissage mécanique",
            "Apprendimento delle macchine", "aprendizagem por máquina", "การเรียนรู้ของเครื่อง", "التعلم الآلي",
            "makine öğrenimi", "機械学習", " ", "Uczenie się maszyn", "Koneoppiminen", "기계 학습", "Inlärning av maskiner", "Máy học",
            "Pembelajaran mesin", "Maskinlæring", "Belajar mesin", "Strojové učení", "למידה של מכונה", "Μηχανική μάθηση", "मशीन लर्निंग",
            "Pag-aaral ng makina", "mašinsko učenje", "învăţarea maşinilor","இயந்திரம் கற்றல்","gépi tanulás","leren van machines",
            "یادگیری ماشین","strojové vzdelávanie","masina õppimine","mašīnu mācīšanās","ការរៀនម៉ាស៊ីន"));


    private static final List<String> DOWNLOAD_CODE_LIST = new ArrayList<>(Arrays.asList(
            "zh", "es", "de", "ru", "fr", "it", "pt", "th", "ar", "tr",
            "ja", "da", "pl", "fi", "ko", "sv", "vi", "ms", "no", "id",
            "cs", "he", "el", "hi", "tl", "sr", "ro","ta","hu","nl","fa",
            "sk","et","lv","km"));

    private final static long M = 1024 * 1024;

    private final static int LEFT = 1;
    private final static int RIGHT = 2;
    private final static int AUTO = 3;

    private final static int NETWORL_NONE = 1;
    private final static int NETWORK_WIFI = 2;
    private final static int NETWORK_MOBILE = 3;

    private Spinner spSourceType;
    private Spinner spDestType;
    private EditText etInputString;
    private TextView tvOutputString;
    private Button btrTranslator;
    private Button btrIdentification;
    private Button btnDownloadMang;

    private ImageButton btrSwitchLang;
    private TextView tvTime;

    private String srcLanguage = "Auto";
    private String dstLanguage = "EN";
    public static final String EN = "en";
    public static final String API_KEY = "client/api_key";

    private View.OnClickListener listener;

    private ArrayAdapter<String> spSourceAdapter;
    private ArrayAdapter<String> spDestAdapter;
    private MLLocalModelManager manager;

    private Dialog modelManageDialog;
    private static TranslateDownloadAdapter downloadAdapter;
    private static ArrayList<String> downloadModels = new ArrayList<>();
    private static HashMap<String, String> downloadMap = new HashMap<>();

    private String bestResult;

    private CallBcak callBcak;

    public abstract static class CallBcak {
        /**
         * download select language
         *
         * @param language  language
         */
        public abstract void download(String language);

        /**
         * delete language
         *
         * @param language language
         */
        public abstract void delete(String language);
    }

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

        DOWNLOAD_LANG_LIST.addAll(LANGUAGE_LIST_ZH);
        DOWNLOAD_LANG_LIST.remove(1);
        DOWNLOAD_LANG_LIST.remove(1);
        DOWNLOAD_LANG_LIST.set(0, "中文");
        DOWNLOAD_LANG_LIST_EN.addAll(LANGUAGE_LIST_EN);
        DOWNLOAD_LANG_LIST_EN.remove(1);
        DOWNLOAD_LANG_LIST_EN.remove(1);
        DOWNLOAD_LANG_LIST_EN.set(0, "Chinese");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_local_translate);
        this.createComponent();
        this.createSpinner();
        this.bindEventListener();
        this.manager = MLLocalModelManager.getInstance();
        setApiKey();

        if (isEngLanguage()) {
            downloadAdapter = new TranslateDownloadAdapter(LocalTranslateActivity.this,
                    (ArrayList) DOWNLOAD_LANG_LIST_EN, (ArrayList) DOWNLOAD_CODE_LIST, downloadModels,
                    downloadMap, callBcak);
        } else {
            downloadAdapter = new TranslateDownloadAdapter(LocalTranslateActivity.this,
                    (ArrayList) DOWNLOAD_LANG_LIST, (ArrayList) DOWNLOAD_CODE_LIST, downloadModels,
                    downloadMap, callBcak);
        }

        manager.getModels(MLLocalTranslatorModel.class).addOnSuccessListener(new OnSuccessListener<Set<MLLocalTranslatorModel>>() {
            @Override
            public void onSuccess(Set<MLLocalTranslatorModel> mlLocalTranslatorModels) {

                downloadModels.clear();
                for (MLRemoteModel m : mlLocalTranslatorModels) {
                    String modelName = m.getModelName().substring(m.getModelName().indexOf("_") + 1);
                    if (!downloadModels.contains(modelName)) {
                        downloadModels.add(modelName);
                    }
                }

                if (downloadAdapter != null) {
                    downloadAdapter.notifyDataSetChanged();
                }

                if (spSourceAdapter != null) {
                    spSourceAdapter.notifyDataSetChanged();
                }
                if (spDestAdapter != null) {
                    spDestAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setApiKey() {
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getApplication());
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }


    private void createSpinner() {
        if (this.isEngLanguage()) {
            this.spSourceAdapter = new TranslateSpinnerAdapter(this,
                    SOURCE_LANGUAGE_CODE, downloadModels,
                    android.R.layout.simple_spinner_dropdown_item, LocalTranslateActivity.SP_SOURCE_LIST_EN);
            this.spDestAdapter = new TranslateSpinnerAdapter(this,
                    DEST_LANGUAGE_CODE, downloadModels,
                    android.R.layout.simple_spinner_dropdown_item, LocalTranslateActivity.SP_DEST_LIST_EN);
        } else {
            this.spSourceAdapter = new TranslateSpinnerAdapter(this,
                    SOURCE_LANGUAGE_CODE, downloadModels,
                    android.R.layout.simple_spinner_dropdown_item, LocalTranslateActivity.SP_SOURCE_LIST);
            this.spDestAdapter = new TranslateSpinnerAdapter(this,
                    DEST_LANGUAGE_CODE, downloadModels,
                    android.R.layout.simple_spinner_dropdown_item, LocalTranslateActivity.SP_DEST_LIST);
        }

        this.spSourceAdapter.setDropDownViewResource(R.layout.translate_spinner_drop_item);
        this.spSourceType.setAdapter(this.spSourceAdapter);

        this.spDestAdapter.setDropDownViewResource(R.layout.translate_spinner_drop_item);
        this.spDestType.setAdapter(this.spDestAdapter);

        this.spSourceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocalTranslateActivity.this.srcLanguage = LocalTranslateActivity.SOURCE_LANGUAGE_CODE.get(position);
                Log.i(LocalTranslateActivity.TAG, "srcLanguage: " + LocalTranslateActivity.this.srcLanguage);
                if (SOURCE_LANGUAGE_CODE.size() > position) {
                    String langCode = SOURCE_LANGUAGE_CODE.get(position);
                    if (!"AUTO".equalsIgnoreCase(langCode) && !"En".equalsIgnoreCase(langCode)) {
                        if ((LanguageCodeUtil.ZHHK.equalsIgnoreCase(langCode))) {
                            if (!downloadModels.contains(LanguageCodeUtil.ZH.toLowerCase(Locale.ENGLISH))){
                                showToast(getString(R.string.download_prompt));
                            }
                        }else{
                            if (!downloadModels.contains(langCode.toLowerCase())) {
                                showToast(getString(R.string.download_prompt));
                            }
                        }
                    }
                }

                if (position > 0) {
                    LocalTranslateActivity.this.etInputString.setText(DEMO_LIST.get(position));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        this.spDestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocalTranslateActivity.this.dstLanguage = LocalTranslateActivity.DEST_LANGUAGE_CODE.get(position);
                Log.i(LocalTranslateActivity.TAG, "dstLanguage: " + LocalTranslateActivity.this.dstLanguage);
                if (DEST_LANGUAGE_CODE.size() > position) {
                    String langCode = DEST_LANGUAGE_CODE.get(position);

                    if (!"AUTO".equalsIgnoreCase(langCode) && !"En".equalsIgnoreCase(langCode)) {
                        if ((LanguageCodeUtil.ZHHK.equalsIgnoreCase(langCode))) {
                            if (!downloadModels.contains(LanguageCodeUtil.ZH.toLowerCase(Locale.ENGLISH))){
                                showToast(getString(R.string.download_prompt));
                            }
                        }else{
                            if (!downloadModels.contains(langCode.toLowerCase())) {
                                showToast(getString(R.string.download_prompt));
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spSourceType.post(new Runnable() {
            @Override
            public void run() {
                spSourceType.setDropDownWidth(spSourceType.getWidth());
            }
        });

        spDestType.post(new Runnable() {
            @Override
            public void run() {
                spDestType.setDropDownWidth(spDestType.getWidth());
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
        if (code.equalsIgnoreCase(LocalTranslateActivity.SOURCE_LANGUAGE_CODE.get(0)) || code.equalsIgnoreCase(LocalTranslateActivity.SP_SOURCE_LIST.get(0))) {
            this.dstLanguage = LocalTranslateActivity.DEST_LANGUAGE_CODE.get(0);
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
        this.btnDownloadMang = this.findViewById(R.id.downloadModeManagement);
        this.tvTime = this.findViewById(R.id.tv_time);
        this.spSourceType = this.findViewById(R.id.spSourceType);
        this.spDestType = this.findViewById(R.id.spDestType);
        this.btrSwitchLang = this.findViewById(R.id.buttonSwitchLang);

        callBcak = new CallBcak() {
            @Override
            public void download(String language) {
                int net = checkNetwork();
                switch (net) {
                    case NETWORK_WIFI:
                        downloadModel(language);
                        break;
                    case NETWORK_MOBILE:
                        new AlertDialog.Builder(LocalTranslateActivity.this).setTitle(R.string.prompt)
                                .setMessage(R.string.no_wifi_prompt)
                                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        downloadModel(language);
                                    }
                                }).setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                        break;
                    default:
                        new AlertDialog.Builder(LocalTranslateActivity.this).setTitle(R.string.prompt)
                                .setMessage(R.string.no_network_prompt)
                                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                }
            }

            @Override
            public void delete(String language) {
                deleteModel(language);
            }
        };
    }

    private void bindEventListener() {

        this.listener = new MyListener();
        this.btrTranslator.setOnClickListener(this.listener);
        this.btrIdentification.setOnClickListener(this.listener);
        this.btrSwitchLang.setOnClickListener(this.listener);
        this.btnDownloadMang.setOnClickListener(this.listener);
        this.findViewById(R.id.back).setOnClickListener(this.listener);
        this.findViewById(R.id.delete_text).setOnClickListener(this.listener);
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
            }
        });
    }

    private int checkNetwork() {

        ConnectivityManager connectMgr = (ConnectivityManager) LocalTranslateActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyMgr = (TelephonyManager) LocalTranslateActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
                return NETWORL_NONE;
            }

            int networkType = networkInfo.getType();
            if (networkType == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_WIFI;
            }

            if (networkType == ConnectivityManager.TYPE_MOBILE) {
                return NETWORK_MOBILE;
            }
        } catch (Exception e) {
            return NETWORL_NONE;
        }

        // Others: no network by default
        return NETWORL_NONE;
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
                case R.id.downloadModeManagement:
                    showModelManagementDialog();
                    break;
                case R.id.delete_text:
                    etInputString.setText("");
                    break;
                default:
                    break;
            }
        }
    }

    private void showModelManagementDialog() {
        if (downloadAdapter == null) {
            showToast(getString(R.string.can_not_get_download_mode));
            return;
        }
        if (modelManageDialog == null) {
            modelManageDialog = new Dialog(this, R.style.translateModelManageDialogStyle);
            modelManageDialog.setContentView(R.layout.dialog_translate_model_manage);
            modelManageDialog.setCanceledOnTouchOutside(true);

            ListView listView = modelManageDialog.findViewById(R.id.listView);
            listView.setAdapter(downloadAdapter);
        }
        if (!modelManageDialog.isShowing()) {
            modelManageDialog.show();
            WindowManager.LayoutParams params = modelManageDialog.getWindow().getAttributes();
            params.width = getResources().getDimensionPixelOffset(R.dimen.dialog_width);
            params.height = getResources().getDimensionPixelOffset(R.dimen.dialog_height);
            params.x = btnDownloadMang.getLeft();
            params.y = btnDownloadMang.getTop() + btnDownloadMang.getHeight();
            params.dimAmount = 0.5f;
            modelManageDialog.getWindow().setAttributes(params);
            modelManageDialog.getWindow().setGravity(Gravity.TOP | Gravity.START);
        }
    }

    private void updateTime(long time) {
        this.tvTime.setText(time + " ms");
    }


    private void downloadModel(final String languageCode) {
        Log.e(TAG, "downloadModel languageCode: " + languageCode);
        MLLocalTranslatorModel model = new MLLocalTranslatorModel.Factory(languageCode).create();
        downloadMap.put(languageCode, getString(R.string.waitting));
        if (btnDownloadMang != null && downloadAdapter != null) {
            btnDownloadMang.post(new Runnable() {
                @Override
                public void run() {
                    downloadAdapter.notifyDataSetChanged();
                }
            });
        }

        MLModelDownloadListener modelDownloadListener = new MLModelDownloadListener() {
            @Override
            public void onProcess(long alreadyDownLength, long totalLength) {
                showProcess(languageCode, alreadyDownLength, totalLength);
            }
        };
        MLModelDownloadStrategy request = new MLModelDownloadStrategy.Factory()
                .create();

        manager.downloadModel(model, request, modelDownloadListener).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                downloadModels.add(languageCode);
                downloadAdapter.notifyDataSetChanged();
                spSourceAdapter.notifyDataSetChanged();
                spDestAdapter.notifyDataSetChanged();
                showToast("DownloadModel Success");
                downloadMap.remove(languageCode);
                if (btnDownloadMang != null && downloadAdapter != null) {
                    btnDownloadMang.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "downloadModel failed: " + e.getMessage());
                showToast("DownloadModel Failed");
                downloadMap.remove(languageCode);
                if (btnDownloadMang != null && downloadAdapter != null) {
                    btnDownloadMang.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void showProcess(String languageCode, long alreadyDownLength, long totalLength) {
        double downDone = alreadyDownLength * 1.0 / M;
        double downTotal = totalLength * 1.0 / M;
        String downD = String.format(Locale.ROOT,"%.2f", downDone);
        String downT = String.format(Locale.ROOT,"%.2f", downTotal);

        String text = downD + "M" + "/" + downT + "M";
        Log.e(TAG, "stringformat:" + downD);
        downloadMap.put(languageCode, text);
        if (downloadAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    private void deleteModel(final String languageCode) {
        Log.i(TAG, "deleteModel languageCode: " + languageCode);
        MLLocalTranslatorModel model = new MLLocalTranslatorModel.Factory(languageCode).create();
        manager.deleteModel(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                downloadModels.remove(languageCode);
                downloadAdapter.notifyDataSetChanged();
                spSourceAdapter.notifyDataSetChanged();
                spDestAdapter.notifyDataSetChanged();
                showToast("DeleteModel Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "deleteModel failed: " + e.getMessage());
                showToast("DeleteModel Failed");
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
                    Log.e(TAG, e.getMessage());
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
                showToast(e.getMessage());
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
                showToast(e.getMessage());
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
        for (int i = 0; i < LocalTranslateActivity.SOURCE_LANGUAGE_CODE.size(); i++) {
            if (code.equalsIgnoreCase(LocalTranslateActivity.SOURCE_LANGUAGE_CODE.get(i))) {
                index = i;
                break;
            }
        }
        return this.spSourceAdapter.getItem(index);
    }

    private String getEnLanguageName(String code) {
        int index;
        for (int i = 0; i < LocalTranslateActivity.LANG_CODE_LIST.size(); i++) {
            if (code.equalsIgnoreCase(LocalTranslateActivity.LANG_CODE_LIST.get(i))) {
                index = i;
                return LocalTranslateActivity.LANGUAGE_LIST_EN.get(index);
            }
        }
        return code;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
