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

import com.mlkit.sample.util.CommonUtils;
import com.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.List;

public class RemoteImageClassificationGraphic extends BaseGraphic {
    private static final int MAX_LENGTH = 30;
    private final Paint textPaint;
    private final GraphicOverlay overlay;
    private Context mContext;
    private List<String> classifications;

    public RemoteImageClassificationGraphic(GraphicOverlay overlay, Context context, List<String> classifications) {
        super(overlay);
        this.overlay = overlay;
        this.mContext = context;
        this.classifications = classifications;
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(CommonUtils.dp2px(this.mContext, 14));
    }

    @Override
    public synchronized void draw(Canvas canvas) {
        float x = 0f;
        int index = 0;
        float space = CommonUtils.dp2px(this.mContext, 16);
        float y = this.overlay.getHeight() - CommonUtils.dp2px(this.mContext, 30);

        for (String classification : this.classifications) {
            if (classification.length() > RemoteImageClassificationGraphic.MAX_LENGTH) {
                canvas.drawText(classification.substring(0, RemoteImageClassificationGraphic.MAX_LENGTH), x, y, this.textPaint);
                y = y - space;
                canvas.drawText(classification.substring(RemoteImageClassificationGraphic.MAX_LENGTH), x, y, this.textPaint);
            } else {
                index++;
                if (index == 1) {
                    x = CommonUtils.dp2px(this.mContext, 12);
                } else if (index == 2) {
                    x = this.overlay.getWidth() / 2;
                }
                canvas.drawText(classification, x, y, this.textPaint);
                if (index == 2) {
                    y = y - space;
                    index = 0;
                }
            }
        }
    }
}