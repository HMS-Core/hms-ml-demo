/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.mlkit.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.mlkit.sample.activity.adapter.GridViewAdapter;
import com.huawei.mlkit.sample.custom.R;
import com.huawei.mlkit.sample.entity.GridViewItem;

import java.util.ArrayList;
import java.util.List;

public class CustModelActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CustModelActivity";
    private static final int PERMISSION_REQUESTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.setStatusBarColor(this, R.color.logo_background);
        setContentView(R.layout.activity_cust_model_main);
        findViewById(R.id.setting_img).setOnClickListener(this);

        if (!this.allPermissionsGranted()) {
            this.getRuntimePermissions();
        }
    }

    public void onLableClick(View view) {
        Intent intent = new Intent(this, CustModelLabelActivity.class);
        startActivity(intent);
    }

    public void onObjectClick(View view) {
        Intent intent = new Intent(this, CustModelObjectActivity.class);
        startActivity(intent);
    }

    private boolean allPermissionsGranted() {
        for (String permission : this.getRequiredPermissions()) {
            if (!CustModelActivity.isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setting_img) {
            startActivity(new Intent(CustModelActivity.this, SettingActivity.class));
        }
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : this.getRequiredPermissions()) {
            if (!CustModelActivity.isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), CustModelActivity.PERMISSION_REQUESTS);
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

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(CustModelActivity.TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(CustModelActivity.TAG, "Permission NOT granted: " + permission);
        return false;
    }

}
