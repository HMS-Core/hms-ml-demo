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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.utils.GameUtils;
import com.huawei.mlkit.sample.views.GameGraphic;

import androidx.appcompat.app.AppCompatActivity;

public class HandGameActivity extends AppCompatActivity {
    private static final String TAG = HandGameActivity.class.getSimpleName();
    private LensEnginePreview mPreview;
    private GameGraphic gameGraphic;
    private RelativeLayout gamestart;
    private RelativeLayout gameover;
    private ImageView start;
    private ImageView exit;
    private ImageView restart;
    private TextView score;

    private int level = 4;
    private float magnification = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_game);

        init();
        initListener();
    }

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
    }

    private void init() {
        mPreview = this.findViewById(R.id.preview);
        gameGraphic = findViewById(R.id.graphic);
        gamestart = findViewById(R.id.gamestart);
        gameover = findViewById(R.id.gameover);
        start = findViewById(R.id.start);
        exit = findViewById(R.id.exit);
        restart = findViewById(R.id.restart);
        score = findViewById(R.id.score);

        Intent intent = getIntent();
        if (intent != null) {
            level = intent.getIntExtra("level", 4);
            magnification = intent.getFloatExtra("magnification", 1);
        }
        gameGraphic.initData(this, gameover, score, level, magnification);

        GameUtils.setHandTransactor(gameGraphic);
    }

    private void initListener() {
        start.setOnClickListener(onClickListener);
        exit.setOnClickListener(onClickListener);
        restart.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start:
                    gamestart.setVisibility(View.GONE);
                    gameGraphic.startGame();
                    gameGraphic.invalidate();
                    break;
                case R.id.exit:
                    finish();
                    break;
                case R.id.restart:
                    gameover.setVisibility(View.GONE);
                    gameGraphic.startGame();
                    gameGraphic.invalidate();
                    break;
                default:
                    break;
            }
        }
    };
}