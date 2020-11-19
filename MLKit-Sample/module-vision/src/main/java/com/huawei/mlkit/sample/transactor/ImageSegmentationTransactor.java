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

package com.huawei.mlkit.sample.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.mlkit.sample.callback.ImageSegmentationResultCallBack;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.util.BitmapUtils;
import com.huawei.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.IOException;

/**
 * A transactor to run object detector.
 *
 * @since 2019-12-26
 */

public class ImageSegmentationTransactor extends BaseTransactor<MLImageSegmentation> {

    private static final String TAG = "ImageSegTransactor";

    private final MLImageSegmentationAnalyzer detector;
    private Context context;
    private Bitmap foregroundBitmap;
    private Bitmap backgroundBitmap;
    private ImageSegmentationResultCallBack imageSegmentationResultCallBack;
    private Boolean isBlur = false;
    private RenderScript renderScript;
    /**
     * Constructor for real-time replacement background.
     *
     * @param context              context.
     * @param options              options.
     * @param backgroundBitmap     background image.
     */
    public ImageSegmentationTransactor(Context context, MLImageSegmentationSetting options, Bitmap backgroundBitmap) {
        this.context = context;
        MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer();
        this.detector = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(options);
        this.backgroundBitmap = backgroundBitmap;
    }

    // Return to processed image.
    public void setImageSegmentationResultCallBack(ImageSegmentationResultCallBack imageSegmentationResultCallBack) {
        this.imageSegmentationResultCallBack = imageSegmentationResultCallBack;
    }

    @Override
    public void stop() {
        super.stop();
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(ImageSegmentationTransactor.TAG, "Exception thrown while trying to close image segmentation transactor: " + e.getMessage());
        }
    }

    @Override
    protected Task<MLImageSegmentation> detectInImage(MLFrame frame) {
        return this.detector.asyncAnalyseFrame(frame);
    }

    protected SparseArray<MLImageSegmentation> analyseFrame(MLFrame frame) {
        return this.detector.analyseFrame(frame);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull MLImageSegmentation results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (results.getForeground() == null) {
            Log.i(TAG, "detection failed.");
            return;
        }
        foregroundBitmap = results.foreground;

        // Replace background.
        Bitmap resultBitmap = this.changeNextBackground(foregroundBitmap);
        if (frameMetadata.getCameraFacing() == CameraConfiguration.CAMERA_FACING_FRONT) {
            resultBitmap = this.convert(resultBitmap);
        }
        if (this.imageSegmentationResultCallBack != null) {
            this.imageSegmentationResultCallBack.callResultBitmap(resultBitmap);
        }
        CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, resultBitmap);
        graphicOverlay.addGraphic(imageGraphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(ImageSegmentationTransactor.TAG, "Image segmentation detection failed: " + e.getMessage());
    }

    /**
     * Replace the images in the assets directory as the background image in order.
     */
    private Bitmap changeNextBackground(Bitmap foregroundBitmap) {
        Bitmap result;
        if (this.backgroundBitmap == null) {
            Toast.makeText(this.context, "No Background Image", Toast.LENGTH_SHORT).show();
            throw new NullPointerException("No background image");
        }

        if (!this.equalToForegroundImageSize()) {
            this.backgroundBitmap = this.resizeImageToForegroundImage(this.backgroundBitmap);
        }
        int[] pixels = new int[this.backgroundBitmap.getWidth() * this.backgroundBitmap.getHeight()];
        this.backgroundBitmap.getPixels(pixels, 0, this.backgroundBitmap.getWidth(), 0, 0,
            this.backgroundBitmap.getWidth(), this.backgroundBitmap.getHeight());
        result = BitmapUtils.joinBitmap(isBlur ? blur(backgroundBitmap,20) :  backgroundBitmap, foregroundBitmap);
        return result;
    }

    /**
     * Blur Bitmap
     * @param bitmap Original Bitmap
     * @param radius Blur Radius (1-25)
     * @return Bitmap
     */
    private Bitmap blur(Bitmap bitmap , int radius){
        Bitmap outBitmap = Bitmap.createBitmap(bitmap);
        Allocation in = Allocation.createFromBitmap(renderScript,bitmap);
        Allocation out = Allocation.createTyped(renderScript,in.getType());
        ScriptIntrinsicBlur scriptintrinsicblur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptintrinsicblur.setRadius(radius);
        scriptintrinsicblur.setInput(in);
        scriptintrinsicblur.forEach(out);
        out.copyTo(outBitmap);
        return  outBitmap;
    }

    /**
     * Stretch background image size to foreground image's.
     *
     * @param bitmap bitmap
     * @return Bitmap object
     */
    private Bitmap resizeImageToForegroundImage(Bitmap bitmap) {
        float scaleWidth = ((float) this.foregroundBitmap.getWidth() / bitmap.getWidth());
        float scaleHeigth = ((float) this.foregroundBitmap.getHeight() / bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeigth);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    private boolean equalToForegroundImageSize() {
        Log.i(ImageSegmentationTransactor.TAG, "FOREGREOUND SIZE;" + this.foregroundBitmap.getWidth() + ", height:" + this.foregroundBitmap.getHeight());
        return this.backgroundBitmap.getHeight() == this.foregroundBitmap.getHeight() && this.backgroundBitmap.getWidth() == this.foregroundBitmap.getWidth();
    }

    /**
     * Front camera image position changed.
     */
    private Bitmap convert(Bitmap bitmap) {
        Matrix m = new Matrix();
        m.setScale(-1, 1);// horizontal flip.
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    public void setBlur(Boolean blur) {
        isBlur = blur;
    }

    public void setRenderScript(RenderScript renderScript) {
        this.renderScript = renderScript;
    }
}
