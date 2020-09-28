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

package com.huawei.hms.mlkit.sample.views;

import android.graphics.Canvas;

import com.huawei.hms.mlkit.sample.camera.FrameMetadata;
import com.huawei.hms.mlkit.sample.views.overlay.GraphicOverlay;

public class LocalDataManager {


    private float previewWidth;
    private float previewHeight;
    
    boolean isLandScape=false;
    private Integer imageMaxWidth;
    private Integer imageMaxHeight;
    
    public void setLandScape(boolean landScape) {
        isLandScape = landScape;
    }

    
    public void setCameraInfo(GraphicOverlay graphicOverlay, Canvas canvas, float width, float height) {
        this.previewWidth = width*graphicOverlay.getWidthScaleValue();
        this.previewHeight = height*graphicOverlay.getHeightScaleValue();
    }

    public Integer getImageMaxWidth(FrameMetadata hmsMLFrameMetadata) {
        if (imageMaxWidth == null) {
            if (isLandScape) {
                imageMaxWidth = hmsMLFrameMetadata.getHeight();
            } else {
                imageMaxWidth = hmsMLFrameMetadata.getWidth();
            }
        }
        return imageMaxWidth;
    }

    public Integer getImageMaxHeight(FrameMetadata hmsMLFrameMetadata) {
        if (imageMaxHeight == null) {
            if (isLandScape) {
                imageMaxHeight = hmsMLFrameMetadata.getWidth();
            } else {
                imageMaxHeight = hmsMLFrameMetadata.getHeight();
            }
        }
        return imageMaxHeight;
    }
}
