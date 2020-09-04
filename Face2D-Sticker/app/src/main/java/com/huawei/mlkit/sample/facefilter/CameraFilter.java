/**
 * Copyright 2018 cain.huang@outlook.com
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
 *   2020.8.13-Changed name from GLImageOESInputFilter to CameraFilter, and adjusted the architecture
 *                  Huawei Technologies Co., Ltd<liqiang220@huawei.com></>.
 */
package com.huawei.mlkit.sample.facefilter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;

import com.huawei.mlkit.sample.util.OpenGLUtils;

public class CameraFilter extends BaseFilter {
    private int mTransformMatrixHandle;
    private float[] mTransformMatrix = new float[16];

    public CameraFilter(Context context) {
        this(context, OpenGLUtils.getShaderFromAssets(context, "shader/vertex_oes_input.glsl"),
                OpenGLUtils.getShaderFromAssets(context, "shader/fragment_oes_input.glsl"));
    }

    public CameraFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
    }

    @Override
    public void initProgramHandle() {
        super.initProgramHandle();
        mTransformMatrixHandle = GLES30.glGetUniformLocation(mProgramHandle, "transformMatrix");
    }

    @Override
    public int getTextureType() {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    @Override
    public void onDrawFrameBegin() {
        super.onDrawFrameBegin();
        GLES30.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, mTransformMatrix, 0);
    }

    public void setTextureTransformMatrix(float[] transformMatrix) {
        for (int i = 0; i < transformMatrix.length; i++) {
            mTransformMatrix[i] = transformMatrix[i];
        }
    }
}

