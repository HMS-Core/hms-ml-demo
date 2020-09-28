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

package com.huawei.mlkit.sample.views.color;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.huawei.mlkit.sample.util.Constant;

public class ColorSelector extends View {
    // By default, the ratio of long edge to short edge is 6:1.
    private static final int SHORT_SIZE = 70;

    private static final int LONG_SIZE = 420;

    /**
     * Color bar fillet rectangle border.
     */
    private final Rect mRect = new Rect();

    /**
     * mBitmapForIndicator where to draw on the view.
     */
    private final Rect mIndicatorRect = new Rect();
    /**
     * Indicator point color
     */
    private int mIndicatorColor;

    /**
     * Paint for view and mBitmapforcolor.
     */
    private Paint mPaint = null;

    /**
     * Paint for indicator.
     */
    private Paint mIndicatorPaint = null;

    private LinearGradient mLinearGradient;

    private int mTop;

    private int mLeft;

    private int mRight;

    private int mBottom;

    /**
     * Point radius.
     */
    private int mRadius;

    private Bitmap mBitmapForColor;

    private Bitmap mBitmapForIndicator;

    private boolean mIsNeedReDrawColorTable = true;

    private int mCurrentX;

    private int mCurrentY;

    private int[] mColors = null;

    private int mCurrentColor;

    private Context mContext;

    private boolean lastLand = false;

    private int lastWidth;

    private boolean isInitialed = false;


    private OnColorSelectorChangeListener mColorSelectorChangeListener;

    public ColorSelector(Context context) {
        super(context);
    }

    public ColorSelector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        this.mBitmapForColor = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        this.mBitmapForIndicator = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        // setShadowLayer is invalid when hardware acceleration is turned on. Hardware acceleration needs to be turned off.
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);

        this.mIndicatorPaint = new Paint();
        this.mIndicatorPaint.setAntiAlias(true);

        this.mCurrentX = this.mCurrentY = Integer.MAX_VALUE;

        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, com.huawei.mlkit.sample.R.styleable.ColorSelector, defStyleAttr, 0);
        this.mIndicatorColor = array.getColor(com.huawei.mlkit.sample.R.styleable.ColorSelector_indicatorColor, Color.WHITE);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {// Set the width is warp_content in XML.
            width = this.getSuggestedMinimumWidth() + this.getPaddingLeft() + this.getPaddingRight();
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = this.getSuggestedMinimumHeight() + this.getPaddingTop() + this.getPaddingBottom();
        }

        width = Math.max(width, ColorSelector.LONG_SIZE);
        height = Math.max(height, ColorSelector.SHORT_SIZE);

        this.setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        this.mTop = this.getPaddingTop();
        this.mLeft = this.getPaddingLeft();
        this.mBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        this.mRight = this.getMeasuredWidth() - this.getPaddingRight();
        if (this.mCurrentX == this.mCurrentY || this.mCurrentY == Integer.MAX_VALUE) {
            this.mCurrentX = this.getWidth() / 2;
            this.mCurrentY = this.getHeight() / 2;
        }

        this.calculateBounds();
        if (this.mColors == null) {
            this.setColors(Constant.COLOR_TABLE);
        } else {
            this.setColors(this.mColors);
        }
        this.createBitmap();


    }

    private void createBitmap() {
        if (this.mBitmapForColor != null) {
            if (!this.mBitmapForColor.isRecycled()) {
                this.mBitmapForColor.recycle();
                this.mBitmapForColor = null;
            }
        }

        if (this.mBitmapForIndicator != null) {
            if (!this.mBitmapForIndicator.isRecycled()) {
                this.mBitmapForIndicator.recycle();
                this.mBitmapForIndicator = null;
            }
        }

        this.mBitmapForColor = Bitmap.createBitmap(this.mRect.width(), this.mRect.height(), Bitmap.Config.ARGB_8888);
        this.mBitmapForIndicator = Bitmap.createBitmap(this.mRadius * 2, this.mRadius * 2, Bitmap.Config.ARGB_8888);
    }

    /**
     * Calculate color bar boundaries.
     */
    private void calculateBounds() {
        int average = 9;

        int height = this.mBottom - this.mTop;
        int width = this.mRight - this.mLeft;
        int size = Math.min(width, height);

        if (width <= height) { // Width is smaller than height, recalculate height in the way of 6:1.
            size = width / 6;
        }

        int each = size / average;
        this.mRadius = each * 7 / 2;

        int top;
        int left;
        int bottom;
        int right;

        int offset = each * 3 / 2;

        left = this.mLeft + this.mRadius;
        right = this.mRight - this.mRadius;

        top = (this.getHeight() / 2) - offset;
        bottom = (this.getHeight() / 2) + offset;

        this.mRect.set(left, top, right, bottom);
    }

    /**
     * Set the gradient color of the color bar.
     *
     * @param colors color value.
     */
    public void setColors(int... colors) {
        this.mLinearGradient = null;
        this.mColors = colors;

        this.mLinearGradient = new LinearGradient(
                this.mRect.left, this.mRect.top,
                this.mRect.right, this.mRect.top,
                colors,
                null,
                Shader.TileMode.CLAMP
        );
        this.mIsNeedReDrawColorTable = true;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.mIsNeedReDrawColorTable) {
            this.createColorTableBitmap();
        }
        canvas.drawBitmap(this.mBitmapForColor, null, this.mRect, this.mPaint);

        this.createIndicatorBitmap();
        if (!isInitialed) {
            lastWidth = getWidth();
            isInitialed = true;
        }
        // Draw indicator points.
        float scale;
        if (lastLand != isLands()) {
            if (isLands()) {
                lastLand = true;
            } else {
                lastLand = false;
            }
            scale = getWidth() / (lastWidth * 1.0f);
            mCurrentX = (int) (mCurrentX * scale);
            lastWidth = getWidth();
        }
        this.mIndicatorRect.set(this.mCurrentX - this.mRadius, this.mCurrentY - this.mRadius, this.mCurrentX + this.mRadius, this.mCurrentY + this.mRadius);
        canvas.drawBitmap(this.mBitmapForIndicator, null, this.mIndicatorRect, this.mPaint);
    }

    private boolean isLands() {
        Configuration mConfiguration = mContext.getResources().getConfiguration();
        int ori = mConfiguration.orientation;
        return ori == mConfiguration.ORIENTATION_LANDSCAPE;
    }

    private void createIndicatorBitmap() {
        this.mIndicatorPaint.setColor(this.mIndicatorColor);
        int radius = 3;
        this.mIndicatorPaint.setShadowLayer(radius, 0, 0, Color.GRAY);

        Canvas canvas = new Canvas(this.mBitmapForIndicator);
        canvas.drawCircle(this.mRadius, this.mRadius, this.mRadius - radius, this.mIndicatorPaint);
    }

    private void createColorTableBitmap() {
        Canvas canvas = new Canvas(this.mBitmapForColor);
        RectF rf = new RectF(0, 0, this.mBitmapForColor.getWidth(), this.mBitmapForColor.getHeight());

        int radius;
        radius = this.mBitmapForColor.getHeight() / 2;

        this.mPaint.setColor(Color.BLACK);
        canvas.drawRoundRect(rf, radius, radius, this.mPaint);

        this.mPaint.setShader(this.mLinearGradient);
        canvas.drawRoundRect(rf, radius, radius, this.mPaint);
        this.mPaint.setShader(null);

        this.mIsNeedReDrawColorTable = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xValue = (int) event.getX();

        if (!this.inBoundOfColorTable(xValue)) {
            return true;
        }

        this.mCurrentX = xValue;
        this.mCurrentY = this.getHeight() / 2;

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (this.mColorSelectorChangeListener != null) {
                this.mColorSelectorChangeListener.onStartColorSelect(this);
                this.calculateColor();
                this.mColorSelectorChangeListener.onColorChanged(this, this.mCurrentColor);
            }

        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            if (this.mColorSelectorChangeListener != null) {
                this.mColorSelectorChangeListener.onStopColorSelect(this);
                this.calculateColor();
                this.mColorSelectorChangeListener.onColorChanged(this, this.mCurrentColor);
            }

        } else {
            if (this.mColorSelectorChangeListener != null) {
                this.calculateColor();
                this.mColorSelectorChangeListener.onColorChanged(this, this.mCurrentColor);
            }
        }

        this.invalidate();
        return true;
    }

    /**
     * Get the color of the current indicator.
     *
     * @return color value.
     */
    public int getColor() {
        return this.calculateColor();
    }

    private boolean inBoundOfColorTable(int xValue) {
        return xValue > this.mLeft + this.mRadius && xValue < this.mRight - this.mRadius;
    }

    private int calculateColor() {
        int x;
        int y;
        y = (this.mRect.bottom - this.mRect.top) / 2;
        if (this.mCurrentX < this.mRect.left) {
            x = 1;
        } else if (this.mCurrentX > this.mRect.right) {
            x = this.mBitmapForColor.getWidth() - 1;
        } else {
            x = this.mCurrentX - this.mRect.left;
        }
        int pixel = this.mBitmapForColor.getPixel(x, y);
        this.mCurrentColor = this.pixelToColor(pixel);
        return this.mCurrentColor;
    }

    private int pixelToColor(int pixel) {
        return Color.argb(Color.alpha(pixel), Color.red(pixel), Color.green(pixel), Color.blue(pixel));
    }


    public void setOnColorSelectorChangeListener(OnColorSelectorChangeListener listener) {
        this.mColorSelectorChangeListener = listener;
    }

    public interface OnColorSelectorChangeListener {

        /**
         * Callback when the selected color value changes.
         *
         * @param picker ColorSelector.
         * @param color  color value.
         */
        void onColorChanged(ColorSelector picker, int color);

        /**
         * Start color selection.
         *
         * @param picker ColorSelector.
         */
        void onStartColorSelect(ColorSelector picker);

        /**
         * Stop color selection.
         *
         * @param picker ColorSelector.
         */
        void onStopColorSelect(ColorSelector picker);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        MySavedState saveState = new MySavedState(parcelable);
        saveState.xValue = this.mCurrentX;
        saveState.yValue = this.mCurrentY;
        saveState.colors = this.mColors;
        saveState.bitmapColorView = this.mBitmapForColor;
        saveState.bitmapIndicatorView = this.mBitmapForIndicator;
        return saveState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof MySavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        MySavedState ss = (MySavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.mCurrentX = ss.xValue;
        this.mCurrentY = ss.yValue;
        this.mColors = ss.colors;

        this.mBitmapForColor = ss.bitmapColorView;

        this.mBitmapForIndicator = ss.bitmapIndicatorView;
        this.mIsNeedReDrawColorTable = true;
    }

    private class MySavedState extends BaseSavedState {
        int xValue;
        int yValue;
        int[] colors;
        Bitmap bitmapColorView;
        Bitmap bitmapIndicatorView = null;

        MySavedState(Parcelable source) {
            super(source);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.xValue);
            out.writeInt(this.yValue);
            out.writeParcelable(this.bitmapColorView, flags);
            out.writeIntArray(this.colors);
            if (this.bitmapIndicatorView != null) {
                out.writeParcelable(this.bitmapIndicatorView, flags);
            }
        }
    }

    public void initData() {
        this.mIsNeedReDrawColorTable = true;
        this.requestLayout();
    }
}
