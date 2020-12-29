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

package com.huawei.mlkit.sample.manager;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscription;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionConfig;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionConstants;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionListener;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionResult;

import java.util.ArrayList;
import java.util.Set;

public class RealTimeTranscriptionManager {

    private final static String TAG = "RealTimeTranscription";

    public static final int RESULT_FINAL = 2;
    public static final int RESULT_RECEVING = 3;

    private AudioManager mAudioManager;

    private boolean mIsListening;
    private OnResultsReady mListener;
    private ArrayList<String> mResultsList = new ArrayList<>();

    private MLSpeechRealTimeTranscription mlAsrLongRecognizer;

    private String language;

    public RealTimeTranscriptionManager(Context context, String mLanguage, OnResultsReady listener) {
        language = mLanguage;
        try {
            mListener = listener;
        } catch (ClassCastException e) {
            Log.e(TAG, e.toString());
        }

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                startLongRecognizer();
            }
        }).start();
    }

    private void startLongRecognizer() {
        MLSpeechRealTimeTranscriptionConfig config = new MLSpeechRealTimeTranscriptionConfig.Factory()
                .setLanguage(language)
                .enablePunctuation(true)
                .enableSentenceTimeOffset(true)
                .enableWordTimeOffset(true)
                // Set the usage scenario to shopping,Currently, only Chinese scenarios are supported.
                // .setScenes(MLSpeechRealTimeTranscriptionConstants.SCENES_SHOPPING)
                .create();
        MLSpeechRealTimeTranscription.getInstance().setRealTimeTranscriptionListener(new SpeechRecognitionListener());
        MLSpeechRealTimeTranscription.getInstance().startRecognizing(config);
    }

    public void destroy() {
        if (mlAsrLongRecognizer != null) {
            mlAsrLongRecognizer.destroy();
            mlAsrLongRecognizer = null;
        }
    }

    protected class SpeechRecognitionListener implements MLSpeechRealTimeTranscriptionListener {
        @Override
        public void onStartListening() {
            Log.d(TAG, "onStartListening");
        }

        @Override
        public void onStartingOfSpeech() {
            Log.d(TAG, "onStartingOfSpeech");
        }

        @Override
        public void onVoiceDataReceived(byte[] data, float energy, Bundle bundle) {
            int length = data == null ? 0 : data.length;
            Log.d(TAG, "onVoiceDataReceived data.length=" + length);
        }

        @Override
        public void onRecognizingResults(Bundle partialResults) {
            if (partialResults != null && mListener != null) {
                mResultsList.clear();
                mResultsList.add(partialResults.getString(MLSpeechRealTimeTranscriptionConstants.RESULTS_RECOGNIZING));
                boolean isFinal = partialResults.getBoolean(MLSpeechRealTimeTranscriptionConstants.RESULTS_PARTIALFINAL);
                if (isFinal) {
                    String result = partialResults.getString(MLSpeechRealTimeTranscriptionConstants.RESULTS_RECOGNIZING);
                    Log.d(TAG, "onRecognizingResults is " + result);
                    mListener.onRecognizingResults(mResultsList, RESULT_FINAL);

                    ArrayList<MLSpeechRealTimeTranscriptionResult> wordOffset = partialResults.getParcelableArrayList(MLSpeechRealTimeTranscriptionConstants.RESULTS_WORD_OFFSET);
                    ArrayList<MLSpeechRealTimeTranscriptionResult> sentenceOffset = partialResults.getParcelableArrayList(MLSpeechRealTimeTranscriptionConstants.RESULTS_SENTENCE_OFFSET);

                    if (wordOffset != null) {
                        for (int i = 0; i < wordOffset.size(); i++) {
                            MLSpeechRealTimeTranscriptionResult remoteResult = wordOffset.get(i);
                            Log.d(TAG, "onRecognizingResults word offset is " + i + " ---> " + remoteResult.toString());
                        }
                    }

                    if (sentenceOffset != null) {
                        for (int i = 0; i < sentenceOffset.size(); i++) {
                            MLSpeechRealTimeTranscriptionResult remoteResult = sentenceOffset.get(i);
                            Log.d(TAG, "onRecognizingResults sentence offset is " + i + " ---> " + remoteResult.toString());
                        }
                    }
                } else {
                    mListener.onRecognizingResults(mResultsList, RESULT_RECEVING);
                }
            }
        }

        @Override
        public void onError(int error, String errorMessage) {
            // If this parameter is not added,
            // the system does not respond after the network is disconnected and the recording is performed again.
            mIsListening = false;
            if (mListener != null) {
                mListener.onError(error);
            }
        }

        @Override
        public void onState(int state, Bundle params) {
            Log.d(TAG, "---------> onState is " + state);
            if (state == MLSpeechRealTimeTranscriptionConstants.STATE_SERVICE_RECONNECTING) { // webSocket Reconnecting
                Log.d(TAG, "onState webSocket reconnect ");
            } else if (state == MLSpeechRealTimeTranscriptionConstants.STATE_SERVICE_RECONNECTED) { // webSocket Reconnection succeeded.
                Log.d(TAG, "onState webSocket reconnect success ");
            }
        }
    }

    public boolean ismIsListening() {
        return mIsListening;
    }

    public interface OnResultsReady {
        /**
         *  OnResultsReady
         *
         * @param results results
         * @param status Status
         */
        void onRecognizingResults(ArrayList<String> results, int status);

        /**
         * Error message
         *
         * @param error error
         */
        void onError(int error);
    }
}
