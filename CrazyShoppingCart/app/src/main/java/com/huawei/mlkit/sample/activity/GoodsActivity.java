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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.utils.GameUtils;
import com.huawei.mlkit.sample.views.GameGraphic;

import java.lang.ref.WeakReference;

public class GoodsActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int REQUEST_CODE = 1;

    private static final int START_GAME = 2;
    private static final int CHECK_TIME = 3;
    private LensEnginePreview mPreview;
    private GameGraphic gameGraphic;
    private TextView topscore;
    private LinearLayout startgame;
    private LinearLayout gameover;
    private Button start;
    private TextView score;
    private ImageView back;
    private Button restart;
    private TextView timeText;
    private String timeStr;

    private float magnification = 1;

    private int time = 60;

    private boolean isPermission = false;

    private int speed = 6;

    private TimeHandler handler = new TimeHandler(GoodsActivity.this);

    static class TimeHandler extends Handler {
        private WeakReference<GoodsActivity> weakReference;

        private TimeHandler(GoodsActivity goodsActivity) {
            weakReference = new WeakReference<>(goodsActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            final GoodsActivity goodsActivity = weakReference.get();
            switch (msg.what) {
                case START_GAME:
                    goodsActivity.handler.sendEmptyMessageDelayed(CHECK_TIME, 1000);
                    goodsActivity.timeText.setText(goodsActivity.timeStr + goodsActivity.time);
                    break;
                case CHECK_TIME:
                    goodsActivity.time--;
                    if(goodsActivity.time==45){
                        goodsActivity.speed=8;
                        goodsActivity.gameGraphic.setSpeed(goodsActivity.speed);
                    }else if(goodsActivity.time==30){
                        goodsActivity.speed=10;
                        goodsActivity.gameGraphic.setSpeed(goodsActivity.speed);
                    }else if(goodsActivity.time==15){
                        goodsActivity.speed=12;
                        goodsActivity.gameGraphic.setSpeed(goodsActivity.speed);
                    }
                    goodsActivity.timeText.setText(goodsActivity.timeStr + goodsActivity.time);
                    if (goodsActivity.time <= 0) {
                        goodsActivity.gameGraphic.gameOver();
                    } else {
                        goodsActivity.handler.sendEmptyMessageDelayed(CHECK_TIME, 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        if (!isGranted(Manifest.permission.CAMERA)) {
            requestPermission(PERMISSIONS, REQUEST_CODE);
        } else {
            magnification = GameUtils.getMagnification(GoodsActivity.this);
        }

        GameUtils.createHandAnalyze();
        GameUtils.initLensEngie(GoodsActivity.this);
        init();
        initListener();
    }

    private void init() {
        mPreview = this.findViewById(R.id.preview);
        gameGraphic = findViewById(R.id.graphic);
        topscore = findViewById(R.id.topscore);
        startgame = findViewById(R.id.startgame);
        gameover = findViewById(R.id.gameover);
        start = findViewById(R.id.start);
        score = findViewById(R.id.score);
        back = findViewById(R.id.back);
        restart = findViewById(R.id.restart);
        timeText = findViewById(R.id.time);

        timeStr = getString(R.string.time);
        timeText.setText(timeStr + time);

        gameGraphic.initData(this, topscore, score, gameover);
        gameGraphic.setMagnification(magnification);
        GameUtils.setHandTransactor(gameGraphic);
    }


    private void initListener() {
        start.setOnClickListener(onClickListener);
        back.setOnClickListener(onClickListener);
        restart.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start:
                    startgame.setVisibility(View.GONE);
                    gameGraphic.setSpeed(4);
                    gameGraphic.startGame();
                    gameGraphic.invalidate();
                    handler.sendEmptyMessage(START_GAME);
                    break;
                case R.id.back:
                    finish();
                    break;
                case R.id.restart:
                    time = 60;
                    timeText.setText(timeStr + time);
                    gameover.setVisibility(View.GONE);
                    gameGraphic.startGame();
                    gameGraphic.invalidate();
                    handler.sendEmptyMessage(START_GAME);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        GameUtils.startLensEngine(mPreview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameUtils.stopPreview(mPreview);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameUtils.releaseAnalyze();
        handler.removeCallbacksAndMessages(null);
    }

    private boolean isGranted(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            isPermission = true;
            return true;
        } else {
            int checkSelfPermission = checkSelfPermission(permission);
            isPermission = checkSelfPermission == PackageManager.PERMISSION_GRANTED;
            return isPermission;
        }
    }

    private boolean requestPermission(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!isGranted(permissions[0])) {
            requestPermissions(permissions, requestCode);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (!isGranted(Manifest.permission.CAMERA)) {
                requestPermission(PERMISSIONS, REQUEST_CODE);
            } else {
                magnification = GameUtils.getMagnification(GoodsActivity.this);
            }
        }
    }
}