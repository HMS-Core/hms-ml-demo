/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.mlkit.sample.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import com.huawei.hms.mlsdk.common.MLPosition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ImageHelper {
    private static final String TAG = "ImageHelper";

    public static Bitmap drawBitmap(Bitmap bm, float hue, float saturation, float lum) {
        //create hue matrix
        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.setRotate(0, hue);//0:Red
        hueMatrix.setRotate(1, hue);//1:Green
        hueMatrix.setRotate(2, hue);//2:Blue

        //create saturation matrix
        ColorMatrix saturationMatrix = new ColorMatrix();
        //set saturation
        saturationMatrix.setSaturation(saturation);

        //create lum matrix
        ColorMatrix lumMatrix = new ColorMatrix();
        //set lum
        lumMatrix.setScale(lum, lum, lum, 1);

        //mix matrix
        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(hueMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(lumMatrix);


        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));

        Bitmap bitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bm, 0, 0, paint);
        return bitmap;
    }


    /**
     * Eye enlargement algorithm
     *
     * @param bitmap           original bitmap
     * @param leftCenterPoint  center point of left eye
     * @param rightCenterPoint center point of right eye
     * @param leftradius       left eye magnification radius
     * @param rightradius      right eye magnification radius
     * @param sizeLevel        level  [0,4]
     */
    public static Bitmap magnifyEyes(Bitmap bitmap, Point leftCenterPoint, Point rightCenterPoint, int leftradius, int rightradius, float sizeLevel) {

        if (leftCenterPoint == null && rightCenterPoint == null) {
            return null;
        }
        if (leftCenterPoint != null) {
            bitmap = magnifyEye(bitmap, leftCenterPoint, leftradius, sizeLevel);
        }
        if (rightCenterPoint != null) {
            bitmap = magnifyEye(bitmap, rightCenterPoint, rightradius, sizeLevel);
        }
        return bitmap;
    }

    public static Bitmap magnifyEye(Bitmap bitmap, Point centerPoint, int radius, float sizeLevel) {
        Bitmap resultBitmap = bitmap.copy(bitmap.getConfig(), true);
        int left = Math.max(centerPoint.x - radius, 0);
        int top = Math.max(centerPoint.y - radius, 0);
        int right = centerPoint.x + radius > bitmap.getWidth() ? bitmap.getWidth() - 1 : centerPoint.x + radius;
        int bottom = centerPoint.y + radius > bitmap.getHeight() ? bitmap.getHeight() - 1 : centerPoint.y + radius;
        int powRadius = radius * radius;

        int offsetX, offsetY, powDistance, powOffsetX, powOffsetY;

        int disX, disY;

        //When it is negative, it is reduced
        float strength = (5 + sizeLevel * 2) / 10;

        for (int i = top; i <= bottom; i++) {
            offsetY = i - centerPoint.y;
            for (int j = left; j <= right; j++) {
                offsetX = j - centerPoint.x;
                powOffsetX = offsetX * offsetX;
                powOffsetY = offsetY * offsetY;
                powDistance = powOffsetX + powOffsetY;

                if (powDistance <= powRadius) {
                    double distance = Math.sqrt(powDistance);
                    double sinA = offsetX / distance;
                    double cosA = offsetY / distance;

                    double scaleFactor = distance / radius - 1;
                    scaleFactor = (1 - scaleFactor * scaleFactor * (distance / radius) * strength);

                    distance = distance * scaleFactor;
                    disY = (int) (distance * cosA + centerPoint.y + 0.5);
                    disY = checkY(disY, bitmap);
                    disX = (int) (distance * sinA + centerPoint.x + 0.5);
                    disX = checkX(disX, bitmap);
                    //the central point is not handled
                    if (!(j == centerPoint.x && i == centerPoint.y)) {
                        resultBitmap.setPixel(j, i, bitmap.getPixel(disX, disY));
                    }
                }
            }
        }
        return resultBitmap;
    }

    private static int checkY(int disY, Bitmap bitmap) {
        if (disY < 0) {
            disY = 0;
        } else if (disY >= bitmap.getHeight()) {
            disY = bitmap.getHeight() - 1;
        }
        return disY;
    }

    private static int checkX(int disX, Bitmap bitmap) {
        if (disX < 0) {
            disX = 0;
        } else if (disX >= bitmap.getWidth()) {
            disX = bitmap.getWidth() - 1;
        }
        return disX;
    }

    public static Bitmap getScaleBitmap(String filepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath);
    }

    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;

    /**
     * face slim algorithm
     *
     * @param bitmap         original bitmap
     * @param leftFacePoint  the list of left face
     * @param rightFacePoint the list of right face
     * @param level          level
     */
    public static Bitmap slimFace(Bitmap bitmap, List<MLPosition> leftFacePoint, List<MLPosition> rightFacePoint, int level) {
        if (bitmap == null) {
            return null;
        }
        if (leftFacePoint == null || leftFacePoint.size() <= 0) {
            return null;
        }
        if (rightFacePoint == null || rightFacePoint.size() <= 0) {
            return null;
        }
        //the number of intersection coordinates
        int count = (WIDTH + 1) * (HEIGHT + 1);

        //coordinates used to save count
        float[] verts = new float[count * 2];


        float bmWidth = bitmap.getWidth();
        float bmHeight = bitmap.getHeight();

        int index = 0;
        for (int i = 0; i < HEIGHT + 1; i++) {
            float fy = bmHeight * i / HEIGHT;
            for (int j = 0; j < WIDTH + 1; j++) {
                float fx = bmWidth * j / WIDTH;
                //the x-axis coordinates are placed on the even number
                verts[index * 2] = fx;
                //the y-axis coordinates are placed in odd digits位
                verts[index * 2 + 1] = fy;
                index += 1;
            }
        }
        int r = 90 + 15 * level;
        warp(verts, leftFacePoint.get(4).getX(), leftFacePoint.get(4).getY(), (leftFacePoint.get(4).getX() + rightFacePoint.get(4).getX()) / 2, leftFacePoint.get(4).getY(), r);
        warp(verts, leftFacePoint.get(6).getX(), leftFacePoint.get(6).getY(), (leftFacePoint.get(6).getX() + rightFacePoint.get(6).getX()) / 2, leftFacePoint.get(6).getY(), r);
        warp(verts, leftFacePoint.get(8).getX(), leftFacePoint.get(8).getY(), (leftFacePoint.get(8).getX() + rightFacePoint.get(8).getX()) / 2, leftFacePoint.get(8).getY(), r);
        warp(verts, leftFacePoint.get(10).getX(), leftFacePoint.get(10).getY(), (leftFacePoint.get(10).getX() + rightFacePoint.get(10).getX()) / 2, leftFacePoint.get(10).getY(), r);
        warp(verts, leftFacePoint.get(12).getX(), leftFacePoint.get(12).getY(), (leftFacePoint.get(12).getX() + rightFacePoint.get(12).getX()) / 2, leftFacePoint.get(12).getY(), r);
        warp(verts, leftFacePoint.get(14).getX(), leftFacePoint.get(14).getY(), (leftFacePoint.get(14).getX() + rightFacePoint.get(14).getX()) / 2, leftFacePoint.get(14).getY(), r);
        warp(verts, leftFacePoint.get(16).getX(), leftFacePoint.get(16).getY(), (leftFacePoint.get(16).getX() + rightFacePoint.get(16).getX()) / 2, leftFacePoint.get(16).getY(), r);

        warp(verts, rightFacePoint.get(4).getX(), rightFacePoint.get(4).getY(), (leftFacePoint.get(4).getX() + rightFacePoint.get(4).getX()) / 2, rightFacePoint.get(4).getY(), r);
        warp(verts, rightFacePoint.get(6).getX(), rightFacePoint.get(6).getY(), (leftFacePoint.get(6).getX() + rightFacePoint.get(6).getX()) / 2, rightFacePoint.get(6).getY(), r);
        warp(verts, rightFacePoint.get(8).getX(), rightFacePoint.get(8).getY(), (leftFacePoint.get(8).getX() + rightFacePoint.get(8).getX()) / 2, rightFacePoint.get(8).getY(), r);
        warp(verts, rightFacePoint.get(10).getX(), rightFacePoint.get(10).getY(), (leftFacePoint.get(10).getX() + rightFacePoint.get(10).getX()) / 2, rightFacePoint.get(10).getY(), r);
        warp(verts, rightFacePoint.get(12).getX(), rightFacePoint.get(12).getY(), (leftFacePoint.get(12).getX() + rightFacePoint.get(12).getX()) / 2, rightFacePoint.get(12).getY(), r);
        warp(verts, rightFacePoint.get(14).getX(), rightFacePoint.get(14).getY(), (leftFacePoint.get(14).getX() + rightFacePoint.get(14).getX()) / 2, rightFacePoint.get(14).getY(), r);
        warp(verts, rightFacePoint.get(16).getX(), rightFacePoint.get(16).getY(), (leftFacePoint.get(16).getX() + rightFacePoint.get(16).getX()) / 2, rightFacePoint.get(16).getY(), r);

        Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmapMesh(bitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
        return resultBitmap;
    }

    private static void warp(float verts[], float startX, float startY, float endX, float endY, int r) {
        //calculate drag distance
        float ddPull = (endX - startX) * (endX - startX) + (endY - startY) * (endY - startY);
        float dPull = (float) Math.sqrt(ddPull);
        //dPull = screenWidth - dPull >= 0.0001f ? screenWidth - dPull : 0.0001f;
        if (dPull < 2 * r) {
            dPull = 2 * r;
        }

        int powR = r * r;
        int index = 0;
        int offset = 1;
        for (int i = 0; i < HEIGHT + 1; i++) {
            for (int j = 0; j < WIDTH + 1; j++) {
                //the boundary area is not treated
                if (i < offset || i > HEIGHT - offset || j < offset || j > WIDTH - offset) {
                    index = index + 1;
                    continue;
                }
                //calculate the distance between each coordinate point and the touch point
                float dx = verts[index * 2] - startX;
                float dy = verts[index * 2 + 1] - startY;
                float dd = dx * dx + dy * dy;

                if (dd < powR) {
                    //twist
                    double e = (powR - dd) * (powR - dd) / ((powR - dd + dPull * dPull) * (powR - dd + dPull * dPull));
                    double pullX = e * (endX - startX);
                    double pullY = e * (endY - startY);
                    verts[index * 2] = (float) (verts[index * 2] + pullX);
                    verts[index * 2 + 1] = (float) (verts[index * 2 + 1] + pullY);
                }
                index = index + 1;
            }
        }
    }

    public static String savePhoto(byte[] data) {
        if (data.length <= 0) {
            return null;
        }
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "NO SD CARD");
            return null;
        }
        // save picture
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        String filePath = Environment.getExternalStorageDirectory().getPath()
                + File.separator
                + "BeautyCamera";
        String fileName = System.currentTimeMillis() + ".jpg";
        File localFile = new File(filePath);
        if (!localFile.exists()) {
            boolean local = localFile.mkdir();
            if (!local) {
                Log.e(TAG, "localFile create fail");
                return null;
            }
        }

        File imagefile = new File(localFile, fileName);
        if (!imagefile.exists()) {
            try {
                boolean image = imagefile.createNewFile();
                if (!image) {
                    Log.e(TAG, "imagefile create fail");
                    return null;
                }
            } catch (IOException e) {
                Log.e(TAG, "create fail");
                return null;
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagefile);
            if (bitmap == null) {
                Log.e(TAG, "bitmap==null");
                return null;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return imagefile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Exception");
            return null;
        }

    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int facing) {
        int rotation = 0;

        Matrix matrix = new Matrix();
        if (facing == 0) {
            rotation = 90;
        } else if (facing == 1) {
            rotation = 270;
        }
        matrix.setRotate(rotation, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
}
