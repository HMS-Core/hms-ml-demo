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
 *
 *   2020.8.13-Changed name from GLImageFacePointsFilter to FacePointsFilter, and adjusted the architecture
 *                  Huawei Technologies Co., Ltd<liqiang220@huawei.com></>.
 */
package com.huawei.mlkit.sample.facefilter;

import android.content.Context;
import android.opengl.GLES30;
import android.text.TextUtils;

import java.nio.FloatBuffer;
import com.huawei.mlkit.sample.util.OpenGLUtils;
import com.huawei.mlkit.sample.facepoint.FacePointEngine;
import com.huawei.mlkit.sample.facepoint.EGLFace;

public class FacePointsFilter extends BaseFilter {
    private static final String VERTEX_SHADER = "" +
            "attribute vec4 aPosition;\n" +
            "void main() {\n" +
            "    gl_Position = aPosition;\n" +
            "    gl_PointSize = 8.0;\n" +
            "}";

    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "uniform vec4 color;\n" +
            "void main() {\n" +
            "    gl_FragColor = color;\n" +
            "}";

    private final float[] color = { 1.0f, 0.0f, 0.0f, 1.0f };

    private int mColorHandle;
    private int mPointCount = 131;//114;
    private float[] mPoints;
    private FloatBuffer mPointVertexBuffer;

    public FacePointsFilter(Context context) {
        this(context, VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public FacePointsFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        mPoints = new float[mPointCount * 2];
        mPointVertexBuffer = OpenGLUtils.createFloatBuffer(mPoints);
    }

    @Override
    public void initProgramHandle() {
        if (!TextUtils.isEmpty(mVertexShader) && !TextUtils.isEmpty(mFragmentShader)) {
            mProgramHandle = OpenGLUtils.createProgram(mVertexShader, mFragmentShader);
            mPositionHandle = GLES30.glGetAttribLocation(mProgramHandle, "aPosition");
            mColorHandle = GLES30.glGetUniformLocation(mProgramHandle, "color");
            mIsInitialized = true;
        } else {
            mPositionHandle = OpenGLUtils.GL_NOT_INIT;

            mColorHandle = OpenGLUtils.GL_NOT_INIT;
            mIsInitialized = false;
        }
        mTextureCoordinateHandle = OpenGLUtils.GL_NOT_INIT;
        mInputTextureHandle = OpenGLUtils.GL_NOT_TEXTURE;
    }

    @Override
    public void onInputSizeChanged(int width, int height) {
        super.onInputSizeChanged(width, height);
    }

    @Override
    public boolean drawFrame(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        if (!mIsInitialized || !mFilterEnable) {
            return false;
        }
        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
        GLES30.glUseProgram(mProgramHandle);
        runPendingOnDrawTasks();
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        onDrawFrameBegin();
        synchronized (this) {
            if (FacePointEngine.getInstance().getFaceSize() > 0) {
                EGLFace eglFace = FacePointEngine.getInstance().getOneFace(0);
                if (eglFace.vertexPoints != null) {
                    for (int i = 0; i < eglFace.vertexPoints.length; i++) {
                        mPoints[2*i] = eglFace.vertexPoints[i].x;
                        mPoints[2*i + 1] = eglFace.vertexPoints[i].y;
                    }
                    mPointVertexBuffer.clear();
                    mPointVertexBuffer.put(mPoints, 0, mPoints.length);
                    mPointVertexBuffer.position(0);
                    GLES30.glVertexAttribPointer(mPositionHandle, 2,
                            GLES30.GL_FLOAT, false, 8, mPointVertexBuffer);
                    GLES30.glDrawArrays(GLES30.GL_POINTS, 0, mPointCount);
                }
            }
        }
        onDrawFrameAfter();
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        return true;
    }

    @Override
    public int drawFrameBuffer(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        if (textureId == OpenGLUtils.GL_NOT_TEXTURE || mFrameBuffers == null
                || !mIsInitialized || !mFilterEnable) {
            return textureId;
        }
        drawFrame(textureId, vertexBuffer, textureBuffer);
        return textureId;
    }

    @Override
    public void initFrameBuffer(int width, int height) {
        // do nothing
    }

    @Override
    public void destroyFrameBuffer() {
        // do nothing
    }
}
