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

package com.huawei.mlkit.sample.transactor;


import android.util.SparseArray;

import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints;

public class HandKeypointTransactor implements MLAnalyzer.MLTransactor<MLHandKeypoints> {
    private int firstPosition = 0;
    private int lastPosition = 0;
    private boolean isStart = false;

    private long firstTime;
    private long lastTime;

    private int imageCount = 0;
    private int videoCount = 0;

    private int imageIndex = 0;
    private int videoIndex = 0;


    private boolean isImage = true;

    public interface BackgroundListener {
        void changeState(boolean isImage, int count);
    }

    private BackgroundListener backgroundListener;

    public void setBackgroundListener(BackgroundListener backgroundListener) {
        this.backgroundListener = backgroundListener;
    }

    public HandKeypointTransactor(int imageIndex, int videoIndex) {
        this.imageIndex = imageIndex;
        this.videoIndex = videoIndex;
    }

    @Override
    public void transactResult(MLAnalyzer.Result<MLHandKeypoints> results) {
        SparseArray<MLHandKeypoints> analyseList = results.getAnalyseList();
        if (analyseList == null || analyseList.size() <= 0) {
            isStart = false;
            return;
        }
        int centerX = analyseList.get(0).getRect().centerX();
        if (!isStart) {
            firstTime = System.currentTimeMillis();
            firstPosition = centerX;

            isStart = true;
        }
        lastPosition = centerX;
        lastTime = System.currentTimeMillis();
        int offsetX = lastPosition - firstPosition;

        long delayTime = lastTime - firstTime;

        if (delayTime > 1000) {
            isStart = false;
            return;
        }

        if (offsetX >= 300) {
            isStart = false;
            if (isImage) {
                if (imageCount > 0) {
                    imageCount--;
                } else {
                    imageCount = imageIndex;
                }
                if (backgroundListener != null) {
                    backgroundListener.changeState(isImage, imageCount);
                }
                isImage = false;
            } else {
                if (videoCount > 0) {
                    videoCount--;
                } else {
                    videoCount = videoIndex;
                }
                if (backgroundListener != null) {
                    backgroundListener.changeState(isImage, videoCount);
                }
                isImage = true;
            }

        } else if (offsetX <= -300) {
            isStart = false;

            if (isImage) {
                if (imageCount < imageIndex) {
                    imageCount++;
                } else {
                    imageCount = 0;
                }
                if (backgroundListener != null) {
                    backgroundListener.changeState(isImage, imageCount);
                }
                isImage = false;
            } else {
                if (videoCount < videoIndex) {
                    videoCount++;
                } else {
                    videoCount = 0;
                }
                isImage = true;
                if (backgroundListener != null) {
                    backgroundListener.changeState(isImage, videoCount);
                }
                isImage = true;
            }

        }
    }

    @Override
    public void destroy() {

    }


}
