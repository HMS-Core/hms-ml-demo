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

package com.huawei.mlkit.sample.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.mlkit.sample.R;

/**
 * Custom switch controls
 *
 * @since 2019-12-26
 */

public class SwitchButton extends View {
    private Bitmap mSwitchIcon;

    private int mSwitchIconWidth;

    private int mSwitchIconXPosition;

    private boolean mSwitchButtonCurrentState = false;

    private Paint mPaint;

    private OnSwitchButtonStateChangeListener mListener;

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public void setCurrentState(boolean currentState) {
        this.mSwitchButtonCurrentState = currentState;
    }

    private void initView() {
        this.mSwitchIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.swich_slider_new);
        this.mSwitchIconWidth = this.mSwitchIcon.getWidth();
        this.mPaint = new Paint();
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeWidth(2);
        // init value
        this.mSwitchIconXPosition = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(this.mSwitchIconWidth * 2, this.mSwitchIconWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF re3 = new RectF(0, 0, this.mSwitchIconWidth * 2, this.mSwitchIconWidth);
        if (this.mSwitchButtonCurrentState) {
            this.mPaint.setColor(this.getResources().getColor(R.color.button_background));
            this.mSwitchIconXPosition = this.mSwitchIconWidth - 1;
        } else {
            this.mPaint.setColor(this.getResources().getColor(R.color.white));
            this.mSwitchIconXPosition = 0;
        }
        canvas.drawRoundRect(re3, this.mSwitchIconWidth / 2.0f, this.mSwitchIconWidth / 2.0f, this.mPaint);
        canvas.drawBitmap(this.mSwitchIcon, this.mSwitchIconXPosition, 1.5f, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            this.mSwitchButtonCurrentState = !this.mSwitchButtonCurrentState;
            this.mListener.onSwitchButtonStateChange(this.mSwitchButtonCurrentState);
        }
        this.invalidate();
        return true;

    }

    /**
     * Set up listener
     *
     * @param listener listener
     */
    public void setOnSwitchButtonStateChangeListener(OnSwitchButtonStateChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnSwitchButtonStateChangeListener {
        /**
         * Switch state change callback method
         * @param state state
         */
        void onSwitchButtonStateChange(boolean state);
    }
}
