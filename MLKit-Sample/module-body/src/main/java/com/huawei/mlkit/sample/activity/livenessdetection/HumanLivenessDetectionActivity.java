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

package com.huawei.mlkit.sample.activity.livenessdetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult;
import com.huawei.mlkit.sample.R;

import java.text.BreakIterator;

public class HumanLivenessDetectionActivity extends AppCompatActivity {
    private static final String TAG = HumanLivenessDetectionActivity.class.getSimpleName();

    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};

    private static final int RC_CAMERA_AND_EXTERNAL_STORAGE = 0x01 << 8;

    private Button mBtn;
    private Button mCustomBtn;
    private static TextView mTextResult;
    private static ImageView mImageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_liveness_detection);

        mBtn = findViewById(R.id.capture_btn);
        mCustomBtn = findViewById(R.id.custom_btn);
        mTextResult = findViewById(R.id.text_detect_result);
        mImageResult = findViewById(R.id.img_detect_result);

        mBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(
                                        HumanLivenessDetectionActivity.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            startCaptureActivity();
                            return;
                        }
                        ActivityCompat.requestPermissions(
                                HumanLivenessDetectionActivity.this, PERMISSIONS, RC_CAMERA_AND_EXTERNAL_STORAGE);
                    }
                });

        mCustomBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(
                                HumanLivenessDetectionActivity.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            startCustomActivity();
                            return;
                        }
                        ActivityCompat.requestPermissions(
                                HumanLivenessDetectionActivity.this, PERMISSIONS, RC_CAMERA_AND_EXTERNAL_STORAGE);
                    }
                });
    }

    private void startCaptureActivity() {
        MLLivenessCapture capture = MLLivenessCapture.getInstance();
        capture.startDetect(this, this.callback);
    }

    private void startCustomActivity() {
        Intent intent = new Intent(this, LivenessCustomDetectionActivity.class);
        this.startActivity(intent);
    }

    private MLLivenessCapture.Callback callback =
            new MLLivenessCapture.Callback() {
                @Override
                public void onSuccess(MLLivenessCaptureResult result) {
                    String [] temp = null;
                    String live = result.toString();
                    String  sub = live.substring(live.indexOf("{"), live.lastIndexOf("}"));
                    temp = sub.split(",");
                    mTextResult.setText("Liveness Detection Result: "+temp[0]+"}");
                    mTextResult.setBackgroundResource(result.isLive() ? R.drawable.bg_blue : R.drawable.bg_red);
                    mImageResult.setImageBitmap(result.getBitmap());
                }

                @Override
                public void onFailure(int errorCode) {
                    mTextResult.setText("errorCode:" + errorCode);
                }
            };

    public static MLLivenessCapture.Callback customCallback = new MLLivenessCapture.Callback() {
        /**
         * Liveness detection success callback.
         * @param result result
         */
        @Override
        public void onSuccess(MLLivenessCaptureResult result) {
            String [] temp = null;
            String live = result.toString();
            String  sub = live.substring(live.indexOf("{"), live.lastIndexOf("}"));
            temp = sub.split(",");
            mTextResult.setText("Live or Not : "+temp[0]+"}");
            mTextResult.setBackgroundResource(result.isLive() ? R.drawable.bg_blue : R.drawable.bg_red);
            mImageResult.setImageBitmap(result.getBitmap());
        }

        @Override
        public void onFailure(int errorCode) {
            mTextResult.setText("errorCode:" + errorCode);
        }
    };

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult ");
        if (requestCode == RC_CAMERA_AND_EXTERNAL_STORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCaptureActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i(TAG, "onActivityResult requestCode " + requestCode + ", resultCode " + resultCode);
    }
}
