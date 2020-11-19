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
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.huawei.hmf.tasks.Task;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.util.SharedPreferencesUtil;
import com.huawei.mlkit.sample.views.graphic.BaseGraphic;
import com.huawei.mlkit.sample.views.graphic.CameraImageGraphic;
import com.huawei.mlkit.sample.views.graphic.LocalTextGraphic;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocalTextTransactor extends BaseTransactor<MLText> {
    private static final String TAG = "TextRecProc";
    private final MLTextAnalyzer detector;

    private MLText mlText;
    private FrameMetadata latestImageMetaData;
    private ByteBuffer latestImage;
    private Handler mHandler;
    int mCount = 0;

    private Context mContext;

    public LocalTextTransactor(Handler handler, Context context) {
        this.mContext = context;
        String language = this.getLanguage();
        Log.d(LocalTextTransactor.TAG, "language:" + language);
        MLLocalTextSetting options = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_TRACKING_MODE)
                .setLanguage(language)
                .create();
        this.detector = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(options);
        this.mHandler = handler;
    }

    private String getLanguage() {
        String position = SharedPreferencesUtil.getInstance(mContext).getStringValue(Constant.POSITION_KEY);
        Log.d(LocalTextTransactor.TAG, "position: " + position);
        String language = "";
        switch (position){
            case Constant.POSITION_CN:
                language = "zh";
                break;
            case Constant.POSITION_EN:
            case Constant.POSITION_LA:
                language = "en";
                break;
            case Constant.POSITION_JA:
            case Constant.POSITION_KO:
                language = "ja";
                break;
            default:
                if (Constant.IS_CHINESE) {
                    language = "zh";
                } else {
                    language = "en";
                }
                Log.d(LocalTextTransactor.TAG, "default value!");
                break;
        }
        return language;
    }

    @Override
    public void stop() {
        try {
            this.detector.close();
        } catch (IOException e) {
            Log.e(LocalTextTransactor.TAG,
                    "Exception thrown while trying to close text transactor: " + e.getMessage());
        }
    }

    @Override
    protected Task<MLText> detectInImage(MLFrame image) {
        this.latestImage =image.getByteBuffer();
        return this.detector.asyncAnalyseFrame(image);
    }

    public ByteBuffer getTransactingImage() {
        return this.latestImage;
    }

    public FrameMetadata getTransactingMetaData() {
        return this.latestImageMetaData;
    }


    public MLText getLastResults(){
        return this.mlText;
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull MLText results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        this.mlText =results;
        this.latestImageMetaData = frameMetadata;
        graphicOverlay.clear();
        List<MLText.Block> blocks = results.getBlocks();
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) && originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }
        if (blocks.size() > 0) {
            this.mCount = 0;
            this.mHandler.sendEmptyMessage(Constant.SHOW_TAKE_PHOTO_BUTTON);
        } else {
            this.mCount++;
            if (this.mCount > 1) {
                this.mHandler.sendEmptyMessage(Constant.HIDE_TAKE_PHOTO_BUTTON);
            }
        }
        for (int i = 0; i < blocks.size(); i++) {
            List<MLText.TextLine> lines = blocks.get(i).getContents();
            for (int j = 0; j < lines.size(); j++) {
                // Display by line, without displaying empty lines.
                if (lines.get(j).getStringValue() != null && lines.get(j).getStringValue().trim().length() != 0) {
                    BaseGraphic textGraphic = new LocalTextGraphic(graphicOverlay,
                            lines.get(j));
                    graphicOverlay.addGraphic(textGraphic);
                }
            }
        }
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(LocalTextTransactor.TAG, "Text detection failed: " + e.getMessage());
    }
}
