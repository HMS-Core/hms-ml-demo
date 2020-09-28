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

package com.huawei.hms.mlkit.sample.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.mlkit.sample.cn.R;

public class SwitchbuttonView extends View {
    private Bitmap swithSilder;
    private int swithWidth;
    private int swithShilerX;

    private int baseHeight;
    private int baseWidth;
    private Boolean currentState = false;
    private Paint paint;

    private OnToggleStateChangeListener mListener;

    public SwitchbuttonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmap();
    }

    private void initBitmap() {
        swithSilder = BitmapFactory.decodeResource(getResources(), R.drawable.swich_slider_new);
        swithWidth = swithSilder.getWidth();
        baseHeight = swithWidth / 20;
        baseWidth = swithWidth / 5;

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E4E4E4"));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        swithShilerX = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF re3 = new RectF(0, 0, swithWidth * 2, swithWidth);
        canvas.drawRoundRect(re3, swithWidth / 2, swithWidth / 2, paint);
        canvas.drawBitmap(swithSilder, swithShilerX, 1.5f, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(swithWidth * 2, swithWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                currentState = !currentState;
                if (currentState) {
                    paint.setColor(Color.parseColor("#566BDE"));
                    swithShilerX = swithWidth - 1;
                } else {
                    paint.setColor(Color.parseColor("#D3D1D1"));
                    swithShilerX = 0;
                }
                mListener.onToggleStateChange(currentState);
                break;
        }
        invalidate();
        return true;

    }

    public void setOnToggleStateChangeListener(OnToggleStateChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnToggleStateChangeListener {
        void onToggleStateChange(boolean state);
    }

}
