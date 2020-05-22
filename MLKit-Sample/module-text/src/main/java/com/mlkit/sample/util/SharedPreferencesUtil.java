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

package com.mlkit.sample.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static final String TAG = "SharedPreferencesUtil";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static SharedPreferencesUtil mSharedPreferencesUtil;


    public SharedPreferencesUtil(Context context) {
        mPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static SharedPreferencesUtil getInstance(Context context) {
        if (mSharedPreferencesUtil == null) {
            synchronized (SharedPreferencesUtil.class) {
                if(mSharedPreferencesUtil == null) {
                    mSharedPreferencesUtil = new SharedPreferencesUtil(context);
                }
            }
        }
        return mSharedPreferencesUtil;
    }

    public void putStringValue(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public String getStringValue(String key) {
        return mPreferences.getString(key, Constant.POSITION_EN);
    }

    public void putIntValue(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public int getIntValue(String key) {
        return mPreferences.getInt(key, -1);
    }
}
