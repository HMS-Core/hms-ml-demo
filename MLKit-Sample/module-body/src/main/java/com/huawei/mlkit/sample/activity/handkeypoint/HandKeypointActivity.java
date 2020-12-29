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

package com.huawei.mlkit.sample.activity.handkeypoint;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerSetting;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.transactor.HandKeypointTransactor;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.views.AddPictureDialog;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.huawei.mlkit.sample.activity.BaseActivity.*;


/**
 *  HandKeypointActivity
 *
 * @since  2020-12-10
 */
public final class HandKeypointActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "HumanSkeletonActivity";
    /**
     * Refresh the interface
     */
    private GraphicOverlay graphicOverlay;

    private LensEngine lensEngine = null;

    private LensEnginePreview preview;

    private CameraConfiguration cameraConfiguration = null;

    private int facing = CameraConfiguration.CAMERA_FACING_BACK;

    private Camera mCamera;

    private HandKeypointTransactor localHandTranstor;

    private static final int MAXHANDRESULTS = 2;

    private ImageView imageSwitch;

    private Handler mHandler = new MsgHandler(this);

    private AddPictureDialog addPictureDialog;

    private static class MsgHandler extends Handler {
        WeakReference<HandKeypointActivity> mMainActivityWeakReference;

        MsgHandler(HandKeypointActivity mainActivity) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HandKeypointActivity mainActivity = mMainActivityWeakReference.get();
            if (mainActivity == null) {
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.black);
        setContentView(R.layout.activity_handkeypoint);
        preview = findViewById(R.id.preview);
        findViewById(R.id.back).setOnClickListener(this);
        graphicOverlay = findViewById(R.id.overlay);
        this.imageSwitch = this.findViewById(R.id.static_pic);
        this.imageSwitch.setOnClickListener(this);
        ToggleButton facingSwitch = findViewById(R.id.facingSwitch);
        facingSwitch.setOnCheckedChangeListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            facingSwitch.setVisibility(View.GONE);
        }

        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(CameraConfiguration.CAMERA_FACING_BACK);
        this.cameraConfiguration.setCameraFacing(CameraConfiguration.CAMERA_FACING_BACK);
        this.createDialog();
        createLensEngineAndAnalyzer();
    }

    private void createLensEngineAndAnalyzer() {
        MLHandKeypointAnalyzerSetting setting;
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        setting = new MLHandKeypointAnalyzerSetting
                .Factory()
                .setSceneType(MLHandKeypointAnalyzerSetting.TYPE_ALL)
                .setMaxHandResults(MAXHANDRESULTS)
                .create();

        this.localHandTranstor = new HandKeypointTransactor(setting, this, mHandler);
        this.lensEngine.setMachineLearningFrameTransactor(localHandTranstor);
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, true);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start lensEngine." + e.getMessage());
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.static_pic) {
            showDialog();
        } else if (view.getId() == R.id.back) {
            finish();
        }
    }

    private void createDialog() {
        this.addPictureDialog = new AddPictureDialog(this);
        final Intent intent = new Intent(HandKeypointActivity.this, HandKeypointImageActivity.class);
        intent.putExtra(Constant.MODEL_TYPE, Constant.CLOUD_IMAGE_CLASSIFICATION);
        this.addPictureDialog.setClickListener(new AddPictureDialog.ClickListener() {
            @Override
            public void takePicture() {
                intent.putExtra(Constant.ADD_PICTURE_TYPE, Constant.TYPE_TAKE_PHOTO);
                preview.stop();
                HandKeypointActivity.this.startActivity(intent);
            }

            @Override
            public void selectImage() {
                intent.putExtra(Constant.ADD_PICTURE_TYPE, Constant.TYPE_SELECT_IMAGE);
                preview.stop();
                HandKeypointActivity.this.startActivity(intent);
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
        Log.i(TAG, "Set facing");
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

    private void reStartLensEngine() {
        startLensEngine();
        if (null != this.lensEngine) {
            this.mCamera = this.lensEngine.getCamera();
            try {
                this.mCamera.setPreviewTexture(this.preview.getSurfaceTexture());
            } catch (IOException e) {
                Log.i(TAG, "initViews IOException, " + e.getMessage());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        preview.stop();
        createLensEngineAndAnalyzer();
        startLensEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        startLensEngine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (lensEngine != null) {
            lensEngine.release();
        }
    }

}
