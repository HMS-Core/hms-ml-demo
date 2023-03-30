/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
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

package com.huawei.mlkit.sample.activity.si.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * LogUtils
 */
public class LogUtils {
    /**
     * MYCENTER_PKG_NAME
     */
    public static final String MYCENTER_PKG_NAME = "com.huawei.mycenter";

    private static final String SHA_256 = "SHA256";

    private static final String DEFAULT_APPEND = "0";

    private static final char DELIMITER = ':';

    private static final String TAG = "LogUtils";

    private static final String AUTHORITIES_SUFFIX = ".fileProvider";

    private static final String MYCENTER_ACTION = "com.huawei.mycenter.launcher";

    private static final String[] MYCENTER_PKG_SIGNS =
            {"BC:8B:6B:E3:AD:05:B9:75:2F:31:71:FA:23:2D:4B:5E:A9:3E:DB:88:AF:6E:56:10:0E:8F:56:EA:8B:D9:4E:F6"};

    private static final String LOG_TAG = "zipLogFileName";

    private static final String LOG_FILE_NAME = "simple.log";

    private static final String LOG_DIR = "Log";

    public static boolean containedSigns(Context context, String packageName, String[] signs) {
        return containedValues(signs, sha256(context, packageName));
    }

    public static boolean containedValues(String[] signs1, String[] signs2) {
        if (signs1 != null && signs1.length > 0 && signs2 != null && signs2.length > 0) {
            for (String s1 : signs1) {
                for (String s2 : signs2) {
                    if (TextUtils.equals(s1, s2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String[] sha256(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signArr = info.signatures;
            if (null != signArr && signArr.length > 0) {
                String[] result = new String[signArr.length];
                int index = 0;
                for (Signature sign : signArr) {
                    byte[] cert = sign.toByteArray();
                    MessageDigest md = MessageDigest.getInstance(SHA_256);
                    byte[] publicKey = md.digest(cert);
                    StringBuffer hexString = new StringBuffer();
                    for (int i = 0; i < publicKey.length; i++) {
                        String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                        if (appendString.length() == 1) {
                            hexString.append(DEFAULT_APPEND);
                        }
                        hexString.append(appendString);
                        hexString.append(DELIMITER);
                    }
                    String result2 = hexString.toString();
                    String substring = result2.substring(0, result2.length() - 1);
                    result[index++] = substring;
                }
                return result;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "SHA256 NameNotFoundException");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "SHA256 NoSuchAlgorithmException");
        }
        return null;
    }

    public static boolean verifyCaller(Context context, String callerPkg) {
        if (TextUtils.isEmpty(callerPkg) || !MYCENTER_PKG_NAME.equals(callerPkg)) {
            Log.e(TAG, "callerPkg[" + callerPkg + "] not match");
            return false;
        }
        boolean checkSigns = containedSigns(context, callerPkg, MYCENTER_PKG_SIGNS);
        if (!checkSigns) {
            Log.e(TAG, "callerSign not match");
            return false;
        }
        Intent intent = new Intent(MYCENTER_ACTION);
        List<ResolveInfo> resolveInfos =
                context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfos != null) {
            for (ResolveInfo info : resolveInfos) {
                if (MYCENTER_PKG_NAME.equals(info.activityInfo.packageName)) {
                    return true;
                }
            }
        }
        Log.e(TAG, "caller[" + callerPkg + "] not contain action>>" + MYCENTER_ACTION);
        return false;
    }

    public static void fileToGrantUriSingle(Context context, Intent data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "unsupported grantUriPermission");
            return;
        }
        String filesDir = context.getFilesDir() + File.separator + LOG_DIR;

        File file = new File(filesDir, LOG_FILE_NAME);
        Log.i(TAG, "file: " + file.getAbsolutePath());
        String authority = context.getPackageName() + AUTHORITIES_SUFFIX;
        try {
            Uri fileUri = FileProvider.getUriForFile(context, authority, file);
            Log.i(TAG, "fileToGrantUri: " + fileUri);
            context.grantUriPermission(LogUtils.MYCENTER_PKG_NAME, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            data.putExtra(LOG_TAG, fileUri.toString());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "onAction getUriForFile error: the given File is outside the paths supported by the provider");
        }
    }

    public static void fileToGrantUriList(Context context, Intent data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.w(TAG, "unsupported grantUriPermission");
            return;
        }
        File logDir = new File(context.getFilesDir(), LOG_DIR);
        File[] logFiles = logDir.listFiles();
        if (logFiles != null && logFiles.length > 0) {
            Log.i(TAG, "log file count = " + logFiles.length);
            String authority = context.getPackageName() + AUTHORITIES_SUFFIX;
            ArrayList<Uri> logUriList = new ArrayList<>();
            for (File logFile : logFiles) {
                Log.i(TAG, "logFileName = " + logFile.getName());
                try {
                    Uri fileUri = FileProvider.getUriForFile(context, authority, logFile);
                    context.grantUriPermission(MYCENTER_PKG_NAME, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    logUriList.add(fileUri);
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, "onAction getUriForFile error: the given File is outside the paths supported by the provider");
                }
            }
            data.putParcelableArrayListExtra(LOG_TAG, logUriList);
        }
    }
}
