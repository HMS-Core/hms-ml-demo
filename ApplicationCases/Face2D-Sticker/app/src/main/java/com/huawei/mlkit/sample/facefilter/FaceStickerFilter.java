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
 *  2020.8.13-Changed name from DynamicStickerNormalFilter to FaceStickerFilter, and adjusted the architecture
 *                  Huawei Technologies Co., Ltd<liqiang220@huawei.com></>.
 */
package com.huawei.mlkit.sample.facefilter;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.huawei.mlkit.sample.util.OpenGLUtils;
import com.huawei.mlkit.sample.facepoint.FacePointEngine;
import com.huawei.mlkit.sample.facepoint.EGLFace;

import org.json.JSONException;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class FaceStickerFilter extends BaseFilter {
    private static final float PROJECTION_SCALE = 2.0f;

    protected float[] cubeVertices = {
            -1.0f, -1.0f,  // 0 bottom left
            1.0f,  -1.0f,  // 1 bottom right
            -1.0f,  1.0f,  // 2 top left
            1.0f,   1.0f,  // 3 top rtight
    };

    protected float[] textureVerticesFlipx = {
            1.0f, 0.0f,     // 0 right bottom
            0.0f, 0.0f,     // 1 left  bottom
            1.0f, 1.0f,     // 2 right top
            0.0f, 1.0f      // 3 left  top
    };

    protected List<FaceStickerJson> mStickerList;
    protected List<FaceStickerLoader> mStickerLoaderList;

    private int mMVPMatrixHandle;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float mRatio;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    private float[] mStickerVertices = new float[8];

    private Context context;

    public FaceStickerFilter(Context context, String folderPath) {
        super(context, OpenGLUtils.getShaderFromAssets(context, "shader/vertex_sticker_normal.glsl"),
                OpenGLUtils.getShaderFromAssets(context, "shader/fragment_sticker_normal.glsl"));

        this.context = context;
        try {
            mStickerList = ResourceDecode.decodeStickerData(context, folderPath + "/" + "json");
        } catch (IOException | JSONException e) {
            Log.d(TAG, "IOException or JSONException: ");
        }

        mStickerLoaderList = new ArrayList<>();

        if (mStickerList != null) {
            for (int i = 0; i < mStickerList.size(); i++) {
                if (mStickerList.get(i) instanceof FaceStickerJson) {
                    String path = folderPath + "/" + mStickerList.get(i).stickerName;
                    mStickerLoaderList.add(new FaceStickerLoader(this, mStickerList.get(i), path));
                }
            }
        }
        initMatrix();
        initBuffer();
    }

    /**
     * initMatrix
     */
    private void initMatrix() {
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    /**
     * initBuffer
     */
    private void initBuffer() {
        releaseBuffer();
        mVertexBuffer = OpenGLUtils.createFloatBuffer(cubeVertices);
        mTextureBuffer = OpenGLUtils.createFloatBuffer(textureVerticesFlipx);
    }

    /**
     * releaseBuffer
     */
    private void releaseBuffer() {
        if (mVertexBuffer != null) {
            mVertexBuffer.clear();
            mVertexBuffer = null;
        }
        if (mTextureBuffer != null) {
            mTextureBuffer.clear();
            mTextureBuffer = null;
        }
    }

    @Override
    public void initProgramHandle() {
        super.initProgramHandle();
        if (mProgramHandle != OpenGLUtils.GL_NOT_INIT) {
            mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        } else {
            mMVPMatrixHandle = OpenGLUtils.GL_NOT_INIT;
        }
    }

    @Override
    public void onInputSizeChanged(int width, int height) {
        super.onInputSizeChanged(width, height);
        mRatio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -mRatio, mRatio, -1.0f, 1.0f, 3.0f, 9.0f);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public void release() {
        super.release();
        releaseBuffer();
    }

    @Override
    public boolean drawFrame(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        int stickerTexture = drawFrameBuffer(textureId, vertexBuffer, textureBuffer);
        return super.drawFrame(stickerTexture, vertexBuffer, textureBuffer);
    }

    @Override
    public int drawFrameBuffer(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        super.drawFrameBuffer(textureId, vertexBuffer, textureBuffer);

        if (mStickerLoaderList.size() > 0 && FacePointEngine.getInstance().hasFace()) {
            int faceCount = Math.min(FacePointEngine.getInstance().getFaceSize(),
                                    mStickerLoaderList.get(0).getMaxCount());
            for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
                EGLFace eglFace = FacePointEngine.getInstance().getOneFace(faceIndex);
                for (int stickerIndex = 0; stickerIndex < mStickerLoaderList.size(); stickerIndex++) {
                    synchronized (this) {
                        mStickerLoaderList.get(stickerIndex).updateStickerTexture(this.context);
                        calculateStickerVertices(mStickerLoaderList.get(stickerIndex).getStickerData(),
                                eglFace);
                        super.drawFrameBuffer(mStickerLoaderList.get(stickerIndex).getStickerTexture(),
                                    mVertexBuffer, mTextureBuffer);
                    }
                }
            }
            GLES30.glFlush();
        }
        return mFrameBufferTextures[0];
    }

    @Override
    public void onDrawFrameBegin() {
        super.onDrawFrameBegin();
        if (mMVPMatrixHandle != OpenGLUtils.GL_NOT_INIT) {
            GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        }

        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendEquation(GLES30.GL_FUNC_ADD);
        GLES30.glBlendFuncSeparate(GLES30.GL_ONE, GLES30.GL_ONE_MINUS_SRC_ALPHA, GLES30.GL_ONE, GLES30.GL_ONE);
    }

    @Override
    public void onDrawFrameAfter() {
        super.onDrawFrameAfter();
        GLES30.glDisable(GLES30.GL_BLEND);
    }

    private void calculateStickerVertices(FaceStickerJson stickerData, EGLFace eglFace) {
        if (eglFace == null || eglFace.vertexPoints == null) {
            return;
        }

        float centerX = 0.0f;
        float centerY = 0.0f;
        if (stickerData.centerIndexList == null) {
            return;
        }
        for (int i = 0; i < stickerData.centerIndexList.length; i++) {
            centerX += (eglFace.vertexPoints[stickerData.centerIndexList[i]].x * 0.5f + 0.5f) * mImageWidth;
            centerY += (eglFace.vertexPoints[stickerData.centerIndexList[i]].y * 0.5f + 0.5f) * mImageHeight;
        }
        centerX /= (float) stickerData.centerIndexList.length;
        centerY /= (float) stickerData.centerIndexList.length;
        centerX = centerX / mImageHeight * PROJECTION_SCALE;
        centerY = centerY / mImageHeight * PROJECTION_SCALE;

        float ndcCenterX = (centerX - mRatio) * PROJECTION_SCALE;
        float ndcCenterY = (centerY - 1.0f) * PROJECTION_SCALE;

        float stickerWidth = (float) OpenGLUtils.getDistance(
                (eglFace.vertexPoints[stickerData.startIndex].x * 0.5f + 0.5f) * mImageWidth,
                (eglFace.vertexPoints[stickerData.startIndex].y * 0.5f + 0.5f) * mImageHeight,
                (eglFace.vertexPoints[stickerData.endIndex].x  * 0.5f + 0.5f) * mImageWidth,
                (eglFace.vertexPoints[stickerData.endIndex].y * 0.5f + 0.5f) * mImageHeight) * stickerData.baseScale;
        float ndcStickerWidth = stickerWidth / mImageHeight * PROJECTION_SCALE;
        float ndcStickerHeight = ndcStickerWidth * (float) stickerData.height / (float) stickerData.width;

        float offsetX = (stickerWidth * stickerData.offsetX) / mImageHeight * PROJECTION_SCALE;
        float stickerHeight = stickerWidth * (float) stickerData.height / (float) stickerData.width;
        float offsetY = (stickerHeight * stickerData.offsetY) / mImageHeight * PROJECTION_SCALE;

        float anchorX = ndcCenterX + offsetX * PROJECTION_SCALE;
        float anchorY = ndcCenterY + offsetY * PROJECTION_SCALE;

        mStickerVertices[0] = anchorX - ndcStickerWidth;
        mStickerVertices[1] = anchorY - ndcStickerHeight;
        mStickerVertices[2] = anchorX + ndcStickerWidth;
        mStickerVertices[3] = anchorY - ndcStickerHeight;
        mStickerVertices[4] = anchorX - ndcStickerWidth;
        mStickerVertices[5] = anchorY + ndcStickerHeight;
        mStickerVertices[6] = anchorX + ndcStickerWidth;
        mStickerVertices[7] = anchorY + ndcStickerHeight;
        mVertexBuffer.clear();
        mVertexBuffer.position(0);
        mVertexBuffer.put(mStickerVertices);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, ndcCenterX, ndcCenterY, 0);

        float pitchAngle = -eglFace.pitch;
        float yawAngle = eglFace.yaw;
        float rollAngle = eglFace.roll;

        if (Math.abs(yawAngle) > 90) {
            yawAngle = (yawAngle / Math.abs(yawAngle)) * 90;
        }

        if (Math.abs(pitchAngle) > 90) {
            pitchAngle = (pitchAngle / Math.abs(pitchAngle)) * 90;
        }

        Matrix.rotateM(mModelMatrix, 0, rollAngle, 0, 0, 1);
        Matrix.rotateM(mModelMatrix, 0, yawAngle, 0, 1, 0);
        Matrix.rotateM(mModelMatrix, 0, pitchAngle, 1, 0, 0);

        Matrix.translateM(mModelMatrix, 0, -ndcCenterX, -ndcCenterY, 0);

        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mModelMatrix, 0);
    }
}
