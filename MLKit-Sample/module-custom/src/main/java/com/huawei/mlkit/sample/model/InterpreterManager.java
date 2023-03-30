/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.mlkit.sample.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.ml.common.utils.SmartLog;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.custom.MLCustomLocalModel;
import com.huawei.hms.mlsdk.custom.MLCustomRemoteModel;
import com.huawei.hms.mlsdk.custom.MLModelExecutor;
import com.huawei.hms.mlsdk.custom.MLModelExecutorSettings;
import com.huawei.hms.mlsdk.custom.MLModelInputOutputSettings;
import com.huawei.hms.mlsdk.custom.MLModelInputs;
import com.huawei.hms.mlsdk.custom.MLModelOutputs;
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadListener;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Customized model inference management class。
 *
 * @since 2020-12-10
 */
public class InterpreterManager {
    private static final String TAG = "InterpreterManager";

    private MLCustomRemoteModel remoteModel;

    private MLCustomLocalModel localModel;

    private MLModelExecutor modelExecutor;

    private ModelOperator mModelOperator;

    public synchronized boolean getIsStop() {
        return isStop;
    }

    public synchronized void setStop(boolean stop) {
        isStop = stop;
    }

    private volatile boolean isStop = false;

    private volatile boolean needFrame = true;

    public synchronized boolean isNeedFrame() {
        return needFrame;
    }

    public synchronized void setNeedFrame(boolean needFrame) {
        this.needFrame = needFrame;
    }

    public ModelOperator getmModelOperator() {
        return mModelOperator;
    }

    private MLModelExecutorSettings settings;
    private ExceutorResult mExceutorResult = null;

    MLModelInputOutputSettings inOutSettings;

    public InterpreterManager(ModelOperator modelOperator, ExceutorResult exceutorResult) {
        this.mModelOperator = modelOperator;
        this.mExceutorResult = exceutorResult;
        try {
            initLocalEnvironment();
        } catch (MLException e) {
            SmartLog.e(TAG, "set input output format failed! " + e.getMessage());
        }
    }

    public interface ExceutorResult {
        /**
         * return Result value
         *
         * @param mlModelOutputs value
         * @return return
         */
        boolean onResult(MLModelOutputs mlModelOutputs);
    }

    public void changeModel(ModelOperator modelOperator) {
        setStop(true);
        this.mModelOperator = modelOperator;
    }

    private void initLocalEnvironment() throws MLException {
        localModel = new MLCustomLocalModel
                .Factory(mModelOperator.getModelName())
                .setAssetPathFile(mModelOperator.getModelFullName())
                .create();
        settings = new MLModelExecutorSettings.Factory(localModel).create();
        try {
            modelExecutor = MLModelExecutor.getInstance(settings);
        } catch (MLException e) {
            throw e;
        }
        createOutputSettings();
    }

    private void initRemoteEnvironment() throws MLException {
        remoteModel = new MLCustomRemoteModel.Factory(mModelOperator.getModelName()).create();
        settings = new MLModelExecutorSettings.Factory(remoteModel).create();
        try {
            modelExecutor = MLModelExecutor.getInstance(settings);
        } catch (MLException e) {
            SmartLog.e(TAG, "set input output format failed! " + e.getMessage());
            throw e;
        }
        createOutputSettings();
    }

    private void createOutputSettings() {
        try {
            // Sets the input and output formats of a model, which is closely related to your model.
            MLModelInputOutputSettings.Factory settingsFactory = new MLModelInputOutputSettings.Factory();
            settingsFactory.setInputFormat(0, mModelOperator.getInputType(), mModelOperator.getInputShape());
            ArrayList<int[]> outputSettingsList = mModelOperator.getOutputShapeList();
            for (int i = 0; i < outputSettingsList.size(); i++) {
                settingsFactory.setOutputFormat(i, mModelOperator.getOutputType(), outputSettingsList.get(i));
            }
            inOutSettings = settingsFactory.create();
        } catch (MLException e) {
            SmartLog.e(TAG, "set input output format failed! " + e.getMessage());
        }
    }


    public void exec(Bitmap bitmap) {
        setNeedFrame(false);

        MLModelInputs inputs = null;
        try {
            inputs = new MLModelInputs.Factory().add(mModelOperator.bitmapTransInput(bitmap)).create();
        } catch (MLException e) {
            SmartLog.e(TAG, "add inputs failed! " + e.getMessage());
        }

        if (modelExecutor == null) {
            try {
                initLocalEnvironment();
            } catch (MLException e) {
                SmartLog.e(TAG, "initLocalEnvironment failed " + e.getMessage());
            }
        }
        modelExecutor.exec(inputs, inOutSettings).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                SmartLog.e(TAG, "ModelExecutor onFailure! " + e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<MLModelOutputs>() {
            @Override
            public void onComplete(Task<MLModelOutputs> task) {
                if (InterpreterManager.this.mExceutorResult == null)
                    return;

                if (getIsStop()) {
                    setNeedFrame(false);
                    close();
                    try {
                        initLocalEnvironment();
                    } catch (MLException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    setStop(false);
                    setNeedFrame(true);
                } else {
                    mExceutorResult.onResult(task.getResult());
                    setNeedFrame(true);
                }
            }
        });

    }

    public void download(MLModelDownloadListener listener, OnSuccessListener successListener, OnFailureListener failureListener) {
        if (isDownloaded()) {
            return;
        }
        MLModelDownloadStrategy downloadStrategy = new MLModelDownloadStrategy.Factory()
                .setRegion(MLModelDownloadStrategy.REGION_DR_CHINA)
                .create();
        remoteModel = new MLCustomRemoteModel.Factory(mModelOperator.modelName).create();
        MLLocalModelManager.getInstance().downloadModel(remoteModel, downloadStrategy, listener)
                .addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public boolean isDownloaded() {
        return MLLocalModelManager.getInstance().isModelExist(remoteModel).getResult();
    }

    public void close() {
        try {
            if (modelExecutor != null)
                modelExecutor.close();
        } catch (IOException error) {
            SmartLog.e(TAG, "close Failure:" + error.getMessage());
        }
    }

}
