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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.huawei.hms.mlsdk.classification.MLImageClassification;
import com.mlkit.sample.util.CommonUtils;
import com.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.List;

public class LocalImageClassificationGraphic extends BaseGraphic {

    private final Paint textPaint;
    private final GraphicOverlay overlay;
    private Context mContext;
    private final List<MLImageClassification> classifications;

    public LocalImageClassificationGraphic(GraphicOverlay overlay, Context context, List<MLImageClassification> classifications) {
        super(overlay);
        this.overlay = overlay;
        this.mContext = context;
        this.classifications = classifications;
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(CommonUtils.dp2px(this.mContext, 16));
    }

    @Override
    public synchronized void draw(Canvas canvas) {
        float x = this.overlay.getWidth() / 4.0f;
        float y = this.overlay.getHeight() / 1.5f;

        for (MLImageClassification classification : this.classifications) {
            canvas.drawText(classification.getName(), x, y, this.textPaint);
            y = y - CommonUtils.dp2px(this.mContext, 18);
        }
    }
}
