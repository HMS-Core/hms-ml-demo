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

package com.huawei.mlkit.sample.activity.aft;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.huawei.hms.mlsdk.aft.MLAftConstants;
import com.huawei.hms.mlsdk.aft.MLAftErrors;
import com.huawei.hms.mlsdk.aft.MLAftEvents;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftEngine;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftListener;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftResult;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftSetting;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.dialog.RecordDialog;
import com.huawei.mlkit.sample.record.AudioManager;
import com.huawei.mlkit.sample.record.RecordingListener;
import com.huawei.mlkit.sample.util.FileUtils;
import com.huawei.mlkit.sample.util.UriFileInfoUtils;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AudioFileTranscriptionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ATFActivity";
    private static final int REQUEST_CHOOSE_IMAGE = 2001;
    private static final String LANGUAGE_EN = "en-US";
    private static final String LANGUAGE_ZH = "zh";

    private static final int TYPE_AFT_SHORT = 1;
    private static final int TYPE_AFT_LONG = 2;

    private LinearLayout mLlExampleRecord;
    private RelativeLayout mRlResultRecord;
    private RelativeLayout rl_type;

    private TextView mTvRecordResult;
    private RelativeLayout rl_language;
    private String mLanguage = LANGUAGE_ZH;
    private TextView tvLanguage;
    private TextView tvType;
    private TextView tvRecord;
    private MLRemoteAftEngine mAnalyzer;

    private RecordDialog mRecordDialog;

    private AudioManager mAudioManager;

    private AlertDialog transferringDialog;

    private String mTaskId;
    private String mLongTaskId;

    private TimerTask mTimerTask;

    private int mType = TYPE_AFT_SHORT;

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
        tvType = findViewById(R.id.typetext);
        tvRecord = findViewById(R.id.tv_record);
        rl_type = findViewById(R.id.rl_type);
        rl_type.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        registerRecordingListener();
        createLanguageDialog();
        createTypeDialog();
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
        MLRemoteAftSetting setting = new MLRemoteAftSetting.Factory()
                .enablePunctuation(true)
                .enableWordTimeOffset(true)
                .enableSentenceTimeOffset(true)
                .setLanguageCode(mLanguage)
                .create();
        taskId = mAnalyzer.shortRecognize(uri, setting);
        return taskId;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.icon_record) {
            if (mType == TYPE_AFT_SHORT) {
                showDialogStartRecord();
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
            }
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
        }  else if (view.getId() == R.id.rl_type) {
            showTypeDialog();
        } else if (view.getId() == R.id.type_asr_plugin) {
            tvType.setText(getString(R.string.aft_short));
            mType = TYPE_AFT_SHORT;
            tvRecord.setText(getString(R.string.record));
            tvRecord.setTextSize(29);
            this.typeDialog.dismiss();
        } else if (view.getId() == R.id.type_custom) {
            tvType.setText(getString(R.string.aft_long));
            tvRecord.setText(getString(R.string.select_file));
            tvRecord.setTextSize(17);
            mType = TYPE_AFT_LONG;
            this.typeDialog.dismiss();
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
        if (null != mTimerTask) {
            mTimerTask.cancel();
        }
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
            case MLAftErrors.ERR_SERVICE_CREDIT:
                return R.string.no_balance;
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
            Log.e(TAG, "MLAsrCallBack onEvent" + eventId);
            if (MLAftEvents.UPLOADED_EVENT == eventId) { // The file is uploaded successfully.
                showConvertingDialog();
                startQueryResult(); // Obtain the translation result.
            }
        }

        @Override
        public void onResult(String taskId, MLRemoteAftResult result, Object ext) {
            Log.i(TAG, "onResult get " + taskId);
            if (result != null) {
                Log.i(TAG, "onResult isComplete " + result.isComplete());
                if (!result.isComplete()) {
                    return;
                }
                if (null != mTimerTask) {
                    mTimerTask.cancel();
                }
                if (result.getText() != null) {
                    Log.e(TAG, result.getText());
                    dismissTransferringDialog();
                    showCovertResult(result.getText());
                }

                List<MLRemoteAftResult.Segment> segmentList = result.getSegments();
                if (segmentList != null && segmentList.size() != 0) {
                    for (MLRemoteAftResult.Segment segment : segmentList) {
                        Log.e(TAG, "MLAsrCallBack segment  text is : " + segment.getText() + ", startTime is : " + segment.getStartTime() + ". endTime is : " + segment.getEndTime());
                    }
                }

                List<MLRemoteAftResult.Segment> words = result.getWords();
                if (words != null && words.size() != 0) {
                    for (MLRemoteAftResult.Segment word : words) {
                        Log.e(TAG, "MLAsrCallBack word  text is : " + word.getText() + ", startTime is : " + word.getStartTime() + ". endTime is : " + word.getEndTime());
                    }
                }

                List<MLRemoteAftResult.Segment> sentences = result.getSentences();
                if (sentences != null && sentences.size() != 0) {
                    for (MLRemoteAftResult.Segment sentence : sentences) {
                        Log.e(TAG, "MLAsrCallBack sentence  text is : " + sentence.getText() + ", startTime is : " + sentence.getStartTime() + ". endTime is : " + sentence.getEndTime());
                    }
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

    private void showTypeDialog() {
        initTypeDialogViews();
        typeDialog.show();
    }

    private Dialog typeDialog;
    private TextView typeShort;
    private TextView typeLong;

    private void initTypeDialogViews() {
        this.typeShort.setSelected(false);
        this.typeLong.setSelected(false);
        switch (mType) {
            case TYPE_AFT_SHORT:
                this.typeShort.setSelected(true);
                break;
            case TYPE_AFT_LONG:
                this.typeLong.setSelected(true);
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
        this.typeShort = view.findViewById(R.id.type_asr_plugin);
        typeShort.setText(getString(R.string.aft_short));
        this.typeShort.setOnClickListener(this);
        this.typeLong = view.findViewById(R.id.type_custom);
        typeLong.setText(getString(R.string.aft_long));
        this.typeLong.setOnClickListener(this);
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

    private void dealAsrLongUri(Intent data) {
        Uri selectedImage = data.getData();
        mAnalyzer.setAftListener(mAsrListener);
        MLRemoteAftSetting setting = new MLRemoteAftSetting.Factory()
                .setLanguageCode(mLanguage)
                .enablePunctuation(true)
                .enableWordTimeOffset(true)
                .enableSentenceTimeOffset(true)
                .create();
        mLongTaskId = mAnalyzer.longRecognize(selectedImage, setting);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE) {
            if (resultCode == RESULT_OK) {
                dealAsrLongUri(data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startQueryResult() {
        Timer mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                getResult();
            }
        };
        mTimer.schedule(mTimerTask, 5000, 10000); // Obtain the long speech conversion result in polling 10s.
    }

    private void getResult() {
        Log.e(TAG, "getResult");
        mAnalyzer.setAftListener(mAsrListener);
        mAnalyzer.getLongAftResult(mLongTaskId);
    }

}
