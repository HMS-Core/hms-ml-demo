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

package com.huawei.mlkit.sample.util;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;

public class CommonUtils {
    private static final String TAG = "CommonUtils";
    /**
     * Handle ByteBuffer
     *
     * @param src High resolution ByteBuffer
     * @param dst Low resolution ByteBuffer
     * @param srcWidth High resolution wide
     * @param srcHeight High resolution height
     * @param dstWidth Low resolution wide
     * @param dstHeight Low resolution height
     */
    public static void handleByteBuffer(ByteBuffer src, ByteBuffer dst,
                                        int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        int sw = srcWidth;
        int sh = srcHeight;
        int dw = dstWidth;
        int dh = dstHeight;
        int y;
        int x;
        int srcY;
        int srcX;
        int srcIndex;
        int xrIntFloat = (sw << 16) / dw + 1;
        int yrIntFloat = (sh << 16) / dh + 1;

        int dstUv = dh * dw;
        int srcUv = sh * sw;
        int dstUvYScanline = 0;
        int srcUvYScanline = 0;
        int dstYSlice = 0;
        int srcYSlice;
        int sp;
        int dp;

        for (y = 0; y < (dh & ~7); ++y) {
            srcY = (y * yrIntFloat) >> 16;
            srcYSlice = srcY * sw;

            if ((y & 1) == 0) {
                dstUvYScanline = dstUv + (y / 2) * dw;
                srcUvYScanline = srcUv + (srcY / 2) * sw;
            }

            for (x = 0; x < (dw & ~7); ++x) {
                srcX = (x * xrIntFloat) >> 16;
                try {
                    dst.put((x + dstYSlice), src.get(srcYSlice + srcX));
                } catch (Exception e) {
                    Log.d(TAG, "nv12_Resize Exception1" + e.getMessage());
                }

                if ((y & 1) == 0) {
                    if ((x & 1) == 0) {
                        srcIndex = (srcX / 2) * 2;
                        sp = dstUvYScanline + x;
                        dp = srcUvYScanline + srcIndex;
                        try {
                            dst.put(sp, src.get(dp));
                        } catch (Exception e) {
                            Log.d(TAG, "nv12_Resize Exception2" + e.getMessage());
                        }
                        ++sp;
                        ++dp;
                        try {
                            dst.put(sp, src.get(dp));
                        } catch (Exception e) {
                            Log.d(TAG, "nv12_Resize Exception3" + e.getMessage());
                        }
                    }
                }
            }
            dstYSlice += dw;
        }
    }

    public static float dp2px(Context context, float dipValue) {
        return dipValue * context.getResources().getDisplayMetrics().density + 0.5f;
    }
}
