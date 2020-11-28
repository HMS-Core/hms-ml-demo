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

package com.huawei.mlkit.sample.activity.icr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlplugin.card.icr.cn.MLCnIcrCapture;
import com.huawei.hms.mlplugin.card.icr.cn.MLCnIcrCaptureConfig;
import com.huawei.hms.mlplugin.card.icr.cn.MLCnIcrCaptureFactory;
import com.huawei.hms.mlplugin.card.icr.cn.MLCnIcrCaptureResult;
import com.huawei.mlkit.sample.R;

public class IDCardRecognitionActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "IDCardRecognition";

    private boolean lastType = false; // false: front， true：back.
    private static final int REQUEST_CODE = 10;
    private static final int INT_REQUEST_CODE = 20;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

    private ImageView frontImg;
    private ImageView backImg;
    private ImageView frontSimpleImg;
    private ImageView backSimpleImg;
    private ImageView frontDeleteImg;
    private ImageView backDeleteImg;
    private LinearLayout frontAddView;
    private LinearLayout backAddView;
    private TextView showResult;
    private String lastFrontResult = "";
    private String lastBackResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_idcard_recognition);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        this.initComponent();
        if (!this.isGranted(Manifest.permission.CAMERA)) {
            this.requestPermission(IDCardRecognitionActivity.PERMISSIONS, IDCardRecognitionActivity.INT_REQUEST_CODE);
            return;
        }
    }

    private void initComponent() {
        this.frontImg = this.findViewById(R.id.avatar_img);
        this.backImg = this.findViewById(R.id.emblem_img);
        this.frontSimpleImg = this.findViewById(R.id.avatar_sample_img);
        this.backSimpleImg = this.findViewById(R.id.emblem_sample_img);
        this.frontDeleteImg = this.findViewById(R.id.avatar_delete);
        this.backDeleteImg = this.findViewById(R.id.emblem_delete);
        this.frontAddView = this.findViewById(R.id.avatar_add);
        this.backAddView = this.findViewById(R.id.emblem_add);
        this.showResult = this.findViewById(R.id.show_result);
        this.frontAddView.setOnClickListener(this);
        this.backAddView.setOnClickListener(this);
        this.frontDeleteImg.setOnClickListener(this);
        this.backDeleteImg.setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.avatar_add:
                Log.i(IDCardRecognitionActivity.TAG, "onClick avatar_img");
                this.lastType = true;
                if (!this.isGranted(Manifest.permission.CAMERA)) {
                    this.requestPermission(IDCardRecognitionActivity.PERMISSIONS, IDCardRecognitionActivity.REQUEST_CODE);
                    return;
                } else {
                    this.startCaptureActivity(this.idCallBack, this.lastType);
                }
                break;
            case R.id.emblem_add:
                Log.i(IDCardRecognitionActivity.TAG, "onClick emblem_img");
                this.lastType = false;
                if (!this.isGranted(Manifest.permission.CAMERA)) {
                    this.requestPermission(IDCardRecognitionActivity.PERMISSIONS, IDCardRecognitionActivity.REQUEST_CODE);
                    return;
                } else {
                    this.startCaptureActivity(this.idCallBack, this.lastType);
                }
                break;
            case R.id.avatar_delete:
                Log.i(IDCardRecognitionActivity.TAG, "onClick avatar_delete");
                this.showFrontDeleteImage();
                this.lastFrontResult = "";
                break;
            case R.id.emblem_delete:
                Log.i(IDCardRecognitionActivity.TAG, "onClick emblem_delete");
                this.showBackDeleteImage();
                this.lastBackResult = "";
                break;
            case R.id.back:
                this.finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != IDCardRecognitionActivity.REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(IDCardRecognitionActivity.TAG, "Camera permission granted - initialize the lensEngine");
            this.startCaptureActivity(this.idCallBack, this.lastType);
            return;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(IDCardRecognitionActivity.TAG, "onConfigurationChanged");
    }

    private void startCaptureActivity(MLCnIcrCapture.CallBack callBack, boolean isFront) {
        Log.i(IDCardRecognitionActivity.TAG, "startCaptureActivity");
        MLCnIcrCaptureConfig config =
                new MLCnIcrCaptureConfig.Factory().setFront(isFront).create();
        MLCnIcrCapture icrCapture = MLCnIcrCaptureFactory.getInstance().getIcrCapture(config);

        icrCapture.capture(callBack, this);
    }

    private String formatIdCardResult(MLCnIcrCaptureResult result, boolean isFront) {
        Log.i(IDCardRecognitionActivity.TAG, "formatIdCardResult");
        StringBuilder resultBuilder = new StringBuilder();
        if (isFront) {
            resultBuilder.append("Name：");
            resultBuilder.append(result.name);
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Sex：");
            resultBuilder.append(result.sex);
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("IDNum: ");
            resultBuilder.append(result.idNum);
            resultBuilder.append(System.lineSeparator());
            Log.i(IDCardRecognitionActivity.TAG, "front result: " + resultBuilder.toString());
        } else {
            resultBuilder.append("ValidDate: ");
            resultBuilder.append(result.validDate);
            resultBuilder.append(System.lineSeparator());
            Log.i(IDCardRecognitionActivity.TAG, "back result: " + resultBuilder.toString());
        }
        return resultBuilder.toString();
    }

    private boolean isGranted(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            int checkSelfPermission = this.checkSelfPermission(permission);
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private boolean requestPermission(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!this.isGranted(permissions[0])) {
            this.requestPermissions(permissions, requestCode);
        }
        return true;
    }

    private MLCnIcrCapture.CallBack idCallBack = new MLCnIcrCapture.CallBack() {
        @Override
        public void onSuccess(MLCnIcrCaptureResult idCardResult) {
            Log.i(IDCardRecognitionActivity.TAG, "IdCallBack onRecSuccess");
            if (idCardResult == null) {
                Log.i(IDCardRecognitionActivity.TAG, "IdCallBack onRecSuccess idCardResult is null");
                return;
            }
            Bitmap bitmap = idCardResult.cardBitmap;
            if (IDCardRecognitionActivity.this.lastType) {
                Log.i(IDCardRecognitionActivity.TAG, "Front");
                IDCardRecognitionActivity.this.showFrontImage(bitmap);
                IDCardRecognitionActivity.this.lastFrontResult = IDCardRecognitionActivity.this.formatIdCardResult(idCardResult, true);
            } else {
                Log.i(IDCardRecognitionActivity.TAG, "back");
                IDCardRecognitionActivity.this.showBackImage(bitmap);
                IDCardRecognitionActivity.this.lastBackResult = IDCardRecognitionActivity.this.formatIdCardResult(idCardResult, false);
            }
            IDCardRecognitionActivity.this.showResult.setText(IDCardRecognitionActivity.this.lastFrontResult);
            IDCardRecognitionActivity.this.showResult.append(IDCardRecognitionActivity.this.lastBackResult);
        }

        @Override
        public void onCanceled() {
            Log.i(IDCardRecognitionActivity.TAG, "IdCallBack onRecCanceled");
        }

        @Override
        public void onFailure(int retCode, Bitmap bitmap) {
            Toast.makeText(IDCardRecognitionActivity.this.getApplicationContext(), R.string.get_data_failed, Toast.LENGTH_SHORT).show();
            Log.i(IDCardRecognitionActivity.TAG, "IdCallBack onRecFailed: " + retCode);
        }

        @Override
        public void onDenied() {
            Log.i(IDCardRecognitionActivity.TAG, "IdCallBack onCameraDenied");
        }
    };

    private void showFrontImage(Bitmap bitmap) {
        Log.i(IDCardRecognitionActivity.TAG, "showFrontImage");
        this.frontImg.setVisibility(View.VISIBLE);
        this.frontImg.setImageBitmap(bitmap);
        this.frontSimpleImg.setVisibility(View.GONE);
        this.frontAddView.setVisibility(View.GONE);
        this.frontDeleteImg.setVisibility(View.VISIBLE);
    }

    private void showBackImage(Bitmap bitmap) {
        this.backImg.setVisibility(View.VISIBLE);
        this.backImg.setImageBitmap(bitmap);
        this.backAddView.setVisibility(View.GONE);
        this.backSimpleImg.setVisibility(View.GONE);
        this.backDeleteImg.setVisibility(View.VISIBLE);
    }

    private void showFrontDeleteImage() {
        this.frontImg.setVisibility(View.GONE);
        this.frontSimpleImg.setVisibility(View.VISIBLE);
        this.frontAddView.setVisibility(View.VISIBLE);
        this.frontDeleteImg.setVisibility(View.GONE);
    }

    private void showBackDeleteImage() {
        this.backImg.setVisibility(View.GONE);
        this.backAddView.setVisibility(View.VISIBLE);
        this.backSimpleImg.setVisibility(View.VISIBLE);
        this.backDeleteImg.setVisibility(View.GONE);
    }
}

