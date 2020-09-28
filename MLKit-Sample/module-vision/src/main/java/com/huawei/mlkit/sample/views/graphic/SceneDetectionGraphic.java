/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.mlkit.sample.views.graphic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.huawei.hms.mlsdk.scd.MLSceneDetection;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.math.BigDecimal;
import java.util.List;

public class SceneDetectionGraphic extends BaseGraphic {

    private final GraphicOverlay overlay;
    private Context mContext;
    private final List<MLSceneDetection> results;
    private float confidence;

    public SceneDetectionGraphic(GraphicOverlay overlay, Context context, List<MLSceneDetection> results, float confidence) {
        super(overlay);
        this.overlay = overlay;
        this.mContext = context;
        this.results = results;
        this.confidence = confidence;
    }

    @Override
    public synchronized void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(48);

        canvas.drawText("SceneCount：" + results.size(), overlay.getWidth()/5, 500, paint);
        for (int i = 0; i < results.size(); i ++) {
            canvas.drawText("Scene：" + results.get(i).getResult(), overlay.getWidth()/5, 100 * (i + 1)+450, paint);
            canvas.drawText("Confidence：" + results.get(i).getConfidence(), overlay.getWidth()/5, (100 * (i + 1))+500 , paint);
        }

    }
}
