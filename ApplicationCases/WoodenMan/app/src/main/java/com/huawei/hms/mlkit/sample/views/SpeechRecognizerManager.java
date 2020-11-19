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

package com.huawei.hms.mlkit.sample.views;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.asr.MLAsrListener;
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer;

import java.util.ArrayList;

public class SpeechRecognizerManager {

    private static final String TAG = "SpeechRecognizerManager";
    private MLAsrRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    private onResultsReady mListener;
    private ArrayList<String> mResultsList = new ArrayList<>();

    public SpeechRecognizerManager(Context context, String language, onResultsReady listener) {
        try {
            mListener = listener;
        } catch (ClassCastException e) {
            // ClassCastException
            Log.e(TAG, "Error: ClassCastException");
        } catch (Exception e) {
            // Exception
            Log.e(TAG, "Error: " + e.getMessage());
        }
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Call an API to create a speech recognizer.
        mSpeechRecognizer = MLAsrRecognizer.createAsrRecognizer(context);
        // Set the ASR result listener callback. You can obtain the ASR result or result code from the listener.
        mSpeechRecognizer.setAsrListener(new SpeechRecognitionListener());
        // Set parameters and start the audio device.
        mSpeechRecognizerIntent = new Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH);
        mSpeechRecognizerIntent
                // Set the language that can be recognized to English. If this parameter is not set,
                // English is recognized by default. Example: "zh-CN": Chinese;"en-US": English;"fr-FR": French;"es-ES": Spanish;"de-DE": German;"it-IT": Italian.
                .putExtra(MLAsrConstants.LANGUAGE, language)
                // Set to return the recognition result along with the speech. If you ignore the setting, this mode is used by default. Options are as follows:
                // MLAsrConstants.FEATURE_WORDFLUX: Recognizes and returns texts through onRecognizingResults.
                // MLAsrConstants.FEATURE_ALLINONE: After the recognition is complete, texts are returned through onResults.
                .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_WORDFLUX);
    }


    public void startListening() {
        mSpeechRecognizer.startRecognizing(mSpeechRecognizerIntent);
    }

    public void destroy() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }
    }

    /**
     * Use the callback to implement the MLAsrListener API and methods in the API.
     */
    protected class SpeechRecognitionListener implements MLAsrListener {
        @Override
        public void onStartListening() {
            // The recorder starts to receive speech.
        }

        @Override
        public void onStartingOfSpeech() {
            // The user starts to speak, that is, the speech recognizer detects that the user starts to speak.
        }

        @Override
        public void onVoiceDataReceived(byte[] data, float energy, Bundle bundle) {
            // Return the original PCM stream and audio power to the user.
            int length = data == null ? 0 : data.length;
        }

        @Override
        public void onRecognizingResults(Bundle partialResults) {
            // Receive the recognized text from MLAsrRecognizer.
            if (partialResults != null && mListener != null) {
                mResultsList.clear();
                mResultsList.add(partialResults.getString(MLAsrRecognizer.RESULTS_RECOGNIZING));
                mListener.onResults(mResultsList);
            }
        }

        @Override
        public void onResults(Bundle results) {
            // Text data of ASR.
            if (results != null && mListener != null) {
                mResultsList.clear();
                mResultsList.add(results.getString(MLAsrRecognizer.RESULTS_RECOGNIZED));
                mListener.onFinish();
            }
        }

        @Override
        public void onError(int error, String errorMessage) {
            // If you don't add this, there will be no response after you cut the network
            Log.e(TAG, "error = " + error);
            Log.e(TAG, "errorMessage = " + errorMessage);
            if (mListener != null) {
                mListener.onError(error);
            }
        }

        @Override
        public void onState(int state, Bundle params) {
            // Notify the app status change.
            if (state == MLAsrConstants.STATE_NO_SOUND_TIMES_EXCEED) {
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        }
    }

    public interface onResultsReady {
        void onResults(ArrayList<String> results);

        void onFinish();

        void onError(int error);
    }
}
