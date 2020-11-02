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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.mlkit.lensengine.BitmapUtils;
import com.huawei.mlkit.sample.photoreader.camera.CapturePhotoActivity;
import com.huawei.mlkit.sample.photoreader.databinding.ActivityReadPhotoBinding;
import com.huawei.mlkit.sample.photoreader.util.Constant;

import java.io.FileInputStream;
import java.io.IOException;


public class ReadPhotoActivity extends AppCompatActivity {

    private static String TAG = "ReadPhotoActivity";
    private String path;
    private Bitmap originBitmap;
    private Integer maxWidthOfImage;
    private Integer maxHeightOfImage;
    boolean isLandScape;
    private int REQUEST_CHOOSE_ORIGINPIC = 2001;
    private int REQUEST_TAKE_PHOTO = 2000;
    private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";
    private static final String KEY_IMAGE_MAX_WIDTH =
            "KEY_IMAGE_MAX_WIDTH";
    private static final String KEY_IMAGE_MAX_HEIGHT =
            "KEY_IMAGE_MAX_HEIGHT";
    private static int TEXT_LENGTH = 499;
    private static final int INIT_VALUE = 0;
    private String sourceText = "";
    private String SpeakText;

    private TextRecognition textRecognition;
    private TextTranslation textTranslation;
    private TextTTS textTTS;
    private LinearLayout.LayoutParams graphicOverlayLayout;

    public static final String EXTRA_SOURCE_LANGUAGE = "EXTRA_SOURCE_LANGUAGE";
    public static final String EXTRA_DESTINATION_LANGUAGE = "EXTRA_DESTINATION_LANGUAGE";

    private String srcLanguage = Constant.ML_CHINESE;
    private String dstLanguage = Constant.ML_ENGLISH;

    private MLTextAnalyzer textAnalyzer;
    private MLTtsEngine mlTtsEngine;
    private MLRemoteTranslator mlRemoteTranslator;

    private ActivityReadPhotoBinding binding;

    final ActivityResultLauncher<Void> chooseLocalImage = registerForActivityResult(
            new ReadPhotoActivityContracts.ChoosePictureContract(), result -> {
                if(result != null) {
                    final Bitmap bitmap = loadBitmap(result);

                }
            });

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        binding = ActivityReadPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_SOURCE_LANGUAGE)) {
            srcLanguage = intent.getStringExtra(EXTRA_SOURCE_LANGUAGE);
        }

        if(intent.hasExtra(EXTRA_DESTINATION_LANGUAGE)) {
            dstLanguage = intent.getStringExtra(EXTRA_DESTINATION_LANGUAGE);
        }

        initTextAnalyser();
        initRemoteTranslator();
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

    private void initActions() {
        binding.tvActReadPhotoSelectPicture.setOnClickListener(v ->
            chooseLocalImage.launch(null)
        );

        binding.tvActReadPhotoTakePicture.setOnClickListener(v -> {
            takePhoto(ReadPhotoActivity.this.REQUEST_TAKE_PHOTO);
        });

        this.relativeLayoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadPhotoActivity.this.takePhoto(ReadPhotoActivity.this.REQUEST_TAKE_PHOTO);
            }
        });

        relativateTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textTranslation == null){
                    Toast.makeText(ReadPhotoActivity.this.getApplicationContext(),
                            R.string.no_text, Toast.LENGTH_SHORT).show();
                    textView.setText(R.string.select_text);
                    return;
                }else {
                    textTranslation.createRemoteTranslator();
                }


            }
        });

        this.relativeLayoutRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SpeakText == null) {
                    Toast.makeText(ReadPhotoActivity.this.getApplicationContext(),
                            R.string.please_select_picture, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mlTtsEngine.speak(SpeakText, MLTtsEngine.QUEUE_APPEND);

                    Toast.makeText(ReadPhotoActivity.this.getApplicationContext(),
                            R.string.read_start, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void takePhoto(int requestCode) {
        Intent intent = new Intent(ReadPhotoActivity.this, CapturePhotoActivity.class);
        this.startActivityForResult(intent, requestCode);
    }

    private void selectLocalImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(intent, requestCode);
    }

    private @NonNull Bitmap loadBitmap(final Uri imageUri) {
        final int targetWidth = binding.svActReadPhotoPaneContainer.getWidth();
        final int targetHeight = binding.svActReadPhotoPaneContainer.getHeight();
        return BitmapUtils.loadFromPath(getContentResolver(), imageUri, targetWidth, targetHeight);
    }

    private void processBitmap(final Bitmap bitmap) {
        binding.ivActReadPhotoPreview.setImageBitmap(bitmap);
        
        lifecycleScope.launch {
            try {
                setActionButtonEnabled(false)
                val mlText = textAnalyzer.asyncAnalyseFrame(MLFrame.fromBitmap(bitmap)).await()
                val sourceText = mlText.stringValue
                etActReadPhotoInput.setText(sourceText)

                if(sourceText.isNotBlank()) {
                    val translated = remoteTranslator.asyncTranslate(sourceText.trim()).await()
                    etActReadPhotoTranslated.setText(translated)
                }

                setActionButtonEnabled(true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to process bitmap", e)
                setActionButtonEnabled(true)
                Toast.makeText(this@ReadPhotoActivity, "Fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == this.REQUEST_CHOOSE_ORIGINPIC)
                && (resultCode == Activity.RESULT_OK)) {
            // In this case, imageUri is returned by the chooser, save it.
            this.imageUri = data.getData();
            this.loadOriginImage();
            textRecognition = new TextRecognition(this,originBitmap, sourceText, mEd_text);
            textAnalyzer = textRecognition.getTextAnalyzer();
            textRecognition.startTextAnalyzer();
            textTranslation = new TextTranslation(dstLanguage, srcLanguage, mEd_text);
            textTranslation.setCallback(new TextTranslation.TranslationCallback() {
                public void onSuccess(String name) {
                    textView.setText(name);
                    if (name.length() > TEXT_LENGTH){
                        String substring = name.substring(INIT_VALUE, TEXT_LENGTH);
                        SpeakText = substring;
                    }else {
                        SpeakText = name;
                    }

                }

                public void onError() {
                    displayFailure();
                }

            });
        } else if ((requestCode == this.REQUEST_TAKE_PHOTO)
                && (resultCode == Activity.RESULT_OK)
                && data != null) {
            this.path = data.getStringExtra(Constant.IMAGE_PATH_VALUE);
            this.loadCameraImage();
            textRecognition = new TextRecognition(this, originBitmap, sourceText, mEd_text);
            textAnalyzer = textRecognition.getTextAnalyzer();
            textRecognition.startTextAnalyzer();
            textTranslation = new TextTranslation(dstLanguage, srcLanguage, mEd_text);
            textTranslation.setCallback(new TextTranslation.TranslationCallback() {
                public void onSuccess(String name) {
                    textView.setText(name);
                    if (name.length() > TEXT_LENGTH){
                        String substring = name.substring(INIT_VALUE, TEXT_LENGTH);
                        SpeakText = substring;
                    }else {
                        SpeakText = name;
                    }
                }

                public void onError() {
                    displayFailure();
                }

            });
        } else {

        }
    }

    private void loadCameraImage() {
        FileInputStream fis = null;

        try {
            if (path == null) {
                return;
            }
            fis = new FileInputStream(path);
            this.originBitmap = BitmapFactory.decodeStream(fis);
            this.originBitmap = this.originBitmap.copy(Bitmap.Config.ARGB_4444, true);
            this.preview.setImageBitmap(this.originBitmap);
        } catch (IOException e) {
            Log.e(TAG, "file not found");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException error) {
                    Log.e(TAG, "Load camera image failed: " + error.getMessage());
                }
            }
        }
    }

    private void loadOriginImage() {
        if (this.imageUri == null) {
            return;
        }
        Pair<Integer, Integer> targetedSize = this.getTargetSize();
        int targetWidth = targetedSize.first;
        int maxHeight = targetedSize.second;
        this.originBitmap = BitmapUtils.loadFromPath(ReadPhotoActivity.this.getContentResolver(), this.imageUri, targetWidth, maxHeight);

        graphicOverlayLayout.width = originBitmap.getWidth();
        graphicOverlayLayout.height = originBitmap.getHeight();
        preview.setLayoutParams(graphicOverlayLayout);

        // Determine how much to scale down the image.
        Log.i(ReadPhotoActivity.TAG, "resized image size width:"
                + this.originBitmap.getWidth() + ",height: " + this.originBitmap.getHeight());
        this.preview.setImageBitmap(this.originBitmap);
    }

    // Returns max width of image.
    private Integer getMaxWidthOfImage() {
        if (this.maxWidthOfImage == null) {
            if (this.isLandScape) {
                this.maxWidthOfImage = ((View) this.preview.getParent()).getHeight();
            } else {
                this.maxWidthOfImage = ((View) this.preview.getParent()).getWidth();
            }
        }
        return this.maxWidthOfImage;
    }

    // Returns max height of image.
    private Integer getMaxHeightOfImage() {
        if (this.maxHeightOfImage == null) {
            if (this.isLandScape) {
                this.maxHeightOfImage = ((View) this.preview.getParent()).getWidth();
            } else {
                this.maxHeightOfImage = ((View) this.preview.getParent()).getHeight();
            }
        }
        return this.maxHeightOfImage;
    }

    // Gets the targeted size(width / height).
    private Pair<Integer, Integer> getTargetSize() {
        Integer targetWidth;
        Integer targetHeight;
        Integer maxWidth = this.getMaxWidthOfImage();
        Integer maxHeight = this.getMaxHeightOfImage();
        targetWidth = this.isLandScape ? maxHeight : maxWidth;
        targetHeight = this.isLandScape ? maxWidth : maxHeight;
        Log.i(ReadPhotoActivity.TAG, "height:" + targetHeight + ",width:" + targetWidth);
        return new Pair<>(targetWidth, targetHeight);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.textAnalyzer != null) {
            try {
                this.textAnalyzer.close();
            } catch (IOException e) {
                Log.e(ReadPhotoActivity.TAG, "Stop analyzer failed: " + e.getMessage());
            }
        }
        if (this.mlTtsEngine != null) {
            this.mlTtsEngine.stop();
        }
        this.imageUri = null;
        this.path = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ReadPhotoActivity.KEY_IMAGE_URI, this.imageUri);
        if (this.maxWidthOfImage != null) {
            outState.putInt(ReadPhotoActivity.KEY_IMAGE_MAX_WIDTH, this.maxWidthOfImage);
        }
        if (this.maxHeightOfImage != null) {
            outState.putInt(ReadPhotoActivity.KEY_IMAGE_MAX_HEIGHT, this.maxHeightOfImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void displayFailure() {
        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
    }
}
