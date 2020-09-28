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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationClassification;
import com.huawei.mlkit.sample.callback.ImageSegmentationResultCallBack;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.util.ImageUtils;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.IOException;

public class StillImageSegmentationTransactor extends BaseTransactor<MLImageSegmentation> {
    private static final String TAG = "StillImageSegTransactor";

    private final MLImageSegmentationAnalyzer detector;
    //private Context context;
    private Bitmap originBitmap;
    private Bitmap backgroundBitmap;
    private ImageView imageView;
    private int detectCategory;
    private int color;
    private ImageSegmentationResultCallBack imageSegmentationResultCallBack;

    /**
     * @param options          Options.
     * @param originBitmap     Foreground, picture to replace.
     * @param imageView        ImageView.
     * @param detectCategory -1 represents all detections, others represent the type of replacement color currently detected.
     */
    public StillImageSegmentationTransactor(MLImageSegmentationSetting options, Bitmap originBitmap, ImageView imageView, int detectCategory) {
        //this.context = context;
        this.detector = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(options);
        this.originBitmap = originBitmap;
        this.backgroundBitmap = null;
        this.imageView = imageView;
        this.detectCategory = detectCategory;
        this.color = Color.WHITE;
    }

    /**
     * Replace background.
     *
     * @param options          Options.
     * @param originBitmap     Foreground, picture to replace.
     * @param backgroundBitmap Background.
     * @param imageView        ImageView.
     * @param detectCategory   -1 represents all detections, others represent the type of replacement color currently detected.
     */
    public StillImageSegmentationTransactor(MLImageSegmentationSetting options, Bitmap originBitmap, Bitmap backgroundBitmap, ImageView imageView, int detectCategory) {
        //this.context = context;
        this.detector = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(options);
        this.originBitmap = originBitmap;
        this.backgroundBitmap = backgroundBitmap;
        this.imageView = imageView;
        this.detectCategory = detectCategory;
        this.color = Color.WHITE;
    }

    // Sets the drawn color of detected features.
    public void setColor(int color) {
        this.color = color;
    }

    // Interface for obtaining processed image data.
    public void setImageSegmentationResultCallBack(ImageSegmentationResultCallBack imageSegmentationResultCallBack) {
        this.imageSegmentationResultCallBack = imageSegmentationResultCallBack;
    }

    @Override
    public void stop() {
        super.stop();
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(StillImageSegmentationTransactor.TAG,
                    "Exception thrown while trying to close image segmentation transactor: " + e.getMessage());
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
        int[] pixels;
        if (results.getMasks() == null) {
            Log.i(TAG, "detection failed, none mask return");
            return;
        }
        // If the originBitmap is automatically recycled, the callback is complete.
        if (this.originBitmap.isRecycled()) {
            return;
        }
        if (this.detectCategory == -1) {
            pixels = this.byteArrToIntArr(results.getMasks());
        } else if (this.backgroundBitmap == null) {
            pixels = this.changeColor(results.getMasks());
        } else {
            // If the backgroundBitmap is automatically recycled, the callback is complete.
            if (this.backgroundBitmap.isRecycled()) {
                return;
            }
            pixels = this.changeBackground(results.getMasks());
        }
        Bitmap processedBitmap = Bitmap.createBitmap(pixels, 0, this.originBitmap.getWidth(), this.originBitmap.getWidth(), this.originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        this.imageView.setImageBitmap(processedBitmap);
        if (this.imageSegmentationResultCallBack != null) {
            this.imageSegmentationResultCallBack.callResultBitmap(processedBitmap);
        }
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(StillImageSegmentationTransactor.TAG, "Image segmentation detection failed: " + e.getMessage());
    }

    private int[] byteArrToIntArr(byte[] masks) {
        int[] results = new int[masks.length];
        for (int i = 0; i < masks.length; i++) {
            if (masks[i] == MLImageSegmentationClassification.TYPE_HUMAN) {
                results[i] = Color.BLACK;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_SKY) {
                results[i] = Color.BLUE;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_GRASS) {
                results[i] = Color.DKGRAY;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_FOOD) {
                results[i] = Color.YELLOW;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_CAT) {
                results[i] = Color.LTGRAY;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_BUILD) {
                results[i] = Color.CYAN;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_FLOWER) {
                results[i] = Color.RED;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_WATER) {
                results[i] = Color.GRAY;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_SAND) {
                results[i] = Color.MAGENTA;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_MOUNTAIN) {
                results[i] = Color.GREEN;
            } else {
                results[i] = Color.WHITE;
            }
        }
        return results;
    }

    // Cut out the desired element, the background is white.
    private int[] changeColor(byte[] masks) {
        int[] results = new int[masks.length];
        int[] orginPixels = new int[this.originBitmap.getWidth() * this.originBitmap.getHeight()];
        this.originBitmap.getPixels(orginPixels, 0, this.originBitmap.getWidth(), 0, 0, this.originBitmap.getWidth(), this.originBitmap.getHeight());
        for (int i = 0; i < masks.length; i++) {
            if (masks[i] == this.detectCategory) {
                results[i] = this.color;
            } else {
                results[i] = orginPixels[i];
            }
        }
        return results;
    }

    // Replace background image.
    private int[] changeBackground(byte[] masks) {
        // Make the background and foreground images the same size.
        if (this.backgroundBitmap != null) {
            if (!ImageUtils.equalImageSize(this.originBitmap, this.backgroundBitmap)) {
                this.backgroundBitmap = ImageUtils.resizeImageToForegroundImage(this.originBitmap, this.backgroundBitmap);
            }
        }
        int[] results = new int[masks.length];
        int[] originPixels = new int[this.originBitmap.getWidth() * this.originBitmap.getHeight()];
        int[] backgroundPixels = new int[originPixels.length];
        this.originBitmap.getPixels(originPixels, 0, this.originBitmap.getWidth(), 0, 0, this.originBitmap.getWidth(), this.originBitmap.getHeight());
        if (null != this.backgroundBitmap) {
            this.backgroundBitmap.getPixels(backgroundPixels, 0, this.originBitmap.getWidth(), 0, 0, this.originBitmap.getWidth(), this.originBitmap.getHeight());
        }
        for (int i = 0; i < masks.length; i++) {
            if (masks[i] == this.detectCategory) {
                results[i] = backgroundPixels[i];
            } else {
                results[i] = originPixels[i];
            }
        }
        return results;
    }
}
