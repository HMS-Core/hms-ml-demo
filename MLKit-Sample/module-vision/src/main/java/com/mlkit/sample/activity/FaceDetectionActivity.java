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

package com.mlkit.sample.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mlkit.sample.R;
import com.mlkit.sample.camera.CameraConfiguration;
import com.mlkit.sample.camera.LensEngine;
import com.mlkit.sample.camera.LensEnginePreview;
import com.mlkit.sample.transactor.LocalFaceTransactor;
import com.mlkit.sample.util.Constant;
import com.mlkit.sample.views.SwitchButton;
import com.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;

import java.io.IOException;

public final class FaceDetectionActivity extends BaseActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, SwitchButton.OnSwitchButtonStateChangeListener {
    private static final String TAG = "FaceDetectionActivity";
    private static final String OPEN_STATUS = "open_status";
    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;
    private SwitchButton switchButton;
    private ToggleButton facingSwitch;
    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;
    private boolean isOpenStatus = false;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_face_detection);
        if (savedInstanceState != null) {
            this.facing = savedInstanceState.getInt(Constant.CAMERA_FACING);
            this.isOpenStatus = savedInstanceState.getBoolean(FaceDetectionActivity.OPEN_STATUS);
        }
        this.preview = this.findViewById(R.id.face_preview);
        this.findViewById(R.id.face_back).setOnClickListener(this);
        this.facingSwitch = this.findViewById(R.id.face_facingSwitch);
        this.facingSwitch.setOnCheckedChangeListener(this);

        if (Camera.getNumberOfCameras() == 1) {
            this.facingSwitch.setVisibility(View.GONE);
        }
        this.graphicOverlay = this.findViewById(R.id.face_overlay);
        this.switchButton = this.findViewById(R.id.switch_button_view);
        this.switchButton.setOnSwitchButtonStateChangeListener(this);
        this.switchButton.setCurrentState(this.isOpenStatus);
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.createLensEngine();
        this.startLensEngine();
        this.setStatusBar();
    }

    private void setDetectorOptions(boolean isOpen) {
        MLFaceAnalyzerSetting detectorOptions;
        if (isOpen) {
            detectorOptions = new MLFaceAnalyzerSetting.Factory()
                    .setFeatureType(MLFaceAnalyzerSetting.TYPE_PRECISION)
                    .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                    .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                    .allowTracing()
                    .create();

        } else {
            detectorOptions = new MLFaceAnalyzerSetting.Factory()
                    .setFeatureType(MLFaceAnalyzerSetting.TYPE_UNSUPPORT_FEATURES)
                    .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                    .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                    .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                    .allowTracing()
                    .create();
        }
        this.lensEngine.setMachineLearningFrameTransactor(new LocalFaceTransactor(detectorOptions, this.getApplicationContext(), isOpen));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.face_back) {
            this.finish();
        }
    }

    @Override
    public void onSwitchButtonStateChange(boolean state) {
        this.isOpenStatus = state;
        this.setDetectorOptions(state);
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
        reStartLensEngine();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constant.CAMERA_FACING, this.facing);
        outState.putBoolean(FaceDetectionActivity.OPEN_STATUS, this.isOpenStatus);
        super.onSaveInstanceState(outState);
    }

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            this.setDetectorOptions(this.isOpenStatus);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Can not create face detection transactor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void reStartLensEngine(){
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

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, true);
            } catch (IOException e) {
                Log.e(FaceDetectionActivity.TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.reStartLensEngine();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.lensEngine != null) {
            this.lensEngine.release();
        }
        this.facing = CameraConfiguration.CAMERA_FACING_BACK;
        this.cameraConfiguration.setCameraFacing(this.facing);
    }
}

