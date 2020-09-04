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
package com.huawei.mlkit.sample;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int PERMISSION_REQUESTS = 1;
    private SurfaceView surfaceView;
    private Camera camera;
    private boolean isPreview = false;
    private String title;

    private int regexpnumber;
    private String regexp = "^[A-Za-z0-9]{16}";
    private String type;

    private int width;
    private int height;
    private MLTextAnalyzer analyzer;
    private LensEngine lensEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (!this.allPermissionsGranted()) {
            this.getRuntimePermissions();
        }

        init();
        initial();
    }

    private void init() {
        surfaceView = findViewById(R.id.surface_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type", "zh");
            regexpnumber = bundle.getInt("regexpnumber", 16);
            title = bundle.getString("title");
        }
        regexp = "^[A-Za-z0-9]{" + regexpnumber + "}";

        if (Camera.getNumberOfCameras() <= 0) {
            return;
        }

        camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        width = parameters.getPreviewSize().width;
        height = parameters.getPreviewSize().height;
        camera.release();

    }

    private void initial() {
        surfaceView.getHolder().addCallback(new SurfaceViewCallback());
        initVideoEngine(this, type);
        setTransactor(this);
    }

    private boolean allPermissionsGranted() {
        for (String permission : this.getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
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

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            // Log.i(StartActivity.TAG, "Permission granted: " + permission);
            return true;
        }
        // Log.i(StartActivity.TAG, "Permission NOT granted: " + permission);
        return false;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : this.getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private class SurfaceViewCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e(TAG, "surfaceCreated");
            initLensEngine(CameraActivity.this, width, height);

            runLensEngine(holder);

            isPreview = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e(TAG, "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            /* 若摄像头正在工作，先停止它 */
            if (isPreview) {
                releaseVideoAnalyzer();
                isPreview = false;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseVideoAnalyzer();
    }


    public void initVideoEngine(Context context, String type) {
        analyzer = new MLTextAnalyzer.Factory(context).setLanguage(type).create();
    }

    public void initLensEngine(Context context, int width, int height) {
        if (analyzer == null) {
            return;
        }

        if (lensEngine == null) {
            lensEngine = new LensEngine.Creator(context, analyzer)
                    .setLensType(LensEngine.BACK_LENS)
                    .applyDisplayDimension(width, height)
                    .applyFps(30.0f)
                    .enableAutomaticFocus(true)
                    .create();
        }
    }

    public void setTransactor(Activity activity) {
        if (analyzer == null) {
            return;
        }
        if (title != null) {
            title = title.replace(" ", "");
        }
        analyzer.setTransactor(new OcrDetectorProcessor(activity, regexp, title, regexpnumber));
    }

    public void runLensEngine(SurfaceHolder holder) {
        try {
            lensEngine.run(holder);
        } catch (IOException e) {
            Log.e(TAG, "e=" + e.getMessage());
        }
    }

    public void releaseVideoAnalyzer() {
        if (analyzer != null) {
            try {
                analyzer.stop();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
        if (lensEngine != null) {
            lensEngine.release();
        }
    }
}
