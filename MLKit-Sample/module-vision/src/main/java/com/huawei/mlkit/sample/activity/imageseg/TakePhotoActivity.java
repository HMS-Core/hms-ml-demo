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

package com.huawei.mlkit.sample.activity.imageseg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.FileProvider;

import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationScene;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.callback.ImageSegmentationResultCallBack;
import com.huawei.mlkit.sample.callback.ImageUtilCallBack;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.transactor.ImageSegmentationTransactor;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.util.ImageUtils;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * It is applied to the image segmentation function. The application scenario is: open the camera,
 * if there is a human body in the picture, then cut out the human body and replace the background
 * to achieve the real-time human detection effect.
 *
 * @since 2019-12-26
 */
public class TakePhotoActivity extends BaseActivity
        implements CompoundButton.OnCheckedChangeListener, ImageSegmentationResultCallBack, View.OnClickListener {
    private LensEngine lensEngine = null;

    private LensEnginePreview preview;

    private GraphicOverlay graphicOverlay;

    private ToggleButton facingSwitch;

    private ImageButton img_takePhoto;

    private ImageButton img_pic;

    private ImageButton img_back;

    private CameraConfiguration cameraConfiguration = null;

    private static String TAG = "TakePhotoActivity";

    private int index;

    private int facing = CameraConfiguration.CAMERA_FACING_FRONT;

    private Bitmap background;

    private Bitmap processImage;

    private ImageSegmentationTransactor transactor;

    private MLImageSegmentationSetting setting;

    private String imgPath;

    private Camera mCamera;

    private Boolean isBlur = false;

    private RenderScript renderScript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_take_photo);
        if (savedInstanceState != null) {
            this.facing = savedInstanceState.getInt(Constant.CAMERA_FACING);
        }
        Intent intent = this.getIntent();
        try {
            this.index = intent.getIntExtra(Constant.VALUE_KEY, -1);
        } catch (RuntimeException e) {
            Log.e(TAG, "Get intent value failed:" + e.getMessage());
        }
        if (this.index < 0) {
            Toast.makeText(this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
            this.finish();
        } else {
            // Decode background image.
            int id = Constant.IMAGES[this.index];
            InputStream is = this.getResources().openRawResource(id);
            this.background = BitmapFactory.decodeStream(is);
        }
        this.initView();
        this.initAction();
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.cameraConfiguration.setFps(6.0f);
        this.cameraConfiguration.setPreviewWidth(CameraConfiguration.DEFAULT_WIDTH);
        this.cameraConfiguration.setPreviewHeight(CameraConfiguration.DEFAULT_HEIGHT);
        this.createLensEngine();
        renderScript = RenderScript.create(this);
    }

    private void initView() {
        this.preview = this.findViewById(R.id.firePreview);
        this.graphicOverlay = this.findViewById(R.id.fireFaceOverlay);
        this.facingSwitch = this.findViewById(R.id.facingSwitch);
        if (Camera.getNumberOfCameras() == 1) {
            this.facingSwitch.setVisibility(View.GONE);
        }
        this.img_takePhoto = this.findViewById(R.id.img_takePhoto);
        this.img_pic = this.findViewById(R.id.img_pic);
        this.img_back = this.findViewById(R.id.back);
    }

    private void initAction() {
        this.facingSwitch.setOnCheckedChangeListener(this);
        img_back.setOnClickListener(this);
        img_pic.setOnClickListener(this);
        // Set the display effect when the takePhoto button is clicked.
        this.img_takePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    TakePhotoActivity.this.img_takePhoto.setColorFilter(Color.GRAY);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    TakePhotoActivity.this.img_takePhoto.setColorFilter(Color.WHITE);
                }
                return false;
            }
        });
        this.img_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save Picture.
                if (TakePhotoActivity.this.processImage == null) {
                    Log.e(TakePhotoActivity.TAG, "The image is null, unable to save.");
                } else {
                    // save current image to gallery.
                    ImageUtils imageUtils = new ImageUtils(TakePhotoActivity.this.getApplicationContext());
                    imageUtils.setImageUtilCallBack(new ImageUtilCallBack() {
                        @Override
                        public void callSavePath(String path) {
                            TakePhotoActivity.this.imgPath = path;
                            Log.i(TakePhotoActivity.TAG, "PATH:" + path);
                        }
                    });
                    imageUtils.saveToAlbum(TakePhotoActivity.this.processImage);
                    Matrix matrix = new Matrix();
                    matrix.postScale(0.3f, 0.3f);
                    Bitmap resizedBitmap = Bitmap.createBitmap(TakePhotoActivity.this.processImage, 0, 0, TakePhotoActivity.this.processImage.getWidth(), TakePhotoActivity.this.processImage.getHeight(), matrix, true);
                    TakePhotoActivity.this.img_pic.setImageBitmap(resizedBitmap);
                }
            }
        });

        findViewById(R.id.bt_blur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBlur = !isBlur;
                if(transactor != null){
                    transactor.setBlur(isBlur);
                    transactor.setRenderScript(renderScript);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            releaseLensEngine();
            finish();
        } else if (view.getId() == R.id.img_pic) {
            if (imgPath == null) {
                Toast.makeText(getApplicationContext(), "please save a picture", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                File imgFile = new File(this.imgPath);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    Uri imgUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", imgFile);
                    Log.i(TakePhotoActivity.TAG, "image uri:" + imgUri.toString());
                    intent.setDataAndType(imgUri, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                this.startActivity(intent);
            }
        }
    }

    private void createLensEngine() {
        // If there's no existing lensEngine, create one.
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            this.setting = new MLImageSegmentationSetting.Factory()
                    .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
                    .setExact(false)
                    .setScene(MLImageSegmentationScene.FOREGROUND_ONLY)
                    .create();
            this.transactor = new ImageSegmentationTransactor(this.getApplicationContext(), this.setting, this.background);
            this.transactor.setImageSegmentationResultCallBack(this);
            this.lensEngine.setMachineLearningFrameTransactor(this.transactor);
        } catch (Exception e) {
            Log.e(TakePhotoActivity.TAG, "Can not create image transactor: " + e);
            Toast.makeText(
                    this.getApplicationContext(),
                    "Can not create image transactor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                if (null != this.preview) {
                    this.preview.start(this.lensEngine, true);
                }
            } catch (IOException e) {
                Log.e(TakePhotoActivity.TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
                this.imgPath = null;
            }
        }
    }

    private void restartLensEngine() {
        this.startLensEngine();
        if (null != this.lensEngine) {
            this.mCamera = this.lensEngine.getCamera();
            try {
                this.mCamera.setPreviewTexture(this.preview.getSurfaceTexture());
            } catch (IOException e) {
                Log.d(TAG, "initViews IOException");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constant.CAMERA_FACING, this.facing);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TakePhotoActivity.TAG, "Set facing");
        if (this.lensEngine != null) {
            if (!isChecked) {
                this.facing = CameraConfiguration.CAMERA_FACING_FRONT;
            } else {
                this.facing = CameraConfiguration.CAMERA_FACING_BACK;
            }
            this.cameraConfiguration.setCameraFacing(this.facing);
            this.setting = new MLImageSegmentationSetting.Factory()
                    .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
                    .create();
            this.transactor = new ImageSegmentationTransactor(this.getApplicationContext(), this.setting, this.background);
            this.transactor.setImageSegmentationResultCallBack(this);
            this.lensEngine.setMachineLearningFrameTransactor(this.transactor);
        }
        this.preview.stop();
        restartLensEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.startLensEngine();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.preview.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseLensEngine();
    }

    private void releaseLensEngine() {
        if (this.lensEngine != null) {
            this.lensEngine.release();
            this.lensEngine = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLensEngine();

        if(transactor != null){
            transactor.setBlur(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            renderScript.releaseAllContexts();
        }else {
            renderScript.finish();
        }
    }

    @Override
    public void callResultBitmap(Bitmap bitmap) {
        this.processImage = bitmap;
    }
}
