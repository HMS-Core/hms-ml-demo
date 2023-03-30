/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.mlkit.sample.activity.interactivelivenessdetection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlsdk.interactiveliveness.MLInteractiveLivenessCapture;
import com.huawei.hms.mlsdk.interactiveliveness.MLInteractiveLivenessCaptureConfig;
import com.huawei.hms.mlsdk.interactiveliveness.MLInteractiveLivenessCaptureError;
import com.huawei.hms.mlsdk.interactiveliveness.MLInteractiveLivenessCaptureResult;
import com.huawei.hms.mlsdk.interactiveliveness.action.InteractiveLivenessStateCode;
import com.huawei.hms.mlsdk.interactiveliveness.action.MLInteractiveLivenessConfig;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.util.InteractiveLivenessDetectionResultEnum;
import com.huawei.mlkit.sample.views.MaskingView;

/**
 * InteractiveLivenessDetectionActivity
 */
public class InteractiveLivenessDetectionActivity extends AppCompatActivity {
    private static final String TAG = InteractiveLivenessDetectionActivity.class.getSimpleName();

    public static InteractiveLivenessDetectionActivity instance = null;

    private static final long TIME_OUT_THRESHOLD = 10000L;

    private Button retestButton;
    private Button exitButton;
    private ImageView faceSuccessImageView;
    private ImageView resultImageView;
    private ImageView mDetectionFailedImageView;
    private ImageView mBackImage;
    private MaskingView mMaskingView;
    private TextView mTextSuccess;
    private TextView mTextFail;
    private TextView mTextFailResult;

    private Bitmap mBitmap;

    private int times = 0;
    private long startTime;
    private long endTime;
    private Boolean isAllActionCorrect = false;
    private Boolean isCustom = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_detection);
        instance = this;
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.livenessbg));
        }

        retestButton = (Button) findViewById(R.id.retest_button);
        exitButton = (Button) findViewById(R.id.exit_button);
        faceSuccessImageView = (ImageView) findViewById(R.id.imageView);
        resultImageView = (ImageView) findViewById(R.id.success_image);
        mDetectionFailedImageView = (ImageView) findViewById(R.id.detection_failed_imageView);
        retestButton.setOnClickListener(new ClickListenerImpl());
        exitButton.setOnClickListener(new ClickListenerImpl());
        mBackImage = (ImageView) findViewById(R.id.img_back);
        mBackImage.setOnClickListener(new ClickListenerImpl());
        mMaskingView = (MaskingView) findViewById(R.id.masking_view);
        mTextSuccess = (TextView) findViewById(R.id.textView);
        mTextFail = (TextView) findViewById(R.id.detection_failed_textView);
        mTextFailResult = (TextView) findViewById(R.id.failure_cause_textView);

        startCaptureActivity();
    }

    private class ClickListenerImpl implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.retest_button:
                    startCaptureActivity();
                    break;

                case R.id.exit_button:
                    Intent intentExit = new Intent(InteractiveLivenessDetectionActivity.this, LivenessActivity.class);
                    startActivity(intentExit);
                    break;

                case R.id.img_back:
                    onBackPressed();
                    break;

                default:
                    break;
            }
        }
    }

    private void startCaptureActivity() {
        MLInteractiveLivenessConfig interactiveLivenessConfig = new MLInteractiveLivenessConfig.Builder().build();

        MLInteractiveLivenessCaptureConfig captureConfig =
                new MLInteractiveLivenessCaptureConfig.Builder()
                        .setOptions(MLInteractiveLivenessCaptureConfig.DETECT_MASK)
                        .setActionConfig(interactiveLivenessConfig)
                        .setDetectionTimeOut(TIME_OUT_THRESHOLD)
                        .build();
        MLInteractiveLivenessCapture capture = MLInteractiveLivenessCapture.getInstance();
        capture.setConfig(captureConfig);
        startTime = System.currentTimeMillis();
        capture.startDetect(this, this.callback);
    }

    private MLInteractiveLivenessCapture.Callback callback =
            new MLInteractiveLivenessCapture.Callback() {
                @Override
                public void onSuccess(MLInteractiveLivenessCaptureResult result) {
                    Log.e(TAG, "Success detection, Thread id is: " + Thread.currentThread().getId());
                    Log.e(TAG, "result.getStateCode(): " + result.getStateCode());
                    endTime = System.currentTimeMillis();
                    long delayTime = endTime - startTime;
                    Log.d(TAG, "Detection time is: " + delayTime);
                    Log.d(TAG, "Result stateCode is: " + result.getStateCode());
                    if (result.getBitmap() != null) {
                        Log.d(TAG, "result.getBitmap is not null");
                        mBitmap = result.getBitmap();
                    }
                    handleDetectionResult(result, mBitmap);
                }

                private void handleDetectionResult(MLInteractiveLivenessCaptureResult result, Bitmap faceBitmap) {
                    int detectionResult = result.getStateCode();
                    int actionStringId = MLInteractiveLivenessConfig.getActionDescByType(result.getActionType());
                    InteractiveLivenessDetectionResultEnum.defaultDetectionFailResultProcess(
                            mTextFailResult, detectionResult, actionStringId, false);
                    if (result.getStateCode() == InteractiveLivenessStateCode.ALL_ACTION_CORRECT) {
                        faceSuccessImageView.setImageBitmap(faceBitmap);
                        resultImageView.setImageResource(R.drawable.ic_public_todo_succeed);
                        mMaskingView.setVisibility(View.VISIBLE);
                        mDetectionFailedImageView.setVisibility(View.INVISIBLE);
                        faceSuccessImageView.setVisibility(View.VISIBLE);
                        mTextSuccess.setVisibility(View.VISIBLE);
                        mTextFail.setVisibility(View.INVISIBLE);
                        mTextFailResult.setVisibility(View.INVISIBLE);
                    } else {
                        mDetectionFailedImageView.setImageResource(R.mipmap.detectionfailed);
                        resultImageView.setImageResource(R.drawable.ic_public_close_filled);
                        mMaskingView.setVisibility(View.INVISIBLE);
                        mDetectionFailedImageView.setVisibility(View.VISIBLE);
                        faceSuccessImageView.setVisibility(View.INVISIBLE);
                        mTextSuccess.setVisibility(View.INVISIBLE);
                        mTextFail.setVisibility(View.VISIBLE);
                        mTextFailResult.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(int errorCode) {
                    Log.d(TAG, "Fail detection, Thread id is: " + Thread.currentThread().getId());
                    String failResult = null;
                    switch (errorCode) {
                        case MLInteractiveLivenessCaptureError.CAMERA_NO_PERMISSION:
                            failResult = "The camera permission is not obtained.";
                            break;

                        case MLInteractiveLivenessCaptureError.CAMERA_START_FAILED:
                            failResult = "Failed to start the camera.";
                            break;

                        case MLInteractiveLivenessCaptureError.DETECT_FACE_TIME_OUT:
                            failResult = "Detection timed out by the face detection module.";
                            break;

                        case MLInteractiveLivenessCaptureError.USER_CANCEL:
                            finish();
                            break;
                        case -1 :
                            failResult = "Initialization failed.";
                            finish();
                            break;
                        case - 6001:
                            failResult = "The offline usage exceeds the threshold.";
                            finish();
                            break;
                        case -6002:
                            failResult = "The offline time exceeds the threshold.";
                            finish();
                            break;
                        case -5001:
                            failResult = "Payment is not enabled, and the free quota is used up.";
                            finish();
                            break;
                        case -5002:
                            failResult = "Account Arrears.";
                            finish();
                            break;
                        case -5003:
                            failResult = "Blacklist.";
                            finish();
                            break;
                        default:
                            break;
                    }
                }
            };

    private boolean isFinish(int detectionResult) {
        return detectionResult == InteractiveLivenessStateCode.ALL_ACTION_CORRECT
                || detectionResult == InteractiveLivenessStateCode.SPOOFING
                || detectionResult == InteractiveLivenessStateCode.ACTION_MUTUALLY_EXCLUSIVE_ERROR
                || detectionResult == InteractiveLivenessStateCode.RESULT_TIME_OUT
                || detectionResult == InteractiveLivenessStateCode.CONTINUITY_DETECTION_ERROR;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
