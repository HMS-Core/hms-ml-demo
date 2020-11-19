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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import com.huawei.mlkit.sample.camera.FrameMetadata;

import java.nio.ByteBuffer;
import java.util.Locale;

public class NV21ToBitmapConverter {
    private RenderScript renderScript;

    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;

    private Type.Builder yuvType;

    private Type.Builder rgbaType;

    private Allocation in;

    private Allocation out;

    private Context applicationContext;

    private int mWidth = -1;

    private int mHeight = -1;

    private int length = -1;

    public NV21ToBitmapConverter(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can't be null");
        }

        Context appContext = context.getApplicationContext();
        if (appContext == null) {
            this.applicationContext = context;
        } else {
            this.applicationContext = appContext;
        }

        renderScript = RenderScript.create(this.applicationContext);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(renderScript, Element.U8_4(renderScript));
    }

    public Context getApplicationContext() {
        if (this.applicationContext == null) {
            throw new IllegalStateException("initial must be called first");
        }

        return this.applicationContext;
    }

    public Bitmap convertYUVtoRGB(byte[] yuvData, int width, int height) {
        if (yuvType == null) {
            yuvType = new Type.Builder(renderScript, Element.U8(renderScript)).setX(yuvData.length);
            in = Allocation.createTyped(renderScript, yuvType.create(), Allocation.USAGE_SCRIPT);

            rgbaType = new Type.Builder(renderScript, Element.RGBA_8888(renderScript)).setX(width).setY(height);
            out = Allocation.createTyped(renderScript, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }

        in.copyFrom(yuvData);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(result);

        return result;
    }

    /**
     * Returns a transformation matrix from one reference frame into another.
     * Handles cropping (if maintaining aspect ratio is desired) and rotation.
     *
     * @param srcWidth Width of source frame.
     * @param srcHeight Height of source frame.
     * @param dstWidth Width of destination frame.
     * @param dstHeight Height of destination frame.
     * @param applyRotation Amount of rotation to apply from one frame to another.
     *        Must be a multiple of 90.
     * @param flipHorizontal should flip horizontally
     * @param flipVertical should flip vertically
     * @param maintainAspectRatio If true, will ensure that scaling in x and y remains constant,
     *        cropping the image if necessary.
     * @return The transformation fulfilling the desired requirements.
     */
    public Matrix getTransformationMatrix(final int srcWidth, final int srcHeight, final int dstWidth,
                                            final int dstHeight, final int applyRotation, boolean flipHorizontal, boolean flipVertical,
                                            final boolean maintainAspectRatio) {
        final Matrix matrix = new Matrix();

        if (applyRotation < 360) {
            if (applyRotation % 90 != 0) {
                throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Rotation of %d", applyRotation));
            }

            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);

            // Rotate around origin.
            matrix.postRotate(applyRotation);
        }

        // Account for the already applied rotation, if any, and then determine how
        // much scaling is needed for each axis.
        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;

        final int inWidth = transpose ? srcHeight : srcWidth;
        final int inHeight = transpose ? srcWidth : srcHeight;

        int flipHorizontalFactor = flipHorizontal ? -1 : 1;
        int flipVerticalFactor = flipVertical ? -1 : 1;

        // Apply scaling if necessary.
        if (inWidth != dstWidth || inHeight != dstHeight) {
            final float scaleFactorX = flipHorizontalFactor * dstWidth / (float) inWidth;
            final float scaleFactorY = flipVerticalFactor * dstHeight / (float) inHeight;

            if (maintainAspectRatio) {
                // Scale by minimum factor so that dst is filled completely while
                // maintaining the aspect ratio. Some image may fall off the edge.
                final float scaleFactor = Math.max(Math.abs(scaleFactorX), Math.abs(scaleFactorY));
                matrix.postScale(scaleFactor, scaleFactor);
            } else {
                // Scale exactly to fill dst from src.
                matrix.postScale(scaleFactorX, scaleFactorY);
            }
        }

        if (applyRotation < 360) {
            // Translate back from origin centered reference to destination frame.
            float dx = dstWidth / 2.0f;
            float dy = dstHeight / 2.0f;
            matrix.postTranslate(dx, dy);

            // if postScale fail, nothing happen
            matrix.postScale(flipHorizontalFactor, flipVerticalFactor, dx, dy);
        }

        return matrix;
    }

    public Bitmap convert(byte[] bytes, int srcWidth, int srcHeight, int destWidth, int destHeight, int rotation) {
        // when width or height changed, recreate yuvType, rgbType etc
        recreateIfNeed(bytes, srcWidth, srcHeight, rotation);

        final Bitmap target = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(target);
        final Bitmap source = convertYUVtoRGB(bytes, srcWidth, srcHeight);
        final Matrix matrix =
                getTransformationMatrix(srcWidth, srcHeight, destWidth, destHeight, rotation, true, false, false);
        canvas.drawBitmap(source, matrix, null);

        return target;
    }

    public Bitmap getBitmap(ByteBuffer data, FrameMetadata metadata) {
        final Bitmap target;
        byte[] bytes = data.array();
        int width = metadata.getWidth();
        int height = metadata.getHeight();
        int rotation = metadata.getRotation() * 90;
        Matrix matrix = new Matrix();
        // when width or height changed, recreate yuvType, rgbType etc
        recreateIfNeed(bytes, metadata.getWidth(), metadata.getHeight(), metadata.getRotation() * 90);
        final Bitmap source = convertYUVtoRGB(bytes, width, height);
        // final Canvas canvas;
        if (rotation == 0 || rotation == 180) {
            target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            if (metadata.getCameraFacing() == 0) {
                matrix = getTransformationMatrix(width, height, width, height, rotation, false, false, false);
            }else {
                matrix = getTransformationMatrix(width, height, width, height, rotation, true, false, false);
            }
        } else {
            target = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
            if (metadata.getCameraFacing() == 0) {
                matrix = getTransformationMatrix(width, height, height, width, rotation, false, false, false);
            } else {
                matrix = getTransformationMatrix(width, height, height, width, rotation, true, false, false);
            }
        }
        final Canvas canvas = new Canvas(target);
        canvas.drawBitmap(source, matrix, null);
        return target;
    }

    private void recreateIfNeed(byte[] bytes, int srcWidth, int srcHeight, int rotation) {
        if (this.mWidth == srcWidth && this.mHeight == srcHeight && this.length == bytes.length) {
            return;
        }

        this.mWidth = srcWidth;
        this.mHeight = srcHeight;
        this.length = bytes.length;
        yuvType = null;
        rgbaType = null;
    }
}