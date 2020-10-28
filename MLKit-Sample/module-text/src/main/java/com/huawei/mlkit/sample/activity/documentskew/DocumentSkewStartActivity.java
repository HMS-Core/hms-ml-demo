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

package com.huawei.mlkit.sample.activity.documentskew;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;

public class DocumentSkewStartActivity extends BaseActivity implements View.OnClickListener {
    private final static int REQUEST_CODE = 100;
    public final static int TAKE_PHOTO = 1;
    public final static int SELECT_ALBUM = 2;
    public int operate_type = 0;

    private String[] chooseTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_skew_start);
        findViewById(R.id.rl_upload_picture).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        setStatusBarColor(this, R.color.black);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_upload_picture) {
            showDialog();
        } else if (v.getId() == R.id.back) {
            finish();
        }
    }

    private void showDialog() {
        chooseTitles = new String[]{getResources().getString(R.string.take_photo), getResources().getString(R.string.select_from_album)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(chooseTitles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                if (position == 0) {
                    operate_type = TAKE_PHOTO;
                    requestPermission(Manifest.permission.CAMERA);
                } else {
                    operate_type = SELECT_ALBUM;
                    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });
        builder.create().show();
    }

    private void requestPermission(String permisssions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startSuperResolutionActivity();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, permisssions)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permisssions}, REQUEST_CODE);
        } else {
            startSuperResolutionActivity();
        }
    }

    private void startSuperResolutionActivity() {
        Intent intent = new Intent(DocumentSkewStartActivity.this, DocumentSkewCorretionActivity.class);
        intent.putExtra("operate_type", operate_type);
        startActivity(intent);

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
