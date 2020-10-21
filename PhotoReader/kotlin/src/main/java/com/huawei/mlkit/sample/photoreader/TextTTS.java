/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.mlkit.sample.photoreader;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;

public class TextTTS {
    public MLTtsEngine mlTtsEngine;
    private Context mContext;
    private String dstLanguage;

    public MLTtsEngine getMlTtsEngine() {
        return mlTtsEngine;
    }

    public TextTTS(Context mContext, String dstLanguage) {
        this.mContext = mContext;
        this.dstLanguage = dstLanguage;
    }

    public void createTtsEngine() {
        String language = dstLanguage.equals("ZH") ? MLTtsConstants.TTS_ZH_HANS : MLTtsConstants.TTS_EN_US;
        String person = dstLanguage.equals("ZH") ? MLTtsConstants.TTS_SPEAKER_FEMALE_ZH : MLTtsConstants.TTS_SPEAKER_FEMALE_EN;

        MLTtsConfig mlConfigs = new MLTtsConfig();
        mlConfigs.setLanguage(language)
                .setPerson(person)
                .setSpeed(1.0f)
                .setVolume(1.0f);

        this.mlTtsEngine = new MLTtsEngine(mlConfigs);
        MLTtsCallback callback = new MLTtsCallback() {
            @Override
            public void onError(String taskId, MLTtsError err) {
                Log.e("MLTtsError", err.getErrorMsg());
            }

            @Override
            public void onWarn(String taskId, MLTtsWarn warn) {
            }

            @Override
            public void onRangeStart(String taskId, int start, int end) {
            }

            @Override
            public void onAudioAvailable(String s, MLTtsAudioFragment mlTtsAudioFragment,
                                         int i, Pair<Integer, Integer> pair, Bundle bundle) {
            }

            @Override
            public void onEvent(String taskId, int eventName, Bundle bundle) {
                if (eventName == MLTtsConstants.EVENT_PLAY_STOP) {
                    if (!bundle.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED)) {
                        Toast.makeText(mContext,
                                R.string.read_finish, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        mlTtsEngine.setTtsCallback(callback);
    }
}
