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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.utils.GameUtils;

import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 1;
    private RelativeLayout face;
    private RelativeLayout hand;
    private LinearLayout linear_level;
    private TextView level;

    private ImageView gameimage;
    private TextView gamename;

    private float magnification = 1;

    private int choice = 0;
    private String[] pickString;

    private RadioGroup radioGroup;
    private TextView cancle;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        init();
        initListener();
    }

    //init widget
    private void init() {
        face = findViewById(R.id.face);
        hand = findViewById(R.id.hand);
        linear_level = findViewById(R.id.linear_level);
        level = findViewById(R.id.level);
        gameimage = findViewById(R.id.gameimage);
        gamename = findViewById(R.id.gamename);


        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null);
        radioGroup = view.findViewById(R.id.radiogroup);
        cancle = view.findViewById(R.id.cancle);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view);

        dialog = builder.create();
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);//去掉圆角背景背后的棱角
        dialogWindow.setGravity(Gravity.BOTTOM);

        if (!isCN(this)) {
            gameimage.setVisibility(View.GONE);
            gamename.setVisibility(View.VISIBLE);
        }
    }

    //add click monitor
    private void initListener() {
        face.setOnClickListener(onClickListener);
        hand.setOnClickListener(onClickListener);
        linear_level.setOnClickListener(onClickListener);

        radioGroup.setOnCheckedChangeListener(onCheckChangeListener);
        cancle.setOnClickListener(onClickListener);
    }

    private RadioGroup.OnCheckedChangeListener onCheckChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.hight://chose hight level
                    choice = 0;
                    level.setText(pickString[choice]);
                    break;
                case R.id.middle://chose middle level
                    choice = 1;
                    level.setText(pickString[choice]);
                    break;
                case R.id.low://chose low level
                    choice = 2;
                    level.setText(pickString[choice]);
                    break;
                default:
                    break;
            }
            dialog.dismiss();
        }
    };


    /**
     * init data
     */
    private void initData() {
        pickString = new String[]{getString(R.string.hight), getString(R.string.middle), getString(R.string.low)};
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.face://face
                    Log.d(TAG, "click face");
                    if (!isGranted(Manifest.permission.CAMERA)) {
                        requestPermission(PERMISSIONS, REQUEST_CODE);
                    } else {
                        GameUtils.createFaceAnalyze();
                        magnification = GameUtils.getMagnification(MainActivity.this) + 0.5f;
                        GameUtils.initLensEngine(MainActivity.this);
                        Intent faceIntent = new Intent(MainActivity.this, FaceGameActivity.class);
                        if (choice == 0) {
                            faceIntent.putExtra("level", 8);
                        } else if (choice == 1) {
                            faceIntent.putExtra("level", 4);
                        } else if (choice == 2) {
                            faceIntent.putExtra("level", 1);
                        }
                        faceIntent.putExtra("magnification", magnification);
                        startActivity(faceIntent);
                    }
                    break;
                case R.id.hand://hand
                    Log.d(TAG, "click hand");
                    if (!isGranted(Manifest.permission.CAMERA)) {
                        requestPermission(PERMISSIONS, REQUEST_CODE);
                    } else {
                        GameUtils.createHandAnalyze();
                        magnification = GameUtils.getMagnification(MainActivity.this) + 0.5f;
                        GameUtils.initLensEngine(MainActivity.this);
                        Intent handIntent = new Intent(MainActivity.this, HandGameActivity.class);
                        if (choice == 0) {
                            handIntent.putExtra("level", 8);
                        } else if (choice == 1) {
                            handIntent.putExtra("level", 4);
                        } else if (choice == 2) {
                            handIntent.putExtra("level", 1);
                        }
                        handIntent.putExtra("magnification", magnification);
                        startActivity(handIntent);
                    }
                    break;

                case R.id.linear_level://click level
                    dialog.show();
                    break;
                case R.id.cancle://dialog dismiss
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

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


    //language mode
    public static boolean isCN(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.equals("zh"))
            return true;
        else
            return false;
    }

}