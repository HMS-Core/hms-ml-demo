/**
 *  Copyright 2018 Google LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  2020.2.21-Changed name from VisionImageProcessor to ImageTransactor.
 *  2020.2.21-Deleted method: process(Bitmap bitmap, GraphicOverlay graphicOverlay,String path,boolean flag);
 *      process(Bitmap bitmap, GraphicOverlay graphicOverlay,String path);
 *                  Huawei Technologies Co., Ltd.
 */

package com.huawei.mlkit.sample.transactor;

import android.graphics.Bitmap;

import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.nio.ByteBuffer;

public interface ImageTransactor {

    /**
     * Start detection
     *
     * @param data ByteBuffer object
     * @param frameMetadata FrameMetadata object
     * @param graphicOverlay GraphicOverlay object
     */
    void process(ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay);

    /**
     * Start detection
     *
     * @param bitmap Bitmap object
     * @param graphicOverlay GraphicOverlay object
     */
    void process(Bitmap bitmap, GraphicOverlay graphicOverlay);

    /**
     * Stop detection
     */
    void stop();

    /**
     * Is it face detection?
     *
     * @return boolean value
     */
    boolean isFaceDetection();
}
