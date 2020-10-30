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
package com.huawei.mlkit.sample.photoreader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.mlkit.sample.photoreader.databinding.ActivityMainBinding;
import com.huawei.mlkit.sample.photoreader.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String[] requiredPermissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final String EXTRA_TRANSLATION_MODE = "EXTRA_TRANSLATION_MODE";
    private static final int PERMISSION_REQUESTS = 1;

    private TranslationMode translationMode;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final TranslationMode defaultTranslationMode;
        if(Locale.getDefault().getISO3Country().equals("CHN")) {
            defaultTranslationMode = TranslationMode.ZH_EN;
        } else {
            defaultTranslationMode = TranslationMode.EN_ZH;
        }

        if(savedInstanceState != null && savedInstanceState.getSerializable(EXTRA_TRANSLATION_MODE) instanceof TranslationMode) {
            translationMode = (TranslationMode)savedInstanceState.getSerializable(EXTRA_TRANSLATION_MODE);
        } else {
            translationMode = defaultTranslationMode;
        }

        binding.btnActMainStartTranslate.setEnabled(allPermissionsGranted());
        binding.btnActMainStartTranslate.setOnClickListener(v -> {
            translatePhoto();
        });

        binding.buttonSwitchLang.setOnClickListener(v -> {
            switch (translationMode) {
                case EN_ZH:
                    translationMode = TranslationMode.ZH_EN;
                    break;
                case ZH_EN:
                    translationMode = TranslationMode.EN_ZH;
                    break;
            }
            updateSrcDestLanguage();
        });
        updateSrcDestLanguage();

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    private void updateSrcDestLanguage() {
        switch (translationMode) {
            case EN_ZH:
                binding.tvActMainSrcLanguage.setText(getString(R.string.english));
                binding.tvActMainDstLanguage.setText(getString(R.string.chinese));
                break;
            case ZH_EN:
                binding.tvActMainSrcLanguage.setText(getString(R.string.chinese));
                binding.tvActMainDstLanguage.setText(getString(R.string.english));
                break;
        }
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : requiredPermissions) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSION_REQUESTS) {
            return;
        }
        boolean isNeedShowDialog = false;
        for (int i = 0; i < permissions.length; i++) {
            if ((permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    || (permissions[i].equals(Manifest.permission.CAMERA) && grantResults[i] != PackageManager.PERMISSION_GRANTED)) {
                isNeedShowDialog = true;
                break;
            }
        }
        if (isNeedShowDialog && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.camera_permission_rationale))
                    .setPositiveButton(getString(R.string.settings), (dialog12, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 200);
                        startActivity(intent);
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog1, which) -> finish()).create();
            dialog.show();
        }
    }

    private void translatePhoto() {
        final Intent intent = new Intent(MainActivity.this, ReadPhotoActivity.class);
        switch (translationMode) {
            case EN_ZH:
                intent.putExtra(ReadPhotoActivity.EXTRA_SOURCE_LANGUAGE, Constant.ML_ENGLISH);
                intent.putExtra(ReadPhotoActivity.EXTRA_DESTINATION_LANGUAGE, Constant.ML_CHINESE);
                break;
            case ZH_EN:
                intent.putExtra(ReadPhotoActivity.EXTRA_SOURCE_LANGUAGE, Constant.ML_CHINESE);
                intent.putExtra(ReadPhotoActivity.EXTRA_DESTINATION_LANGUAGE, Constant.ML_ENGLISH);
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_TRANSLATION_MODE, translationMode);
    }

    enum TranslationMode {
        EN_ZH,
        ZH_EN
    }
}
