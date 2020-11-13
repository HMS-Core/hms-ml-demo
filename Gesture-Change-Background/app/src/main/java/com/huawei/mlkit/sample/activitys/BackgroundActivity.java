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

package com.huawei.mlkit.sample.activitys;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLCompositeAnalyzer;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzer;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerFactory;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.transactor.HandKeypointTransactor;
import com.huawei.mlkit.sample.transactor.ImageSegmentAnalyzerTransactor;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class BackgroundActivity extends AppCompatActivity implements HandKeypointTransactor.BackgroundListener {
    private static final String TAG = BackgroundActivity.class.getSimpleName();
    private LensEnginePreview mPreview;
    private GraphicOverlay mOverlay;
    private Button facingSwitch;

    private VideoView videoView;

    private LensEngine mLensEngine;
    private MLCompositeAnalyzer analyzer;
    private int lensType = LensEngine.FRONT_LENS;
    private boolean isFront = true;
    private ImageSegmentAnalyzerTransactor imageSegmentAnalyzerTransactor;

    private String uriHead;
    private int[] uri = {R.raw.city};

    private ImageView background;

    private int[] backgroundId = {R.drawable.beach, R.drawable.bed, R.drawable.cartoon,
            R.drawable.dommsday, R.drawable.kitchen, R.drawable.market, R.drawable.pearl,
            R.drawable.space, R.drawable.universe,};

    private BackgroundHandler backgroundHandler = new BackgroundHandler(this);

    static class BackgroundHandler extends Handler {
        private WeakReference<BackgroundActivity> weakReference;

        private BackgroundHandler(BackgroundActivity backgroundActivity) {
            weakReference = new WeakReference<>(backgroundActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (weakReference == null || weakReference.get() == null) {
                return;
            }

            BackgroundActivity backgroundActivity = weakReference.get();

            boolean isImage = (boolean) msg.obj;
            int count = msg.arg1;
            if (isImage) {
                backgroundActivity.videoView.setVisibility(View.GONE);
                backgroundActivity.background.setVisibility(View.VISIBLE);
                backgroundActivity.background.setImageDrawable(backgroundActivity.getResources().getDrawable(backgroundActivity.backgroundId[count]));
            } else {

                backgroundActivity.videoView.setVisibility(View.VISIBLE);

                backgroundActivity.background.setVisibility(View.INVISIBLE);

                backgroundActivity.resetPlayer(backgroundActivity.uriHead + backgroundActivity.uri[count]);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        initData();
        init();
        initListener();

        createAnalyzer();
        createLensEngine();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPlay();
        startLensEngine();
    }

    int position = 0;

    @Override
    protected void onPause() {
        super.onPause();
        position = videoView.getCurrentPosition();
        stopPlay();
        stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlay();
        releaseAnalyze();
    }

    private void initData() {
        uriHead = "android.resource://" + getPackageName() + "/";
    }

    private void init() {
        mPreview = findViewById(R.id.preview);
        mOverlay = findViewById(R.id.graphic);
        facingSwitch = findViewById(R.id.facingSwitch);
        videoView = findViewById(R.id.video);
        background = findViewById(R.id.background);

        videoView.setVideoURI(Uri.parse(uriHead + uri[0]));
    }

    private void initListener() {
        facingSwitch.setOnClickListener(onClickListener);
    }

    private void createAnalyzer() {
        MLImageSegmentationAnalyzer imageSegmentationAnalyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer();
        imageSegmentAnalyzerTransactor = new ImageSegmentAnalyzerTransactor(mOverlay);
        imageSegmentationAnalyzer.setTransactor(imageSegmentAnalyzerTransactor);

        MLHandKeypointAnalyzer handKeypointAnalyzer = MLHandKeypointAnalyzerFactory.getInstance().getHandKeypointAnalyzer();
        HandKeypointTransactor handKeypointTransactor = new HandKeypointTransactor(backgroundId.length - 1, uri.length - 1);
        handKeypointAnalyzer.setTransactor(handKeypointTransactor);

        analyzer = new MLCompositeAnalyzer.Creator()
                .add(imageSegmentationAnalyzer)
                .add(handKeypointAnalyzer)
                .create();

        handKeypointTransactor.setBackgroundListener(this);
    }

    private void createLensEngine() {
        mLensEngine = new LensEngine.Creator(this, analyzer)
                .setLensType(lensType)
                .applyDisplayDimension(1280, 720)
                .applyFps(25.0f)
                .enableAutomaticFocus(true)
                .create();
    }

    public void startLensEngine() {
        if (mLensEngine != null) {
            try {
                mPreview.start(mLensEngine);
            } catch (IOException e) {
                mLensEngine.release();
                mLensEngine = null;
            }
        }
    }

    public void stopPreview() {
        mPreview.stop();
    }

    public void releaseAnalyze() {
        if (mLensEngine != null) {
            mLensEngine.release();
        }
        if (analyzer != null) {
            analyzer.destroy();
        }
    }

    private void startPlay() {
        if (!videoView.isPlaying()) {
            videoView.seekTo(position);
            videoView.start();
        }
    }

    private void stopPlay() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    private void releasePlay() {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isFront = !isFront;
            imageSegmentAnalyzerTransactor.setFront(isFront);
            if (isFront) {
                lensType = LensEngine.FRONT_LENS;
            } else {
                lensType = LensEngine.BACK_LENS;
            }
            if (mLensEngine != null) {
                mLensEngine.close();
            }
            createLensEngine();
            startLensEngine();
        }
    };

    @Override
    public void changeState(boolean isImage, int count) {
        Message message = backgroundHandler.obtainMessage();
        message.obj = isImage;
        message.arg1 = count;
        backgroundHandler.sendMessage(message);

    }

    private void resetPlayer(String uri) {
        videoView.setVideoURI(Uri.parse(uri));

        videoView.start();
    }
}
