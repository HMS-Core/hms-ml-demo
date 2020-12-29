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

package com.huawei.mlkit.sample.activity.asr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;

import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.dialog.RecordDialog;
import com.huawei.mlkit.sample.manager.SpeechRecognizerManager;

import java.util.ArrayList;

public class AsrAudioActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AsrActivity";
    private static final int ML_ASR_CAPTURE_CODE = 2;
    private static final int MSG_VOLUME_CHANGED = 1001;

    private static final String LAN_EN_US = "en-US";
    private static final String LAN_ZH_CN = "zh-CN";
    private static final String LAN_FR_FR = "fr-FR";
    private static final String LAN_DE_DE = "de-DE";
    private static final String LAN_ES_ES = "es-ES";
    private static final String LAN_IT_IT = "it-IT";


    private static final int TYPE_ASR_PLUGIN = 1;
    private static final int TYPE_CUSTOM = 2;

    private LinearLayout mLlExampleRecord;
    private RelativeLayout mRlResultRecord;

    private SpeechRecognizerManager mSpeechManager;

    private TextView mTvRecordResult;
    private RelativeLayout rl_language;
    private RelativeLayout rl_type;
    private String text = "";
    private String mLanguage = LAN_ZH_CN;
    private int mType = TYPE_ASR_PLUGIN;
    private TextView tvLanguage;
    private TextView tvType;
    private RecordDialog mRecordDialog;
    private boolean mIsRecording = false;

    private OnResultListener listener = new OnResultListener();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VOLUME_CHANGED:
                    mRecordDialog.updateVolumeLevel(getRandomVolume());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr);
        initUI();
        createLanguageDialog();
        createTypeDialog();
    }

    private void initUI() {
        findViewById(R.id.icon_record).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        rl_language = findViewById(R.id.rl_language);
        rl_language.setOnClickListener(this);
        rl_type = findViewById(R.id.rl_type);
        rl_type.setOnClickListener(this);
        mLlExampleRecord = findViewById(R.id.ll_example);
        mRlResultRecord = findViewById(R.id.rl_result_record);
        mTvRecordResult = findViewById(R.id.tv_record_result);
        tvLanguage = findViewById(R.id.languagetext);
        tvType = findViewById(R.id.typetext);
        mRecordDialog = new RecordDialog(this, RecordDialog.TYPE_WITHOUT_COMPLETE_BUTTON);
        mRecordDialog.setOnBackPressedListener(new RecordDialog.OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                if(mSpeechManager != null){
                    mSpeechManager.destroy();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.icon_record) {
            if (mType == TYPE_ASR_PLUGIN) {
                startRecodingOnPlugin();
            } else {
                startRecodingOnCustom();
            }
        } else if (view.getId() == R.id.back) {
            finish();
        } else if (view.getId() == R.id.rl_language) {
            showLanguageDialog();
        } else if (view.getId() == R.id.rl_type) {
            showTypeDialog();
        } else if (view.getId() == R.id.simple_cn) {
            tvLanguage.setText(R.string.chinese);
            mLanguage = LAN_ZH_CN;
            this.languageDialog.dismiss();
        } else if (view.getId() == R.id.english) {
            tvLanguage.setText(R.string.english_choose);
            mLanguage = LAN_EN_US;
            this.languageDialog.dismiss();
        } else if (view.getId() == R.id.french) {
            tvLanguage.setText(R.string.french);
            mLanguage = LAN_FR_FR;
            this.languageDialog.dismiss();
        }else if (view.getId() == R.id.spanish) {
            tvLanguage.setText(R.string.spanish);
            mLanguage = LAN_ES_ES;
            this.languageDialog.dismiss();
        } else if (view.getId() == R.id.German) {
            tvLanguage.setText(R.string.German);
            mLanguage = LAN_DE_DE;
            this.languageDialog.dismiss();
        }else if (view.getId() == R.id.Italian) {
            tvLanguage.setText(R.string.Italian);
            mLanguage = LAN_IT_IT;
            this.languageDialog.dismiss();
        }else if (view.getId() == R.id.type_asr_plugin) {
            tvType.setText(R.string.asr_sound_pickup_interface_type_plugin);
            mType = TYPE_ASR_PLUGIN;
            this.typeDialog.dismiss();
        } else if (view.getId() == R.id.type_custom) {
            tvType.setText(R.string.asr_sound_pickup_interface_type_custom);
            mType = TYPE_CUSTOM;
            this.typeDialog.dismiss();
        } else if (view.getId() == R.id.close) {
            changeUi(View.VISIBLE, View.INVISIBLE);
        }
    }

    private void startRecodingOnPlugin() {
        Intent intent = new Intent(this, MLAsrCaptureActivity.class)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, mLanguage)
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
            // Set the usage scenario to shopping,Currently, only Chinese scenarios are supported.
            // .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING);
        startActivityForResult(intent, ML_ASR_CAPTURE_CODE);
        overridePendingTransition(R.anim.mlkit_asr_popup_slide_show, 0);
    }

    private void startRecodingOnCustom() {
        if (mSpeechManager == null) {
            startListening();
        } else{
            mSpeechManager.destroy();
            startListening();
        }

        if (mRecordDialog != null) {
            mRecordDialog.show();
        }
        updateVolume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ML_ASR_CAPTURE_CODE) {
            switch (resultCode) {
                case MLAsrCaptureConstants.ASR_SUCCESS:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                            text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                        }
                        if (text != null && !"".equals(text)) {
                            changeUi(View.INVISIBLE, View.VISIBLE);
                            mTvRecordResult.setText(text);
                        }
                    }
                    break;
                case MLAsrCaptureConstants.ASR_FAILURE:
                    if (data != null) {
                        int errorCode;
                        Bundle bundle = data.getExtras();
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                            showFailedDialog(getPrompt(errorCode));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void startListening() {
        mSpeechManager = new SpeechRecognizerManager(this, mLanguage, listener);
        mSpeechManager.startListening();
    }

    private int getPrompt(int errorCode) {
        switch (errorCode) {
            case MLAsrConstants.ERR_NO_NETWORK:
                return R.string.error_no_network;
            case MLAsrConstants.ERR_NO_UNDERSTAND:
                return R.string.error_no_understand;
            case MLAsrConstants.ERR_SERVICE_UNAVAILABLE:
                return R.string.error_service_unavailable;
            default:
                return errorCode;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void changeUi(int visible, int gone) {
        mLlExampleRecord.setVisibility(visible);
        mRlResultRecord.setVisibility(gone);
    }

    private Dialog languageDialog;
    private TextView asrTextCN;
    private TextView asrTextEN;
    private TextView asrTextFR;
    private TextView asrTextES;
    private TextView asrTextDE;
    private TextView asrTextIT;

    private void showLanguageDialog() {
        initLanguageDialogViews();
        languageDialog.show();
    }

    private void initLanguageDialogViews() {
        this.asrTextCN.setSelected(false);
        this.asrTextEN.setSelected(false);
        this.asrTextFR.setSelected(false);
        this.asrTextES.setSelected(false);
        this.asrTextDE.setSelected(false);
        this.asrTextIT.setSelected(false);
        switch (mLanguage) {
            case LAN_ZH_CN:
                this.asrTextCN.setSelected(true);
                break;
            case LAN_EN_US:
                this.asrTextEN.setSelected(true);
                break;
            case LAN_FR_FR:
                this.asrTextFR.setSelected(true);
                break;
            case LAN_ES_ES:
                this.asrTextES.setSelected(true);
                break;
            case LAN_DE_DE:
                this.asrTextDE.setSelected(true);
                break;
            case LAN_IT_IT:
                this.asrTextIT.setSelected(true);
                break;
            default:
                break;
        }
    }

    private void createLanguageDialog() {
        this.languageDialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_asr_language, null);
        // Set up a custom layout
        this.languageDialog.setContentView(view);
        this.asrTextCN = view.findViewById(R.id.simple_cn);
        this.asrTextCN.setOnClickListener(this);
        this.asrTextEN = view.findViewById(R.id.english);
        this.asrTextEN.setOnClickListener(this);
        this.asrTextFR = view.findViewById(R.id.french);
        this.asrTextFR.setOnClickListener(this);
        this.asrTextES = view.findViewById(R.id.spanish);
        this.asrTextES.setOnClickListener(this);
        this.asrTextDE = view.findViewById(R.id.German);
        this.asrTextDE.setOnClickListener(this);
        this.asrTextIT = view.findViewById(R.id.Italian);
        this.asrTextIT.setOnClickListener(this);
        this.languageDialog.setCanceledOnTouchOutside(true);
        // Set the size of the dialog
        Window dialogWindow = this.languageDialog.getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(layoutParams);
        }
    }

    private Dialog typeDialog;
    private TextView typePlugin;
    private TextView typeCustom;

    private void showTypeDialog() {
        initTypeDialogViews();
        typeDialog.show();
    }

    private void initTypeDialogViews() {
        this.typePlugin.setSelected(false);
        this.typeCustom.setSelected(false);
        switch (mType) {
            case TYPE_ASR_PLUGIN:
                this.typePlugin.setSelected(true);
                break;
            case TYPE_CUSTOM:
                this.typeCustom.setSelected(true);
                break;
            default:
                break;
        }
    }

    private void createTypeDialog() {
        this.typeDialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_asr_type, null);
        // Set up a custom layout
        this.typeDialog.setContentView(view);
        this.typePlugin = view.findViewById(R.id.type_asr_plugin);
        this.typePlugin.setOnClickListener(this);
        this.typeCustom = view.findViewById(R.id.type_custom);
        this.typeCustom.setOnClickListener(this);
        this.typeDialog.setCanceledOnTouchOutside(true);
        // Set the size of the dialog
        Window dialogWindow = this.typeDialog.getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(layoutParams);
        }
    }

    private void updateVolume() {
        mIsRecording = true;
        new Thread(mGetVolumeLevelRunnable).start();
    }

    private int volume = 0;

    private int getRandomVolume() {
        if (volume < 12) {
            volume += 1;
        } else {
            volume = 1;
        }
        return volume;
    }

    private Runnable mGetVolumeLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (mIsRecording) {
                try {
                    Thread.sleep(200);
                    mHandler.sendEmptyMessage(MSG_VOLUME_CHANGED);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    };

    private void dismissCustomDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsRecording = false;
                if (mRecordDialog != null) {
                    mRecordDialog.dismiss();
                }
            }
        });
    }

    private class OnResultListener implements SpeechRecognizerManager.OnResultsReady {
        @Override
        public void onResults(ArrayList<String> results) {
            if (results != null && results.size() > 0) {
                changeUi(View.INVISIBLE, View.VISIBLE);
                if (results.size() == 1) {
                    mTvRecordResult.setText(results.get(0));
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (results.size() > 5) {
                        results = (ArrayList<String>) results.subList(0, 5);
                    }
                    for (String result : results) {
                        sb.append(result).append("\n");
                    }
                    mTvRecordResult.setText(sb.toString());
                }
            }
        }

        @Override
        public void onError(int error) {
            dismissCustomDialog();
            showFailedDialog(getPrompt(error));
        }

        @Override
        public void onFinsh() {
            dismissCustomDialog();
        }
    }

    private void showFailedDialog(int res) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(res)
                .setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.button_background));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsRecording = false;
        if (mSpeechManager != null) {
            mSpeechManager.destroy();
        }
    }
}
