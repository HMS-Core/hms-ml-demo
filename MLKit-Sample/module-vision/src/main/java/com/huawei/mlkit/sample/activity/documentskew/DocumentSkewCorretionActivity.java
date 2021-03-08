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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;

import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionAnalyzer;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionAnalyzerFactory;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionAnalyzerSetting;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionCoordinateInput;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionResult;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewDetectResult;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.views.DocumentCorrectImageView;
import com.huawei.mlkit.sample.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentSkewCorretionActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SuperResolutionActivity";
    private static final int REQUEST_SELECT_IMAGE = 1000;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private ImageView desImageView;
    private ImageButton adjustImgButton;
    private Bitmap srcBitmap;
    private Bitmap getCompressesBitmap;
    private Uri imageUri;
    private MLDocumentSkewCorrectionAnalyzer analyzer;
    private Bitmap corrected;
    private ImageView back;
    private Task<MLDocumentSkewCorrectionResult> correctionTask;
    private DocumentCorrectImageView documetScanView;
    private Point[] _points;
    private RelativeLayout layout_image;
    private MLFrame frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_skew_corretion);
        setStatusBarColor(this, R.color.black);

        analyzer = createAnalyzer();
        adjustImgButton = findViewById(R.id.adjust);
        layout_image = findViewById(R.id.layout_image);
        desImageView = findViewById(R.id.des_image);
        documetScanView = findViewById(R.id.iv_documetscan);
        back = findViewById(R.id.back);
        adjustImgButton.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.rl_chooseImg).setOnClickListener(this);
        back.setOnClickListener(this);
        int operate_type = getIntent().getIntExtra("operate_type", 0);
        if (operate_type == 1) {
            takePhoto();
        } else if (operate_type == 2) {
            selectLocalImage();
        }
    }

    private String[] chooseTitles;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.adjust) {
            List<Point> points = new ArrayList<>();
            Point[] cropPoints = documetScanView.getCropPoints();
            if (cropPoints != null) {
                points.add(cropPoints[0]);
                points.add(cropPoints[1]);
                points.add(cropPoints[2]);
                points.add(cropPoints[3]);
            }
            MLDocumentSkewCorrectionCoordinateInput coordinateData = new MLDocumentSkewCorrectionCoordinateInput(points);
            getDetectdetectResult(coordinateData, frame);

        } else if (v.getId() == R.id.rl_chooseImg) {
            chooseTitles = new String[]{getResources().getString(R.string.take_photo), getResources().getString(R.string.select_from_album)};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(chooseTitles, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position) {
                    if (position == 0) {
                        takePhoto();
                    } else {
                        selectLocalImage();
                    }
                }
            });
            builder.create().show();
        } else if (v.getId() == R.id.back) {
            finish();
        }
    }

    private MLDocumentSkewCorrectionAnalyzer createAnalyzer() {
        MLDocumentSkewCorrectionAnalyzerSetting setting = new MLDocumentSkewCorrectionAnalyzerSetting
                .Factory()
                .create();
        return MLDocumentSkewCorrectionAnalyzerFactory.getInstance().getDocumentSkewCorrectionAnalyzer(setting);
    }

    private void takePhoto() {
        layout_image.setVisibility(View.GONE);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            this.imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
            this.startActivityForResult(takePictureIntent, DocumentSkewCorretionActivity.this.REQUEST_TAKE_PHOTO);
        }
    }

    private void selectLocalImage() {
        layout_image.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            try {
                if (imageUri != null) {
                    srcBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    String realPathFromURI = getRealPathFromURI(imageUri);
                    int i = readPictureDegree(realPathFromURI);
                    Bitmap spBitmap = rotaingImageView(i, srcBitmap);
                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    getCompressesBitmap = Bitmap.createBitmap(spBitmap, 0, 0, spBitmap.getWidth(),
                            spBitmap.getHeight(), matrix, true);
                    reloadAndDetectImage();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            try {
                if (imageUri != null) {
                    srcBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    String realPathFromURI = getRealPathFromURI(imageUri);
                    int i = readPictureDegree(realPathFromURI);
                    Bitmap spBitmap = rotaingImageView(i, srcBitmap);
                    Matrix matrix = new Matrix();
                    matrix.setScale(0.5f, 0.5f);
                    getCompressesBitmap = Bitmap.createBitmap(spBitmap, 0, 0, spBitmap.getWidth(),
                            srcBitmap.getHeight(), matrix, true);
                    reloadAndDetectImage();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        } else if (resultCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    private void reloadAndDetectImage() {
        if (imageUri == null) {
            return;
        }
        frame = MLFrame.fromBitmap(getCompressesBitmap);
        Task<MLDocumentSkewDetectResult> task = analyzer.asyncDocumentSkewDetect(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLDocumentSkewDetectResult>() {

            public void onSuccess(MLDocumentSkewDetectResult result) {
                if (result.getResultCode() != 0) {
                    corrected = null;
                    Toast.makeText(DocumentSkewCorretionActivity.this, "The picture does not meet the requirements.", Toast.LENGTH_SHORT).show();
                } else {
                    // Recognition success.
                    Point leftTop = result.getLeftTopPosition();
                    Point rightTop = result.getRightTopPosition();
                    Point leftBottom = result.getLeftBottomPosition();
                    Point rightBottom = result.getRightBottomPosition();

                    _points = new Point[4];
                    _points[0] = leftTop;
                    _points[1] = rightTop;
                    _points[2] = rightBottom;
                    _points[3] = leftBottom;
                    layout_image.setVisibility(View.GONE);
                    documetScanView.setImageBitmap(getCompressesBitmap);
                    documetScanView.setPoints(_points);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDetectdetectResult(MLDocumentSkewCorrectionCoordinateInput coordinateData, MLFrame frame) {
        try {
            correctionTask = analyzer.asyncDocumentSkewCorrect(frame, coordinateData);
        } catch (Exception e) {
            Log.e(TAG, "The image does not meet the detection requirements.");
        }

        try {
            correctionTask.addOnSuccessListener(new OnSuccessListener<MLDocumentSkewCorrectionResult>() {
                @Override
                public void onSuccess(MLDocumentSkewCorrectionResult refineResult) {
                    // The check is successful.
                    if (refineResult != null && refineResult.getResultCode() == 0) {
                        corrected = refineResult.getCorrected();
                        layout_image.setVisibility(View.VISIBLE);
                        desImageView.setImageBitmap(corrected);
                    } else {
                        Toast.makeText(DocumentSkewCorretionActivity.this, "The check fails.", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(DocumentSkewCorretionActivity.this, "The check fails.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Please set an image.");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (srcBitmap != null) {
            srcBitmap.recycle();
        }
        if (getCompressesBitmap != null) {
            getCompressesBitmap.recycle();
        }
        if (corrected != null) {
            corrected.recycle();
        }
        if (analyzer != null) {
            try {
                analyzer.stop();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return degree;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        result = FileUtil.getFilePathByUri(this, contentURI);
        return result;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

}