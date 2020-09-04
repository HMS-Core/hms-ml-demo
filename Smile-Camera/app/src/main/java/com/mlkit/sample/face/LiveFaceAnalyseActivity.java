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

package com.mlkit.sample.face;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.common.MLResultTrailer;

import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.huawei.hms.mlsdk.face.MLFaceEmotion;
import com.huawei.hms.mlsdk.face.MLMaxSizeFaceTransactor;
import com.mlkit.sample.R;
import com.mlkit.sample.camera.LensEnginePreview;
import com.mlkit.sample.overlay.GraphicOverlay;
import com.mlkit.sample.overlay.LocalFaceGraphic;
import com.mlkit.sample.util.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LiveFaceAnalyseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LiveFaceAnalyseActivity";

    private MLFaceAnalyzer analyzer;

    private LensEngine mLensEngine;

    private LensEnginePreview mPreview;

    private GraphicOverlay overlay;

    private int lensType = LensEngine.BACK_LENS;

    private boolean isFront = false;

    private final float smilingRate = 0.8f;

    private final float smilingPossibility = 0.95f;

    private final static int STOP_PREVIEW = 1;

    private final static int TAKE_PHOTO = 2;

    private boolean safeToTakePicture = false;

    private String storePath = "/storage/emulated/0/DCIM/Camera";

    private Button restart;

    private int detectMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_live_face_analyse);
        if (savedInstanceState != null) {
            this.lensType = savedInstanceState.getInt("lensType");
        }
        this.mPreview = this.findViewById(R.id.preview);
        Intent intent = this.getIntent();
        try {
            this.detectMode = intent.getIntExtra(Constant.DETECT_MODE, -1);
        } catch (RuntimeException e) {
            Log.e(LiveFaceAnalyseActivity.TAG, "Get intent value failed:" + e.getMessage());
        }
        this.createFaceAnalyzer();
        this.overlay = findViewById(R.id.face_overlay);
        this.findViewById(R.id.facingSwitch).setOnClickListener(this);
        this.restart = findViewById(R.id.restart);
        this.createLensEngine();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.startLensEngine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mLensEngine != null) {
            this.mLensEngine.release();
        }
        if (this.analyzer != null) {
            try {
                this.analyzer.stop();
            } catch (IOException e) {
                Log.e(LiveFaceAnalyseActivity.TAG, "Stop failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("lensType", this.lensType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        this.isFront = !this.isFront;
        if (this.isFront) {
            this.lensType = LensEngine.FRONT_LENS;
        } else {
            this.lensType = LensEngine.BACK_LENS;
        }
        if (this.mLensEngine != null) {
            this.mLensEngine.close();
        }
        this.startPreview(v);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP_PREVIEW:
                    stopPreview();
                    break;
                case TAKE_PHOTO:
                    takePhoto();
                    break;
                default:
                    break;
            }
        }
    };

    private void createFaceAnalyzer() {
        // Create a face analyzer. You can create an analyzer using the provided customized face detection parameter
        // MLFaceAnalyzerSetting
        MLFaceAnalyzerSetting setting =
                new MLFaceAnalyzerSetting.Factory()
                        .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
                        .setKeyPointType(MLFaceAnalyzerSetting.TYPE_UNSUPPORT_KEYPOINTS)
                        .setMinFaceProportion(0.1f)
                        .setTracingAllowed(true)
                        .create();
        this.analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting);
        if (this.detectMode == Constant.NEAREST_PEOPLE) {

            MLMaxSizeFaceTransactor transactor = new MLMaxSizeFaceTransactor.Creator(analyzer, new MLResultTrailer<MLFace>() {
                @Override
                public void objectCreateCallback(int itemId, MLFace obj) {
                    LiveFaceAnalyseActivity.this.overlay.clear();
                    if (obj == null) {
                        return;
                    }
                    LocalFaceGraphic faceGraphic =
                            new LocalFaceGraphic(LiveFaceAnalyseActivity.this.overlay, obj, LiveFaceAnalyseActivity.this);
                    LiveFaceAnalyseActivity.this.overlay.addGraphic(faceGraphic);
                    MLFaceEmotion emotion = obj.getEmotions();
                    if (emotion.getSmilingProbability() > smilingPossibility) {
                        safeToTakePicture = false;
                        mHandler.sendEmptyMessage(TAKE_PHOTO);
                    }
                }

                @Override
                public void objectUpdateCallback(MLAnalyzer.Result<MLFace> var1, MLFace obj) {
                    LiveFaceAnalyseActivity.this.overlay.clear();
                    if (obj == null) {
                        return;
                    }
                    LocalFaceGraphic faceGraphic =
                            new LocalFaceGraphic(LiveFaceAnalyseActivity.this.overlay, obj, LiveFaceAnalyseActivity.this);
                    LiveFaceAnalyseActivity.this.overlay.addGraphic(faceGraphic);
                    MLFaceEmotion emotion = obj.getEmotions();
                    if (emotion.getSmilingProbability() > smilingPossibility && safeToTakePicture) {
                        safeToTakePicture = false;
                        mHandler.sendEmptyMessage(TAKE_PHOTO);
                    }
                }

                @Override
                public void lostCallback(MLAnalyzer.Result<MLFace> result) {
                    LiveFaceAnalyseActivity.this.overlay.clear();
                }

                @Override
                public void completeCallback() {
                    LiveFaceAnalyseActivity.this.overlay.clear();

                }
            }).create();
            this.analyzer.setTransactor(transactor);

        } else {
            this.analyzer.setTransactor(new MLAnalyzer.MLTransactor<MLFace>() {
                @Override
                public void destroy() {
                }

                @Override
                public void transactResult(MLAnalyzer.Result<MLFace> result) {
                    SparseArray<MLFace> faceSparseArray = result.getAnalyseList();
                    int flag = 0;
                    for (int i = 0; i < faceSparseArray.size(); i++) {
                        MLFaceEmotion emotion = faceSparseArray.valueAt(i).getEmotions();
                        if (emotion.getSmilingProbability() > smilingPossibility) {
                            flag++;
                        }
                    }
                    if (flag > faceSparseArray.size() * smilingRate && safeToTakePicture) {
                        safeToTakePicture = false;
                        mHandler.sendEmptyMessage(TAKE_PHOTO);
                    }
                }
            });
        }
    }

    private void createLensEngine() {
        Context context = this.getApplicationContext();
        // Create LensEngine
        this.mLensEngine = new LensEngine.Creator(context, this.analyzer).setLensType(this.lensType)
                .applyDisplayDimension(640, 480)
                .applyFps(25.0f)
                .enableAutomaticFocus(true)
                .create();
    }

    private void startLensEngine() {
        this.restart.setVisibility(View.GONE);
        if (this.mLensEngine != null) {
            try {
                if (this.detectMode == Constant.NEAREST_PEOPLE) {
                    this.mPreview.start(this.mLensEngine, this.overlay);
                } else {
                    this.mPreview.start(this.mLensEngine);
                }

                this.safeToTakePicture = true;
            } catch (IOException e) {
                Log.e(LiveFaceAnalyseActivity.TAG, "Failed to start lens engine.", e);
                this.mLensEngine.release();
                this.mLensEngine = null;
            }
        }
    }

    private void takePhoto() {
        this.mLensEngine.photograph(null,
                new LensEngine.PhotographListener() {
                    @Override
                    public void takenPhotograph(byte[] bytes) {
                        mHandler.sendEmptyMessage(STOP_PREVIEW);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        saveBitmapToDisk(bitmap);
                    }
                });
    }

    public void startPreview(View view) {
        this.createFaceAnalyzer();
        this.mPreview.release();
        this.createLensEngine();
        this.startLensEngine();
    }

    private void stopPreview() {
        this.restart.setVisibility(View.VISIBLE);
        if (mLensEngine != null) {
            mLensEngine.release();
            this.safeToTakePicture = false;
        }
        if (analyzer != null) {
            try {
                this.analyzer.stop();
            } catch (IOException e) {
                Log.e(LiveFaceAnalyseActivity.TAG, "Stop failed: " + e.getMessage());
            }
        }
    }

    private String saveBitmapToDisk(Bitmap bitmap) {
        String filePath = "";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            boolean res = appDir.mkdir();
            if (!res) {
                Log.e(TAG, "saveBitmapToDisk failed");
                return "";
            }
        }

        String fileName = "SmileDemo" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();

            Uri uri = Uri.fromFile(file);
            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        try {
            filePath=file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
