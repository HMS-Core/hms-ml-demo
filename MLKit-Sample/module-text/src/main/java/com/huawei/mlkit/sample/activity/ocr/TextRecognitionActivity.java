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

package com.huawei.mlkit.sample.activity.ocr;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.activity.docdetection.RemoteDetectionActivity;
import com.huawei.mlkit.sample.activity.dialog.AddPictureDialog;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.processor.LocalDataProcessor;
import com.huawei.mlkit.sample.transactor.LocalTextTransactor;
import com.huawei.mlkit.sample.util.BitmapUtils;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.util.SharedPreferencesUtil;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.mlkit.sample.views.overlay.ZoomImageView;
import com.huawei.mlkit.sample.R;

import java.io.IOException;
import java.lang.ref.WeakReference;

import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

public final class TextRecognitionActivity extends BaseActivity
        implements OnRequestPermissionsResultCallback, View.OnClickListener {
    private static final String TAG = "TextRecognitionActivity";
    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;
    private ImageButton takePicture;
    private ImageButton imageSwitch;
    private RelativeLayout zoomImageLayout;
    private ZoomImageView zoomImageView;
    private ImageButton zoomImageClose;
    CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;
    private Camera mCamera;
    private boolean isLandScape;
    private Bitmap bitmap;
    private Bitmap bitmapCopy;
    private LocalTextTransactor localTextTransactor;
    private Handler mHandler = new MsgHandler(this);
    private Dialog languageDialog;
    private AddPictureDialog addPictureDialog;
    private TextView textCN;
    private TextView textEN;
    private TextView textJN;
    private TextView textKN;
    private TextView textLN;
    private String textType = Constant.POSITION_CN;
    private boolean isInitialization = false;

    private static class MsgHandler extends Handler {
        WeakReference<TextRecognitionActivity> mMainActivityWeakReference;

        public MsgHandler(TextRecognitionActivity mainActivity) {
            this.mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextRecognitionActivity mainActivity = this.mMainActivityWeakReference.get();
            if (mainActivity == null) {
                return;
            }
            Log.d(TextRecognitionActivity.TAG, "msg what :" + msg.what);
            if (msg.what == Constant.SHOW_TAKE_PHOTO_BUTTON) {
                mainActivity.setVisible();
            } else if (msg.what == Constant.HIDE_TAKE_PHOTO_BUTTON) {
                mainActivity.setGone();
            }
        }
    }

    private void setVisible() {
        if (this.takePicture.getVisibility() == View.GONE) {
            this.takePicture.setVisibility(View.VISIBLE);
        }
    }

    private void setGone() {
        if (this.takePicture.getVisibility() == View.VISIBLE) {
            this.takePicture.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_text_recognition);
        if (savedInstanceState != null) {
            this.facing = savedInstanceState.getInt(Constant.CAMERA_FACING);
        }
        this.preview = this.findViewById(R.id.live_preview);
        this.graphicOverlay = this.findViewById(R.id.live_overlay);
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.initViews();
        this.isLandScape = (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        this.createLensEngine();
        this.setStatusBar();
    }

    private void initViews() {
        this.takePicture = this.findViewById(R.id.takePicture);
        this.takePicture.setOnClickListener(this);
        this.imageSwitch = this.findViewById(R.id.text_imageSwitch);
        this.imageSwitch.setOnClickListener(this);
        this.zoomImageLayout = this.findViewById(R.id.zoomImageLayout);
        this.zoomImageView = this.findViewById(R.id.take_picture_overlay);
        this.zoomImageClose = this.findViewById(R.id.zoomImageClose);
        this.zoomImageClose.setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.language_setting).setOnClickListener(this);
        this.createLanguageDialog();
        this.createAddPictureDialog();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.takePicture) {
            this.takePicture();
        } else if (view.getId() == R.id.zoomImageClose) {
            this.zoomImageLayout.setVisibility(View.GONE);
            this.recycleBitmap();
        } else if (view.getId() == R.id.text_imageSwitch) {
            this.showAddPictureDialog();
        } else if (view.getId() == R.id.language_setting) {
            this.showLanguageDialog();
        } else if (view.getId() == R.id.simple_cn) {
            SharedPreferencesUtil.getInstance(this)
                    .putStringValue(Constant.POSITION_KEY, Constant.POSITION_CN);
            this.languageDialog.dismiss();
            this.restartLensEngine(Constant.POSITION_CN);
        } else if (view.getId() == R.id.english) {
            SharedPreferencesUtil.getInstance(this)
                    .putStringValue(Constant.POSITION_KEY, Constant.POSITION_EN);
            this.languageDialog.dismiss();
            this.preview.release();
            this.restartLensEngine(Constant.POSITION_EN);
        } else if (view.getId() == R.id.japanese) {
            SharedPreferencesUtil.getInstance(this)
                    .putStringValue(Constant.POSITION_KEY, Constant.POSITION_JA);
            this.languageDialog.dismiss();
            this.preview.release();
            this.restartLensEngine(Constant.POSITION_JA);
        } else if (view.getId() == R.id.korean) {
            SharedPreferencesUtil.getInstance(this)
                    .putStringValue(Constant.POSITION_KEY, Constant.POSITION_KO);
            this.languageDialog.dismiss();
            this.preview.release();
            this.restartLensEngine(Constant.POSITION_KO);
        } else if (view.getId() == R.id.latin) {
            SharedPreferencesUtil.getInstance(this)
                    .putStringValue(Constant.POSITION_KEY, Constant.POSITION_LA);
            this.languageDialog.dismiss();
            this.preview.release();
            this.restartLensEngine(Constant.POSITION_LA);
        } else if (view.getId() == R.id.back) {
            releaseLensEngine();
            this.finish();
        }
    }

    private void restartLensEngine(String type) {
        if (this.textType.equals(type)) {
            return;
        }
        this.lensEngine.release();
        this.lensEngine = null;
        this.createLensEngine();
        this.startLensEngine();
        if (this.lensEngine == null || this.lensEngine.getCamera() == null) {
            return;
        }
        this.mCamera = this.lensEngine.getCamera();
        try {
            this.mCamera.setPreviewDisplay(this.preview.getSurfaceHolder());
        } catch (IOException e) {
            Log.d(TextRecognitionActivity.TAG, "initViews IOException");
        }
    }

    @Override
    public void onBackPressed() {
        if (this.zoomImageLayout.getVisibility() == View.VISIBLE) {
            this.zoomImageLayout.setVisibility(View.GONE);
            this.recycleBitmap();
        } else {
            super.onBackPressed();
            releaseLensEngine();
        }
    }

    private void createLanguageDialog() {
        this.languageDialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_language_setting, null);
        // Set up a custom layout
        this.languageDialog.setContentView(view);
        this.textCN = view.findViewById(R.id.simple_cn);
        this.textCN.setOnClickListener(this);
        this.textEN = view.findViewById(R.id.english);
        this.textEN.setOnClickListener(this);
        this.textJN = view.findViewById(R.id.japanese);
        this.textJN.setOnClickListener(this);
        this.textKN = view.findViewById(R.id.korean);
        this.textKN.setOnClickListener(this);
        this.textLN = view.findViewById(R.id.latin);
        this.textLN.setOnClickListener(this);
        this.languageDialog.setCanceledOnTouchOutside(true);
        // Set the size of the dialog
        Window dialogWindow = this.languageDialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(layoutParams);
    }

    private void showLanguageDialog() {
        this.initDialogViews();
        this.languageDialog.show();
    }

    private void createAddPictureDialog() {
        this.addPictureDialog = new AddPictureDialog(this, AddPictureDialog.TYPE_NORMAL);
        final Intent intent = new Intent(TextRecognitionActivity.this, RemoteDetectionActivity.class);
        intent.putExtra(Constant.MODEL_TYPE, Constant.CLOUD_TEXT_DETECTION);
        this.addPictureDialog.setClickListener(new AddPictureDialog.ClickListener() {
            @Override
            public void takePicture() {
                lensEngine.release();
                isInitialization = false;
                intent.putExtra(Constant.ADD_PICTURE_TYPE, Constant.TYPE_TAKE_PHOTO);
                TextRecognitionActivity.this.startActivity(intent);
            }

            @Override
            public void selectImage() {
                intent.putExtra(Constant.ADD_PICTURE_TYPE, Constant.TYPE_SELECT_IMAGE);
                TextRecognitionActivity.this.startActivity(intent);
            }

            @Override
            public void doExtend() {

            }
        });
    }

    private void showAddPictureDialog() {
        this.addPictureDialog.show();
    }

    private void initDialogViews() {
        String position = SharedPreferencesUtil.getInstance(this).getStringValue(Constant.POSITION_KEY);
        this.textType = position;
        this.textCN.setSelected(false);
        this.textEN.setSelected(false);
        this.textJN.setSelected(false);
        this.textLN.setSelected(false);
        this.textKN.setSelected(false);
        switch (position) {
            case Constant.POSITION_CN:
                this.textCN.setSelected(true);
                break;
            case Constant.POSITION_EN:
                this.textEN.setSelected(true);
                break;
            case Constant.POSITION_LA:
                this.textLN.setSelected(true);
                break;
            case Constant.POSITION_JA:
                this.textJN.setSelected(true);
                break;
            case Constant.POSITION_KO:
                this.textKN.setSelected(true);
                break;
            default:
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constant.CAMERA_FACING, this.facing);
        super.onSaveInstanceState(outState);
    }


    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            this.localTextTransactor = new LocalTextTransactor(this.mHandler, this);
            this.lensEngine.setMachineLearningFrameTransactor(this.localTextTransactor);
            isInitialization = true;
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Can not create image transactor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, false);
            } catch (IOException e) {
                Log.e(TextRecognitionActivity.TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isInitialization){
            createLensEngine();
        }
        this.startLensEngine();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.preview.stop();
    }

    private void releaseLensEngine() {
        if (this.lensEngine != null) {
            this.lensEngine.release();
            this.lensEngine = null;
        }
        recycleBitmap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLensEngine();
    }

    private void recycleBitmap() {
        if (this.bitmap != null && !this.bitmap.isRecycled()) {
            this.bitmap.recycle();
            this.bitmap = null;
        }
        if (this.bitmapCopy != null && !this.bitmapCopy.isRecycled()) {
            this.bitmapCopy.recycle();
            this.bitmapCopy = null;
        }
    }

    private void takePicture() {
        this.zoomImageLayout.setVisibility(View.VISIBLE);
        LocalDataProcessor localDataProcessor = new LocalDataProcessor();
        localDataProcessor.setLandScape(this.isLandScape);
        this.bitmap = BitmapUtils.getBitmap(this.localTextTransactor.getTransactingImage(), this.localTextTransactor.getTransactingMetaData());

        float previewWidth = localDataProcessor.getMaxWidthOfImage(this.localTextTransactor.getTransactingMetaData());
        float previewHeight = localDataProcessor.getMaxHeightOfImage(this.localTextTransactor.getTransactingMetaData());
        if (this.isLandScape) {
            previewWidth = localDataProcessor.getMaxHeightOfImage(this.localTextTransactor.getTransactingMetaData());
            previewHeight = localDataProcessor.getMaxWidthOfImage(this.localTextTransactor.getTransactingMetaData());
        }
        this.bitmapCopy = Bitmap.createBitmap(this.bitmap).copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(this.bitmapCopy);
        float min = Math.min(previewWidth, previewHeight);
        float max = Math.max(previewWidth, previewHeight);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            localDataProcessor.setCameraInfo(this.graphicOverlay, canvas, min, max);
        } else {
            localDataProcessor.setCameraInfo(this.graphicOverlay, canvas, max, min);
        }
        localDataProcessor.drawHmsMLVisionText(canvas, this.localTextTransactor.getLastResults().getBlocks());
        this.zoomImageView.setImageBitmap(this.bitmapCopy);
    }
}
