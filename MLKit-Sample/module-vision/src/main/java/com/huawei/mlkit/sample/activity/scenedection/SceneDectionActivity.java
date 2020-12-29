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

package com.huawei.mlkit.sample.activity.scenedection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.scd.MLSceneDetection;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzer;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzerFactory;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzerSetting;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.transactor.SceneDetectionTransactor;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SceneDectionActivity extends Activity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = SceneDectionActivity.class.getSimpleName();

    private static final int PERMISSION_REQUESTS = 1;

    private static final int REQUEST_SELECT_ALBUM = 10;
    private static final int REQUEST_TAKE_PHOTO = 20;

    private TextView resultText;
    private Bitmap _bitmap;
    private Uri imageUri;
    private MLSceneDetectionAnalyzer analyzer;
    private MLSceneDetectionAnalyzerSetting setting;
    private float confidence;
    private int operateType = REQUEST_TAKE_PHOTO;

    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;
    private ToggleButton facingSwitch;
    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;
    private Camera mCamera;

    private ImageView iv_return_back;
    private ImageView iv_select_album;
    private ImageView iv_result;
    private RelativeLayout rl_select_album_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_dection);
        String con = getIntent().getStringExtra("confidence");
        if (!TextUtils.isEmpty(con)) {
            confidence = Float.valueOf(con);
        }
        initView();


        cameraConfiguration = new CameraConfiguration();
        cameraConfiguration.setCameraFacing(facing);

        facingSwitch.setOnCheckedChangeListener(this);

        if (Camera.getNumberOfCameras() == 1) {
            facingSwitch.setVisibility(View.GONE);
        }
        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
        createLensEngine();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (lensEngine != null) {
            if (isChecked) {
                facing = CameraConfiguration.CAMERA_FACING_FRONT;
                cameraConfiguration.setCameraFacing(facing);
            } else {
                facing = CameraConfiguration.CAMERA_FACING_BACK;
                cameraConfiguration.setCameraFacing(facing);
            }
        }
        preview.stop();
        restartLensEngine();
    }

    private void createLensEngine() {
        if (lensEngine == null) {
            lensEngine = new LensEngine(this, cameraConfiguration, graphicOverlay);
        }
        try {
            lensEngine.setMachineLearningFrameTransactor(new SceneDetectionTransactor(getApplicationContext(), confidence));
        } catch (Exception e) {
            Toast.makeText(
                    this, "Can not create image transactor: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void restartLensEngine() {
        startLensEngine();
        if (null != lensEngine) {
            mCamera = lensEngine.getCamera();
            try {
                mCamera.setPreviewDisplay(preview.getSurfaceHolder());
            } catch (IOException e) {
                Log.d(TAG, "initViews IOException");
            }
        }
    }

    private void startLensEngine() {
        if (lensEngine != null) {
            try {
                preview.start(lensEngine, false);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start lensEngine.", e);
                lensEngine.release();
                lensEngine = null;
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    getPackageManager()
                            .getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
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
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startLensEngine();
    }

    @Override
    protected void onStop() {
        super.onStop();
        preview.stop();
    }

    private void releaseLensEngine() {
        if (lensEngine != null) {
            lensEngine.release();
            lensEngine = null;
        }
    }

    private void initAnalyzer() {
        Log.e("TAG", "confidence =" + confidence);
        setting = new MLSceneDetectionAnalyzerSetting.Factory().setConfidence(confidence/100).create();
        analyzer = MLSceneDetectionAnalyzerFactory.getInstance().getSceneDetectionAnalyzer(setting);
    }

    private void initView() {
        resultText = findViewById(R.id.result);
        preview = findViewById(R.id.preview);
        graphicOverlay = findViewById(R.id.overlay);
        facingSwitch = findViewById(R.id.facingSwitch);
        iv_return_back = findViewById(R.id.iv_return_back);
        iv_select_album = findViewById(R.id.iv_select_album);
        iv_result = findViewById(R.id.iv_result);
        rl_select_album_result = findViewById(R.id.rl_select_album_result);

        iv_return_back.setOnClickListener(this);
        iv_select_album.setOnClickListener(this);
    }

    private void detectImage() {
        if (_bitmap == null) {
            resultText.setText("Picture error");
            return;
        }

        Log.e(TAG, "formType=" + _bitmap.getConfig());
        MLFrame frame = new MLFrame.Creator()
                .setBitmap(_bitmap)
                .create();


        SparseArray sparseArray = analyzer.analyseFrame(frame);
        if (sparseArray == null || sparseArray.size() == 0) {
            resultText.setText("No scene was identified ");
            operateType = REQUEST_SELECT_ALBUM;
            rl_select_album_result.setVisibility(View.VISIBLE);
            iv_result.setImageBitmap(_bitmap);
            return;
        }
        boolean hasBigConfidence = false;
        String str = "scene count：" + sparseArray.size() + "\n";
        for (int i = 0; i < sparseArray.size(); i++) {
            MLSceneDetection sceneInfo = (MLSceneDetection) sparseArray.get(i);
            float realConfidence = sceneInfo.getConfidence();
            if (Double.doubleToLongBits(realConfidence) >= Double.doubleToLongBits(confidence)) {
                BigDecimal a = new BigDecimal(realConfidence);
                BigDecimal b = new BigDecimal(100);
                float c = a.multiply(b).floatValue();

                BigDecimal d = new BigDecimal(c);
                float variable = d.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

                if (Float.toString(variable).contains("E")) {
                    variable = 0.01f;
                }
                hasBigConfidence = true;
                str += "Scene detection is：" + sceneInfo.getResult() + "\n" + "The credibility of the scenario is ：" + variable + "%" + "\n";
            }
        }
        if (!hasBigConfidence) {
            str = "No scene was identified";
        }
        resultText.setText(str);
        operateType = REQUEST_SELECT_ALBUM;
        rl_select_album_result.setVisibility(View.VISIBLE);
        iv_result.setImageBitmap(_bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (analyzer != null) {
            analyzer.stop();
        }
        releaseLensEngine();
    }

    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_SELECT_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_SELECT_ALBUM && resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    _bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    releaseLensEngine();
                    initAnalyzer();
                    detectImage();
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                if (imageUri != null) {
                    _bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    releaseLensEngine();
                    initAnalyzer();
                    detectImage();
                }
            }
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return_back:
                if (operateType == REQUEST_SELECT_ALBUM) {
                    rl_select_album_result.setVisibility(View.GONE);
                    operateType = REQUEST_TAKE_PHOTO;
                    createLensEngine();
                    restartLensEngine();
                } else {
                    finish();
                }
                break;
            case R.id.iv_select_album:
                selectFromAlbum();
                break;
        }
    }

    public static ByteBuffer bytetoBuffer(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(value.length);
        byteBuffer.clear();
        byteBuffer.get(value, 0, value.length);
        return byteBuffer;
    }

    /**
     * Convert the bitmap into ARGB data and then into NV21 data.
     */
    public static byte[] bitmapToNv21(Bitmap src, int width, int height) {
        if (src != null && src.getWidth() >= width && src.getHeight() >= height) {
            int[] argb = new int[width * height];
            src.getPixels(argb, 0, width, 0, 0, width, height);
            return argbToNv21(argb, width, height);
        }

        return new byte[]{};
    }

    /**
     * Converting ARGB data to NV21 data
     */
    private static byte[] argbToNv21(int[] argb, int width, int height) {
        int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int index = 0;
        byte[] nv21 = new byte[width * height * 3 / 2];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int r = (argb[index] & 0xFF0000) >> 16;
                int g = (argb[index] & 0x00FF00) >> 8;
                int b = argb[index] & 0x0000FF;
                int y = (66 * r + 129 * g + 25 * b + 128 >> 8) + 16;
                int u = (-38 * r - 74 * g + 112 * b + 128 >> 8) + 128;
                int v = (112 * r - 94 * g - 18 * b + 128 >> 8) + 128;
                nv21[yIndex++] = (byte) (y < 0 ? 0 : (y > 255 ? 255 : y));
                if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.length - 2) {
                    nv21[uvIndex++] = (byte) (v < 0 ? 0 : (v > 255 ? 255 : v));
                    nv21[uvIndex++] = (byte) (u < 0 ? 0 : (u > 255 ? 255 : u));
                }
                ++index;
            }
        }
        return nv21;
    }

}
