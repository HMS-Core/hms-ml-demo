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

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.huawei.hms.mlkit.sample.cn.R;

import static android.graphics.Paint.Style.STROKE;

public class CircleProgressBar extends View {
    // Color of progress
    private int outsideColor;
    // Radius of outer circle
    private float outsideRadius;
    // background color
    private int insideColor;
    // Width of the ring
    private float progressWidth;
    // Maximum progress
    private int maxProgress;
    // Current progress
    private float progress;
    // Which direction does the progress start from (four values are set, left, top, right, bottom)
    private int direction;

    private Paint paint;
    private ValueAnimator animator;

    enum DirectionEnum {
        LEFT(0, 180.0f),
        TOP(1, 270.0f),
        RIGHT(2, 0.0f),
        BOTTOM(3, 90.0f);

        private final int direction;
        private final float degree;

        DirectionEnum(int direction, float degree) {
            this.direction = direction;
            this.degree = degree;
        }

        public int getDirection() {
            return direction;
        }

        public float getDegree() {
            return degree;
        }

        public boolean equalsDescription(int direction) {
            return this.direction == direction;
        }

        public static DirectionEnum getDirection(int direction) {
            for (DirectionEnum enumObject : values()) {
                if (enumObject.equalsDescription(direction)) {
                    return enumObject;
                }
            }
            return RIGHT;
        }

        public static float getDegree(int direction) {
            DirectionEnum enumObject = getDirection(direction);
            if (enumObject == null) {
                return 0;
            }
            return enumObject.getDegree();
        }
    }

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typed = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0);
        outsideColor = typed.getColor(R.styleable.CircleProgressBar_outside_color, getResources().getColor(R.color.stopwatch_progress));
        outsideRadius = typed.getDimension(R.styleable.CircleProgressBar_outside_radius, 0);
        insideColor = typed.getColor(R.styleable.CircleProgressBar_inside_color, getResources().getColor(R.color.black_10));
        progressWidth = typed.getDimension(R.styleable.CircleProgressBar_progress_width, 0);
        maxProgress = typed.getInt(R.styleable.CircleProgressBar_max_progress, 0);
        progress = typed.getFloat(R.styleable.CircleProgressBar_progress, 0);
        direction = typed.getInt(R.styleable.CircleProgressBar_direction, 0);
        typed.recycle();
        paint = new Paint();
    }

    /**
     * Step 1: draw the background (inner circle)
     * Step 2: draw progress (ARC)
     * @param canvas
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int circlePoint = getWidth() / 2;
        // Sets the color of the circle
        paint.setColor(insideColor);
        // Set void
        paint.setStyle(STROKE);
        // Sets the width of the circle
        paint.setStrokeWidth(progressWidth);
        // Antialiasing
        paint.setAntiAlias(true);
        // Draw a circle
        canvas.drawCircle(circlePoint, circlePoint, outsideRadius, paint);

        // Set the color of the progress
        paint.setColor(outsideColor);
        // Limits for the shape and size of an arc that you define
        RectF oval = new RectF(circlePoint - outsideRadius, circlePoint - outsideRadius,
                circlePoint + outsideRadius, circlePoint + outsideRadius);
        // Draw an arc according to the progress
        canvas.drawArc(oval, DirectionEnum.getDegree(direction),
                360 * (progress / maxProgress), false, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = (int) ((2 * outsideRadius) + progressWidth);
        }
        size = MeasureSpec.getSize(heightMeasureSpec);
        mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = (int) ((2 * outsideRadius) + progressWidth);
        }
        setMeasuredDimension(width, height);
    }

    public int getOutsideColor() {
        return outsideColor;
    }

    public void setOutsideColor(int outsideColor) {
        this.outsideColor = outsideColor;
    }

    public float getOutsideRadius() {
        return outsideRadius;
    }

    public void setOutsideRadius(float outsideRadius) {
        this.outsideRadius = outsideRadius;
    }

    public int getInsideColor() {
        return insideColor;
    }

    public void setInsideColor(int insideColor) {
        this.insideColor = insideColor;
    }

    public float getProgressWidth() {
        return progressWidth;
    }

    public void setProgressWidth(float progressWidth) {
        this.progressWidth = progressWidth;
    }

    public synchronized int getMaxProgress() {
        return maxProgress;
    }

    public synchronized void setMaxProgress(int maxProgress) {
        if (maxProgress < 0) {
            // This is an exception to pass an illegal parameter
            throw new IllegalArgumentException("maxProgress should not be less than 0");
        }
        this.maxProgress = maxProgress;
    }

    public synchronized float getProgress() {
        return progress;
    }

    /**
     * Locking ensures thread safety and can be used in threads
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress should not be less than 0");
        }
        if (progress > maxProgress) {
            progress = maxProgress;
        }
        startAnim(progress);
    }

    private void startAnim(float startProgress) {
        animator = ObjectAnimator.ofFloat(0, startProgress);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                CircleProgressBar.this.progress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.setStartDelay(500);
        animator.setDuration(30000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
}
