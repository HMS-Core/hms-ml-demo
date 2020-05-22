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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.mlkit.sample.callback.ImageUtilCallBack;
import com.huawei.hms.mlsdk.common.internal.client.SmartLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private  Context context;
    private ImageUtilCallBack imageUtilCallBack;

    public ImageUtils(Context context){
        this.context = context;
    }

    public void setImageUtilCallBack(ImageUtilCallBack imageUtilCallBack){
        this.imageUtilCallBack = imageUtilCallBack;
    }

    // Save the picture to the system album and refresh it.
    public void saveToAlbum(Bitmap bitmap){
        File file = null;
        String fileName = System.currentTimeMillis() +".jpg";
        File root = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), this.context.getPackageName());
        File dir = new File(root, "image");
        if(dir.mkdirs() || dir.isDirectory()){
            file = new File(dir, fileName);
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();

        } catch (FileNotFoundException e) {
            SmartLog.e(TAG, e.getMessage());
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
        }finally {
            try {
                if(os != null) {
                    os.close();
                }
            }catch (IOException e){
                SmartLog.e(TAG, e.getMessage());
            }
        }
        if(file == null){
            return;
        }
        if(imageUtilCallBack != null) {
            imageUtilCallBack.callSavePath(file.getAbsolutePath());
        }
        // Gallery refresh.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String path = null;
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
            }
            MediaScannerConnection.scanFile(this.context, new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(uri);
                            ImageUtils.this.context.sendBroadcast(mediaScanIntent);
                        }
                    });
        } else {
            String relationDir = file.getParent();
            File file1 = new File(relationDir);
            this.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
        }
    }

    /**
     * Compare the size of the two pictures.
     *
     * @param foregroundBitmap the first bitmap
     * @param backgroundBitmap the second bitmap
     * @return true: same size; false: not.
     */
    public static boolean equalImageSize(Bitmap foregroundBitmap, Bitmap backgroundBitmap) {
        return backgroundBitmap.getHeight() == foregroundBitmap.getHeight() && backgroundBitmap.getWidth() == foregroundBitmap.getWidth();
    }

    /**
     * Scale background (background picture) size to foreground (foreground picture) size.
     *
     * @param foregroundBitmap foreground picture
     * @param backgroundBitmap background picture
     * @return A background image that is the same size as the foreground image.
     */
    public static Bitmap resizeImageToForegroundImage(Bitmap foregroundBitmap, Bitmap backgroundBitmap) {
        float scaleWidth = ((float) foregroundBitmap.getWidth() / backgroundBitmap.getWidth());
        float scaleHeight = ((float) foregroundBitmap.getHeight() / backgroundBitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        backgroundBitmap = Bitmap.createBitmap(backgroundBitmap, 0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), matrix, true);
        return backgroundBitmap;
    }
}
