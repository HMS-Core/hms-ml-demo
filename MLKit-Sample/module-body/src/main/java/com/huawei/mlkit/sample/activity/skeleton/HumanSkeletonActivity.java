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

package com.huawei.mlkit.sample.activity.skeleton;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import static com.huawei.mlkit.sample.activity.BaseActivity.*;

import static com.huawei.mlkit.sample.activity.skeleton.TemplateActivity.getSelectedIndex;

/**
 *  HumanSkeleton
 *
 * @since  2020-12-10
 */
public final class HumanSkeletonActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "HumanSkeletonActivity";
    /**
     * Refresh the interface
     */
    public static final int UPDATE_VIEW = 102;

    public static final String SIMILARITY = "similarity";

    private static boolean isSimilarityChecked = false;

    private static boolean isYogaChecked = false;

    private GraphicOverlay graphicOverlay;

    private Button templateSelect;

    private ImageView templateImgView;

    private TextView templateSimilarityTextView;

    private RelativeLayout templateSimilarityImageLayout;

    private LensEngine lensEngine = null;

    private LensEnginePreview preview;

    private CameraConfiguration cameraConfiguration = null;

    private int facing = CameraConfiguration.CAMERA_FACING_BACK;

    private Camera mCamera;

    private LocalSketlonTranstor localSketlonTranstor;

    private Handler mHandler = new MsgHandler(this);

    //  Whether the camera preview interface drawing is asynchronous, if synchronized, frame by frame will be very stuttered
    private static boolean isAsynchronous = true;

    // Handler Message, to display similarity.
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

            Log.i(TAG, "msg what :" + msg.what);
            if (msg.what == UPDATE_VIEW) {
                Bundle bundle = msg.getData();
                float result = bundle.getFloat(SIMILARITY);
                mainActivity.templateSimilarityTextView.setText("similarity:" + (int) (result * 100) + "%  ");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.black);
        setContentView(R.layout.activity_human_skeleton);
        setIsAsynchronous(false);
        preview = findViewById(R.id.firePreview);
        templateSimilarityTextView = findViewById(R.id.tv_similarity);
        templateImgView = findViewById(R.id.template_image_view);
        templateSimilarityImageLayout = findViewById(R.id.similarity_layout);
        templateSelect = findViewById(R.id.select_template);
        SwitchButton similarityButton = findViewById(R.id.switch_button_similarity);
        SwitchButton yogaButton = findViewById(R.id.switch_button_yoga);
        initSwitchListener(similarityButton, yogaButton);
        similarityButton.setCurrentState(this.isSimilarityChecked);
        yogaButton.setCurrentState(this.isYogaChecked);

        findViewById(R.id.back).setOnClickListener(this);
        templateSelect.setOnClickListener(this);
        graphicOverlay = findViewById(R.id.fireOverlay);
        ToggleButton facingSwitch = findViewById(R.id.facingSwitch);
        facingSwitch.setOnCheckedChangeListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            facingSwitch.setVisibility(View.GONE);
        }

        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(CameraConfiguration.CAMERA_FACING_BACK);
        createLensEngineAndAnalyzer(isYogaChecked);
    }

    private void initSwitchListener(SwitchButton similarityButton, SwitchButton yogaButton) {
        similarityButton.setOnSwitchButtonStateChangeListener(state -> {
            isSimilarityChecked = state;
            if (isSimilarityChecked) {
                templateSimilarityImageLayout.setVisibility(View.VISIBLE);
            } else {
                templateSimilarityImageLayout.setVisibility(View.GONE);
            }
        });
        yogaButton.setOnSwitchButtonStateChangeListener(state -> {
            isYogaChecked = state;
            Log.i(TAG, isYogaChecked + "");
            this.preview.stop();
            createLensEngineAndAnalyzer(isYogaChecked);
            reStartLensEngine();
        });
    }

    private void createLensEngineAndAnalyzer(boolean isYogaChecked) {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        MLSkeletonAnalyzerSetting setting;
        if (isYogaChecked) {
            setting = new MLSkeletonAnalyzerSetting.Factory()
                    // Set analyzer mode.
                    // MLSkeletonAnalyzerSetting.TYPE_NORMAL:Detect skeleton corresponding to common human posture.
                    // MLSkeletonAnalyzerSetting.TYPE_YOGA：Detect skeleton points corresponding to yoga posture.
                    .setAnalyzerType(MLSkeletonAnalyzerSetting.TYPE_YOGA)
                    .create();
            Log.i(TAG, "yogamode");
        } else {
            setting = new MLSkeletonAnalyzerSetting.Factory().create();
            Log.i(TAG, "skeletonmode");
        }

        this.localSketlonTranstor = new LocalSketlonTranstor(setting, this, mHandler);
        this.lensEngine.setMachineLearningFrameTransactor(localSketlonTranstor);
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

    public static boolean isAsynchronous() {
        return isAsynchronous;
    }

    public static void setIsAsynchronous(boolean isAsynchronous) {
        HumanSkeletonActivity.isAsynchronous = isAsynchronous;
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
        createLensEngineAndAnalyzer(isYogaChecked);
        startLensEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        startLensEngine();

        // After returning to the main page, if there is a value selected in the template, it is displayed, otherwise the value of the default template key0 is displayed
        if (getSelectedIndex() != -1) {
            GridItem mlSkeleton =
                    TemplateActivity.getTemplateDataMap().get(TemplateActivity.getKey() + getSelectedIndex());
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

        isSimilarityChecked = false;
        setIsAsynchronous(false);
    }

    public static boolean isOpenStatus() {
        return isSimilarityChecked;
    }

    public static boolean isYogaChecked() {
        return isYogaChecked;
    }
}
