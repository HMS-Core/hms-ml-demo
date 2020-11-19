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

package com.huawei.mlkit.sample.activity.object;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.transactor.LocalObjectTransactor;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzerSetting;

import java.io.IOException;

public final class ObjectDetectionActivity extends BaseActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "ObjectDetectionActivity";

    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;
    private ToggleButton facingSwitch;
    private Camera mCamera;
    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStatusBar();
        this.setContentView(R.layout.activity_object_detection);
        if (savedInstanceState != null) {
            this.facing = savedInstanceState.getInt(Constant.CAMERA_FACING);
        }
        this.preview = this.findViewById(R.id.object_preview);
        this.findViewById(R.id.object_back).setOnClickListener(this);
        this.facingSwitch = this.findViewById(R.id.object_facingSwitch);
        this.facingSwitch.setOnCheckedChangeListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            this.facingSwitch.setVisibility(View.GONE);
        }
        this.graphicOverlay = this.findViewById(R.id.object_overlay);
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.createLensEngine();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.object_back) {
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
        reStartLensEngine();
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
            MLObjectAnalyzerSetting options = new MLObjectAnalyzerSetting.Factory()
                    .setAnalyzerType(MLObjectAnalyzerSetting.TYPE_VIDEO)
                    .allowMultiResults()
                    .allowClassification().create();
            this.lensEngine.setMachineLearningFrameTransactor(new LocalObjectTransactor(options));
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Can not create face detection transactor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void reStartLensEngine() {
        this.startLensEngine();
        if (null != this.lensEngine) {
            this.mCamera = this.lensEngine.getCamera();
            try {
                this.mCamera.setPreviewDisplay(this.preview.getSurfaceHolder());
            } catch (IOException e) {
                Log.d(ObjectDetectionActivity.TAG, "initViews IOException");
            }
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, false);
            } catch (IOException e) {
                Log.e(ObjectDetectionActivity.TAG, "Unable to start lensEngine.", e);
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
