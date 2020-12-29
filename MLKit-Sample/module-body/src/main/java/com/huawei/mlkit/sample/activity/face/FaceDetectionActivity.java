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

package com.huawei.mlkit.sample.activity.face;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.transactor.LocalFaceTransactor;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.views.SwitchButton;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;

public final class FaceDetectionActivity extends BaseActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "FaceDetectionActivity";
    private static final String OPEN_STATUS = "open_status";
    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;
    private SwitchButton switchFaceFeature;
    private SwitchButton switchFacePoints;
    private ToggleButton facingSwitch;
    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;
    private boolean isFacePointsChecked = false;
    private boolean isFaceFeatureChecked = false;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_face_detection);
        if (savedInstanceState != null) {
            this.facing = savedInstanceState.getInt(Constant.CAMERA_FACING);
            this.isFacePointsChecked = savedInstanceState.getBoolean(FaceDetectionActivity.OPEN_STATUS);
            this.isFaceFeatureChecked = savedInstanceState.getBoolean(FaceDetectionActivity.OPEN_STATUS);
        }
        this.preview = this.findViewById(R.id.face_preview);
        this.findViewById(R.id.face_back).setOnClickListener(this);
        this.facingSwitch = this.findViewById(R.id.face_facingSwitch);
        this.facingSwitch.setOnCheckedChangeListener(this);

        if (Camera.getNumberOfCameras() == 1) {
            this.facingSwitch.setVisibility(View.GONE);
        }
        this.graphicOverlay = this.findViewById(R.id.face_overlay);
        this.switchFacePoints = this.findViewById(R.id.switch_button_point);
        clickPoints();
        this.switchFacePoints.setCurrentState(this.isFacePointsChecked);

        this.switchFaceFeature = this.findViewById(R.id.switch_button_feature);
        clickFeature();
        this.switchFaceFeature.setCurrentState(this.isFaceFeatureChecked);

        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.createLensEngine();
        this.setStatusBar();
    }

    private void clickFeature() {
        this.switchFaceFeature.setOnSwitchButtonStateChangeListener(state -> {
            isFaceFeatureChecked = state;
            setAnalyzer();
        });
    }

    private void clickPoints() {
        this.switchFacePoints.setOnSwitchButtonStateChangeListener(state -> {
            isFacePointsChecked = state;
            setAnalyzer();
        });
    }

    private void setAnalyzer() {
        int featureType = MLFaceAnalyzerSetting.TYPE_UNSUPPORT_FEATURES;
        int pointsType = MLFaceAnalyzerSetting.TYPE_UNSUPPORT_KEYPOINTS;
        int shapeType = MLFaceAnalyzerSetting.TYPE_UNSUPPORT_SHAPES;
        if (isFacePointsChecked) {
            pointsType = MLFaceAnalyzerSetting.TYPE_KEYPOINTS;
            shapeType = MLFaceAnalyzerSetting.TYPE_SHAPES;
        }
        if (isFaceFeatureChecked) {
            featureType = MLFaceAnalyzerSetting.TYPE_FEATURES;
        }
        Log.i(TAG, "face analyzer recreate, isFacePointsChecked = " + isFacePointsChecked + "; isFaceFeatureChecked = " + isFaceFeatureChecked);
        // Create a face analyzer. You can create an analyzer using the provided customized face detection parameter
        MLFaceAnalyzerSetting mlFaceAnalyzerSetting = new MLFaceAnalyzerSetting.Factory()
                .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                .setFeatureType(featureType)
                .setKeyPointType(pointsType)
                .setShapeType(shapeType)
                .setPoseDisabled(false)
                .create();

        this.lensEngine.setMachineLearningFrameTransactor(new LocalFaceTransactor(mlFaceAnalyzerSetting, getApplicationContext()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.face_back) {
            releaseLensEngine();
            this.finish();
        }
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
        this.reStartLensEngine();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constant.CAMERA_FACING, this.facing);
        outState.putBoolean(FaceDetectionActivity.OPEN_STATUS, this.isFacePointsChecked);
        outState.putBoolean(FaceDetectionActivity.OPEN_STATUS, this.isFaceFeatureChecked);
        super.onSaveInstanceState(outState);
    }

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            setAnalyzer();
        } catch (Exception e) {
            Log.e(TAG, "createLensEngine IOException." + e.getMessage());
        }
    }

    private void reStartLensEngine() {
        this.startLensEngine();
        if (null != this.lensEngine) {
            this.mCamera = this.lensEngine.getCamera();
            try {
                this.mCamera.setPreviewTexture(this.preview.getSurfaceTexture());
            } catch (IOException e) {
                Log.e(TAG, "initViews IOException." + e.getMessage());
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
    }
}

