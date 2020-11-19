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
 */
package com.huawei.mlkit.sample.facefilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import com.huawei.mlkit.sample.util.OpenGLUtils;
import com.huawei.mlkit.sample.facepoint.FacePointEngine;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;

public class FaceStickerLoader {
    private static final String TAG = "FaceStickerLoader";

    private int mStickerTexture;
    // Texture id
    private int mRestoreTexture;
    // FaceSticke path
    private String mFolderPath;
    // FaceSticke data
    private FaceStickerJson mStickerData;

    private ResourceDecode mResourceIndexCodec;

    private int mFrameIndex = -1;

    private long mCurrentTime = -1L;

    private final WeakReference<FaceStickerFilter> mWeakFilter;

    public FaceStickerLoader(FaceStickerFilter filter, FaceStickerJson stickerData, String folderPath) {
        mWeakFilter = new WeakReference<>(filter);
        mStickerTexture = OpenGLUtils.GL_NOT_TEXTURE;
        mRestoreTexture = OpenGLUtils.GL_NOT_TEXTURE;
        mFolderPath = folderPath.startsWith("file://") ? folderPath.substring("file://".length()) : folderPath;
        mStickerData = stickerData;
        Pair pair = ResourceDecode.getResourceFile(mFolderPath);
        if (pair != null) {
            mResourceIndexCodec = new ResourceDecode(mFolderPath + "/" + String.valueOf(pair.second));
        }
        if (mResourceIndexCodec != null) {
            try {
                mResourceIndexCodec.init();
            } catch (IOException e) {
                Log.e(TAG, "init merge res reader failed", e);
                mResourceIndexCodec = null;
            }
        }
        mStickerTexture = OpenGLUtils.GL_NOT_TEXTURE;
        mRestoreTexture = OpenGLUtils.GL_NOT_TEXTURE;
    }

    /**
     * updateStickerTexture
     */
    public void updateStickerTexture(Context context) {
        if (!FacePointEngine.getInstance().hasFace()) {
            mCurrentTime = -1L;
            return;
        }

        if (mCurrentTime == -1L) {
            mCurrentTime = System.currentTimeMillis();
        }
        int frameIndex = (int) ((System.currentTimeMillis() - mCurrentTime) / mStickerData.duration);
        if (frameIndex >= mStickerData.frames) {
            if (!mStickerData.stickerLooping) {
                mCurrentTime = -1L;
                mRestoreTexture = mStickerTexture;
                mStickerTexture = OpenGLUtils.GL_NOT_TEXTURE;
                mFrameIndex = -1;
                return;
            }
            frameIndex = 0;
            mCurrentTime = System.currentTimeMillis();
        }
        if (frameIndex < 0) {
            frameIndex = 0;
        }
        if (mFrameIndex == frameIndex) {
            return;
        }

        Bitmap bitmap = null;
        String path = String.format(Locale.ENGLISH,
                            mStickerData.stickerName + "_%03d.png", new Object[]{frameIndex});
        try {
            InputStream bit = context.getAssets().open( mFolderPath + "/" + path);
            bitmap = BitmapFactory.decodeStream(bit);
        } catch (IOException e) {
            Log.d(TAG, "IOException: ");
        }
        if (null != bitmap) {
            if (mStickerTexture == OpenGLUtils.GL_NOT_TEXTURE
                    && mRestoreTexture != OpenGLUtils.GL_NOT_TEXTURE) {
                mStickerTexture = mRestoreTexture;
            }
            if (mStickerTexture == OpenGLUtils.GL_NOT_TEXTURE) {
                mStickerTexture = OpenGLUtils.createTexture(bitmap);
            } else {
                mStickerTexture = OpenGLUtils.createTexture(bitmap, mStickerTexture);
            }
            mRestoreTexture = mStickerTexture;
            mFrameIndex = frameIndex;
            bitmap.recycle();
        } else {
            mRestoreTexture = mStickerTexture;
            mStickerTexture = OpenGLUtils.GL_NOT_TEXTURE;
            mFrameIndex = -1;
        }
    }

    /**
     * release
     */
    public void release() {
        if (mStickerTexture == OpenGLUtils.GL_NOT_TEXTURE) {
            mStickerTexture = mRestoreTexture;
        }
        OpenGLUtils.deleteTexture(mStickerTexture);
        mStickerTexture = OpenGLUtils.GL_NOT_TEXTURE;
        mRestoreTexture = OpenGLUtils.GL_NOT_TEXTURE;
        if (mWeakFilter.get() != null) {
            mWeakFilter.clear();
        }
    }

    public int getStickerTexture() {
        return mStickerTexture;
    }

    public int getMaxCount() {
        return mStickerData == null ? 0 : mStickerData.maxCount;
    }

    public FaceStickerJson getStickerData() {
        return mStickerData;
    }
}
