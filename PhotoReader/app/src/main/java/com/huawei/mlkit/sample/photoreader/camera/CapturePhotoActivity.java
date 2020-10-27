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


package com.huawei.mlkit.sample.photoreader.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.mlkit.lensengine.CameraConfiguration;
import com.huawei.mlkit.lensengine.LensEngine;
import com.huawei.mlkit.lensengine.LensEnginePreview;
import com.huawei.mlkit.sample.photoreader.R;
import com.huawei.mlkit.sample.photoreader.util.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CapturePhotoActivity extends AppCompatActivity {
    private static final String TAG = "CapturePhotoActivity";
    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_photo);
        ImageButton takePhotoButton = findViewById(R.id.img_takePhoto);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CapturePhotoActivity.this.toTakePhoto();
            }
        });
        ImageButton backButton = findViewById(R.id.capture_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CapturePhotoActivity.this.finish();
            }
        });
        this.preview = this.findViewById(R.id.capture_preview);
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacingBack(this.facing);
        this.createLensEngine();
        this.startLensEngine();
    }

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration);
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, false);
            } catch (IOException e) {
                Log.e(CapturePhotoActivity.TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.startLensEngine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.lensEngine != null) {
            this.lensEngine.release();
        }
        this.facing = CameraConfiguration.CAMERA_FACING_BACK;
        this.cameraConfiguration.setCameraFacingBack(this.facing);
    }

    private void toTakePhoto() {
        lensEngine.takePicture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                String filePath = null;
                try {
                    filePath = saveBitmapToDisk(bitmap);
                } catch (IOException e) {
                    Log.e(TAG, "Save bitmap failed: " + e.getMessage());
                }
                Intent intent = new Intent();
                intent.putExtra(Constant.IMAGE_PATH_VALUE, filePath);
                setResult(Activity.RESULT_OK, intent);
                CapturePhotoActivity.this.finish();
            }
        });
    }

    private String saveBitmapToDisk(Bitmap bitmap) throws IOException {
        String storePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "PhotoTranslate";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            boolean res = appDir.mkdir();
            if (!res) {
                Log.e(TAG, "saveBitmapToDisk failed");
                return "";
            }
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            fos = null;

            Uri uri = Uri.fromFile(file);
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Save bitmap failed: " + e.getMessage());
        }  catch (IOException e) {
            Log.e(TAG, "Save bitmap failed: " + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Close stream failed: " + e.getMessage());
            }
            fos = null;
        }

        return file.getCanonicalPath();
    }
}
