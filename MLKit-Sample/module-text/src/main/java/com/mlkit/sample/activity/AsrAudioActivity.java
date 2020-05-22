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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.mlkit.sample.R;

public class AsrAudioActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AsrActivity";
    private static final int ML_ASR_CAPTURE_CODE = 2;

    private static final String LANGUAGE_EN = "en-US";
    private static final String LANGUAGE_ZH = "zh";

    private LinearLayout mLlExampleRecord;
    private RelativeLayout mRlResultRecord;

    private TextView mTvRecordResult;
    private RelativeLayout rl_language;
    private String text = "";
    private String mLanguage = LANGUAGE_ZH;
    private TextView tvLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr);
        initUI();
        createLanguageDialog();
    }

    private void initUI() {
        findViewById(R.id.icon_record).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        rl_language=findViewById(R.id.rl_language);
        rl_language.setOnClickListener(this);
        mLlExampleRecord = findViewById(R.id.ll_example);
        mRlResultRecord = findViewById(R.id.rl_result_record);
        TextView mTvRecord = findViewById(R.id.tv_record);
        mTvRecordResult = findViewById(R.id.tv_record_result);
        tvLanguage = findViewById(R.id.languagetext);
        mTvRecord.setText(R.string.start);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.icon_record) {
            Intent intent = new Intent(this, MLAsrCaptureActivity.class)
                    .putExtra(MLAsrCaptureConstants.LANGUAGE, mLanguage)
                    .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
            startActivityForResult(intent, ML_ASR_CAPTURE_CODE);
            overridePendingTransition(R.anim.mlkit_asr_popup_slide_show, 0);
        }else if(view.getId() == R.id.back){
            finish();
        } else if(view.getId() == R.id.rl_language) {
            showLanguageDialog();
        }else if(view.getId() == R.id.simple_cn) {
            tvLanguage.setText(R.string.chinese);
            mLanguage = LANGUAGE_ZH;
            this.languageDialog.dismiss();
        }else if(view.getId() == R.id.english) {
            tvLanguage.setText(R.string.english_choose);
            mLanguage = LANGUAGE_EN;
            this.languageDialog.dismiss();
        }else if(view.getId() == R.id.close){
            changeUi(View.VISIBLE, View.INVISIBLE);
        }
    }

    private void showLanguageDialog() {
        initLanguageDialogViews();
        languageDialog.show();
    }

    private void initLanguageDialogViews() {
        this.asrTextCN.setSelected(false);
        this.asrTextEN.setSelected(false);
        switch (mLanguage) {
            case LANGUAGE_ZH:
                this.asrTextCN.setSelected(true);
                break;
            case LANGUAGE_EN:
                this.asrTextEN.setSelected(true);
                break;
            default:
                break;
        }
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
                        Integer subErrorCode;
                        String message = "";

                        Bundle bundle = data.getExtras();
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                            message = message + errorCode;
                        }
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                            message = message + subErrorCode;
                        }

                        Toast toast = Toast.makeText(AsrAudioActivity.this, "ERROR:" + message, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    break;
                default:
                    Toast toast = Toast.makeText(AsrAudioActivity.this, "ERROR...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mLlExampleRecord != null && mLlExampleRecord.getVisibility() == View.INVISIBLE) {
            changeUi(View.VISIBLE, View.INVISIBLE);
        } else {
            finish();
        }
    }

    private void changeUi(int visible, int gone) {
        mLlExampleRecord.setVisibility(visible);
        mRlResultRecord.setVisibility(gone);
    }

    private Dialog languageDialog;
    private TextView asrTextCN;
    private TextView asrTextEN;

    private void createLanguageDialog() {
        this.languageDialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_language, null);
        // Set up a custom layout
        this.languageDialog.setContentView(view);
        this.asrTextCN = view.findViewById(R.id.simple_cn);
        this.asrTextCN.setOnClickListener(this);
        this.asrTextEN = view.findViewById(R.id.english);
        this.asrTextEN.setOnClickListener(this);
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
}
