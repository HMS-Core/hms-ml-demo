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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import com.huawei.hms.mlkit.sample.cn.R;
import com.huawei.hms.mlkit.sample.transactor.LocalSketlonTranstor;
import com.huawei.hms.mlkit.sample.util.ClickRepeat;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo app chooser which takes care of runtime permission requesting and allows you to pick from
 * all available testing Activities.
 */
public final class ChooserActivity extends BaseActivity
        implements OnRequestPermissionsResultCallback, View.OnClickListener {
    private static final String TAG = "ChooserActivity";
    private static final int PERMISSION_REQUESTS = 1;

    private static boolean isAsynchronous = true;

    public static LocalSketlonTranstor localSketlonTranstor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        setContentView(R.layout.activity_chooser);
        findViewById(R.id.face).setOnClickListener(new ClickRepeat(this));
        findViewById(R.id.skeletons).setOnClickListener(new ClickRepeat(this));
        findViewById(R.id.model).setOnClickListener(new ClickRepeat(this));
        findViewById(R.id.tongue).setOnClickListener(new ClickRepeat(this));
        findViewById(R.id.setting_img).setOnClickListener(new ClickRepeat(this));
        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }

        MLSkeletonAnalyzerSetting setting = new MLSkeletonAnalyzerSetting.Factory().create();
        this.localSketlonTranstor = new LocalSketlonTranstor(setting, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skeletons:
                startActivity(new Intent(ChooserActivity.this, WoodenManActivity.class));
                break;
            case R.id.model:
                startActivity(new Intent(ChooserActivity.this, ModelGameActivity.class));
                break;
            case R.id.tongue:
                startActivity(new Intent(ChooserActivity.this, TongueTwisterActivity.class));
                break;
            case R.id.setting_img:
                startActivity(new Intent(ChooserActivity.this, SettingActivity.class));
                break;
            default:
                break;
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
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public static boolean isAsynchronous() {
        return isAsynchronous;
    }

    public static void setIsAsynchronous(boolean isAsynchronous) {
        ChooserActivity.isAsynchronous = isAsynchronous;
    }

}
