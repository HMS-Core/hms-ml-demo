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
 *   2020.8.13-Changed name from GLImageFilter to BaseFilter, and adjusted the architecture
 *                  Huawei Technologies Co., Ltd<liqiang220@huawei.com></>.
 */
package com.huawei.mlkit.sample.facefilter;

import android.content.Context;
import android.opengl.GLES30;
import android.text.TextUtils;

import com.huawei.mlkit.sample.util.OpenGLUtils;

import java.nio.FloatBuffer;
import java.util.LinkedList;

public class BaseFilter {
    protected static final String VERTEX_SHADER = "" +
            "attribute vec4 aPosition;                                  \n" +
            "attribute vec4 aTextureCoord;                              \n" +
            "varying vec2 textureCoordinate;                            \n" +
            "void main() {                                              \n" +
            "    gl_Position = aPosition;                               \n" +
            "    textureCoordinate = aTextureCoord.xy;                  \n" +
            "}                                                          \n";

    protected static final String FRAGMENT_SHADER = "" +
            "precision mediump float;                                   \n" +
            "varying vec2 textureCoordinate;                            \n" +
            "uniform sampler2D inputTexture;                                \n" +
            "void main() {                                              \n" +
            "    gl_FragColor = texture2D(inputTexture, textureCoordinate); \n" +
            "}                                                          \n";

    protected String TAG = getClass().getSimpleName();
    protected String mVertexShader;
    protected String mFragmentShader;

    protected boolean mIsInitialized;

    protected boolean mFilterEnable = true;

    protected int mCoordsPerVertex = 2;

    protected int mVertexCount = 4;

    protected int mProgramHandle;
    protected int mPositionHandle;
    protected int mTextureCoordinateHandle;
    protected int mInputTextureHandle;

    protected int mImageWidth;
    protected int mImageHeight;

    protected int mDisplayWidth;
    protected int mDisplayHeight;

    protected int mFrameWidth = -1;
    protected int mFrameHeight = -1;

    protected int[] mFrameBuffers;
    protected int[] mFrameBufferTextures;

    private final LinkedList<Runnable> mRunOnDraw;

    public BaseFilter(Context context) {
        this(context, VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public BaseFilter(Context context, String vertexShader, String fragmentShader) {
        mRunOnDraw = new LinkedList<>();
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
        initProgramHandle();
    }

    public void initProgramHandle() {
        if (!TextUtils.isEmpty(mVertexShader) && !TextUtils.isEmpty(mFragmentShader)) {
            mProgramHandle = OpenGLUtils.createProgram(mVertexShader, mFragmentShader);
            mPositionHandle = GLES30.glGetAttribLocation(mProgramHandle, "aPosition");
            mTextureCoordinateHandle = GLES30.glGetAttribLocation(mProgramHandle, "aTextureCoord");
            mInputTextureHandle = GLES30.glGetUniformLocation(mProgramHandle, "inputTexture");
            mIsInitialized = true;
        } else {
            mPositionHandle = OpenGLUtils.GL_NOT_INIT;
            mTextureCoordinateHandle = OpenGLUtils.GL_NOT_INIT;
            mInputTextureHandle = OpenGLUtils.GL_NOT_TEXTURE;
            mIsInitialized = false;
        }
    }

    public void onInputSizeChanged(int width, int height) {
        mImageWidth = width;
        mImageHeight = height;
    }

    public void onDisplaySizeChanged(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
    }

    public boolean drawFrame(int textureId, FloatBuffer vertexBuffer,
                                FloatBuffer textureBuffer) {
        if (!mIsInitialized || textureId == OpenGLUtils.GL_NOT_INIT || !mFilterEnable) {
            return false;
        }

        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUseProgram(mProgramHandle);
        runPendingOnDrawTasks();

        onDrawTexture(textureId, vertexBuffer, textureBuffer);

        return true;
    }

    public int drawFrameBuffer(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        if (textureId == OpenGLUtils.GL_NOT_TEXTURE || mFrameBuffers == null
                || !mIsInitialized || !mFilterEnable) {
            return textureId;
        }

        GLES30.glViewport(0, 0, mFrameWidth, mFrameHeight);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES30.glUseProgram(mProgramHandle);
        runPendingOnDrawTasks();

        onDrawTexture(textureId, vertexBuffer, textureBuffer);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return mFrameBufferTextures[0];
    }

    public int drawFrameBufferClear(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        if (textureId == OpenGLUtils.GL_NOT_TEXTURE || mFrameBuffers == null
                || !mIsInitialized || !mFilterEnable) {
            return textureId;
        }

        GLES30.glViewport(0, 0, mFrameWidth, mFrameHeight);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(mProgramHandle);
        runPendingOnDrawTasks();

        onDrawTexture(textureId, vertexBuffer, textureBuffer);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return mFrameBufferTextures[0];
    }

    protected void onDrawTexture(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(mPositionHandle, mCoordsPerVertex,
                GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        textureBuffer.position(0);
        GLES30.glVertexAttribPointer(mTextureCoordinateHandle, 2,
                GLES30.GL_FLOAT, false, 0, textureBuffer);
        GLES30.glEnableVertexAttribArray(mTextureCoordinateHandle);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(getTextureType(), textureId);
        GLES30.glUniform1i(mInputTextureHandle, 0);
        onDrawFrameBegin();
        onDrawFrame();
        onDrawFrameAfter();

        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mTextureCoordinateHandle);
        GLES30.glBindTexture(getTextureType(), 0);

        GLES30.glUseProgram(0);
    }

    public void onDrawFrameBegin() {
    }

    protected void onDrawFrame() {
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, mVertexCount);
    }

    public void onDrawFrameAfter() {
    }

    protected void onUnbindTextureValue() {
    }

    public int getTextureType() {
        return GLES30.GL_TEXTURE_2D;
    }

    public void release() {
        if (mIsInitialized) {
            GLES30.glDeleteProgram(mProgramHandle);
            mProgramHandle = OpenGLUtils.GL_NOT_INIT;
        }
        destroyFrameBuffer();
    }

    public void initFrameBuffer(int width, int height) {
        if (!isInitialized()) {
            return;
        }
        if (mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height)) {
            destroyFrameBuffer();
        }
        if (mFrameBuffers == null) {
            mFrameWidth = width;
            mFrameHeight = height;
            mFrameBuffers = new int[1];
            mFrameBufferTextures = new int[1];
            OpenGLUtils.createFrameBuffer(mFrameBuffers, mFrameBufferTextures, width, height);
        }
    }

    public void destroyFrameBuffer() {
        if (!mIsInitialized) {
            return;
        }
        if (mFrameBufferTextures != null) {
            GLES30.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }

        if (mFrameBuffers != null) {
            GLES30.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
        mFrameWidth = -1;
        mFrameHeight = -1;
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public void setFilterEnable(boolean enable) {
        mFilterEnable = enable;
    }

    public int getDisplayWidth() {
        return mDisplayWidth;
    }

    public int getDisplayHeight() {
        return mDisplayHeight;
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    protected static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }
}
