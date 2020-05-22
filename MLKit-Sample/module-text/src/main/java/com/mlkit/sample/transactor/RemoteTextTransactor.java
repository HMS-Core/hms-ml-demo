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

package com.mlkit.sample.transactor;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.mlkit.sample.callback.CouldInfoResultCallBack;
import com.mlkit.sample.camera.FrameMetadata;
import com.mlkit.sample.util.Constant;
import com.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

public class RemoteTextTransactor extends BaseTransactor<MLText> {

    private static final String TAG = "RemoteTextTransactor";

    private final MLTextAnalyzer detector;

    private CouldInfoResultCallBack callBack;

    private Handler handler;

    public RemoteTextTransactor(Handler handler) {
        super();
        MLRemoteTextSetting options =
                new MLRemoteTextSetting.Factory().setBorderType(MLRemoteTextSetting.ARC).create();
        this.detector = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer(options);
        this.handler = handler;
    }

    @Override
    protected Task<MLText> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    public void addCouldTextResultCallBack(CouldInfoResultCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull MLText text,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        this.handler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
        graphicOverlay.clear();
        this.callBack.onSuccessForText(originalCameraImage, text, graphicOverlay);
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        this.handler.sendEmptyMessage(Constant.GET_DATA_FAILED);
        Log.e(RemoteTextTransactor.TAG, "Remote text detection failed: " + e.getMessage());
    }
}
