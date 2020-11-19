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

package com.huawei.mlkit.sample.activity.Imagesupersesolution;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.activity.textsuperresolution.TextImageSuperResolutionActivity;
import com.huawei.mlkit.sample.util.Constant;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ImageSuperResolutionStartActivity extends BaseActivity implements View.OnClickListener {

    private final static int REQUEST_CODE = 100;
    private String type = Constant.TYPE_IMAGE_SUPER_RESOLUTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_super_resolution_start);
        TextView title = findViewById(R.id.title);
        Intent intent = this.getIntent();
        if(intent != null){
            type = intent.getStringExtra(Constant.SUPER_RESOLUTION_TYPE);
            if(Constant.TYPE_TEXT_SUPER_RESOLUTION.equals(type)){
                title.setText(R.string.text_super_resolution);
            }
        }
        findViewById(R.id.rl_upload_picture).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        setStatusBarColor(this, R.color.black);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_upload_picture) {
            requestPermission();
        } else if (v.getId() == R.id.back) {
            finish();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startSuperResolutionActivity();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            startSuperResolutionActivity();
        }
    }

    private void startSuperResolutionActivity() {
        if(Constant.TYPE_IMAGE_SUPER_RESOLUTION.equals(type)) {
            startActivity(new Intent(ImageSuperResolutionStartActivity.this, ImageSuperResolutionActivity.class));
        }else {
            startActivity(new Intent(ImageSuperResolutionStartActivity.this, TextImageSuperResolutionActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSuperResolutionActivity();
            } else {
                Toast.makeText(this, "Permission application failed, you denied the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
