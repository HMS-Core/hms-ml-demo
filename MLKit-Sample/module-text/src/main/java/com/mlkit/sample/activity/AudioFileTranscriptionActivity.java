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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.hms.mlsdk.aft.MLAftErrors;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftEngine;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftListener;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftResult;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftSetting;
import com.mlkit.sample.R;
import com.mlkit.sample.activity.dialog.RecordDialog;
import com.mlkit.sample.record.AudioManager;
import com.mlkit.sample.record.RecordingListener;
import com.mlkit.sample.util.FileUtils;
import com.mlkit.sample.util.UriFileInfoUtils;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class AudioFileTranscriptionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ATFActivity";

    private static final String LANGUAGE_EN = "en-US";
    private static final String LANGUAGE_ZH = "zh";

    private LinearLayout mLlExampleRecord;
    private RelativeLayout mRlResultRecord;

    private TextView mTvRecordResult;
    private RelativeLayout rl_language;
    private String mLanguage = LANGUAGE_ZH;
    private TextView tvLanguage;
    private MLRemoteAftEngine mAnalyzer;

    private RecordDialog mRecordDialog;

    private AudioManager mAudioManager;

    private AlertDialog transferringDialog;

    private String mTaskId;

    private RecordingListener recordingStateListener = new RecordingListener() {
        @Override
        public void recordingReady() {
            Log.i(TAG, "recordingReady");
            mAudioManager.startRecording();
        }

        @Override
        public void onComplete(float durationTime, String filePath) {
            Log.i(TAG, "onComplete durationTime:" + durationTime + " filePath:" + filePath);
            File fileToTrans = new File(filePath);
            Uri uri = Uri.fromFile(fileToTrans);
            Log.i(TAG, "start asr in path " + uri.toString());
            mTaskId = startTransfer(uri);
            showConvertingDialog();
            FileUtils.deleteQuietly(fileToTrans);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_file_description);
        mAnalyzer = MLRemoteAftEngine.getInstance();
        mAnalyzer.init(getApplicationContext());
        mAnalyzer.setAftListener(mAsrListener);

        findViewById(R.id.icon_record).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        rl_language = findViewById(R.id.rl_language);
        rl_language.setOnClickListener(this);
        mLlExampleRecord = findViewById(R.id.ll_example);
        mRlResultRecord = findViewById(R.id.rl_result_record);
        mTvRecordResult = findViewById(R.id.tv_record_result);
        tvLanguage = findViewById(R.id.languagetext);
        findViewById(R.id.back).setOnClickListener(this);
        registerRecordingListener();
        createLanguageDialog();
    }

    private void registerRecordingListener() {
        mRecordDialog = new RecordDialog(this, RecordDialog.TYPE_WITH_COMPLETE_BUTTON);
        mRecordDialog.setOnCompleteListener(new RecordDialog.OnCompleteListener() {
            @Override
            public void onComplete() {
                mAudioManager.recordingComplete();
                mRecordDialog.dismiss();
            }
        });
        mRecordDialog.setOnBackPressedListener(new RecordDialog.OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                mAudioManager.release();
                mRecordDialog.dismiss();
            }
        });
        String dirPath = Environment.getExternalStorageDirectory() + "/audio_path";
        mAudioManager = new AudioManager(dirPath, mRecordDialog);
        mAudioManager.setOnRecordingStateListener(recordingStateListener);
    }

    public String startTransfer(Uri uri) {
        Log.i(TAG, "startAsr transfer " + uri.toString() + " " + uri.getAuthority() + " " + uri.getEncodedAuthority());
        String taskId;
        long timeDuration = UriFileInfoUtils.getDuration(getApplicationContext(), uri);
        Log.i(TAG, "timeDuration " + timeDuration);
        MLRemoteAftSetting setting =
                new MLRemoteAftSetting.Factory().enablePunctuation(true).enableTimeOffset(true).setLanguageCode(mLanguage).create();
        taskId = mAnalyzer.shortRecognize(uri, setting);
        return taskId;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.icon_record) {
            showDialogStartRecord();
        } else if (view.getId() == R.id.back) {
            finish();
        } else if (view.getId() == R.id.rl_language) {
            showLanguageDialog();
        } else if (view.getId() == R.id.simple_cn) {
            tvLanguage.setText(R.string.chinese);
            mLanguage = LANGUAGE_ZH;
            this.languageDialog.dismiss();
        } else if (view.getId() == R.id.english) {
            tvLanguage.setText(R.string.english_choose);
            mLanguage = LANGUAGE_EN;
            this.languageDialog.dismiss();
        } else if (view.getId() == R.id.close) {
            changeUi(View.VISIBLE, View.INVISIBLE);
        }
    }

    private void changeUi(int visible, int gone) {
        mLlExampleRecord.setVisibility(visible);
        mRlResultRecord.setVisibility(gone);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Dialog languageDialog;
    private TextView asrTextCN;
    private TextView asrTextEN;

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

    private void createLanguageDialog() {
        this.languageDialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_aft_language, null);
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

    private void showConvertingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.record_converting, null);
        builder.setView(dialogView);
        transferringDialog = builder.create();
        transferringDialog.show();
        Button cancelBtn = dialogView.findViewById(R.id.convert_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (transferringDialog != null) {
                    transferringDialog.dismiss();
                    mAnalyzer.destroyTask(mTaskId);
                }
            }
        });
    }

    private void dismissTransferringDialog() {
        if (transferringDialog != null) {
            Log.i(TAG, "remove waiting dialog");
            transferringDialog.dismiss();
        }
    }

    private void showTransferFailedDialog(int res) {
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

    private int getPrompt(int errorCode) {
        switch (errorCode) {
            case MLAftErrors.ERR_NETCONNECT_FAILED:
                return R.string.error_aft_network;
            case MLAftErrors.ERR_AUDIO_FILE_SIZE_OVERFLOW:
                return R.string.error_aft_audio_length_overflow;
            case MLAftErrors.ERR_ILLEGAL_PARAMETER:
                return R.string.error_aft_illegal_parameters;
            case MLAftErrors.ERR_INTERNAL:
                return R.string.error_aft_internal;
            case MLAftErrors.ERR_AUTHORIZE_FAILED:
                return R.string.error_aft_authorize_failed;
            default:
                return R.string.error_aft_audio_transcript_failed;
        }
    }

    private void showDialogStartRecord() {
        if (mRecordDialog != null) {
            mRecordDialog.show();
        }
        mAudioManager.prepareAudio();
    }


    private void showCovertResult(String content) {
        changeUi(View.INVISIBLE, View.VISIBLE);
        mTvRecordResult.setText(content);
    }

    private MLRemoteAftListener mAsrListener = new MLRemoteAftListener() {

        @Override
        public void onInitComplete(String taskId, Object ext) {
            Log.i(TAG, "MLRemoteAftListener onInitComplete" + taskId);
            mAnalyzer.startTask(taskId);
        }

        @Override
        public void onUploadProgress(String taskId, double progress, Object ext) {
            Log.i(TAG, " MLRemoteAftListener onUploadProgress is " + taskId + " " + progress);
        }

        @Override
        public void onEvent(String taskId, int eventId, Object ext) {
            Log.i(TAG, "MLRemoteAftListener onEvent " + taskId + " " + eventId);
        }

        @Override
        public void onResult(String taskId, MLRemoteAftResult result, Object ext) {
            Log.i(TAG, "onResult get " + taskId);
            if (result != null) {
                Log.i(TAG, "onResult isComplete " + result.isComplete());
                if (!result.isComplete()) {
                    return;
                }
                if (result.getText() != null) {
                    Log.e(TAG, result.getText());
                    dismissTransferringDialog();
                    showCovertResult(result.getText());
                }

                List<MLRemoteAftResult.Segment> segmentList = result.getSegments();
                if (segmentList == null) {
                    return;
                }
                for (MLRemoteAftResult.Segment segment : segmentList) {
                    Log.i(TAG, segment.getText());
                }
            }

        }

        @Override
        public void onError(String taskId, int errorCode, String message) {
            Log.i(TAG, "MLRemoteAftListener onError " + errorCode);
            if (errorCode != MLAftErrors.ERR_RESULT_WHEN_UPLOADING && errorCode != MLAftErrors.ERR_ENGINE_BUSY) {
                Log.i(TAG, "MLRemoteAftListener stop query result due to error " + errorCode);
                dismissTransferringDialog();
                showTransferFailedDialog(getPrompt(errorCode));
            }
        }
    };

}
