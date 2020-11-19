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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.huawei.mlkit.sample.R;

@SuppressLint("AppCompatCustomView")
public class DocumentCorrectImageView extends ImageView {

    private Paint _pointPaint;
    private Paint _pointFillPaint;
    private Paint _linePaint;
    private Paint _guideLinePaint;
    private Point mPoint = null;

    private float[] mMatrix = new float[9];
    private Path mPointLinePath = new Path();

    private int LEFT_TOP = 0;
    private int RIGHT_TOP = 1;
    private int RIGHT_BOTTOM = 2;
    private int LEFT_BOTTOM = 3;

    private float _scaleX;
    private float _scaleY;
    private int _rectWidth;
    private int _rectHeight;
    private int _rectTop;
    private int _rectLeft;

    private float _lineWidth;
    private int _pointColor;
    private float _pointWidth;
    private float _guideLineWidth;
    private int _pointFillColor;
    private int _pointFillAlpha;
    private int _lineColor;
    private int _guideLineColor;

    public DocumentCorrectImageView(Context context) {
        this(context, null);
    }

    public DocumentCorrectImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DocumentCorrectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DocumentCorrectImageView);
        _lineColor = typedArray.getColor(R.styleable.DocumentCorrectImageView_LineColor, 0xFF00FFFF);
        _lineWidth = typedArray.getDimension(R.styleable.DocumentCorrectImageView_LineWidth, dp2px(1));
        _pointColor = typedArray.getColor(R.styleable.DocumentCorrectImageView_PointColor, 0xFF00FFFF);
        _pointWidth = typedArray.getDimension(R.styleable.DocumentCorrectImageView_PointWidth, dp2px(1));
        _guideLineWidth = typedArray.getDimension(R.styleable.DocumentCorrectImageView_GuideLineWidth, dp2px(0.5f));
        _guideLineColor = typedArray.getColor(R.styleable.DocumentCorrectImageView_GuideLineColor, Color.WHITE);
        _pointFillColor = typedArray.getColor(R.styleable.DocumentCorrectImageView_PointFillColor, Color.WHITE);
        _pointFillAlpha = Math.min(Math.max(0, typedArray.getInt(R.styleable.DocumentCorrectImageView_PointFillAlpha, 175)), 255);
        typedArray.recycle();

        _pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _pointPaint.setColor(_pointColor);
        _pointPaint.setStrokeWidth(_pointWidth);
        _pointPaint.setStyle(Paint.Style.STROKE);

        _pointFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _pointFillPaint.setColor(_pointFillColor);
        _pointFillPaint.setStyle(Paint.Style.FILL);
        _pointFillPaint.setAlpha(_pointFillAlpha);

        _linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _linePaint.setColor(_lineColor);
        _linePaint.setStrokeWidth(_lineWidth);
        _linePaint.setStyle(Paint.Style.STROKE);

        _guideLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _guideLinePaint.setColor(_guideLineColor);
        _guideLinePaint.setStyle(Paint.Style.FILL);
        _guideLinePaint.setStrokeWidth(_guideLineWidth);
    }

    private Point[] mChoosePoints;

    public void setPoints(Point[] mChoosePoints) {
        if (this.getDrawable() != null) {
            this.mChoosePoints = mChoosePoints;
            invalidate();
        }
    }

    public Point[] getCropPoints() {
        return mChoosePoints;
    }

    public boolean IsIrRegular() {
        if (!isNull(mChoosePoints)) {
            Point left_top = mChoosePoints[0];
            Point right_top = mChoosePoints[1];
            Point right_bottoom = mChoosePoints[2];
            Point left_bottom = mChoosePoints[3];
            return (operater(left_top, right_bottoom, left_bottom.x, left_bottom.y) * operater(left_top, right_bottoom, right_top.x, right_top.y) < 0) &&
                    (operater(left_bottom, right_top, left_top.x, left_top.y) * operater(left_bottom, right_top, right_bottoom.x, right_bottoom.y) < 0);
        }
        return false;
    }

    private float dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return dp * density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Drawable drawable = getDrawable();
        getImageMatrix().getValues(mMatrix);
        _scaleX = mMatrix[0];
        _scaleY = mMatrix[4];

        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();

            _rectWidth = Math.round(intrinsicWidth * _scaleX);
            _rectHeight = Math.round(intrinsicHeight * _scaleY);
            _rectTop = (getHeight() - _rectHeight) / 2;
            _rectLeft = (getWidth() - _rectWidth) / 2;
        }
        if (isNull(mChoosePoints)) {
            mPointLinePath.reset();
            Point left_top = mChoosePoints[0];
            Point right_top = mChoosePoints[1];
            Point right_bottoom = mChoosePoints[2];
            Point left_bottom = mChoosePoints[3];

            mPointLinePath.moveTo(getPointX(left_top), getPointY(left_top));
            mPointLinePath.lineTo(getPointX(right_top), getPointY(right_top));
            mPointLinePath.lineTo(getPointX(right_bottoom), getPointY(right_bottoom));
            mPointLinePath.lineTo(getPointX(left_bottom), getPointY(left_bottom));
            mPointLinePath.close();

            Path path = mPointLinePath;
            if (path != null) {
                canvas.drawPath(path, _linePaint);
            }
            for (Point point : mChoosePoints) {
                canvas.drawCircle(getPointX(point), getPointY(point), dp2px(10), _pointPaint);
                canvas.drawCircle(getPointX(point), getPointY(point), dp2px(10), _pointFillPaint);
            }
        }
    }

    private long operater(Point point1, Point point2, int x, int y) {
        long point1_x = point1.x;
        long point1_y = point1.y;
        long point2_x = point2.x;
        long point2_y = point2.y;
        return (x - point1_x) * (point2_y - point1_y) - (y - point1_y) * (point2_x - point1_x);
    }

    private float getPointX(Point point) {
        return point.x * _scaleX + _rectLeft;
    }

    private float getPointY(Point point) {
        return point.y * _scaleY + _rectTop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean variable = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isNull(mChoosePoints)) {
                    for (Point point : mChoosePoints) {
                        float downX = event.getX();
                        float downY = event.getY();
                        float pointX = getPointX(point);
                        float pointY = getPointY(point);
                        double distance = Math.sqrt(Math.pow(downX - pointX, 2) + Math.pow(downY - pointY, 2));
                        if (distance < dp2px(14)) {
                            mPoint = point;
                        }
                    }
                }
                if (mPoint == null) variable = false;
                break;
            case MotionEvent.ACTION_MOVE:
                PointType pointType = getPointType(mPoint);
                int x = (int) ((Math.min(Math.max(event.getX(), _rectLeft), _rectLeft + _rectWidth) - _rectLeft) / _scaleX);
                int y = (int) ((Math.min(Math.max(event.getY(), _rectTop), _rectTop + _rectHeight) - _rectTop) / _scaleY);
                if (mPoint != null && pointType != null) {
                    if (pointType == PointType.LEFT_TOP && moveLeftTop(x, y) ||
                            pointType == PointType.RIGHT_TOP && moveRightTop(x, y) ||
                            pointType == PointType.RIGHT_BOTTOM && moveRightBottom(x, y) ||
                            pointType == PointType.LEFT_BOTTOM && moveLeftBottom(x, y)
                    ) {
                        mPoint.x = x;
                        mPoint.y = y;
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                mPoint = null;
                break;
        }
        invalidate();
        return variable || super.onTouchEvent(event);
    }

    private boolean compare(Point point1, Point point2, int x, int y, Point point3) {
        if (operater(point1, point2, x, y) *
                operater(point1, point2, point3.x, point3.y) > 0) {
            return false;
        }
        return true;
    }

    private boolean moveLeftTop(int x, int y) {
        compare(mChoosePoints[RIGHT_TOP], mChoosePoints[LEFT_BOTTOM], x, y, mChoosePoints[RIGHT_BOTTOM]);
        compare(mChoosePoints[RIGHT_TOP], mChoosePoints[RIGHT_BOTTOM], x, y, mChoosePoints[LEFT_BOTTOM]);
        compare(mChoosePoints[LEFT_BOTTOM], mChoosePoints[RIGHT_BOTTOM], x, y, mChoosePoints[RIGHT_TOP]);
        return true;
    }

    private boolean moveRightTop(int x, int y) {
        compare(mChoosePoints[LEFT_TOP], mChoosePoints[RIGHT_BOTTOM], x, y, mChoosePoints[LEFT_BOTTOM]);
        compare(mChoosePoints[LEFT_TOP], mChoosePoints[LEFT_BOTTOM], x, y, mChoosePoints[RIGHT_BOTTOM]);
        compare(mChoosePoints[LEFT_BOTTOM], mChoosePoints[RIGHT_BOTTOM], x, y, mChoosePoints[LEFT_TOP]);
        return true;
    }

    private boolean moveRightBottom(int x, int y) {
        compare(mChoosePoints[RIGHT_TOP], mChoosePoints[LEFT_BOTTOM], x, y, mChoosePoints[LEFT_TOP]);
        compare(mChoosePoints[LEFT_TOP], mChoosePoints[RIGHT_TOP], x, y, mChoosePoints[LEFT_BOTTOM]);
        compare(mChoosePoints[LEFT_TOP], mChoosePoints[LEFT_BOTTOM], x, y, mChoosePoints[RIGHT_TOP]);
        return true;
    }

    private boolean moveLeftBottom(int x, int y) {
        compare(mChoosePoints[LEFT_TOP], mChoosePoints[RIGHT_BOTTOM], x, y, mChoosePoints[RIGHT_TOP]);
        compare(mChoosePoints[LEFT_TOP], mChoosePoints[RIGHT_TOP], x, y, mChoosePoints[RIGHT_BOTTOM]);
        compare(mChoosePoints[RIGHT_TOP], mChoosePoints[RIGHT_BOTTOM], x, y, mChoosePoints[LEFT_TOP]);
        return true;
    }

    private PointType getPointType(Point point) {
        PointType type = null;
        if (point != null) {
            if (isNull(mChoosePoints)) {
                for (int i = 0; i < mChoosePoints.length; i++) {
                    if (point == mChoosePoints[i]) {
                        type = PointType.values()[i];
                    }
                }
            }
        }
        return type;
    }

    public boolean isNull(Point[] points) {
        return points != null && points.length == 4;
    }

}
