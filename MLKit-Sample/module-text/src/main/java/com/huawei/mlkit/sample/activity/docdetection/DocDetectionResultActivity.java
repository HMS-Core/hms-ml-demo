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

package com.huawei.mlkit.sample.activity.docdetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.mlkit.sample.R;

public class DocDetectionResultActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout docBack;
    private ImageView desImage;
    private Bitmap resultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_detection_result);

        docBack = findViewById(R.id.doc_back);
        desImage = findViewById(R.id.des_image);
        docBack.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        byte[] bytes = bundle.getByteArray("bitmap");

        resultBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        desImage.setImageBitmap(resultBitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doc_back:
                finish();
                overridePendingTransition(0,R.anim.pic_out);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
    }
}