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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.mlkit.lensengine.BitmapUtils;
import com.huawei.mlkit.sample.photoreader.camera.CapturePhotoActivity;
import com.huawei.mlkit.sample.photoreader.util.Constant;

import java.io.FileInputStream;
import java.io.IOException;


public class ReadPhotoActivity extends AppCompatActivity {
    private static String TAG = "ReadPhotoActivity";
    private RelativeLayout relativeLayoutLoadPhoto;
    private RelativeLayout relativeLayoutTakePhoto;
    private RelativeLayout relativeLayoutRead;
    private ImageView preview;
    private TextView textView;
    private Uri imageUri;
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

    private String srcLanguage = "Chinese";
    private String dstLanguage = "EN";
    private EditText mEd_text;
    private RelativeLayout relativateTranslate;
    private TextRecognition textRecognition;
    private TextTranslation textTranslation;
    private TextTTS textTTS;
    private MLTtsEngine mlTtsEngine;
    private MLTextAnalyzer textAnalyzer;
    private LinearLayout.LayoutParams graphicOverlayLayout;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this.setContentView(R.layout.activity_read_photo);

        Intent intent = this.getIntent();
        try {
            this.srcLanguage = intent.getStringExtra(Constant.SOURCE_VALUE);
            this.dstLanguage = intent.getStringExtra(Constant.DEST_VALUE);
        } catch (RuntimeException e) {
            Log.e(ReadPhotoActivity.TAG, "Get intent value failed:" + e.getMessage());
        }

        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadPhotoActivity.this.finish();
            }
        });

        this.isLandScape =
                (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        textTTS = new TextTTS(this,dstLanguage);
        this.initView();
        this.initAction();
        textTTS.createTtsEngine();
        mlTtsEngine = textTTS.getMlTtsEngine();
    }

    private void initView() {
        this.relativeLayoutLoadPhoto = this.findViewById(R.id.relativate_chooseImg);
        this.relativeLayoutTakePhoto = this.findViewById(R.id.relativate_camera);
        this.relativeLayoutRead = this.findViewById(R.id.relativate_read);
        relativateTranslate = findViewById(R.id.relativate_translate);
        this.preview = this.findViewById(R.id.previewPane);
        this.textView = this.findViewById(R.id.translate_result);
        mEd_text = findViewById(R.id.et_input);

        graphicOverlayLayout = (LinearLayout.LayoutParams) preview.getLayoutParams();
    }

    private void initAction() {
        this.relativeLayoutLoadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadPhotoActivity.this.selectLocalImage(ReadPhotoActivity.this.REQUEST_CHOOSE_ORIGINPIC);
            }
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
