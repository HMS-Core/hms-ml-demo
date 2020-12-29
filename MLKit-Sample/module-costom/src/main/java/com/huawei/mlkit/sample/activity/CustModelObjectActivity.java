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

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.custom.MLModelOutputs;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.camera.GraphicOverlay;
import com.huawei.mlkit.sample.camera.graphic.CustmodelObjectGraphic;
import com.huawei.mlkit.sample.camera.transactor.ImageTransactor;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.custom.R;
import com.huawei.mlkit.sample.entity.Recognition;
import com.huawei.mlkit.sample.model.InterpreterManager;
import com.huawei.mlkit.sample.model.ModelOperator;
import com.huawei.mlkit.sample.model.ObjectTfModel;
import com.huawei.mlkit.sample.utils.BitmapUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class CustModelObjectActivity extends BaseActivity implements View.OnClickListener, InterpreterManager.ExceutorResult {
    private static final String TAG = "CustModelObjectActivity";

    private LensEnginePreview mCamera_preview;
    private GraphicOverlay mCamera_overlay;

    private LensEngine mLensEngine;
    private InterpreterManager mInterpreterManager;
    private ModelOperator mModelOperator;
    private Integer sensorOrientation;
    private boolean rotated = false;

    private ArrayList<Recognition> recognitions;
    private CameraConfiguration cameraConfiguration;
    private FrameMetadata frameMetadata;
    private Matrix frameToCanvasMatrix;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private ImageTransactor imageTransactor;
    private Bitmap framBitmap;

    private static final String THREADNAME = "CustmodelThread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_model_object_result);
        findview();

        mModelOperator = new ObjectTfModel(this);
        MLApplication.initialize(getApplicationContext());
        mInterpreterManager = new InterpreterManager(mModelOperator, this);

    }

    private void findview() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        mCamera_preview = findViewById(R.id.camera_preview);
        mCamera_overlay = findViewById(R.id.camera_overlay);
        setStatusBarColor(this, R.color.black);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back || v.getId() == R.id.iv_back) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLens();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initLens();
        startLens();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLens();
    }

    private void initLens() {
        cameraConfiguration = new CameraConfiguration();
        mLensEngine = new LensEngine(this, cameraConfiguration, mCamera_overlay);
        imageTransactor = new ImageTransactor() {
            @Override
            public void process(final ByteBuffer data, final FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {

                if (!mInterpreterManager.isNeedFrame()) {
                    return;
                }

                com.huawei.mlkit.sample.activity.CustModelObjectActivity.this.frameMetadata = frameMetadata;
                mInterpreterManager.exec(BitmapUtils.getBitmap(data, frameMetadata));

            }

            @Override
            public void process(Bitmap bitmap, GraphicOverlay graphicOverlay) {
            }

            @Override
            public void stop() {
                mInterpreterManager.close();
            }

            @Override
            public boolean isFaceDetection() {
                return false;
            }
        };
        mLensEngine.setMachineLearningFrameTransactor(imageTransactor);
    }

    private void startLens() {
        try {
            mCamera_preview.start(mLensEngine, false);
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private void releaseLens() {
        if (mLensEngine != null)
            mLensEngine.release();
    }

    private void stopLens() {
        if (mLensEngine != null)
            mLensEngine.stop();
    }


    @Override
    public boolean onResult(MLModelOutputs mlModelOutputs) {
        recognitions = (ArrayList<Recognition>) mModelOperator.resultPostProcess(mlModelOutputs);
        showPreview();
        return true;
    }

    private void showPreview() {
        mCamera_overlay.clear();

        sensorOrientation = 90 * frameMetadata.getRotation();
        rotated = sensorOrientation % 180 == 90;

        frameToCropTransform = BitmapUtils.getTransformationMatrix(
                frameMetadata.getWidth(),
                frameMetadata.getHeight(),
                mModelOperator.getmBitmapSize(),
                mModelOperator.getmBitmapSize(),
                sensorOrientation, false);
        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        int mPreW = frameMetadata.getWidth();
        int mPreH = frameMetadata.getHeight();

        int overlayW = mCamera_overlay.getWidth();
        int overlayH = mCamera_overlay.getHeight();

        final float multiplier =
                Math.min(
                        overlayH / (float) (rotated ? mPreW : mPreH),
                        overlayW / (float) (rotated ? mPreH : mPreW));

        frameToCanvasMatrix =
                BitmapUtils.getTransformationMatrix(
                        mPreW,
                        mPreH,
                        (int) (multiplier * (rotated ? mPreH : mPreW)),
                        (int) (multiplier * (rotated ? mPreW : mPreH)),
                        sensorOrientation,
                        false);

        for (Recognition rec : recognitions) {
            if (rec.getConfidence() < 0.5) {
                continue;
            }
            RectF trackedPos = new RectF(rec.getLocation());
            cropToFrameTransform.mapRect(trackedPos);
            RectF lastRect = new RectF(trackedPos);
            frameToCanvasMatrix.mapRect(lastRect);
            rec.setLocation(lastRect);
            mCamera_overlay.addGraphic(new CustmodelObjectGraphic(mCamera_overlay, rec));
        }
        mCamera_overlay.postInvalidate();

    }
}
