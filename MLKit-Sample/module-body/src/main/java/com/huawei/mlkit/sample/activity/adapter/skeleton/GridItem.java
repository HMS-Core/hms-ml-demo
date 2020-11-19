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

package com.huawei.mlkit.sample.activity.adapter.skeleton;

import android.graphics.Bitmap;

import com.huawei.hms.mlsdk.skeleton.MLSkeleton;

import java.util.List;


public class GridItem {
    private Bitmap bitmap;

    private boolean isSelected = false;

    private List<MLSkeleton> skeletonList;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelect) {
        isSelected = isSelect;
    }

    public List<MLSkeleton> getSkeletonList() {
        return skeletonList;
    }

    public void setSkeletonList(List<MLSkeleton> skeletonList) {
        this.skeletonList = skeletonList;
    }
}
