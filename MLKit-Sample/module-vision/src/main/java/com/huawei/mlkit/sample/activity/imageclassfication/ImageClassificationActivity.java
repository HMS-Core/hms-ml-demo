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

package com.huawei.mlkit.sample.activity.imageclassfication;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.activity.RemoteDetectionActivity;
import com.huawei.mlkit.sample.activity.dialog.AddPictureDialog;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;

import com.huawei.mlkit.sample.transactor.LocalImageClassificationTransactor;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;

import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

public final class ImageClassificationActivity extends BaseActivity
        implements OnRequestPermissionsResultCallback,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "ClassificationActivity";
    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;
    private ToggleButton facingSwitch;
    private ImageButton imageSwitch;
    CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;
    private Camera mCamera;
    private AddPictureDialog addPictureDialog;
    private boolean isInitialization = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_classification);
        if (savedInstanceState != null) {
            this.facing = savedInstanceState.getInt(Constant.CAMERA_FACING);
        }
        this.preview = this.findViewById(R.id.classification_preview);
        this.findViewById(R.id.classification_back).setOnClickListener(this);
        this.imageSwitch = this.findViewById(R.id.imageSwitch);
        this.imageSwitch.setOnClickListener(this);
        this.graphicOverlay = this.findViewById(R.id.classification_overlay);
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.facingSwitch = this.findViewById(R.id.classification_facingSwitch);
        this.facingSwitch.setOnCheckedChangeListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            this.facingSwitch.setVisibility(View.GONE);
        }
        this.createDialog();
        this.createLensEngine();
        this.setStatusBar();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.classification_back) {
            releaseLensEngine();
            this.finish();
        } else if (view.getId() == R.id.imageSwitch) {
            showDialog();
        }
    }

    private void createDialog() {
        this.addPictureDialog = new AddPictureDialog(this);
        final Intent intent = new Intent(ImageClassificationActivity.this, RemoteDetectionActivity.class);
        intent.putExtra(Constant.MODEL_TYPE, Constant.CLOUD_IMAGE_CLASSIFICATION);
        this.addPictureDialog.setClickListener(new AddPictureDialog.ClickListener() {
            @Override
            public void takePicture() {
                lensEngine.release();
                isInitialization = false;
                intent.putExtra(Constant.ADD_PICTURE_TYPE, Constant.TYPE_TAKE_PHOTO);
                ImageClassificationActivity.this.startActivity(intent);
            }

            @Override
            public void selectImage() {
                intent.putExtra(Constant.ADD_PICTURE_TYPE, Constant.TYPE_SELECT_IMAGE);
                ImageClassificationActivity.this.startActivity(intent);
            }

            @Override
            public void doExtend() {

            }
        });
    }

    private void showDialog() {
        this.addPictureDialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (this.lensEngine != null) {
            if (isChecked) {
                this.facing = CameraConfiguration.CAMERA_FACING_FRONT;
                this.cameraConfiguration.setCameraFacing(this.facing);
            } else {
                this.facing = CameraConfiguration.CAMERA_FACING_BACK;
                this.cameraConfiguration.setCameraFacing(this.facing);
            }
        }
        this.preview.stop();
        restartLensEngine();
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
            this.lensEngine.setMachineLearningFrameTransactor(new LocalImageClassificationTransactor(this.getApplicationContext()));
            isInitialization = true;
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Can not create image transactor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void restartLensEngine() {
        this.startLensEngine();
        if (null != this.lensEngine) {
            this.mCamera = this.lensEngine.getCamera();
            try {
                this.mCamera.setPreviewDisplay(this.preview.getSurfaceHolder());
            } catch (IOException e) {
                Log.d(ImageClassificationActivity.TAG, "initViews IOException");
            }
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, false);
            } catch (IOException e) {
                Log.e(ImageClassificationActivity.TAG, "Unable to start lensEngine.", e);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseLensEngine();
    }

    private void releaseLensEngine(){
        if (this.lensEngine != null) {
            this.lensEngine.release();
            this.lensEngine = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLensEngine();
    }
}

