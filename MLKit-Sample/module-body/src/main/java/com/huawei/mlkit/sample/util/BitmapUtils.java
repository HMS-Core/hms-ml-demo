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

package com.huawei.mlkit.sample.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera.CameraInfo;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.mlkit.sample.camera.FrameMetadata;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    /**
     * Convert nv21 format byte buffer to bitmap
     *
     * @param data ByteBuffer data
     * @param metadata Frame meta data
     * @return Bitmap object
     */
    @Nullable
    public static Bitmap getBitmap(ByteBuffer data, FrameMetadata metadata) {
        data.rewind();
        byte[] imageBuffer = new byte[data.limit()];
        data.get(imageBuffer, 0, imageBuffer.length);
        try {
            YuvImage yuvImage = new YuvImage(imageBuffer, ImageFormat.NV21, metadata.getWidth(),
                    metadata.getHeight(), null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, metadata.getWidth(), metadata.getHeight()), 80, stream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
            return rotateBitmap(bitmap, metadata.getRotation(), metadata.getCameraFacing());
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return null;
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int rotation, int facing) {
        Matrix matrix = new Matrix();
        int rotationDegree = 0;
        if (rotation == MLFrame.SCREEN_SECOND_QUADRANT) {
            rotationDegree = 90;
        } else if (rotation == MLFrame.SCREEN_THIRD_QUADRANT) {
            rotationDegree = 180;
        } else if (rotation == MLFrame.SCREEN_FOURTH_QUADRANT) {
            rotationDegree = 270;
        }
        matrix.postRotate(rotationDegree);
        if (facing != CameraInfo.CAMERA_FACING_BACK) {
            matrix.postScale(-1.0f, 1.0f);
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static void recycleBitmap(Bitmap... bitmaps) {
        for (Bitmap bitmap : bitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    private static String getImagePath(Activity activity, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    public static Bitmap loadFromPath(Activity activity, Uri uri, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        String path = getImagePath(activity, uri);
        BitmapFactory.decodeFile(path, options);
        int sampleSize = calculateInSampleSize(options, width, height);
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;

        Bitmap bitmap = zoomImage(BitmapFactory.decodeFile(path, options), width, height);
        return rotateBitmap(bitmap, getRotationAngle(path));
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate height and required height scale.
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            // Calculate width and required width scale.
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Take the larger of the values.
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // Scale pictures to screen width.
    private static Bitmap zoomImage(Bitmap imageBitmap, int targetWidth, int maxHeight) {
        float scaleFactor =
                Math.max(
                        (float) imageBitmap.getWidth() / (float) targetWidth,
                        (float) imageBitmap.getHeight() / (float) maxHeight);
        Bitmap resizedBitmap =
                Bitmap.createScaledBitmap(
                        imageBitmap,
                        (int) (imageBitmap.getWidth() / scaleFactor),
                        (int) (imageBitmap.getHeight() / scaleFactor),
                        true);

        return resizedBitmap;
    }

    /**
     * Get the rotation angle of the photo.
     *
     * @param path photo path.
     * @return angle.
     */
    public static int getRotationAngle(String path) {
        int rotation = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to get rotation: " + e.getMessage());
        }
        return rotation;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Failed to rotate bitmap: " + e.getMessage());
        }
        if (result == null) {
            return bitmap;
        }
        return result;
    }

    /**
     * Stretch the bitmap based on the given width and height
     *
     * @param origin    Original image
     * @param newWidth  Width of the new bitmap
     * @param newHeight Height of the new bitmap
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        float scaleWidth;
        float scaleHeight;
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        if(height>width){
            scaleWidth = ((float) newWidth) / width;
            scaleHeight = ((float) newHeight) / height;
        }else{
            scaleWidth = ((float) newWidth) / height;
            scaleHeight = ((float) newHeight) / width;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        return newBitmap;
    }

    /**
     * Fusion of two images.
     * @param background background image.
     * @param foreground foreground image.
     * @return image.
     */
    public static Bitmap joinBitmap(Bitmap background, Bitmap foreground) {
        if (background == null || foreground == null) {
            Log.e(TAG, "bitmap is null.");
            return null;
        }

        if (background.getHeight() != foreground.getHeight() || background.getWidth() != foreground.getWidth()) {
            Log.e(TAG, "bitmap size is not match.");
            return null;
        }
        Bitmap newmap = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, 0, 0, null);
        canvas.save();
        canvas.restore();
        return newmap;
    }

    public static void saveToAlbum(Bitmap bitmap, final Context context){
        File file = null;
        String fileName = System.currentTimeMillis() +".jpg";
        File root = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), context.getPackageName());
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
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }finally {
            try {
                if(os != null) {
                    os.close();
                }
            }catch (IOException e){
                Log.e(TAG, e.getMessage());
            }
        }

        // Insert pictures into the system gallery.
        try {
            if (null != file) {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getCanonicalPath(), fileName, null);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        if (file == null) {
            return;
        }
        // Gallery refresh.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String path = null;
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            MediaScannerConnection.scanFile(context, new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(uri);
                            context.sendBroadcast(mediaScanIntent);
                        }
                    });
        } else {
            String relationDir = file.getParent();
            File file1 = new File(relationDir);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
        }
    }

    public static Bitmap loadBitmapFromView(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0,0,width,height);
        view.draw(canvas);
        return bitmap;
    }
}
