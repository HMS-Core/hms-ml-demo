/*
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

package com.huawei.hms.mlkit.sample.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlkit.sample.sceneenhance.R;
import com.huawei.hms.mlkit.sample.util.ConstantData;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.scd.MLSceneDetection;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzer;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SceneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SceneActivity";

    private TextView mMainText, mCopyText, mDescribe, mCamera, mPhoto;
    private ImageView mMainImage, mCopyImage;
    private LinearLayout.LayoutParams mMainImageLayout, mCopyImageLayout;

    private MLSceneDetectionAnalyzer analyzer;
    private List<String> mSceneList;
    private List<Float> mSceneInfoList;
    private List<float[]> mSceneColorList;

    private ColorMatrix mColorMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mMainText = findViewById(R.id.main_text);
        mCopyText = findViewById(R.id.copy_text);
        mDescribe = findViewById(R.id.text_describe);
        mCamera = findViewById(R.id.text_camera);
        mPhoto = findViewById(R.id.text_photo);
        mMainImage = findViewById(R.id.main_image);
        mCopyImage = findViewById(R.id.copy_image);
        mMainImageLayout = (LinearLayout.LayoutParams) mMainImage.getLayoutParams();
        mCopyImageLayout = (LinearLayout.LayoutParams) mCopyImage.getLayoutParams();
    }

    private void initData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestCameraPermission();
        }
        analyzer = MLSceneDetectionAnalyzerFactory.getInstance().getSceneDetectionAnalyzer();
        mSceneList = new ArrayList<>();
        mSceneInfoList = new ArrayList<>();
        mSceneColorList = new ArrayList<>();
        mSceneColorList.add(ConstantData.sky);
        mSceneColorList.add(ConstantData.food);
        mSceneColorList.add(ConstantData.flower);
        mSceneColorList.add(ConstantData.grass);
        mSceneColorList.add(ConstantData.darkness);
    }

    private void initEvent() {
        mCamera.setOnClickListener(this);
        mPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_camera:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                0);
                    }
                }
                break;
            case R.id.text_photo:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && data != null) {
            mMainText.setVisibility(View.VISIBLE);
            // Create an MLFrame by using android.graphics.Bitmap.
            Bitmap bitmap = data.getParcelableExtra("data");
            imageMeasure(bitmap);
            Glide.with(SceneActivity.this)
                    .load(bitmap)
                    .into(mMainImage);
            sceneRecognition(bitmap);
        }

        if (requestCode == 1 && data != null) {
            mMainText.setVisibility(View.VISIBLE);
            // Create an MLFrame by using android.graphics.Bitmap.
            Uri uri = data.getData();
            Bitmap bitmap = null;
            if (uri != null) {
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                } catch (FileNotFoundException e) {
                    // FileNotFoundException
                    Log.e(TAG, "Error: FileNotFoundException");
                } catch (Exception e) {
                    // Exception
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
            imageMeasure(bitmap);
            Glide.with(SceneActivity.this)
                    .load(uri)
                    .into(mMainImage);
            sceneRecognition(bitmap);
        }
    }

    private void imageMeasure(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        int width = (int) ((double) this.getWindowManager().getDefaultDisplay().getWidth() / 2);
        int height = (int) (bitmap.getHeight() * ((double) this.getWindowManager()
                .getDefaultDisplay().getWidth() / (double) bitmap.getWidth()) / 2);
        mMainImageLayout.width = width;
        mMainImageLayout.height = height;
        mMainImage.setLayoutParams(mMainImageLayout);
        mCopyImageLayout.width = width;
        mCopyImageLayout.height = height;
        mCopyImage.setLayoutParams(mCopyImageLayout);
    }

    private void sceneRecognition (final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        mCopyText.setVisibility(View.GONE);
        mCopyImage.setVisibility(View.GONE);
        MLFrame frame = new MLFrame.Creator()
                .setBitmap(bitmap)
                .create();
        Task<List<MLSceneDetection>> task = this.analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<List<MLSceneDetection>>() {
            @Override
            public void onSuccess(List<MLSceneDetection> sceneInfos) {
                // Processing logic for scene detection success.
                if (sceneInfos != null && !sceneInfos.isEmpty()) {
                    mSceneList.clear();
                    mSceneInfoList.clear();
                    SceneActivity.this.displaySuccess(sceneInfos, bitmap);
                } else {
                    SceneActivity.this.displayFailure();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Processing logic for scene detection failure.
                if (e instanceof MLException) {
                    MLException exception = (MLException) e;
                    // Obtain the result code.
                    int errCode = exception.getErrCode();
                    // Obtain the error information.
                    String message = exception.getMessage();
                    Log.e(TAG, "MLException: " + message + ", ErrCode: " + errCode);
                } else {
                    // Other errors.
                    Log.e(TAG, "MLError: " + e.getMessage());
                }
                SceneActivity.this.displayFailure();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displaySuccess(List<MLSceneDetection> sceneInfos, Bitmap bitmap) {
        StringBuilder string = new StringBuilder("SceneCount : " + sceneInfos.size() + "\n");
        for (int i = 0; i < sceneInfos.size(); i++) {
            MLSceneDetection sceneInfo = sceneInfos.get(i);
            string.append("Scene ")
                    .append(i + 1)
                    .append(" : ")
                    .append(sceneInfo.getResult())
                    .append("\n")
                    .append("Confidence : ")
                    .append(sceneInfo.getConfidence())
                    .append("\n");
            Log.d(TAG, string.toString());
            mSceneList.add(sceneInfo.getResult());
            mSceneInfoList.add(sceneInfo.getConfidence());
        }
        mDescribe.setText("Scene : " + mSceneList.get(0));
        for (int i = 0; i < ConstantData.scene.length; i++) {
            if (ConstantData.scene[i].equals(mSceneList.get(0))) {
                if (mSceneInfoList.get(0) >= 0.9f) {
                    mCopyText.setVisibility(View.VISIBLE);
                    mCopyImage.setVisibility(View.VISIBLE);
                    mColorMatrix = new ColorMatrix(mSceneColorList.get(i));
                    setImageMatrix(bitmap);
                } else {
                    Toast.makeText(this, getString(R.string.complex), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private void displayFailure() {
        mDescribe.setText("");
        mCopyText.setVisibility(View.GONE);
        mCopyImage.setVisibility(View.GONE);
        Toast.makeText(this.getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
    }

    private void setImageMatrix(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(
                bitmap.getWidth(),
                bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(mColorMatrix);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        mCopyImage.setImageBitmap(createBitmap);
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        }
    }
}
