/**
 *  Copyright 2018 Google LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.huawei.mlkit.sample.views.graphic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.huawei.hms.mlsdk.gesture.MLGesture;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;

import java.util.List;

public class GestureGraphic extends BaseGraphic {

    private final GraphicOverlay overlay;
    private Context mContext;
    private final List<MLGesture> results;

    private Paint textPaint;
    private Paint linePaint;
    private Paint rectPaint;
    private final Rect rect;


    public GestureGraphic(GraphicOverlay overlay, Context context, List<MLGesture> results) {
        super(overlay);
        this.overlay = overlay;
        this.mContext = context;
        this.results = results;

        textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(5f);
        textPaint.setTextSize(100);

        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
        linePaint.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(Color.BLUE);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5f);
        rectPaint.setAntiAlias(true);

        rect = new Rect();

    }

    @Override
    public synchronized void draw(Canvas canvas) {

        for (int i = 0; i < results.size(); i++) {
            MLGesture mlGesture = results.get(i);

            canvas.drawRect(rect, rectPaint);

            Rect rect = translateRect(mlGesture.getRect());
            if (rect.right < rect.left) {
                int x = rect.left;
                rect.left = rect.right;
                rect.right = x;
            }
            canvas.drawRect(rect, linePaint);
            canvas.drawText(getChineseDescription(mlGesture.getCategory()),
                    translateX((mlGesture.getRect().left+mlGesture.getRect().right)/2),
                    translateY((mlGesture.getRect().top+mlGesture.getRect().bottom)/2),
                    textPaint);

        }
    }

    private String getChineseDescription(int gestureCategory) {
        String chineseDescription;
        switch (gestureCategory) {
            case MLGesture.ONE:
                chineseDescription = mContext.getString(R.string.gesture_one);
                break;
            case MLGesture.SECOND:
                chineseDescription = mContext.getString(R.string.gesture_two);
                break;
            case MLGesture.THREE:
                chineseDescription = mContext.getString(R.string.gesture_three);
                break;
            case MLGesture.FOUR:
                chineseDescription = mContext.getString(R.string.gesture_four);
                break;
            case MLGesture.FIVE:
                chineseDescription = mContext.getString(R.string.gesture_five);
                break;
            case MLGesture.SIX:
                chineseDescription = mContext.getString(R.string.gesture_six);
                break;
            case MLGesture.SEVEN:
                chineseDescription = mContext.getString(R.string.gesture_seven);
                break;
            case MLGesture.EIGHT:
                chineseDescription = mContext.getString(R.string.gesture_eight);
                break;
            case MLGesture.NINE:
                chineseDescription = mContext.getString(R.string.gesture_nine);
                break;
            case MLGesture.DISS:
                chineseDescription = mContext.getString(R.string.gesture_diss);
                break;
            case MLGesture.FIST:
                chineseDescription = mContext.getString(R.string.gesture_clench);
                break;
            case MLGesture.GOOD:
                chineseDescription = mContext.getString(R.string.gesture_likes);
                break;
            case MLGesture.HEART:
                chineseDescription = mContext.getString(R.string.gesture_bx);
                break;
            case MLGesture.OK:
                chineseDescription = mContext.getString(R.string.gesture_confirm);
                break;
            default:
                chineseDescription = mContext.getString(R.string.gesture_others);
                break;

        }
        return chineseDescription;
    }

}
