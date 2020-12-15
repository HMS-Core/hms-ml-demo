/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.mlkit.sample.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.mlkit.sample.R;

public class CustomProgressView extends View {
    private static final float mMaxValue = 60;
    private int progress = 60;
    private float progressWidth;
    private float progressHeight;
    private float aboveProgressWidth;
    private Paint backgroundPaint;
    private Paint abovePaint;

    private float ratio;
    private float radio;

    private Bitmap bitmap;
    private float bitmapWidth;
    private float bitmapHeight;


    public void setProgress(int mValue) {
        this.progress = mValue;
        aboveProgressWidth = ratio * progress;
        invalidate();
    }


    public CustomProgressView(Context context) {
        super(context);
        init(context);
    }

    public CustomProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        progressWidth = dip2px(context, 138);
        progressHeight = dip2px(context, 3);
        radio = dip2px(context, 2);
        ratio = progressWidth / mMaxValue;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.deer);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();

        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.progressbackground));
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroundPaint.setAntiAlias(true);

        abovePaint = new Paint();
        abovePaint.setColor(getResources().getColor(R.color.progresstop));
        abovePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        abovePaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bitmap, aboveProgressWidth, 0, null);

        drawRect(canvas, backgroundPaint, bitmapWidth / 2, bitmapHeight + progressHeight, progressWidth + bitmapWidth / 2, bitmapHeight + progressHeight * 2);
        drawRect(canvas, abovePaint, bitmapWidth / 2, bitmapHeight + progressHeight, aboveProgressWidth + bitmapWidth / 2, bitmapHeight + progressHeight * 2);
        super.onDraw(canvas);
    }


    private void drawRect(Canvas canvas, Paint paint, float left, float top, float right, float bottom) {
        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rectF, radio, radio, paint);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
