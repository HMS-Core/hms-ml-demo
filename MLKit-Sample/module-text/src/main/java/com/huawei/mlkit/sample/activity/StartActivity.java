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

package com.huawei.mlkit.sample.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.adapter.GridViewAdapter;
import com.huawei.mlkit.sample.activity.aft.AudioFileTranscriptionActivity;
import com.huawei.mlkit.sample.activity.asr.AsrAudioActivity;
import com.huawei.mlkit.sample.activity.asrlong.RealTimeTranscriptionActivity;
import com.huawei.mlkit.sample.activity.bcr.BankCardRecognitionActivity;
import com.huawei.mlkit.sample.activity.docdetection.RemoteDetectionActivity;
import com.huawei.mlkit.sample.activity.entity.GridViewItem;
import com.huawei.mlkit.sample.activity.gcr.GeneralCardRecognitionActivity;
import com.huawei.mlkit.sample.activity.icr.IDCardActivity;
import com.huawei.mlkit.sample.activity.sounddect.SoundDectActivity;
import com.huawei.mlkit.sample.activity.ocr.TextRecognitionActivity;
import com.huawei.mlkit.sample.activity.textemdding.TextEmbeddingActivity;
import com.huawei.mlkit.sample.activity.translate.TranslateActivity;
import com.huawei.mlkit.sample.activity.tts.TtsAnalyseActivity;
import com.huawei.mlkit.sample.util.Constant;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

public final class StartActivity extends BaseActivity
        implements OnRequestPermissionsResultCallback, View.OnClickListener {
    private static final String TAG = "StartActivity";
    public static final String API_KEY = "client/api_key";
    private static final int PERMISSION_REQUESTS = 1;
    private static final int[] ICONS = {R.drawable.icon_translate, R.drawable.icon_asr ,R.drawable.icon_tts, R.drawable.icon_aft,
            R.drawable.icon_bcr, R.drawable.icon_gcr, R.drawable.icon_text, R.drawable.icon_icr,
            R.drawable.icon_document,R.drawable.icon_sound_dect,R.drawable.icon_textembedding,R.drawable.icon_asr_long_voice};

    private static final int[] TITLES = {R.string.translate, R.string.asr,R.string.tts, R.string.aft, R.string.bcr,
            R.string.gcr, R.string.text_detection, R.string.icr, R.string.document_recognition,R.string.sound_dect_title,R.string.textembedding,R.string.speechRtt,};

    private GridView mGridView;
    private ArrayList<GridViewItem> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.setStatusBarColor(this, R.color.logo_background);
        this.setContentView(R.layout.activity_start);
        this.findViewById(R.id.setting_img).setOnClickListener(this);
        initData();
        this.mGridView = findViewById(R.id.gridview);
        GridViewAdapter mAdapter = new GridViewAdapter(this.mDataList, getApplicationContext());
        this.mGridView.setAdapter(mAdapter);
        initClickEvent();
        // Set the ApiKey of the application for accessing cloud services.
        setApiKey();
        if (!this.allPermissionsGranted()) {
            this.getRuntimePermissions();
        }
    }

    /**
     * Read the ApiKey field in the sample-agconnect-services.json to obtain the API key of the application and set it.
     * For details about how to apply for the sample-agconnect-services.json, see section https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-add-agc.
     */
    private void setApiKey(){
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getApplication());
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }

    private void initClickEvent() {
        if (this.mGridView == null) {
            return;
        }
        this.mGridView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(StartActivity.this, TranslateActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(StartActivity.this, AsrAudioActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(StartActivity.this, TtsAnalyseActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(StartActivity.this, AudioFileTranscriptionActivity.class));
                    break;
                case 4:
                    // BCR
                    startActivity(new Intent(StartActivity.this, BankCardRecognitionActivity.class));
                    break;
                case 5:
                    // GCR
                    startActivity(new Intent(StartActivity.this, GeneralCardRecognitionActivity.class));
                    break;
                case 6:
                    // Text recognition
                    startActivity(new Intent(StartActivity.this, TextRecognitionActivity.class));
                    break;
                case 7:
                    // ICR
                    startActivity(new Intent(StartActivity.this, IDCardActivity.class));
                    break;
                case 8:
                    // Document recognition
                    final Intent intent = new Intent(StartActivity.this, RemoteDetectionActivity.class);
                    intent.putExtra(Constant.MODEL_TYPE, Constant.CLOUD_DOCUMENT_TEXT_DETECTION);
                    startActivity(intent);
                    break;
                case 9:
                    // Voice Recognition
                    startActivity(new Intent(StartActivity.this, SoundDectActivity.class));
                    break;
                case 10:
                    // Text Embedding
                    startActivity(new Intent(StartActivity.this, TextEmbeddingActivity.class));
                    break;
                case 11:
                    // asr Long
                    startActivity(new Intent(StartActivity.this, RealTimeTranscriptionActivity.class));
                    break;
                default:
                    Toast.makeText(getApplicationContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setting_img) {
            this.startActivity(new Intent(StartActivity.this, SettingActivity.class));
        }
    }

    private void initData() {
        this.mDataList = new ArrayList<GridViewItem>();
        GridViewItem item;
        for (int i = 0; i < ICONS.length; i++) {
            item = new GridViewItem(ICONS[i], TITLES[i]);
            this.mDataList.add(item);
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : this.getRequiredPermissions()) {
            if (!StartActivity.isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : this.getRequiredPermissions()) {
            if (!StartActivity.isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), StartActivity.PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(StartActivity.TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(StartActivity.TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != StartActivity.PERMISSION_REQUESTS) {
            return;
        }
        boolean isNeedShowDiag = false;
        for (int i = 0; i < permissions.length; i++) {
            if ((permissions[i].equals(Manifest.permission.CAMERA) && grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    || (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    || (permissions[i].equals(Manifest.permission.RECORD_AUDIO) && grantResults[i] != PackageManager.PERMISSION_GRANTED)) {
                // If the camera or storage permissions are not authorized, need to pop up an authorization prompt box.
                isNeedShowDiag = true;
            }
        }
        if (isNeedShowDiag && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(this.getString(R.string.camera_permission_rationale))
                    .setPositiveButton(this.getString(R.string.settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            // Open the corresponding setting interface according to the package name.
                            intent.setData(Uri.parse("package:" + StartActivity.this.getPackageName()));
                            StartActivity.this.startActivityForResult(intent, 200);
                            StartActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StartActivity.this.finish();
                        }
                    }).create();
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (!this.allPermissionsGranted()) {
                this.getRuntimePermissions();
            }
        }
    }
}
