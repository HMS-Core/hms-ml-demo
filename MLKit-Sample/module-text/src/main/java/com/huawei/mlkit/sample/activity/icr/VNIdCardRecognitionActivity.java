/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlplugin.card.icr.vn.MLVnIcrCapture;
import com.huawei.hms.mlplugin.card.icr.vn.MLVnIcrCaptureConfig;
import com.huawei.hms.mlplugin.card.icr.vn.MLVnIcrCaptureFactory;
import com.huawei.hms.mlplugin.card.icr.vn.MLVnIcrCaptureResult;
import com.huawei.mlkit.sample.R;

public class VNIdCardRecognitionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "IDCardRecognition";

    private boolean lastType = false; // false: front， true：back.
    private static final int REQUEST_CODE = 10;
    private static final int INT_REQUEST_CODE = 20;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

    private ImageView frontImg;
    private ImageView frontSimpleImg;
    private ImageView frontDeleteImg;
    private ImageView backDeleteImg;
    private LinearLayout frontAddView;
    private LinearLayout backAddView;
    private TextView showResult;
    private String lastFrontResult = "";
    private RelativeLayout rnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_idcard_recognition);
        rnBack = findViewById(R.id.rn_back);
        rnBack.setVisibility(View.GONE);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        this.initComponent();
        if (!this.isGranted(Manifest.permission.CAMERA)) {
            this.requestPermission(VNIdCardRecognitionActivity.PERMISSIONS, VNIdCardRecognitionActivity.INT_REQUEST_CODE);
            return;
        }
    }

    private void initComponent() {
        this.frontImg = this.findViewById(R.id.avatar_img);
        this.frontSimpleImg = this.findViewById(R.id.avatar_sample_img);
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
                Log.i(VNIdCardRecognitionActivity.TAG, "onClick avatar_img");
                this.lastType = true;
                if (!this.isGranted(Manifest.permission.CAMERA)) {
                    this.requestPermission(VNIdCardRecognitionActivity.PERMISSIONS, VNIdCardRecognitionActivity.REQUEST_CODE);
                    return;
                } else {
                    this.startCaptureActivity(this.idCallBack);
                }
                break;
            case R.id.avatar_delete:
                Log.i(VNIdCardRecognitionActivity.TAG, "onClick avatar_delete");
                this.showFrontDeleteImage();
                this.lastFrontResult = "";
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
        if (requestCode != VNIdCardRecognitionActivity.REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(VNIdCardRecognitionActivity.TAG, "Camera permission granted - initialize the lensEngine");
            this.startCaptureActivity(this.idCallBack);
            return;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(VNIdCardRecognitionActivity.TAG, "onConfigurationChanged");
    }

    private void startCaptureActivity(MLVnIcrCapture.CallBack callBack) {
        Log.i(VNIdCardRecognitionActivity.TAG, "startCaptureActivity");
        MLVnIcrCaptureConfig config =
                new MLVnIcrCaptureConfig.Factory().create();
        MLVnIcrCapture icrCapture = MLVnIcrCaptureFactory.getInstance().getIcrCapture(config);

        icrCapture.capture(callBack, this);
    }

    private String formatIdCardResult(MLVnIcrCaptureResult result) {
        Log.i(VNIdCardRecognitionActivity.TAG, "formatIdCardResult");
        StringBuilder resultBuilder = new StringBuilder();

        resultBuilder.append("Name：");
        resultBuilder.append(result.getName());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Sex：");
        resultBuilder.append(result.getSex());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Birthday：");
        resultBuilder.append(result.getBirthday());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("IDNum: ");
        resultBuilder.append(result.getIdNum());
        resultBuilder.append(System.lineSeparator());

        Log.i(VNIdCardRecognitionActivity.TAG, "front result: " + resultBuilder.toString());
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

    private MLVnIcrCapture.CallBack idCallBack = new MLVnIcrCapture.CallBack() {
        @Override
        public void onSuccess(MLVnIcrCaptureResult idCardResult) {
            Log.i(VNIdCardRecognitionActivity.TAG, "IdCallBack onRecSuccess");
            if (idCardResult == null) {
                Log.i(VNIdCardRecognitionActivity.TAG, "IdCallBack onRecSuccess idCardResult is null");
                return;
            }
            Bitmap bitmap = idCardResult.getCardBitmap();
            if (VNIdCardRecognitionActivity.this.lastType) {
                Log.i(VNIdCardRecognitionActivity.TAG, "Front");
                VNIdCardRecognitionActivity.this.showFrontImage(bitmap);
                VNIdCardRecognitionActivity.this.lastFrontResult = VNIdCardRecognitionActivity.this.formatIdCardResult(idCardResult);
            }
            VNIdCardRecognitionActivity.this.showResult.setText(VNIdCardRecognitionActivity.this.lastFrontResult);
        }

        @Override
        public void onCanceled() {
            Log.i(VNIdCardRecognitionActivity.TAG, "IdCallBack onRecCanceled");
        }

        @Override
        public void onFailure(int retCode, Bitmap bitmap) {
            Toast.makeText(VNIdCardRecognitionActivity.this.getApplicationContext(), R.string.get_data_failed, Toast.LENGTH_SHORT).show();
            Log.i(VNIdCardRecognitionActivity.TAG, "IdCallBack onRecFailed: " + retCode);
        }

        @Override
        public void onDenied() {
            Log.i(VNIdCardRecognitionActivity.TAG, "IdCallBack onCameraDenied");
        }
    };

    private void showFrontImage(Bitmap bitmap) {
        Log.i(VNIdCardRecognitionActivity.TAG, "showFrontImage");
        this.frontImg.setVisibility(View.VISIBLE);
        this.frontImg.setImageBitmap(bitmap);
        this.frontSimpleImg.setVisibility(View.GONE);
        this.frontAddView.setVisibility(View.GONE);
        this.frontDeleteImg.setVisibility(View.VISIBLE);
    }

    private void showFrontDeleteImage() {
        this.frontImg.setVisibility(View.GONE);
        this.frontSimpleImg.setVisibility(View.VISIBLE);
        this.frontAddView.setVisibility(View.VISIBLE);
        this.frontDeleteImg.setVisibility(View.GONE);
    }

}

