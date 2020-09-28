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

package com.huawei.mlkit.sample.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.adapter.skeleton.GridItem;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.transactor.LocalSketlonTranstor;
import com.huawei.mlkit.sample.views.SwitchButton;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.lang.ref.WeakReference;

public final class HumanSkeletonActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    /**
     * Automatic photo
     */
    public static final int AUTO_TAKE_PHOTO = 101;

    /**
     * Refresh the interface
     */
    public static final int UPDATE_VIEW = 102;

    private static final String TAG = "HumanSkeletonActivity";

    private static boolean isOpenStatus = false;

    private static int mOrientation;

    private GraphicOverlay graphicOverlay;

    private SwitchButton switchButton;

    private Button selectTemplate;

    private Bitmap bitmap;

    private Bitmap bitmapCopy;

    private RelativeLayout zoomImageLayout;

    private ImageView zoomImageView;

    private ImageView templateImgView;

    private TextView similarityTv;

    private RelativeLayout similarityImageview;

    private OrientationEventListener mOrientationListener;

    private LensEngine lensEngine = null;

    private LensEnginePreview preview;

    private CameraConfiguration cameraConfiguration = null;

    private int facing = CameraConfiguration.CAMERA_FACING_BACK;

    private Camera mCamera;

    private LocalSketlonTranstor localSketlonTranstor;

    //  Whether the camera preview interface drawing is asynchronous, if synchronized, frame by frame will be very stuttered

    private static boolean isAsynchronous = true;

    // In the template (including the SDK's own and manually generated quantity)
    private static int mCount = 0;

   // Handler Message class

    private static class MsgHandler extends Handler {
        WeakReference<HumanSkeletonActivity> mMainActivityWeakReference;

        MsgHandler(HumanSkeletonActivity mainActivity) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HumanSkeletonActivity mainActivity = mMainActivityWeakReference.get();
            if (mainActivity == null) {
                return;
            }

            Log.d(TAG, "msg what :" + msg.what);
            if (msg.what == UPDATE_VIEW) {
                Bundle bundle = msg.getData();
                float result = bundle.getFloat("similarity");
                mainActivity.similarityTv.setVisibility(View.VISIBLE);
                mainActivity.similarityTv.setText("similarity:" + (int) (result * 100) + "%  ");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.setStatusBarColor(this, R.color.black);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_human_skeleton);
        HumanSkeletonActivity.setIsAsynchronous(false);
        preview = findViewById(R.id.firePreview);
        similarityTv = findViewById(R.id.tv_similarity);
        templateImgView = findViewById(R.id.template_image_view);
        similarityImageview = findViewById(R.id.similarity_imageview);
        zoomImageLayout = findViewById(R.id.zoomImageLayout);
        zoomImageView = findViewById(R.id.take_picture_overlay);
        selectTemplate = findViewById(R.id.select_template);
        switchButton = findViewById(R.id.switch_button_view);
        switchButton.setOnSwitchButtonStateChangeListener(new SwitchButton.OnSwitchButtonStateChangeListener() {
            @Override
            public void onSwitchButtonStateChange(boolean state) {
                if (state) {
                    isOpenStatus = true;
                    similarityImageview.setVisibility(View.VISIBLE);

                } else {
                    isOpenStatus = false;
                    similarityImageview.setVisibility(View.GONE);
                }
            }


        });
        findViewById(R.id.back).setOnClickListener(this);
        selectTemplate.setOnClickListener(this);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
        ToggleButton facingSwitch = findViewById(R.id.facingSwitch);
        facingSwitch.setOnCheckedChangeListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            facingSwitch.setVisibility(View.GONE);
        }

        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(CameraConfiguration.CAMERA_FACING_BACK);
        initOrientationListener();
        createLensEngine();

    }

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            MLSkeletonAnalyzerSetting setting = new MLSkeletonAnalyzerSetting.Factory().create();
            this.localSketlonTranstor = new LocalSketlonTranstor(setting, this);
            this.lensEngine.setMachineLearningFrameTransactor(localSketlonTranstor);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Can not create face detection transactor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, true);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    public static boolean isAsynchronous() {
        return isAsynchronous;
    }
    public static void setIsAsynchronous(boolean isAsynchronous) {
        HumanSkeletonActivity.isAsynchronous = isAsynchronous;
    }


    private void initOrientationListener() {
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                mOrientation = (orientation + 45) / 90;
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    public static int getOrientation() {
        return mOrientation;
    }

    public static void setOrientation(int orientation) {
        HumanSkeletonActivity.mOrientation = orientation;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.select_template) {
            Intent intent = new Intent(HumanSkeletonActivity.this, TemplateActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.back) {
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
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
                Log.d(TAG, "initViews IOException");
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
        createLensEngine();
        startLensEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startLensEngine();

        // After returning to the main page, if there is a value selected in the template, it is displayed, otherwise the value of the default template key0 is displayed
        if (TemplateActivity.getSelectedIndex() != -1) {
            GridItem mlSkeleton =
                    TemplateActivity.getTemplateDataMap().get("key" + TemplateActivity.getSelectedIndex());
            if (mlSkeleton != null) {
                templateImgView.setImageBitmap(mlSkeleton.getBitmap());
            }
        } else {
            GridItem skeleton = TemplateActivity.getTemplateDataMap().get("key0");
            if (skeleton != null) {
                templateImgView.setImageBitmap(skeleton.getBitmap());
            }
        }
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

        isOpenStatus = false;
        HumanSkeletonActivity.setIsAsynchronous(false);
        mOrientationListener.disable();
    }

    public static boolean isOpenStatus() {
        return isOpenStatus;
    }


    private void recycleBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        if (bitmapCopy != null && !bitmapCopy.isRecycled()) {
            bitmapCopy.recycle();
            bitmapCopy = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (zoomImageLayout.getVisibility() == View.VISIBLE) {
            zoomImageLayout.setVisibility(View.GONE);
            recycleBitmap();
            startLensEngine();
        } else {
            super.onBackPressed();
        }
    }
}
