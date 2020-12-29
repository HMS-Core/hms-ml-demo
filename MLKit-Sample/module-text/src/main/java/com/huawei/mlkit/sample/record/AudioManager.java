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

package com.huawei.mlkit.sample.record;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huawei.mlkit.sample.activity.dialog.RecordDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Recording management
 *
 * @since 2019-12-26
 */

public class AudioManager {
    private static final String TAG = "AudioManager";

    private MediaRecorder mRecorder;

    private String mFileDir;

    private String mFilePath;

    private boolean mIsReady;

    private RecordDialog mRecordDialog;

    private boolean mIsRecording = false;

    private float mRecordingTime = 0F;

    private static final int MSG_START_RECORDING = 1000;

    // volume changed event when recording.
    private static final int MSG_VOLUME_CHANGED = 1001;

    private static final int MAX_TIME = 60; // max record time in second.

    private RecordingListener mListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VOLUME_CHANGED:
                    mRecordDialog.updateVolumeLevel(getVolumeLevel(12));
                    break;
                default:
                    break;
            }
        }
    };

    public AudioManager(String dir, RecordDialog recordDialog) {
        mRecordDialog = recordDialog;
        mFileDir = dir;
    }

    public void setOnRecordingStateListener(RecordingListener listener) {
        mListener = listener;
    }

    /**
     * prepare to record the audio
     */
    public void prepareAudio() {
        try {
            mIsReady = false;
            File dir = new File(mFileDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = generateFileName();

            File file = new File(dir, fileName);

            mFilePath = file.getCanonicalPath();

            mRecorder = new MediaRecorder();
            mRecorder.setOutputFile(file.getCanonicalPath());
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
            mIsReady = true;
            if (mListener != null) {
                mListener.recordingReady();
            }
        } catch (IOException e) {
            Log.d(TAG, "IOException" + e.toString());
        }
    }

    private String generateFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str + ".amr";
    }

    public int getVolumeLevel(int maxLevel) {
        if (mIsReady) {
            try {
                float curLevel = (float) maxLevel * mRecorder.getMaxAmplitude() / 32768f;
                if (curLevel <= 0.2f) {
                    return 1;
                } else if (curLevel >= 0.2f && curLevel <= 0.4f) {
                    return 2;
                } else if (curLevel > 0.4f && curLevel <= 0.6f) {
                    return 3;
                } else if (curLevel > 0.6f && curLevel <= 0.8f) {
                    return 4;
                } else if (curLevel > 0.8f && curLevel <= 1.0f) {
                    return 5;
                } else if (curLevel > 1.0f && curLevel <= 1.2f) {
                    return 6;
                } else if (curLevel > 1.2 && curLevel <= 1.4f) {
                    return 7;
                } else if (curLevel > 1.4f && curLevel <= 1.6f) {
                    return 8;
                } else if (curLevel > 1.6f && curLevel <= 1.8f) {
                    return 9;
                } else if (curLevel > 1.8f && curLevel <= 2.0f) {
                    return 10;
                } else if (curLevel > 2.0f && curLevel <= 2.2f) {
                    return 11;
                } else if (curLevel > 2.2f) {
                    return 12;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception");
            }
        }
        return 1;
    }

    public void release() {
        if (mRecorder != null)
            mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void startRecording() {
        mIsRecording = true;
        new Thread(mGetVolumeLevelRunnable).start();
    }

    private Runnable mGetVolumeLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (mIsRecording) {
                try {
                    Thread.sleep(100);
                    mRecordingTime += 0.1; // interval time 0.1s
                    mHandler.sendEmptyMessage(MSG_VOLUME_CHANGED);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Get volume level failed: " + e.getMessage());
                }
            }
        }
    };

    private void initData() {
        mIsRecording = false;
        mRecordingTime = 0;
        mIsReady = false;
    }

    public void recordingComplete() {
        release();
        if (mListener != null) {
            mListener.onComplete(mRecordingTime, getFilePath());
        }
        initData();
    }
}