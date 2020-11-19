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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huawei.mlkit.sample.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 1;
    private static final int CHOOSE_PHOTO = 2;

    private Button camera;
    private Button photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initListener();
    }

    private void init() {
        camera = findViewById(R.id.camera);
        photo = findViewById(R.id.photo);
    }


    private void initListener() {
        camera.setOnClickListener(onClickListener);
        photo.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.camera:
                    Log.e(TAG, "click camera");
                    if (!isGranted(Manifest.permission.CAMERA)) {
                        requestPermission(PERMISSIONS, REQUEST_CODE);
                    } else {
                        startActivity(new Intent(MainActivity.this, CameraActivity.class));
                    }
                    break;
                case R.id.photo:
                    Log.e(TAG, "click photo");
                    if (!isGranted(Manifest.permission.CAMERA)) {
                        requestPermission(PERMISSIONS, REQUEST_CODE);
                    } else {
                        choosePhoto();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, CHOOSE_PHOTO);
    }

    private boolean isGranted(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            int checkSelfPermission = checkSelfPermission(permission);
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private boolean requestPermission(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!isGranted(permissions[0])) {
            requestPermissions(permissions, requestCode);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CHOOSE_PHOTO) {
            Uri uri = data.getData();
            if (uri != null) {
                Intent intent = new Intent(MainActivity.this, BeautyActivity.class);
                intent.putExtra("uri", uri.toString());
                startActivity(intent);
            }
        }
    }
}
