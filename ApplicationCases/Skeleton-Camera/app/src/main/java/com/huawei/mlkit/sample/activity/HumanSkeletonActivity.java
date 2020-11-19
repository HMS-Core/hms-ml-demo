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

package com.huawei.mlkit.sample.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.utils.SkeletonUtils;
import com.huawei.mlkit.sample.views.graphic.ImageGraphic;
import com.huawei.mlkit.sample.views.graphic.SkeletonGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import androidx.appcompat.app.AppCompatActivity;

public class HumanSkeletonActivity extends AppCompatActivity {
    private static final String TAG = HumanSkeletonActivity.class.getSimpleName();
    private SurfaceView surfaceView;
    private GraphicOverlay overlay;
    private GraphicOverlay graphic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_skeleton);

        init();
        initial();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SkeletonUtils.closeLensEngine();
    }

    private void init() {
        surfaceView = findViewById(R.id.surface);
        overlay = findViewById(R.id.overlay);
        graphic = findViewById(R.id.graphic);
    }

    private void initial() {
        overlay.setCameraInfo(720, 1280, 0);

        Bitmap bitmap = SkeletonUtils.getBitmap();
        graphic.setCameraInfo(bitmap.getWidth(), bitmap.getHeight(), 0);
        ImageGraphic cameraImageGraphic = new ImageGraphic(graphic, BitmapFactory.decodeResource(getResources(), R.mipmap.model));
        graphic.addGraphic(cameraImageGraphic);
        SkeletonGraphic skeletonGraphic = new SkeletonGraphic(graphic, SkeletonUtils.getTemplateData());
        graphic.addGraphic(skeletonGraphic);
        graphic.postInvalidate();

        SkeletonUtils.initLensEngie(this);
        SkeletonUtils.setTransactor(overlay, HumanSkeletonActivity.this);
        surfaceView.getHolder().addCallback(new SurfaceViewCallback());
    }

    private static class SurfaceViewCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e(TAG, "surfaceCreated");
            SkeletonUtils.runLensEngine(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e(TAG, "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG, "surfaceDestroyed");
            /* If the camera is working, stop it.*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkeletonUtils.releaseLensEngine();
    }
}