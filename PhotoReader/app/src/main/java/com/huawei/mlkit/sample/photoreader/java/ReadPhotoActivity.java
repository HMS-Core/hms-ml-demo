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
package com.huawei.mlkit.sample.photoreader.java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;
import com.huawei.mlkit.lensengine.BitmapUtils;
import com.huawei.mlkit.sample.photoreader.Constant;
import com.huawei.mlkit.sample.photoreader.R;
import com.huawei.mlkit.sample.photoreader.databinding.ActivityReadPhotoBinding;

import static com.huawei.mlkit.sample.photoreader.Constant.ML_CHINESE;
import static com.huawei.mlkit.sample.photoreader.Constant.ML_TTS_MAX_ALLOWED_CHAR_LENGTH;


public class ReadPhotoActivity extends AppCompatActivity {

    private static final String TAG = "ReadPhotoActivity";

    public static final String EXTRA_SOURCE_LANGUAGE = "EXTRA_SOURCE_LANGUAGE";
    public static final String EXTRA_DESTINATION_LANGUAGE = "EXTRA_DESTINATION_LANGUAGE";

    private String srcLanguage = ML_CHINESE;
    private String dstLanguage = Constant.ML_ENGLISH;

    private MLTextAnalyzer textAnalyzer;
    private MLTtsEngine mlTtsEngine;
    private MLRemoteTranslator mlRemoteTranslator;

    private ActivityReadPhotoBinding binding;

    private final ActivityResultLauncher<Void> chooseLocalImage = registerForActivityResult(
            new ReadPhotoActivityContracts.ChoosePictureContract(), result -> {
                if (result != null) {
                    final Bitmap bitmap = loadBitmap(result);
                    processBitmap(bitmap);
                }
            });

    private final ActivityResultLauncher<Void> takePictureContract = registerForActivityResult(
            new ReadPhotoActivityContracts.TakePictureContract(), this::processBitmap
    );

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding = ActivityReadPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SOURCE_LANGUAGE)) {
            srcLanguage = intent.getStringExtra(EXTRA_SOURCE_LANGUAGE);
        }

        if (intent.hasExtra(EXTRA_DESTINATION_LANGUAGE)) {
            dstLanguage = intent.getStringExtra(EXTRA_DESTINATION_LANGUAGE);
        }

        initTextAnalyser();
        initRemoteTranslator();
        initMlTts();
        initActions();
    }

    private void initTextAnalyser() {
        final MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .setLanguage(srcLanguage)
                .create();
        textAnalyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);
    }

    private void initRemoteTranslator() {
        final MLRemoteTranslateSetting setting = new MLRemoteTranslateSetting.Factory()
                .setSourceLangCode(srcLanguage)
                .setTargetLangCode(dstLanguage)
                .create();
        mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);
    }

    private void initMlTts() {
        final String language = dstLanguage.equals(ML_CHINESE) ? MLTtsConstants.TTS_ZH_HANS
                : MLTtsConstants.TTS_EN_US;
        final String person = dstLanguage.equals(ML_CHINESE) ? MLTtsConstants.TTS_SPEAKER_FEMALE_ZH
                : MLTtsConstants.TTS_SPEAKER_FEMALE_EN;

        final MLTtsConfig mlConfigs = new MLTtsConfig();
        mlConfigs.setLanguage(language)
                .setPerson(person)
                .setSpeed(1.0f)
                .setVolume(1.0f);

        mlTtsEngine = new MLTtsEngine(mlConfigs);
        final MLTtsCallback callback = new MLTtsCallback() {

            @Override
            public void onError(String s, MLTtsError mlTtsError) {
                Log.e(TAG, mlTtsError.getErrorMsg());
            }

            @Override
            public void onWarn(String s, MLTtsWarn mlTtsWarn) {
            }

            @Override
            public void onRangeStart(String s, int i, int i1) {
            }

            @Override
            public void onAudioAvailable(String s, MLTtsAudioFragment mlTtsAudioFragment,
                                         int i, Pair<Integer, Integer> pair, Bundle bundle) {
            }

            @Override
            public void onEvent(String taskId, int event, Bundle bundle) {
                if (event == MLTtsConstants.EVENT_PLAY_STOP) {
                    if(bundle != null && bundle.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED)) {
                        Toast.makeText(ReadPhotoActivity.this, R.string.read_finish,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        mlTtsEngine.setTtsCallback(callback);
    }

    private void initActions() {
        binding.tvActReadPhotoSelectPicture.setOnClickListener(v ->
                chooseLocalImage.launch(null)
        );

        binding.tvActReadPhotoTakePicture.setOnClickListener(v ->
                takePictureContract.launch(null)
        );

        binding.tvActReadPhotoTranslate.setOnClickListener(v -> {
            final String inputText = binding.etActReadPhotoInput.getText().toString().trim();
            if(!inputText.isEmpty()) {
                setActionButtonEnabled(false);
                mlRemoteTranslator.asyncTranslate(inputText).addOnSuccessListener(translated -> {
                    binding.etActReadPhotoTranslated.setText(translated);
                    setActionButtonEnabled(true);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to translate", e);
                    setActionButtonEnabled(true);
                    Toast.makeText(ReadPhotoActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, R.string.no_text, Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvActReadPhotoRead.setOnClickListener(v -> {
            final String translatedText = binding.etActReadPhotoTranslated.getText().toString().trim();
            if(!translatedText.isEmpty()) {
                Toast.makeText(this, R.string.read_start, Toast.LENGTH_SHORT).show();
                final String ttsText;
                if(translatedText.length() > ML_TTS_MAX_ALLOWED_CHAR_LENGTH) {
                    ttsText = translatedText.substring(0, ML_TTS_MAX_ALLOWED_CHAR_LENGTH);
                } else {
                    ttsText = translatedText;
                }

                mlTtsEngine.speak(ttsText, MLTtsEngine.QUEUE_APPEND);
            } else {
                Toast.makeText(this, R.string.no_text_to_speak, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private @NonNull Bitmap loadBitmap(final Uri imageUri) {
        final int targetWidth = binding.svActReadPhotoPaneContainer.getWidth();
        final int targetHeight = binding.svActReadPhotoPaneContainer.getHeight();
        return BitmapUtils.loadFromPath(getContentResolver(), imageUri, targetWidth, targetHeight);
    }

    private void processBitmap(final Bitmap bitmap) {
        if (bitmap == null)
            return;

        binding.ivActReadPhotoPreview.setImageBitmap(bitmap);
        setActionButtonEnabled(false);

        textAnalyzer.asyncAnalyseFrame(MLFrame.fromBitmap(bitmap)).addOnSuccessListener(mlText -> {
            final String text = mlText.getStringValue().trim();

            this.binding.etActReadPhotoInput.setText(text);
            if (!text.isEmpty()) {
                translateText(text);
            } else {
                setActionButtonEnabled(true);
            }
        }).addOnFailureListener(this::onProcessBitmapFailure);
    }

    private void translateText(final String text) {
        mlRemoteTranslator.asyncTranslate(text).addOnSuccessListener(translated -> {
            binding.etActReadPhotoTranslated.setText(translated);
            setActionButtonEnabled(true);
        }).addOnFailureListener(this::onProcessBitmapFailure);
    }

    private void onProcessBitmapFailure(final Exception e) {
        Log.e(TAG, "Failed to process bitmap", e);
        setActionButtonEnabled(true);
        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
    }

    private void setActionButtonEnabled(final boolean enabled) {
        binding.tvActReadPhotoSelectPicture.setEnabled(enabled);
        binding.tvActReadPhotoTakePicture.setEnabled(enabled);
        binding.tvActReadPhotoTranslate.setEnabled(enabled);
        binding.tvActReadPhotoRead.setEnabled(enabled);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mlTtsEngine.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textAnalyzer.release();
        mlTtsEngine.shutdown();
    }
}
