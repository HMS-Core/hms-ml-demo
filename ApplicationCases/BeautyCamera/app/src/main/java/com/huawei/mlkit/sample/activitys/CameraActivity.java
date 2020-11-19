/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.mlkit.sample.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.utils.ImageHelper;

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
    private ToggleButton facingSwitch;
    private Button takePhoto;
    private boolean isPreview = false;
    private int facing = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        init();
        initial();
    }

    private void init() {
        surfaceView = findViewById(R.id.surface_view);
        facingSwitch = findViewById(R.id.facingSwitch);
        takePhoto = findViewById(R.id.takephoto);

        if (!this.allPermissionsGranted()) {
            this.getRuntimePermissions();
        }

        if (Camera.getNumberOfCameras() == 1) {
            facingSwitch.setVisibility(View.GONE);
        }

        facingSwitch.setOnClickListener(onClickListener);
        takePhoto.setOnClickListener(onClickListener);
    }

    private SurfaceHolder holder;

    private void initial() {
        holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceViewCallback());
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.facingSwitch:
                    camera.stopPreview();
                    camera.release();

                    if (facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    } else if (facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        facing = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }

                    camera = Camera.open(facing);
                    camera.setDisplayOrientation(90);
                    try {
                        Camera.Parameters parameters = camera.getParameters();
                        int width = parameters.getPreviewSize().width;
                        int height = parameters.getPreviewSize().height;
                        parameters.setPictureSize(width, height);
                        List<String> foccusModes = parameters.getSupportedFocusModes();
                        if (foccusModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        }
                        camera.setParameters(parameters);

                        camera.setPreviewDisplay(holder);

                    } catch (IOException e) {
                        Log.e(TAG, "error");
                    }
                    camera.startPreview();
                    break;
                case R.id.takephoto:
                    if (camera == null) return;
                    camera.takePicture(null, null, null, picture);
                    break;
                default:
                    break;
            }

        }
    };

    private Camera.PictureCallback picture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String path = ImageHelper.savePhoto(data);
            Intent intent = new Intent(CameraActivity.this, BeautyActivity.class);
            intent.putExtra("filepath", path);
            intent.putExtra("facing", facing);
            startActivity(intent);
        }
    };

    private Camera.ErrorCallback errorCallback = new Camera.ErrorCallback() {
        @Override
        public void onError(int error, Camera camera) {
            switch (error) {
                case Camera.CAMERA_ERROR_SERVER_DIED:
                    Log.e(TAG, "Camera.CAMERA_ERROR_SERVER_DIED");
                    //Reinitialize camera
                    if (camera != null) {
                        camera.stopPreview();
                        camera.release();
                    }
                    camera = Camera.open(facing);
                    camera.setDisplayOrientation(90);

                    try {
                        Camera.Parameters parameters = camera.getParameters();
                        List<String> foccusModes = parameters.getSupportedFocusModes();
                        if (foccusModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        }

                        int width = parameters.getPreviewSize().width;
                        int height = parameters.getPreviewSize().height;
                        parameters.setPictureSize(width, height);
                        camera.setParameters(parameters);

                        camera.setPreviewDisplay(holder);
                        camera.setErrorCallback(errorCallback);
                        camera.startPreview();
                        isPreview = true;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Log.e(TAG, "e=" + e.getMessage());
                        isPreview = false;
                    }
                    break;
                case Camera.CAMERA_ERROR_UNKNOWN:
                    Log.e(TAG, "Camera.CAMERA_ERROR_UNKNOWN");
                    break;
                default:
                    break;
            }
        }
    };

    private class SurfaceViewCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e(TAG, "surfaceCreated");
            camera = Camera.open(facing);
            camera.setDisplayOrientation(90);

            try {
                Camera.Parameters parameters = camera.getParameters();
                List<String> foccusModes = parameters.getSupportedFocusModes();
                if (foccusModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }

                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;
                parameters.setPictureSize(width, height);
                camera.setParameters(parameters);

                camera.setPreviewDisplay(holder);
                camera.startPreview();
                isPreview = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "e=" + e.getMessage());
                isPreview = false;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e(TAG, "surfaceChanged");

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG, "surfaceDestroyed");
            if (camera != null) {
                //if camera is working ,stop it
                if (isPreview) {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.unlock();
                    isPreview = false;
                }
                camera.setErrorCallback(null);
                camera.release();
            }
        }

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
            return true;
        }
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
}