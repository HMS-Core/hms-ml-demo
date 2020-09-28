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


package com.huawei.hms.mlkit.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.huawei.hms.mlkit.sample.cn.R;
import com.huawei.hms.mlkit.sample.util.ClickRepeat;


public class ModelGameActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ModelGameActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate Start.");
        setContentView(R.layout.activity_model_main);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.model_title).setOnClickListener(this);
        findViewById(R.id.iv_one).setOnClickListener(new ClickRepeat(this));
        findViewById(R.id.tv_one).setOnClickListener(new ClickRepeat(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
            case R.id.model_title:
                finish();
                break;
            case R.id.iv_one:
            case R.id.tv_one:
                startActivity(new Intent(ModelGameActivity.this, ModelGameStartOneActivity.class));
                break;
            default:
                break;
        }
    }

}
