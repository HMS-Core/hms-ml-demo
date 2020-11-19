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


package com.huawei.hms.mlkit.sample.activity;

import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.res.ResourcesCompat;

import com.huawei.hms.mlkit.sample.camera.CameraConfiguration;
import com.huawei.hms.mlkit.sample.camera.LensEngine;
import com.huawei.hms.mlkit.sample.camera.LensEnginePreview;
import com.huawei.hms.mlkit.sample.cn.R;
import com.huawei.hms.mlkit.sample.transactor.FaceDetectionProcessor;
import com.huawei.hms.mlkit.sample.transactor.ModelGameOneProcessor;
import com.huawei.hms.mlkit.sample.views.CustomDialog;
import com.huawei.hms.mlkit.sample.views.GridItem;
import com.huawei.hms.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.huawei.hms.mlsdk.skeleton.MLJoint;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class ModelGameStartOneActivity extends BaseActivity
        implements OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "ModelGameStartOne";

    public static final int AUTO_START = 101;

    public static final int UPDATE_SCORES_VIEW = ModelGameOneProcessor.UPDATE_SCORES_VIEW;

    public static final int UPDATE_SIMILARITY_VIEW = 102;

    private static int mOrientation;

    private LensEnginePreview preview;

    private GraphicOverlay graphicOverlay;

    private TextView infoTxtView;

    private Handler mHandler = new MsgHandler(this);

    private ImageView templateImgView;

    private TextView similarityTv;

    private CustomDialog mCustomDialog;

    private android.widget.LinearLayout linearLayout;

    private OrientationEventListener mOrientationListener;

    private ImageView iv_preview;

    private ImageView iv_result;

    private TextView tvCountDown;

    private ImageView imCountDown;

    private TextView iv_time;

    private ConstraintLayout cl_result_main;

    private ImageView iv_ok_fail;

    private TextView tv_result;

    private RelativeLayout rl_model_bg;

    private ImageView iv_result_success;

    private ConstraintLayout cl_result_content;

    private TextView tv_result_content;

    private static boolean isStartStatus = false;

    private Animation animation1;

    private AnimationSet animationSet;

    private AnimationSet animationSet1;

    private Animation im_animation;

    private int count_time;

    private int mTotal_level = 7;

    private final int repeatCount = 3;

    private CameraConfiguration cameraConfiguration = null;

    private LensEngine lensEngine = null;

    public static float[][] SKELETON_DATA1 = {{550.35333f, 550.1141f}, {441.23962f, 697.07214f}, {366.67883f, 623.22125f}
            , {735.02826f, 549.95483f}, {770.57355f, 698.27966f}, {771.85065f, 845.1564f}, {549.988f, 844.82605f}, {440.8803f, 1065.3068f}
            , {441.07526f, 1285.3752f}, {660.91174f, 881.2363f}, {624.3671f, 1100.4437f}, {588.1999f, 1248.9507f}
            , {623.9152f, 373.71536f}, {624.9052f, 513.67206f}};

    public static float[][] SKELETON_DATA2 = {{514.9476f, 330.68948f}, {368.32486f, 476.59866f}, {294.37695f, 330.0497f}
            , {735.0521f, 330.1429f}, {770.8379f, 549.56946f}, {771.2041f, 697.72314f}, {514.297f, 697.40265f}, {477.29898f, 953.8659f}
            , {405.01035f, 1138.9143f}, {660.9089f, 698.2858f}, {660.30066f, 954.771f}, {660.3182f, 1174.7828f}
            , {623.8705f, 130.47752f}, {624.19604f, 293.05368f}};

    public static float[][] SKELETON_DATA3 = {{513.6042f, 550.3417f}, {366.22253f, 513.6976f}, {256.19427f, 403.2271f}
            , {697.1588f, 477.23648f}, {808.6501f, 330.03894f}, {807.56586f, 147.13832f}, {551.08997f, 881.5079f}, {403.11227f, 1100.2458f}
            , {441.2785f, 1321.3439f}, {698.3421f, 881.52405f}, {772.08997f, 1137.9535f}, {882.037f, 1322.1995f}
            , {515.14856f, 332.67096f}, {586.70325f, 478.1015f}};

    public static float[][] SKELETON_DATA5 = {{403.62494f, 403.90762f}, {221.25839f, 477.55713f}, {183.41634f, 330.69208f}
            , {588.5649f, 439.87463f}, {734.39636f, 622.95905f}, {880.844f, 733.06604f}, {404.21228f, 807.3434f}, {256.11603f, 992.1823f}
            , {294.36746f, 1211.6254f}, {586.7537f, 807.4777f}, {661.41583f, 1027.4777f}, {770.67114f, 1211.8186f}
            , {545.5843f, 244.38123f}, {513.6136f, 403.08554f}};

    public static float[][] SKELETON_DATA6 = {{257.44775f, 845.67065f}, {293.6812f, 955.66144f}, {293.65222f, 1101.2375f}
            , {220.14081f, 624.4273f}, {293.37067f, 440.39276f}, {293.27716f, 220.86852f}, {478.04263f, 844.50195f}, {404.85175f, 1027.7692f}
            , {294.8457f, 1211.5898f}, {624.598f, 771.39343f}, {697.9851f, 1027.9076f}, {734.4559f, 1248.6893f}
            , {89.56189f, 796.7244f}, {220.2685f, 770.8063f}};

    public static float[][] SKELETON_DATA8 = {{514.0497f, 403.65686f}, {367.35315f, 549.9428f}, {294.2942f, 697.2243f}
            , {661.50165f, 476.39307f}, {772.4453f, 551.5649f}, {808.66113f, 440.2379f}, {441.51505f, 771.42993f}, {441.74405f, 1063.7181f}
            , {256.48672f, 1102.3064f}, {551.8719f, 807.54395f}, {550.27954f, 1064.0021f}, {477.76935f, 1284.1041f}
            , {661.74884f, 256.11816f}, {623.61926f, 403.4606f}};

    public static float[][] SKELETON_DATA9 = {{513.1971f, 477.52966f}, {330.95474f, 549.74695f}, {219.47269f, 550.7715f}
            , {661.7305f, 476.14587f}, {807.7426f, 476.87665f}, {954.5131f, 476.92596f}, {513.60376f, 771.534f}, {293.8743f, 881.61017f}
            , {403.4485f, 1100.822f}, {624.40656f, 808.46814f}, {587.58795f, 1027.9064f}, {551.49457f, 1247.2305f}
            , {553.61664f, 306.09204f}, {587.02814f, 441.5858f}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_model_game);
        ChooserActivity.setIsAsynchronous(false);
        mCustomDialog = new CustomDialog(this);
        preview = findViewById(R.id.firePreview);
        similarityTv = findViewById(R.id.tv_similarity);
        templateImgView = findViewById(R.id.template_image_view);
        iv_preview = findViewById(R.id.iv_preview);
        iv_result = findViewById(R.id.iv_result);

        tvCountDown = findViewById(R.id.tvCountDown);
        imCountDown = findViewById(R.id.imCountDown);
        iv_time = findViewById(R.id.iv_time);
        cl_result_main = findViewById(R.id.cl_result_main);
        iv_ok_fail = findViewById(R.id.iv_ok_fail);
        tv_result = findViewById(R.id.tv_result);
        rl_model_bg = findViewById(R.id.rl_model_bg);
        iv_result_success = findViewById(R.id.iv_result_success);

        cl_result_content = findViewById(R.id.cl_result_content);
        tv_result_content = findViewById(R.id.tv_result_content);

        TextView tv_ok = findViewById(R.id.tv_ok);
        TextView tv_cancel = findViewById(R.id.tv_cancel);
        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        im_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_model_daojishi);
        im_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imCountDown.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imCountDown.setVisibility(View.GONE);
                rl_model_bg.startAnimation(animation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatMode(Animation.RESTART);
        alphaAnimation.setRepeatCount(repeatCount - 1);
        alphaAnimation.setInterpolator(new LinearInterpolator());

        final AlphaAnimation alphaAnimation1 = new AlphaAnimation(1, 0);
        alphaAnimation1.setDuration(1000);
        alphaAnimation1.setRepeatMode(Animation.RESTART);
        int repeatCount1 = 9;
        alphaAnimation1.setRepeatCount(repeatCount1);
        alphaAnimation1.setInterpolator(new LinearInterpolator());

        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatMode(Animation.RESTART);
        scaleAnimation.setRepeatCount(repeatCount);
        scaleAnimation.setInterpolator(new LinearInterpolator());

        animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        animationSet1 = new AnimationSet(false);
        animationSet1.addAnimation(alphaAnimation1);

        alphaAnimation1.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isStartStatus = true;
                tvCountDown.setVisibility(View.GONE);
                iv_time.setVisibility(View.VISIBLE);
                iv_time.setText("" + count_time);
                count_time--;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                count_time = 9;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                iv_time.setText("" + count_time);
                count_time--;
                if (count_time == -1) {
                    isStartStatus = false;
                    count_time = 9;
                    iv_time.clearAnimation();

                    rl_model_bg.clearAnimation();
                    rl_model_bg.invalidate();
                    rl_model_bg.setVisibility(View.GONE);
                    iv_time.setVisibility(View.GONE);
                    cl_result_main.setVisibility(View.VISIBLE);
                    iv_ok_fail.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shibai, null));
                    switch (mTotal_level) {
                        case 7:
                            tv_result_content.setText(R.string.game_zero_des);
                            iv_result.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.paizhaogpognzuo1, null));
                            tv_result.setText(R.string.game_one);
                            break;
                        case 6:
                            tv_result_content.setText(R.string.game_one_des);
                            iv_result.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.paizhaogpognzuo2, null));
                            tv_result.setText(R.string.game_two);
                            break;
                        case 5:
                            tv_result_content.setText(R.string.game_two_des);
                            iv_result.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.paizhaogpognzuo3, null));
                            tv_result.setText(R.string.game_three);
                            break;
                        case 4:
                            tv_result_content.setText(R.string.game_tree_des);
                            iv_result.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.paizhaogpognzuo5, null));
                            tv_result.setText(R.string.game_four);
                            break;
                        case 3:
                            tv_result_content.setText(R.string.game_four_des);
                            iv_result.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.paizhaogpognzuo6, null));
                            tv_result.setText(R.string.game_five);
                            break;
                        case 2:
                            tv_result_content.setText(R.string.game_five_des);
                            iv_result.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.paizhaogpognzuo8, null));
                            tv_result.setText(R.string.game_six);
                            break;
                        case 1:
                            tv_result_content.setText(R.string.game_six_des);
                            iv_result.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.paizhaogpognzuo9, null));
                            tv_result.setText(R.string.game_seven);
                            break;
                        default:
                            break;
                    }
                    cl_result_content.setVisibility(View.VISIBLE);
                }
            }
        });

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            int count = repeatCount;

            @Override
            public void onAnimationStart(Animation animation) {
                tvCountDown.setVisibility(View.VISIBLE);
                tvCountDown.setText("" + count);
                count--;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvCountDown.setVisibility(View.GONE);
                count_time = 9;
                count = 3;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                tvCountDown.setText("" + count);
                count--;
            }
        });

        animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_model_main);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rl_model_bg.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.daojishi_bg, null));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_time.startAnimation(animationSet1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tvCountDown.startAnimation(animationSet);
        imCountDown.startAnimation(im_animation);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.model_title).setOnClickListener(this);
        ToggleButton toggleButton = findViewById(R.id.camera_takePicture);
        toggleButton.setOnClickListener(this);
        Button modifyThreshold = findViewById(R.id.threshold_mod);
        modifyThreshold.setOnClickListener(this);
        getTemplateData(SKELETON_DATA1);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
        infoTxtView = findViewById(R.id.live_info_txt);
        ToggleButton facingSwitch = findViewById(R.id.facingSwitch);
        linearLayout = findViewById(R.id.linear_views);
        facingSwitch.setOnCheckedChangeListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            facingSwitch.setVisibility(View.GONE);
        }

        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(CameraConfiguration.CAMERA_FACING_FRONT);

        initOrientationListener();
        createLensEngine();
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
        ModelGameStartOneActivity.mOrientation = orientation;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back || view.getId() == R.id.model_title) {
            linearLayout.setVisibility(View.GONE);
            finish();
        } else if (view.getId() == R.id.threshold_mod) {
            mCustomDialog.show();
        } else if (view.getId() == R.id.tv_ok) {
            finish();
        } else if (view.getId() == R.id.tv_cancel) {
            finish();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (this.lensEngine != null) {
            int facing;
            if (!isChecked) {
                facing = CameraConfiguration.CAMERA_FACING_FRONT;
                this.cameraConfiguration.setCameraFacing(facing);
            } else {
                facing = CameraConfiguration.CAMERA_FACING_BACK;
                this.cameraConfiguration.setCameraFacing(facing);
            }
        }
        preview.stop();
        reStartLensEngine();
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

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            MLFaceAnalyzerSetting setting = new MLFaceAnalyzerSetting.Factory()
                    .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                    .setTracingAllowed(true, MLFaceAnalyzerSetting.MODE_TRACING_FAST)
                    .setFeatureType(MLFaceAnalyzerSetting.TYPE_UNSUPPORT_FEATURES)
                    .setKeyPointType(MLFaceAnalyzerSetting.TYPE_UNSUPPORT_KEYPOINTS)
                    .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                    .setPoseDisabled(true)
                    .create();

            FaceDetectionProcessor faceDetectionProcessor = new FaceDetectionProcessor(mHandler, setting);

            ModelGameOneProcessor modelGameOneProcessor = new ModelGameOneProcessor(mHandler, this);
            this.lensEngine.setMachineLearningFrameTransactor(modelGameOneProcessor, faceDetectionProcessor);
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

    private void reStartLensEngine() {
        startLensEngine();
        if (null != this.lensEngine) {
            Camera mCamera = this.lensEngine.getCamera();
            try {
                mCamera.setPreviewTexture(this.preview.getSurfaceTexture());
            } catch (IOException e) {
                Log.d(TAG, "initViews IOException");
            }
        }
    }

    private void releaseLensEngine() {
        if (this.lensEngine != null) {
            this.lensEngine.release();
            this.lensEngine = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startLensEngine();
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
    protected void onStop() {
        super.onStop();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseLensEngine();
        ChooserActivity.setIsAsynchronous(false);
        mOrientationListener.disable();
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        preview.stop();
        createLensEngine();
        startLensEngine();
    }

    private static class MsgHandler extends Handler {
        WeakReference<ModelGameStartOneActivity> mMainActivityWeakReference;

        MsgHandler(ModelGameStartOneActivity mainActivity) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final ModelGameStartOneActivity mainActivity = mMainActivityWeakReference.get();
            if (mainActivity == null) {
                return;
            }
            Log.d(TAG, "msg what :" + msg.what);
            switch (msg.what) {
                case AUTO_START:
                    Log.i(TAG, "AUTO_START Start.");
                    isStartStatus = false;
                    mainActivity.iv_time.clearAnimation();
                    mainActivity.count_time = 9;
                    mainActivity.rl_model_bg.clearAnimation();
                    mainActivity.rl_model_bg.invalidate();
                    mainActivity.rl_model_bg.setVisibility(View.GONE);
                    mainActivity.iv_time.setVisibility(View.GONE);
                    mainActivity.cl_result_main.setVisibility(View.VISIBLE);
                    mainActivity.iv_ok_fail.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.chenggong, null));

                    Log.i(TAG, "mainActivity.mTotal_level value is : " + mainActivity.mTotal_level);
                    switch (mainActivity.mTotal_level) {
                        case 7:
                            mainActivity.getTemplateData(SKELETON_DATA2);
                            mainActivity.tv_result.setText(R.string.game_one);
                            mainActivity.iv_result.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo1, null));
                            mainActivity.iv_preview.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo2, null));
                            break;
                        case 6:
                            mainActivity.getTemplateData(SKELETON_DATA3);
                            mainActivity.tv_result.setText(R.string.game_two);
                            mainActivity.iv_result.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo2, null));
                            mainActivity.iv_preview.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo3, null));
                            break;
                        case 5:
                            mainActivity.getTemplateData(SKELETON_DATA5);
                            mainActivity.tv_result.setText(R.string.game_three);
                            mainActivity.iv_result.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo3, null));
                            mainActivity.iv_preview.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo5, null));
                            break;
                        case 4:
                            mainActivity.getTemplateData(SKELETON_DATA6);
                            mainActivity.tv_result.setText(R.string.game_four);
                            mainActivity.iv_result.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo5, null));
                            mainActivity.iv_preview.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo6, null));
                            break;
                        case 3:
                            mainActivity.getTemplateData(SKELETON_DATA8);
                            mainActivity.tv_result.setText(R.string.game_five);
                            mainActivity.iv_result.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo6, null));
                            mainActivity.iv_preview.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo8, null));
                            break;
                        case 2:
                            mainActivity.getTemplateData(SKELETON_DATA9);
                            mainActivity.tv_result.setText(R.string.game_six);
                            mainActivity.iv_result.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo8, null));
                            mainActivity.iv_preview.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo9, null));
                            break;
                        case 1:
                            mainActivity.tv_result_content.setText(R.string.game_seven_des);
                            mainActivity.getTemplateData(SKELETON_DATA1);
                            mainActivity.tv_result.setText(R.string.game_seven);
                            mainActivity.iv_result.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo9, null));
                            mainActivity.iv_preview.setImageDrawable(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.paizhaogpognzuo1, null));
                            break;
                        default:
                            break;
                    }
                    mainActivity.iv_result_success.setVisibility(View.VISIBLE);

                    mainActivity.tvCountDown.clearAnimation();
                    mainActivity.imCountDown.clearAnimation();
                    mainActivity.mTotal_level--;

                    if (mainActivity.mTotal_level > 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.iv_result_success.setVisibility(View.GONE);
                                mainActivity.rl_model_bg.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.model_bg, null));
                                mainActivity.rl_model_bg.setVisibility(View.VISIBLE);

                                mainActivity.tvCountDown.startAnimation(mainActivity.animationSet);
                                mainActivity.imCountDown.startAnimation(mainActivity.im_animation);
                            }
                        }, 3000);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.rl_model_bg.setBackground(ResourcesCompat.getDrawable(mainActivity.getResources(), R.drawable.daojishi_bg, null));
                            }
                        }, 6000);
                    }

                    if (mainActivity.mTotal_level == 0) {
                        mainActivity.iv_result_success.setVisibility(View.GONE);
                        mainActivity.cl_result_content.setVisibility(View.VISIBLE);
                    }

                    break;
                case UPDATE_SIMILARITY_VIEW:
                    Bundle bundle = msg.getData();
                    float result = bundle.getFloat("similarity");
                    mainActivity.similarityTv.setVisibility(View.VISIBLE);
                    mainActivity.similarityTv.setText("similarity:" + (int) (result * 100) + "%  ");
                    break;
                case UPDATE_SCORES_VIEW:
                    String infoStr = (msg.obj == null) ? null : msg.obj.toString();
                    mainActivity.infoTxtView.setText(infoStr);
                    if (infoStr == null || infoStr.isEmpty()) {
                        mainActivity.infoTxtView.setVisibility(View.GONE);
                    } else {
                        mainActivity.infoTxtView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static boolean isStartStatus() {
        return isStartStatus;
    }

    private void getTemplateData(float[][] SKELETON_DATA) {
        List<MLJoint> mlJointList = new ArrayList<>();
        int type = 100;
        for (int i = 0; i < SKELETON_DATA.length; i++) {
            type++;
            MLJoint mlJoint = new MLJoint(SKELETON_DATA[i][0], SKELETON_DATA[i][1], type, 1.0f);
            mlJointList.add(mlJoint);
        }

        List<MLSkeleton> list = new ArrayList<>();
        list.add(new MLSkeleton(mlJointList));

        GridItem gridItem = new GridItem();

        gridItem.setSkeletonList(list);
        TemplateActivity.getTemplateDataMap().put("key" + 0, gridItem);
    }
}
