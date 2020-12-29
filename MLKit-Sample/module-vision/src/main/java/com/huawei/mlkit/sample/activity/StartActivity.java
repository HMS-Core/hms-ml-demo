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
import android.widget.AdapterView;
import android.widget.GridView;

import com.huawei.agconnect.config.AGConnectServicesConfig;

import com.huawei.hms.mlplugin.productvisionsearch.MLProductVisionSearchCapture;
import com.huawei.hms.mlplugin.productvisionsearch.MLProductVisionSearchCaptureConfig;
import com.huawei.hms.mlplugin.productvisionsearch.MLProductVisionSearchCaptureFactory;
import com.huawei.hms.mlsdk.common.MLApplication;


import java.util.ArrayList;

import java.util.List;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.Imagesupersesolution.ImageSuperResolutionStartActivity;
import com.huawei.mlkit.sample.activity.adapter.GridViewAdapter;
import com.huawei.mlkit.sample.activity.documentskew.DocumentSkewStartActivity;
import com.huawei.mlkit.sample.activity.entity.GridViewItem;
import com.huawei.mlkit.sample.activity.fragment.ProductFragment;
import com.huawei.mlkit.sample.activity.imageclassfication.ImageClassificationActivity;
import com.huawei.mlkit.sample.activity.imageseg.ImageSegmentationActivity;
import com.huawei.mlkit.sample.activity.object.ObjectDetectionActivity;
import com.huawei.mlkit.sample.activity.scenedection.SceneStartActivity;
import com.huawei.mlkit.sample.activity.table.TableRecognitionStartActivity;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;


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
    private static final int[] ICONS = {R.drawable.icon_table,R.drawable.icon_segmentation,
            R.drawable.icon_object, R.drawable.icon_classification,
            R.drawable.icon_landmark, R.drawable.icon_image_super_resolution,
            R.drawable.icon_text_super_resolution, R.drawable.icon_scene_detection,R.drawable.icon_shopping,R.drawable.icon_documentskew};

    private static final int[] TITLES = {R.string.table,R.string.image_segmentation,
            R.string.object_detection, R.string.image_classification,
            R.string.landmark, R.string.image_super_resolution_s,
            R.string.text_super_resolution_s, R.string.scene_detection,R.string.photographed_shopping,R.string.document_skew};

    private GridView mGridView;
    private ArrayList<GridViewItem> mDataList;

    private GraphicOverlay graphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.setStatusBarColor(this, R.color.logo_background);
        this.setContentView(R.layout.activity_start);
        this.findViewById(R.id.setting_img).setOnClickListener(this);

        graphicOverlay = findViewById(R.id.fireFaceOverlay);
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
    private void setApiKey() {
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getApplication());
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }

    private void initClickEvent() {
        if (this.mGridView == null) {
            return;
        }
        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //Table Recognition
                        startActivity(new Intent(StartActivity.this, TableRecognitionStartActivity.class));
                        break;
                    case 1:
                        // Image Segmentation
                        startActivity(new Intent(StartActivity.this, ImageSegmentationActivity.class));
                        break;
                    case 2:
                        // Object detection and tracking
                        startActivity(
                                new Intent(StartActivity.this, ObjectDetectionActivity.class));
                        break;
                    case 3:
                        // Image classification
                        startActivity(new Intent(StartActivity.this, ImageClassificationActivity.class));
                        break;
                    case 4:
                        // Landmark recognition
                        Intent intent = new Intent(StartActivity.this, RemoteDetectionActivity.class);
                        intent.putExtra(Constant.MODEL_TYPE, Constant.CLOUD_LANDMARK_DETECTION);
                        startActivity(intent);
                        break;
                    case 5:
                        // Image super resolution
                        Intent intentIsr = new Intent(StartActivity.this, ImageSuperResolutionStartActivity.class);
                        intentIsr.putExtra(Constant.SUPER_RESOLUTION_TYPE, Constant.TYPE_IMAGE_SUPER_RESOLUTION);
                        startActivity(intentIsr);
                        break;
                    case 6:
                        // Text image super resolution
                        Intent intentTsr = new Intent(StartActivity.this, ImageSuperResolutionStartActivity.class);
                        intentTsr.putExtra(Constant.SUPER_RESOLUTION_TYPE, Constant.TYPE_TEXT_SUPER_RESOLUTION);
                        startActivity(intentTsr);
                        break;
                    case 7:
                        //Scene
                        startActivity(new Intent(StartActivity.this, SceneStartActivity.class));
                        break;
                    case 8:
                        // Product Visual Search
                        MLProductVisionSearchCaptureConfig config = new MLProductVisionSearchCaptureConfig.Factory()
                                .setLargestNumOfReturns(16)
                                .setProductFragment(new ProductFragment())
                                .setRegion(MLProductVisionSearchCaptureConfig.REGION_DR_CHINA)
                                .create();
                        MLProductVisionSearchCapture capture = MLProductVisionSearchCaptureFactory.getInstance().create(config);
                        capture.startCapture(StartActivity.this);
                        break;
                    case 9:
                        // Document Skew Corretion
                        startActivity(new Intent(StartActivity.this, DocumentSkewStartActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setting_img) {
            startActivity(new Intent(StartActivity.this, SettingActivity.class));
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
                    || (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] != PackageManager.PERMISSION_GRANTED)) {
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
