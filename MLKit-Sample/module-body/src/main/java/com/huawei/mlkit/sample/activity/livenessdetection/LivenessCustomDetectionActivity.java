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

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessDetectView;
import com.huawei.hms.mlsdk.livenessdetection.OnMLLivenessDetectCallback;
import com.huawei.mlkit.sample.R;

import static com.huawei.hms.mlsdk.livenessdetection.MLLivenessDetectView.DETECT_MASK;

/**
 * Custom Liveness
 *
 * @since  2020-12-10
 */
public class LivenessCustomDetectionActivity extends Activity {

    private MLLivenessDetectView mlLivenessDetectView;
    private FrameLayout mPreviewContainer;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_custom_detection);
        mPreviewContainer = findViewById(R.id.surface_layout);
        img_back = findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Obtain MLLivenessDetectView
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;

        mlLivenessDetectView = new MLLivenessDetectView.Builder()
                .setContext(this)
                .setOptions(DETECT_MASK)
                // set Rect of face frame relative to surface in layout
                .setFaceFrameRect(new Rect(0, 0, widthPixels,dip2px(this,480) ))
                .setDetectCallback(new OnMLLivenessDetectCallback() {
                    @Override
                    public void onCompleted(MLLivenessCaptureResult result) {
                        HumanLivenessDetectionActivity.customCallback.onSuccess(result);
                        finish();
                    }

                    @Override
                    public void onError(int error) {
                        HumanLivenessDetectionActivity.customCallback.onFailure(error);
                        finish();
                    }

                    public void onInfo(int infoCode, Bundle bundle) {

                    }

                    @Override
                    public void onStateChange(int state, Bundle bundle) {

                    }
                }).build();

        mPreviewContainer.addView(mlLivenessDetectView);
        mlLivenessDetectView.onCreate(savedInstanceState);
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlLivenessDetectView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mlLivenessDetectView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mlLivenessDetectView.onResume();
    }


}
