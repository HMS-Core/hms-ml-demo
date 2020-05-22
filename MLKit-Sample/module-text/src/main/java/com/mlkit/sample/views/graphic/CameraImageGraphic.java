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

package com.mlkit.sample.views.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.mlkit.sample.views.overlay.GraphicOverlay;

public class CameraImageGraphic extends BaseGraphic {

    private final Bitmap bitmap;

    private boolean isFill = true;

    public CameraImageGraphic(GraphicOverlay overlay, Bitmap bitmap) {
        super(overlay);
        this.bitmap = bitmap;
    }

    public CameraImageGraphic(GraphicOverlay overlay, Bitmap bitmap, boolean isFill) {
        super(overlay);
        this.bitmap = bitmap;
        this.isFill = isFill;
    }

    @Override
    public void draw(Canvas canvas) {
        int width;
        int height;
        if (this.isFill) {
            width = canvas.getWidth();
            height = canvas.getHeight();
        } else {
            width = this.bitmap.getWidth();
            height = this.bitmap.getHeight();
        }
        canvas.drawBitmap(this.bitmap, null, new Rect(0, 0, width, height), null);
    }
}

