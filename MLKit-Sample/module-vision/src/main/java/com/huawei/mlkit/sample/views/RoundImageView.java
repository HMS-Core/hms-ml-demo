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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.util.CommonUtils;

public class RoundImageView extends AppCompatImageView {
    private Context context;
    private int outerBorderColor = Color.WHITE; // Outer border color
    private int outerBorderWidth; // Outer border color width

    private int innerBorderWidth; // Inner border width
    private int innerBorderColor = Color.WHITE; // Inner border color

    private Xfermode xfermode;

    private int width;
    private int height;
    private float radius;

    private RectF rectF;

    private Paint paint;
    private Paint dashPaint;
    private Path path;
    private Path srcPath;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, 0, 0);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.RoundImageView_outer_border_width) {
                outerBorderWidth = typedArray.getDimensionPixelSize(attr, (int) CommonUtils.dp2px(context, 25));
            } else if (attr == R.styleable.RoundImageView_outer_border_color) {
                outerBorderColor = typedArray.getColor(attr, outerBorderColor);
            } else if (attr == R.styleable.RoundImageView_inner_border_width) {
                innerBorderWidth = typedArray.getDimensionPixelSize(attr, (int) CommonUtils.dp2px(context, 15));
            } else if (attr == R.styleable.RoundImageView_inner_border_color) {
                innerBorderColor = typedArray.getColor(attr, innerBorderColor);
            }
        }
        typedArray.recycle();
        init();
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 14 * getScreenWidth() / 15;
        int height = 1 * getScreenHeight() / 2;
        int a = Math.min(width, height);
        setMeasuredDimension(a, a);
    }

    private void init() {
        rectF = new RectF();
        paint = new Paint();
        dashPaint = new Paint();
        path = new Path();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        } else {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            srcPath = new Path();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        initSrcRectF();
        // Use graphics blending mode to display pictures in a specified area
        canvas.saveLayer(rectF, null, Canvas.ALL_SAVE_FLAG);

        float scaleX = 1.0f * (width - 2 * outerBorderWidth - 2 * innerBorderWidth) / width;
        float scaleY = 1.0f * (height - 2 * outerBorderWidth - 2 * innerBorderWidth) / height;
        // Scale the canvas so that the content of the image is not covered by borders
        canvas.scale(scaleX, scaleY, width / 2.0f, height / 2.0f);

        super.onDraw(canvas);
        paint.reset();
        dashPaint.reset();
        path.reset();
        path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xfermode);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            canvas.drawPath(path, paint);
        } else {
            srcPath.addRect(rectF, Path.Direction.CCW);
            srcPath.op(path, Path.Op.DIFFERENCE);
            canvas.drawPath(srcPath, paint);
        }
        paint.setXfermode(null);
        canvas.restore();
        drawBorders(canvas);
    }

    private void drawBorders(Canvas canvas) {
        drawCircleBorder(canvas, outerBorderWidth, outerBorderColor, radius - outerBorderWidth / 2.0f, 15);
        drawDashCircleBorder(canvas, outerBorderWidth, outerBorderColor, radius - outerBorderWidth / 2.0f);
        drawCircleBorder(canvas, innerBorderWidth, innerBorderColor, radius - outerBorderWidth - innerBorderWidth / 2.0f, 25);
    }

    private void drawCircleBorder(Canvas canvas, int borderWidth, int borderColor, float radius, int alpha) {
        initBorderPaint(borderWidth, borderColor);
        paint.setAlpha(alpha);
        path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW);
        canvas.drawPath(path, paint);
    }

    private void drawDashCircleBorder(Canvas canvas, int borderWidth, int borderColor, float radius) {
        dashPaint.setColor(borderColor);
        dashPaint.setStrokeWidth(CommonUtils.dp2px(context, 1));
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setAlpha(20);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{35, 40}, 0));
        canvas.drawCircle(width / 2.0f, height / 2.0f, radius + borderWidth / 2, dashPaint);
    }

    private void initBorderPaint(int borderWidth, int borderColor) {
        path.reset();
        paint.setStrokeWidth(borderWidth);
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void initSrcRectF() {
        radius = Math.min(width, height) / 2.0f;
        rectF.set(width / 2.0f - radius, height / 2.0f - radius, width / 2.0f + radius, height / 2.0f + radius);
    }

}
