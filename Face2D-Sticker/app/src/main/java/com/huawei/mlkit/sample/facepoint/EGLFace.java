/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.huawei.mlkit.sample.facepoint;

import android.graphics.PointF;

public class EGLFace {
    public float pitch;

    public float yaw;

    public float roll;

    // Contour point coordinates, refer to 131 contour points index in FacePointIndex.java
    public PointF[] vertexPoints;

    @Override
    protected EGLFace clone() {
        EGLFace copy = new EGLFace();
        copy.pitch = this.pitch;
        copy.yaw = this.yaw;
        copy.roll = this.roll;
        if (vertexPoints != null)
            copy.vertexPoints = this.vertexPoints.clone();
        return copy;
    }
}
