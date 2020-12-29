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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;
import com.huawei.mlkit.sample.activity.adapter.GridViewAdapter;
import com.huawei.mlkit.sample.activity.adapter.skeleton.GridItem;
import com.huawei.mlkit.sample.activity.entity.GridViewItem;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.face.FaceDetectionActivity;
import com.huawei.mlkit.sample.activity.face3d.Live3DFaceAnalyseActivity;
import com.huawei.mlkit.sample.activity.facecompare.FaceVerificationActivity;
import com.huawei.mlkit.sample.activity.gesture.GestureActivity;
import com.huawei.mlkit.sample.activity.handkeypoint.HandKeypointActivity;
import com.huawei.mlkit.sample.activity.livenessdetection.HumanLivenessDetectionActivity;
import com.huawei.mlkit.sample.activity.skeleton.HumanSkeletonActivity;
import com.huawei.mlkit.sample.activity.skeleton.TemplateActivity;
import com.huawei.mlkit.sample.transactor.LocalSketlonTranstor;
import com.huawei.mlkit.sample.util.BitmapUtils;
import com.huawei.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.mlkit.sample.views.graphic.LocalSkeletonGraphic;
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
    private static final int[] ICONS = {com.huawei.mlkit.sample.R.drawable.icon_face, R.drawable.icon_skeleton ,
            R.drawable.icon_liveness,R.drawable.icon_handkeypoint,R.drawable.icon_face3d,R.drawable.icon_faceverification,R.drawable.icon_handkeypoint};

    private static final int[] TITLES = { com.huawei.mlkit.sample.R.string.face_detection,
            R.string.skeletlon, R.string.liveness_detection,R.string.handKeypoint, R.string.face_3d,R.string.faceVerification,R.string.gesture};
    private GridView mGridView;
    private ArrayList<GridViewItem> mDataList;


    private LocalSketlonTranstor localSketlonTranstor;

    private GraphicOverlay graphicOverlay;

    // Template (including the quantity provided by the SDK and that manually generated)
    private static int mCount = 0;

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
        MLSkeletonAnalyzerSetting setting = new MLSkeletonAnalyzerSetting.Factory().create();
        localSketlonTranstor = new LocalSketlonTranstor(setting, this, null);
        // Start thread to load bone template (Preloading the skeletal template in advance )
        new Thread(mRunnable).start();
    }

    /**
     * Read the ApiKey field in the sample-agconnect-services.json to obtain the API key of the application and set it.
     * For details about how to apply for the agconnect-services.json, see section https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-add-agc.
     */
    private void setApiKey(){
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
                        // Face detection
                        startActivity(new Intent(StartActivity.this, FaceDetectionActivity.class));
                        break;
                    case 1:
                        // Skeleton
                        startActivity(new Intent(StartActivity.this, HumanSkeletonActivity.class));
                        break;
                    case 2:
                        // LivenessDetection
                        startActivity(new Intent(StartActivity.this, HumanLivenessDetectionActivity.class));
                        break;
                    case 3:
                        // HandKeypoint
                        startActivity(new Intent(StartActivity.this, HandKeypointActivity.class));
                        break;
                    case 4:
                        // Face3D
                        startActivity(new Intent(StartActivity.this, Live3DFaceAnalyseActivity.class));
                        break;
                    case 5:
                        // FaceVerification
                        startActivity(new Intent(StartActivity.this, FaceVerificationActivity.class));
                        break;
                    case 6:
                        // HandKeypoint
                        startActivity(new Intent(StartActivity.this, GestureActivity.class));
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
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
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

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (localSketlonTranstor == null) {
                return;
            }
            getTemplateData();
        }
    };

    public static int getCount() {
        return mCount;
    }

    public static void setCount() {
        StartActivity.mCount = StartActivity.mCount + 1;
    }

    public  void getTemplateData() {
        final Bitmap tmpBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_img);
        MLFrame frame = new MLFrame.Creator().setBitmap(tmpBitmap).create();
        Task<List<MLSkeleton>> task = localSketlonTranstor.detectInImage(frame);
        task.addOnSuccessListener(new OnSuccessListener<List<MLSkeleton>>() {
            @Override
            public void onSuccess(List<MLSkeleton> results) {
                Log.i(TAG,"onSuccess"+results.size());
                // Detection success.
                if(results != null && !results.isEmpty()) {
                    if (graphicOverlay == null){
                        return;
                    }
                    graphicOverlay.clear();
                    CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, tmpBitmap);
                    graphicOverlay.addGraphic(imageGraphic);

                    LocalSkeletonGraphic skeletonGraphic = new LocalSkeletonGraphic(graphicOverlay, results);
                    graphicOverlay.addGraphic(skeletonGraphic);
                    graphicOverlay.postInvalidate();

                    GridItem gridItem = new GridItem();

                    gridItem.setBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay,
                            tmpBitmap.getWidth(), tmpBitmap.getHeight()));
                    gridItem.setSkeletonList(results);
                    TemplateActivity.getTemplateDataMap().put(TemplateActivity.getKey() + mCount, gridItem);
                    mCount++;
                }
            }
        });
    }
}
