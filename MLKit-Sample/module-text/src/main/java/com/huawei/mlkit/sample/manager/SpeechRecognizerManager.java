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
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.asr.MLAsrListener;
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer;

import java.util.ArrayList;


public class SpeechRecognizerManager {
    private final static String TAG = "SpeechRecognizerManager";

    protected AudioManager mAudioManager;
    protected MLAsrRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;

    private OnResultsReady mListener;
    ArrayList<String> mResultsList = new ArrayList<>();

    public SpeechRecognizerManager(Context context, String language, OnResultsReady listener) {
        try {
            mListener = listener;
        } catch (ClassCastException e) {
            Log.e(TAG, e.toString());
        }
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = MLAsrRecognizer.createAsrRecognizer(context);
        mSpeechRecognizer.setAsrListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH);

        mSpeechRecognizerIntent.putExtra(MLAsrConstants.LANGUAGE, language)
                .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX);
                // Set the usage scenario to shopping.
                //.putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING);
    }


    public void startListening() {
        mSpeechRecognizer.startRecognizing(mSpeechRecognizerIntent);
    }

    public void destroy() {
        Log.d(TAG, "onDestroy");
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }

    }

    protected class SpeechRecognitionListener implements MLAsrListener {
        @Override
        public void onStartListening() {
            Log.d(TAG, "onStartListening--");
        }

        @Override
        public void onStartingOfSpeech() {
            Log.d(TAG, "onStartingOfSpeech--");
        }

        @Override
        public void onVoiceDataReceived(byte[] data, float energy, Bundle bundle) {
            int length = data == null ? 0 : data.length;
            Log.d(TAG, "onVoiceDataReceived-- data.length=" + length);
        }

        @Override
        public void onRecognizingResults(Bundle partialResults) {
            if (partialResults != null && mListener != null) {
                mResultsList.clear();
                mResultsList.add(partialResults.getString(MLAsrRecognizer.RESULTS_RECOGNIZING));
                mListener.onResults(mResultsList);
                Log.d(TAG, "onResults is " + partialResults);
            }
        }

        @Override
        public void onResults(Bundle results) {
            Log.e(TAG, "onResults");
            if (results != null && mListener != null) {
                mResultsList.clear();
                mResultsList.add(results.getString(MLAsrRecognizer.RESULTS_RECOGNIZED));
                mListener.onFinsh();
                Log.d(TAG, "onResults is " + results);
            }
        }

        @Override
        public void onError(int error, String errorMessage) {
            Log.e(TAG, "onError: " + errorMessage);
            // If you don't add this, there will be no response after you cut the network
            if (mListener != null) {
                mListener.onError(error);
            }
        }

        @Override

        public void onState(int state, Bundle params) {
            Log.e(TAG, "onState :" + state);
            if (state == MLAsrConstants.STATE_NO_SOUND_TIMES_EXCEED) {
                if (mListener != null) {
                    mListener.onFinsh();
                }
            }
        }
    }

    public interface OnResultsReady {
        /**
         * results
         *
         * @param results results
         */
        void onResults(ArrayList<String> results);

        /**
         *  finish
         */
        void onFinsh();

        /**
         * error message
         *
         * @param error  error
         */
        void onError(int error);
    }

}
