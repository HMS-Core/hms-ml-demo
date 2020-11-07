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
package com.huawei.mlkit.sample.photoreader.java;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.mlkit.lensengine.CameraConfiguration;
import com.huawei.mlkit.lensengine.LensEngine;
import com.huawei.mlkit.sample.photoreader.databinding.ActivityCapturePhotoBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.huawei.mlkit.sample.photoreader.Constant.EXTRA_IMAGE_PATH;

public class CapturePhotoActivity extends AppCompatActivity {

    private static final String TAG = "CapturePhotoActivity";

    private LensEngine lensEngine;

    private ActivityCapturePhotoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding = ActivityCapturePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivActCapturePhotoTakePhoto.setOnClickListener(v -> toTakePhoto());
        createLensEngine();
    }

    private void createLensEngine() {
        final CameraConfiguration configuration = new CameraConfiguration();
        configuration.setCameraFacingBack(CameraConfiguration.CAMERA_FACING_BACK);
        lensEngine = new LensEngine(this, configuration);
    }

    private void startLensEngine() {
        try {
            binding.lepActCapturePhoto.start(this.lensEngine, false);
        } catch (IOException e) {
            Log.e(CapturePhotoActivity.TAG, "Unable to start lensEngine.", e);
            this.lensEngine.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLensEngine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.lepActCapturePhoto.stop();;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lensEngine.release();
    }

    private void toTakePhoto() {
        lensEngine.takePicture((data, camera) -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            String filePath = null;
            try {
                filePath = saveBitmapToDisk(bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Save bitmap failed: " + e.getMessage());
            }
            Intent intent = new Intent();
            intent.putExtra(EXTRA_IMAGE_PATH, filePath);
            setResult(Activity.RESULT_OK, intent);
            CapturePhotoActivity.this.finish();
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

        final String fileName = System.currentTimeMillis() + ".jpg";
        final File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            fos = null;

            Uri uri = Uri.fromFile(file);
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (IOException e) {
            Log.e(TAG, "Save bitmap failed: " + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Close stream failed: " + e.getMessage());
            }
        }

        return file.getCanonicalPath();
    }
}
