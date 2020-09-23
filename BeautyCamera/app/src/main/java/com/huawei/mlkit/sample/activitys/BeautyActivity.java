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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.common.MLPosition;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceShape;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.utils.ImageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static com.huawei.hms.mlsdk.face.MLFaceShape.TYPE_FACE;
import static com.huawei.hms.mlsdk.face.MLFaceShape.TYPE_LEFT_EYE;
import static com.huawei.hms.mlsdk.face.MLFaceShape.TYPE_RIGHT_EYE;

public class BeautyActivity extends AppCompatActivity {
    private static final String TAG = BeautyActivity.class.getSimpleName();
    private static final int MAX_VALUE = 255;
    private static final int MID_VALUE = 127;
    private static final int MAX_EYE_LEVEL = 4;
    private static final int MAX_FACE_LEVEL = 8;

    private Bitmap bitmap;
    private ImageView image;
    private ImageView photo;
    private MLFaceAnalyzer analyzer;

    private float mHue = 0;
    private float mSaturation = 1;
    private float mLum = 1;
    private SeekBar seekBarHue;
    private SeekBar seekBarSaturation;
    private SeekBar seekBarLum;
    private SeekBar seekBarEye;
    private SeekBar seekBarFace;

    private Bitmap beauty;
    private Point lefteyepoint;
    private Point righteyepoint;
    private float lefteyeleft;
    private float lefteyetop;
    private float lefteyeright;
    private float lefteyebottom;
    private float righteyeleft;
    private float righteyetop;
    private float righteyeright;
    private float righteyebottom;
    private List<MLPosition> leftfacePoints = null;
    private List<MLPosition> rightfacePoints = null;

    private Bitmap initBeauty;
    private int eyevalue = 0;
    private int facevalue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty);

        initFaceAnalyzer();

        init();
        setImage(bitmap);
        initSeekbar();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAnalyse();
    }

    private void init() {
        image = findViewById(R.id.image);
        photo = findViewById(R.id.photo);

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        String filepath = intent.getStringExtra("filepath");
        if (filepath != null) {
            bitmap = ImageHelper.getScaleBitmap(filepath);
            int facing = intent.getIntExtra("facing", -1);
            if (facing != -1) {
                bitmap = ImageHelper.rotateBitmap(bitmap, facing);
            }

        }

        String string = intent.getStringExtra("uri");
        if (string != null) {
            Uri uri = Uri.parse(string);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = ImageHelper.scaleBitmap(bitmap, 0.5f);
            } catch (IOException error) {
                Log.e(TAG, "error");
            }
        }

        seekBarHue = findViewById(R.id.seekbarHue);
        seekBarSaturation = findViewById(R.id.seekbarSaturation);
        seekBarLum = findViewById(R.id.seekbarLum);
        seekBarEye = findViewById(R.id.seekbareye);
        seekBarFace = findViewById(R.id.seekbarface);
    }

    private void initListener() {
        seekBarHue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarSaturation.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarLum.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarEye.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarFace.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }


    private void initSeekbar() {
        //set seekbar max value
        seekBarHue.setMax(MAX_VALUE);
        seekBarSaturation.setMax(MAX_VALUE);
        seekBarLum.setMax(MAX_VALUE);
        seekBarEye.setMax(MAX_EYE_LEVEL);
        seekBarFace.setMax(MAX_FACE_LEVEL);

        //set seekbar progress
        seekBarHue.setProgress(MID_VALUE);
        seekBarSaturation.setProgress(MID_VALUE);
        seekBarLum.setProgress(MID_VALUE);
    }

    private void setImage(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        image.setImageBitmap(bitmap);
        beauty = ImageHelper.drawBitmap(bitmap, mHue, mSaturation, mLum);
        photo.setImageBitmap(beauty);
        initBeauty = beauty.copy(beauty.getConfig(), false);
        if (initBeauty == null) {
            return;
        }
        analyzerFace(beauty);
    }

    private void initFaceAnalyzer() {
        analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer();
    }


    private void analyzerFace(final Bitmap bitmap) {
        //create MLFrame from bitmap
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<List<MLFace>> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<List<MLFace>>() {
            @Override
            public void onSuccess(List<MLFace> faces) {
                // detect success
                if (faces.size() <= 0) {
                    return;
                }
                MLFaceShape face = faces.get(0).getFaceShape(TYPE_FACE);
                if (leftfacePoints != null) {
                    leftfacePoints.clear();
                }
                leftfacePoints = new ArrayList<>();
                for (int i = 0; i < face.getPoints().size() / 2; i++) {
                    leftfacePoints.add(face.getPoints().get(i));
                }

                if (rightfacePoints != null) {
                    rightfacePoints.clear();
                }

                rightfacePoints = new ArrayList<>();
                for (int i = face.getPoints().size() / 2; i < face.getPoints().size(); i++) {
                    rightfacePoints.add(face.getPoints().get(i));
                }

                MLFaceShape lefteye = faces.get(0).getFaceShape(TYPE_LEFT_EYE);
                lefteyeleft = lefteye.getPoints().get(0).getX();
                lefteyetop = lefteye.getPoints().get(0).getY();
                lefteyeright = lefteye.getPoints().get(0).getX();
                lefteyebottom = lefteye.getPoints().get(0).getY();
                for (int i = 0; i < lefteye.getPoints().size(); i++) {
                    if (lefteye.getPoints().get(i).getX() < lefteyeleft) {
                        lefteyeleft = lefteye.getPoints().get(i).getX();
                    }
                    if (lefteye.getPoints().get(i).getX() > lefteyeright) {
                        lefteyeright = lefteye.getPoints().get(i).getX();
                    }
                    if (lefteye.getPoints().get(i).getX() < lefteyetop) {
                        lefteyetop = lefteye.getPoints().get(i).getY();
                    }
                    if (lefteye.getPoints().get(i).getX() > lefteyebottom) {
                        lefteyebottom = lefteye.getPoints().get(i).getY();
                    }
                }

                lefteyepoint = new Point((int) ((lefteyeleft + lefteyeright) / 2), (int) ((lefteyetop + lefteyebottom) / 2));

                MLFaceShape righteye = faces.get(0).getFaceShape(TYPE_RIGHT_EYE);
                righteyeleft = righteye.getPoints().get(0).getX();
                righteyetop = righteye.getPoints().get(0).getY();
                righteyeright = righteye.getPoints().get(0).getX();
                righteyebottom = righteye.getPoints().get(0).getY();
                for (int i = 0; i < righteye.getPoints().size(); i++) {
                    if (righteye.getPoints().get(i).getX() < righteyeleft) {
                        righteyeleft = righteye.getPoints().get(i).getX();
                    }
                    if (righteye.getPoints().get(i).getX() > righteyeright) {
                        righteyeright = righteye.getPoints().get(i).getX();
                    }
                    if (righteye.getPoints().get(i).getX() < righteyetop) {
                        righteyetop = righteye.getPoints().get(i).getY();
                    }
                    if (righteye.getPoints().get(i).getX() > righteyebottom) {
                        righteyebottom = righteye.getPoints().get(i).getY();
                    }
                }

                righteyepoint = new Point((int) ((righteyeleft + righteyeright) / 2), (int) ((righteyetop + righteyebottom) / 2));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // detect fail
                Log.e(TAG, "e=" + e.getMessage());
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (beauty == null || bitmap == null) {
                Toast.makeText(BeautyActivity.this, "bitmap is null", Toast.LENGTH_SHORT).show();
                return;
            }
            switch (seekBar.getId()) {
                case R.id.seekbarHue://hue
                    mHue = (progress - MID_VALUE) * 1.0f / MID_VALUE;
                    beauty = ImageHelper.drawBitmap(initBeauty, mHue, mSaturation, mLum);
                    if (eyevalue > 0 && lefteyepoint != null && righteyepoint != null) {
                        beauty = ImageHelper.magnifyEyes(beauty, lefteyepoint, righteyepoint, (int) ((lefteyeright - lefteyeleft) / 2 * 3), (int) ((righteyeright - righteyeleft) / 2 * 3), eyevalue);
                    }
                    if (facevalue > 0 && leftfacePoints != null && rightfacePoints != null) {
                        beauty = ImageHelper.slimFace(beauty, rightfacePoints, leftfacePoints, facevalue);
                    }
                    break;

                case R.id.seekbarSaturation://saturation
                    mSaturation = progress * 1.0f / MID_VALUE;
                    beauty = ImageHelper.drawBitmap(initBeauty, mHue, mSaturation, mLum);
                    if (eyevalue > 0 && lefteyepoint != null && righteyepoint != null) {
                        beauty = ImageHelper.magnifyEyes(beauty, lefteyepoint, righteyepoint, (int) ((lefteyeright - lefteyeleft) / 2 * 3), (int) ((righteyeright - righteyeleft) / 2 * 3), eyevalue);
                    }
                    if (facevalue > 0 && leftfacePoints != null && rightfacePoints != null) {
                        beauty = ImageHelper.slimFace(beauty, rightfacePoints, leftfacePoints, facevalue);
                    }
                    break;

                case R.id.seekbarLum://lum
                    mLum = progress * 1.0f / MID_VALUE;
                    beauty = ImageHelper.drawBitmap(initBeauty, mHue, mSaturation, mLum);
                    if (eyevalue > 0 && lefteyepoint != null && righteyepoint != null) {
                        beauty = ImageHelper.magnifyEyes(beauty, lefteyepoint, righteyepoint, (int) ((lefteyeright - lefteyeleft) / 2 * 3), (int) ((righteyeright - righteyeleft) / 2 * 3), eyevalue);
                    }
                    if (facevalue > 0 && leftfacePoints != null && rightfacePoints != null) {
                        beauty = ImageHelper.slimFace(beauty, rightfacePoints, leftfacePoints, facevalue);
                    }
                    break;

                case R.id.seekbareye://big eyes
                    if (lefteyepoint == null && righteyepoint == null) {
                        Toast.makeText(BeautyActivity.this, "no eyes", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (facevalue > 0 && leftfacePoints != null && rightfacePoints != null) {
                        beauty = ImageHelper.slimFace(initBeauty, rightfacePoints, leftfacePoints, facevalue);
                        beauty = ImageHelper.drawBitmap(beauty, mHue, mSaturation, mLum);
                    } else {
                        beauty = ImageHelper.drawBitmap(initBeauty, mHue, mSaturation, mLum);
                    }

                    eyevalue = progress;
                    beauty = ImageHelper.magnifyEyes(beauty, lefteyepoint, righteyepoint, (int) ((lefteyeright - lefteyeleft) / 2 * 3), (int) ((righteyeright - righteyeleft) / 2 * 3), progress);
                    break;

                case R.id.seekbarface://thine face
                    if (leftfacePoints == null && rightfacePoints == null) {
                        Toast.makeText(BeautyActivity.this, "no face", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (eyevalue > 0 && lefteyepoint != null && righteyepoint != null) {
                        beauty = ImageHelper.magnifyEyes(initBeauty, lefteyepoint, righteyepoint, (int) ((lefteyeright - lefteyeleft) / 2 * 3), (int) ((righteyeright - righteyeleft) / 2 * 3), eyevalue);
                        beauty = ImageHelper.drawBitmap(beauty, mHue, mSaturation, mLum);
                    } else {
                        beauty = ImageHelper.drawBitmap(initBeauty, mHue, mSaturation, mLum);
                    }
                    facevalue = progress;
                    beauty = ImageHelper.slimFace(beauty, rightfacePoints, leftfacePoints, progress);
                    break;
                default:
                    break;
            }
            photo.setImageBitmap(beauty);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private void releaseAnalyse() {
        try {
            if (analyzer != null) {
                analyzer.stop();
            }
        } catch (IOException e) {
            Log.e(TAG, "e=" + e.getMessage());
        }
    }
}