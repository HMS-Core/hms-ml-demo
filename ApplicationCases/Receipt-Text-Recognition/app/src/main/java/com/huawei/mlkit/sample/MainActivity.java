/**
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
package com.huawei.mlkit.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView result;
    private Button start;
    private RadioGroup languagetype;

    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_VIDEO = 1002;

    private TextView rulers;
    private EditText cardnumber;
    private TextView title;
    private EditText titlenumber;
    private String str;
    private int regexpnumber = 16;
    private String type = "zh";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initListener();
    }

    private void init() {
        result = findViewById(R.id.result);
        start = findViewById(R.id.start);
        rulers = findViewById(R.id.rulers);
        cardnumber = findViewById(R.id.number);
        rulers.setText(getString(R.string.cardnumberruler) + regexpnumber + getString(R.string.digits));
        title = findViewById(R.id.title);
        titlenumber = findViewById(R.id.titlenumber);
        languagetype = findViewById(R.id.language_type);

        cardnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int numbers;
                try {
                    numbers = Integer.parseInt(cardnumber.getText().toString());
                } catch (Exception e) {
                    numbers = 16;
                }
                rulers.setText(getString(R.string.cardnumberruler) + numbers + getString(R.string.digits));
                regexpnumber = numbers;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        titlenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                str = titlenumber.getText().toString();
                if (str.length() > 0) {
                    title.setText(getString(R.string.cardheadruler) + str);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initListener() {
        start.setOnClickListener(onClickListener);
        languagetype.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start:
                    if (!isGranted(Manifest.permission.CAMERA)) {
                        requestPermission(PERMISSIONS, REQUEST_CODE);
                        return;
                    } else {
                        detectPreview();
                    }
                    break;
                default:
                    break;
            }

        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.chinese:
                    type = "zh";
                    break;
                case R.id.english:
                    type = "en";
                    break;
                default:
                    break;
            }
        }
    };

    // video
    private void detectPreview() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("regexpnumber", regexpnumber);
        if (str != null && str.length() > 0) {
            intent.putExtra("title", str);
        }
        startActivityForResult(intent, REQUEST_IMAGE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i(TAG, "onActivityResult requestCode " + requestCode + ", resultCode " + resultCode);
        if (requestCode == REQUEST_IMAGE_VIDEO && resultCode == RESULT_OK) {
            String data = intent.getStringExtra("result");
            if (data == null) {
                result.setText(getString(R.string.nocard));
            }
            result.setText(data);
        }
    }

    private boolean isGranted(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            int checkSelfPermission = checkSelfPermission(permission);
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
