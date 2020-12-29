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

package com.huawei.mlkit.sample.activity.gcr;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.huawei.mlkit.sample.R;

import com.huawei.mlkit.sample.activity.dialog.AddPictureDialog;
import com.huawei.mlkit.sample.activity.entity.GeneralCardResult;
import com.huawei.mlkit.sample.processor.gcr.GeneralCardProcessor;
import com.huawei.mlkit.sample.processor.gcr.homecard.HomeCardProcessor;
import com.huawei.mlkit.sample.processor.gcr.hongkong.HKIdCardProcessor;
import com.huawei.mlkit.sample.processor.gcr.passcard.PassCardProcessor;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCapture;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureConfig;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureFactory;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureResult;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureUIConfig;

import java.io.IOException;

public class GeneralCardRecognitionActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "GCRActivity";
    private Uri mImageUri;
    private static final int REQUEST_IMAGE_SELECT_FROM_ALBUM = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private Object object = false;
    private ImageView frontImg;
    private ImageView frontSimpleImg;
    private ImageView frontDeleteImg;
    private LinearLayout frontAddView;
    private TextView showResult;
    private RadioGroup cardType;
    private CardType cardTypeEnum = CardType.PASSCARD;
    private Bitmap imageBitmap;
    RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.passCard:
                    updateCardType(CardType.PASSCARD);
                    break;
                case R.id.HKIdCard:
                    updateCardType(CardType.HKIDCARD);
                    break;
                case R.id.comeHomeCard:
                    updateCardType(CardType.COMEHOMECARD);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_card_recognition);
        initComponent();
    }

    private void initComponent() {
        frontImg = findViewById(R.id.avatar_img);
        frontSimpleImg = findViewById(R.id.avatar_sample_img);
        frontDeleteImg = findViewById(R.id.avatar_delete);
        frontAddView = findViewById(R.id.avatar_add);
        showResult = findViewById(R.id.show_result);

        frontAddView.setOnClickListener(this);
        frontDeleteImg.setOnClickListener(this);
        cardType = findViewById(R.id.card_type);
        cardType.setOnCheckedChangeListener(radioListener);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private void updateCardType(CardType type) {
        if (cardTypeEnum != type) {
            showResult.setText("");
            showFrontDeleteImage();
        }
        cardTypeEnum = type;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.avatar_add:
                showChoosePicDialog();
                break;
            case R.id.avatar_delete:
                showFrontDeleteImage();
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    private void showChoosePicDialog() {
        AddPictureDialog addPictureDialog = new AddPictureDialog(this, AddPictureDialog.TYPE_CUSTOM);
        addPictureDialog.setClickListener(new AddPictureDialog.ClickListener() {
            @Override
            public void takePicture() {
                detectPhoto(object, callback);
            }

            @Override
            public void selectImage() {
                startChooseImageIntentForResult();
            }

            @Override
            public void doExtend() {
                detectPreview(object, callback);
            }
        });
        addPictureDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i(TAG, "onActivityResult requestCode " + requestCode + ", resultCode " + resultCode);
        if (requestCode == REQUEST_IMAGE_SELECT_FROM_ALBUM && resultCode == RESULT_OK) {
            if (intent != null) {
                mImageUri = intent.getData();
            }
            tryReloadAndDetectInImage();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            tryReloadAndDetectInImage();
        }
    }

    public enum CardType {
        HKIDCARD, PASSCARD, COMEHOMECARD
    }

    // video stream
    private void detectPreview(Object object, MLGcrCapture.Callback callback) {
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().setLanguage("en").create();
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                .setTipText(getResources().getString(R.string.vedio_tip))
                .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO).create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig, uiConfig);
        ocrManager.capturePreview(this, object, callback);
    }

    // take a picture
    private void detectPhoto(Object object, MLGcrCapture.Callback callback) {
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().setLanguage("en").create();
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                .setTipText(getResources().getString(R.string.capture_tip))
                .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO).create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig, uiConfig);

        ocrManager.capturePhoto(this, object, callback);
    }

    // local image
    private void detectLocalImage(Bitmap bitmap, Object object, MLGcrCapture.Callback callback) {
        MLGcrCaptureConfig config = new MLGcrCaptureConfig.Factory().create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(config);
        ocrManager.captureImage(bitmap, object, callback);
    }

    private MLGcrCapture.Callback callback = new MLGcrCapture.Callback() {
        @Override
        public int onResult(MLGcrCaptureResult result, Object object) {
            Log.i(TAG, "callback onRecSuccess");
            if (result == null) {
                Log.e(TAG, "callback onRecSuccess result is null");
                return MLGcrCaptureResult.CAPTURE_CONTINUE;
            }

            GeneralCardProcessor idCard = null;
            GeneralCardResult cardResult = null;

            if (cardTypeEnum == CardType.PASSCARD) {
                idCard = new PassCardProcessor(result.text);
            } else if (cardTypeEnum == CardType.HKIDCARD) {
                idCard = new HKIdCardProcessor(result.text);
            } else if (cardTypeEnum == CardType.COMEHOMECARD) {
                idCard = new HomeCardProcessor(result.text);
            }

            if (idCard != null) {
                cardResult = idCard.getResult();
            }

            showFrontImage(result.cardBitmap);
            displayResult(cardResult);

            // If the results don't match
            if (cardResult == null || cardResult.valid.isEmpty() || cardResult.number.isEmpty()) {
                return MLGcrCaptureResult.CAPTURE_CONTINUE;
            }

            displayResult(cardResult);
            return MLGcrCaptureResult.CAPTURE_STOP;
        }

        @Override
        public void onCanceled() {
            Log.i(TAG, "callback onRecCanceled");
        }

        @Override
        public void onFailure(int i, Bitmap bitmap) {

        }

        @Override
        public void onDenied() {
            Log.i(TAG, "callback onCameraDenied");
        }
    };

    private void displayResult(GeneralCardResult result) {
        if (result == null) {
            return;
        }
        if (showResult.getText().length() != 0) {
            showResult.setText("");
        }
        StringBuilder builder = new StringBuilder();

        builder.append("valid: ");
        builder.append(result.valid);
        builder.append(System.lineSeparator());

        builder.append("number: ");
        builder.append(result.number);

        showResult.setText(builder.toString());
    }

    private void showFrontImage(Bitmap bitmap) {
        frontImg.setVisibility(View.VISIBLE);
        frontImg.setImageBitmap(bitmap);
        frontSimpleImg.setVisibility(View.GONE);
        frontAddView.setVisibility(View.GONE);
        frontDeleteImg.setVisibility(View.VISIBLE);
    }

    private void showFrontDeleteImage() {
        frontImg.setVisibility(View.GONE);
        frontSimpleImg.setVisibility(View.VISIBLE);
        frontAddView.setVisibility(View.VISIBLE);
        frontDeleteImg.setVisibility(View.GONE);
    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_IMAGE_SELECT_FROM_ALBUM);
    }

    private void tryReloadAndDetectInImage() {
        if (mImageUri == null) {
            return;
        }
        Bitmap mTryImageBitmap = null;
        try {
            mTryImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
        } catch (IOException error) {
            Log.e(TAG, "Failed to get bitmap from uri: " + error.getMessage());
        }
        detectLocalImage(mTryImageBitmap, null, callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageBitmap != null && !imageBitmap.isRecycled()) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
    }
}

