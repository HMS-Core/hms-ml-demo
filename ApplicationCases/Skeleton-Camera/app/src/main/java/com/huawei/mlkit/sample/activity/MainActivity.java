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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.transactor.TakePictureListener;
import com.huawei.mlkit.sample.utils.SkeletonUtils;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements TakePictureListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 1;
    private Button liveButton;
    private ImageView image;

    public static float[][] SKELETON_DATA = {{416.6629f, 312.46442f, 101, 0.8042025f}, {382.3348f, 519.43396f, 102, 0.86383355f}, {381.0387f, 692.09515f, 103, 0.7551306f}
            , {659.49194f, 312.24445f, 104, 0.8305682f}, {693.5356f, 519.4844f, 105, 0.8932837f}, {694.0054f, 692.4169f, 106, 0.8742422f}
            , {485.08786f, 726.8787f, 107, 0.6004682f}, {485.02808f, 935.4897f, 108, 0.7334503f}, {485.09384f, 1177.127f, 109, 0.67240065f}
            , {623.7807f, 726.7474f, 110, 0.5483011f}, {624.5828f, 936.3222f, 111, 0.730425f}, {625.81915f, 1212.2491f, 112, 0.72417295f}
            , {521.47363f, 103.95903f, 113, 0.7780853f}, {521.6231f, 277.2533f, 114, 0.7745689f}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initListener();
    }

    private void init() {
        liveButton = findViewById(R.id.live);
        image = findViewById(R.id.image);
        SkeletonUtils.createMLSkeletonAnalyzer();
        SkeletonUtils.setBitmap(this, R.mipmap.model);
        SkeletonUtils.setTemplateData(SKELETON_DATA);
        SkeletonUtils.setTakePictureListener(this);
    }

    private void initListener() {
        liveButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "onClick");
            if (!isGranted(Manifest.permission.CAMERA)) {
                requestPermission(PERMISSIONS, REQUEST_CODE);
            } else {
                startActivity(new Intent(MainActivity.this, HumanSkeletonActivity.class));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkeletonUtils.releaseAnalyzer();
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
    public void picture(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        image.setImageBitmap(bitmap);
    }
}