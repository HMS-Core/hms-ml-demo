/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.mlkit.sample.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.camera.CameraGLSurfaceView;
import com.huawei.mlkit.sample.camera.EGLCamera;
import com.huawei.mlkit.sample.views.SwitchButton;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;

import java.util.ArrayList;
import java.util.List;

public final class FaceDetectionActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener,
        SwitchButton.OnSwitchButtonStateChangeListener {
    private static final String TAG = "FaceDetectionActivity";
    private static final String OPEN_STATUS = "open_status";
    private static final int PERMISSION_REQUESTS = 1;
    public boolean drawFacePoints = false;
    private EGLCamera eglCamera = null;
    private CameraGLSurfaceView mCameraView;
    private SwitchButton switchButton;
    private ToggleButton facingSwitch;
    private MLFaceAnalyzer detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        mCameraView = findViewById(R.id.face_preview);
        eglCamera = mCameraView.getEGLCamera();
        if (savedInstanceState != null) {
            this.drawFacePoints = savedInstanceState.getBoolean(FaceDetectionActivity.OPEN_STATUS);
        }

        this.findViewById(R.id.face_back).setOnClickListener(this);
        this.facingSwitch = this.findViewById(R.id.face_facingSwitch);
        this.facingSwitch.setOnCheckedChangeListener(this);

        if (Camera.getNumberOfCameras() == 1) {
            this.facingSwitch.setVisibility(View.GONE);
        }

        this.switchButton = this.findViewById(R.id.switch_button_view);
        this.switchButton.setOnSwitchButtonStateChangeListener(this);
        this.switchButton.setCurrentState(this.drawFacePoints);

        MLFaceAnalyzerSetting detectorOptions;
        detectorOptions = new MLFaceAnalyzerSetting.Factory()
                .setFeatureType(MLFaceAnalyzerSetting.TYPE_UNSUPPORT_FEATURES)
                .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                .allowTracing(MLFaceAnalyzerSetting.MODE_TRACING_FAST)
                .create();
        detector = MLAnalyzerFactory.getInstance().getFaceAnalyzer(detectorOptions);
        eglCamera.setDetector(detector);
        mCameraView.showFacePoints(drawFacePoints);
        if (!this.allPermissionsGranted()) {
            this.getRuntimePermissions();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.face_back) {
            this.finish();
        }
    }

    @Override
    public void onSwitchButtonStateChange(boolean state) {
        this.drawFacePoints = state;
        if (mCameraView != null)
            mCameraView.showFacePoints(drawFacePoints);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        eglCamera.switchCamera();
        if (mCameraView != null)
            eglCamera.startPreview(mCameraView.getSurfaceTexture());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FaceDetectionActivity.OPEN_STATUS, this.drawFacePoints);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eglCamera.releaseCamera();
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : this.getRequiredPermissions()) {
            if (!FaceDetectionActivity.isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : this.getRequiredPermissions()) {
            if (!FaceDetectionActivity.isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), FaceDetectionActivity.PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(FaceDetectionActivity.TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(FaceDetectionActivity.TAG, "Permission NOT granted: " + permission);
        return false;
    }
}

