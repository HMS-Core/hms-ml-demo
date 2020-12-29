/**
 *  Copyright 2020 https://github.com/MikeOrtiz/TouchImageView
 *
 *  Licensed under the MIT License;
 *  you may not use this file except in compliance with the License.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  The above copyright notice and this permission notice shall be included in all copies or
 *  substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 *  FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
 */

package com.huawei.mlkit.sample.views.overlay;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.Scroller;

@SuppressLint("AppCompatCustomView")
public class ZoomImageView extends ImageView {

    private static final String DEBUG = "DEBUG";

    //
    // SuperMin and SuperMax multipliers. Determine how much the image can be
    // zoomed below or above the zoom boundaries, before animating back to the
    // min/max zoom boundary.
    //
    private static final float SUPER_MIN_MULTIPLIER = 0.75f;
    private static final float SUPER_MAX_MULTIPLIER = 1.25f;

    //
    // Scale of image ranges from minScale to maxScale, where minScale == 1
    // when the image is stretched to fit view.
    //
    private float normalizedScale;

    //
    // Matrix applied to image. MSCALE_X and MSCALE_Y should always be equal.
    // MTRANS_X and MTRANS_Y are the other values used. prevMatrix is the matrix
    // saved prior to the screen rotating.
    //
    private Matrix matrix;
    private Matrix prevMatrix;

    private enum State {
        NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM
    }

    private State state;

    private float minScale;
    private float maxScale;
    private float superMinScale;
    private float superMaxScale;
    private float[] m;

    private Context context;
    private Fling fling;

    private ScaleType mScaleType;

    private boolean imageRenderedAtLeastOnce;
    private boolean onDrawReady;

    private ZoomVariables delayedZoomVariables;

    //
    // Size of view and previous view size (ie before rotation)
    //
    private int viewWidth;
    private int viewHeight;
    private int prevViewWidth;
    private int prevViewHeight;

    //
    // Size of image when it is stretched to fit view. Before and After rotation.
    //
    private float matchViewWidth;
    private float matchViewHeight;
    private float prevMatchViewWidth;
    private float prevMatchViewHeight;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private GestureDetector.OnDoubleTapListener doubleTapListener = null;
    private OnTouchListener userTouchListener = null;
    private OnTouchImageViewListener touchImageViewListener = null;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.configureImageView(context);
    }

    private void configureImageView(Context context) {
        this.context = context;

        super.setClickable(true);

        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.mGestureDetector = new GestureDetector(context, new GestureListener());

        this.matrix = new Matrix();
        this.prevMatrix = new Matrix();

        this.m = new float[9];
        this.normalizedScale = 1;
        if (this.mScaleType == null) {
            this.mScaleType = ScaleType.FIT_CENTER;
        }

        this.minScale = 1;
        this.maxScale = 3;

        this.superMinScale = ZoomImageView.SUPER_MIN_MULTIPLIER * this.minScale;
        this.superMaxScale = ZoomImageView.SUPER_MAX_MULTIPLIER * this.maxScale;

        this.setImageMatrix(this.matrix);
        this.setScaleType(ScaleType.MATRIX);
        this.setState(State.NONE);

        this.onDrawReady = false;
        super.setOnTouchListener(new PrivateOnTouchListener());
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this.userTouchListener = l;
    }

    public void setOnTouchImageViewListener(OnTouchImageViewListener l) {
        this.touchImageViewListener = l;
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener l) {
        this.doubleTapListener = l;
    }

    @Override
    public void setImageResource(int resId) {
        this.imageRenderedAtLeastOnce = false;
        super.setImageResource(resId);
        this.savePreviousImageValues();
        this.fitImageToView();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        this.imageRenderedAtLeastOnce = false;
        super.setImageBitmap(bm);
        this.savePreviousImageValues();
        this.fitImageToView();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        this.imageRenderedAtLeastOnce = false;
        super.setImageDrawable(drawable);
        this.savePreviousImageValues();
        this.fitImageToView();
    }

    @Override
    public void setImageURI(Uri uri) {
        this.imageRenderedAtLeastOnce = false;
        super.setImageURI(uri);
        this.savePreviousImageValues();
        this.fitImageToView();
    }

    @Override
    public void setScaleType(ScaleType type) {
        if (type == ScaleType.MATRIX) {
            super.setScaleType(ScaleType.MATRIX);

        } else {
            this.mScaleType = type;
            if (this.onDrawReady) {
                //
                // If the image is already rendered, scaleType has been called programmatically
                // and the TouchImageView should be updated with the new scaleType.
                //
                this.setZoom(this);
            }
        }
    }

    @Override
    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    /**
     * Returns false if image is in initial, unzoomed state. False, otherwise.
     *
     * @return true if image is zoomed
     */
    public boolean isZoomed() {
        return this.normalizedScale != 1;
    }

    /**
     * Return a Rect representing the zoomed image.
     *
     * @return rect representing zoomed image
     */
    public RectF getZoomedRect() {
        if (this.mScaleType == ScaleType.FIT_XY) {
            throw new UnsupportedOperationException("getZoomedRect() not supported with FIT_XY");
        }
        PointF topLeft = this.transformCoordTouchToBitmap(0, 0, true);
        PointF bottomRight = this.transformCoordTouchToBitmap(this.viewWidth, this.viewHeight, true);

        float w = this.getDrawable().getIntrinsicWidth();
        float h = this.getDrawable().getIntrinsicHeight();
        return new RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h);
    }

    /**
     * Save the current matrix and view dimensions
     * in the prevMatrix and prevView variables.
     */
    public void savePreviousImageValues() {
        if (this.matrix != null && this.viewHeight != 0 && this.viewWidth != 0) {
            this.matrix.getValues(this.m);
            this.prevMatrix.setValues(this.m);
            this.prevMatchViewHeight = this.matchViewHeight;
            this.prevMatchViewWidth = this.matchViewWidth;
            this.prevViewHeight = this.viewHeight;
            this.prevViewWidth = this.viewWidth;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putFloat("saveScale", this.normalizedScale);
        bundle.putFloat("matchViewHeight", this.matchViewHeight);
        bundle.putFloat("matchViewWidth", this.matchViewWidth);
        bundle.putInt("viewWidth", this.viewWidth);
        bundle.putInt("viewHeight", this.viewHeight);
        this.matrix.getValues(this.m);
        bundle.putFloatArray("matrix", this.m);
        bundle.putBoolean("imageRendered", this.imageRenderedAtLeastOnce);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.normalizedScale = bundle.getFloat("saveScale");
            this.m = bundle.getFloatArray("matrix");
            this.prevMatrix.setValues(this.m);
            this.prevMatchViewHeight = bundle.getFloat("matchViewHeight");
            this.prevMatchViewWidth = bundle.getFloat("matchViewWidth");
            this.prevViewHeight = bundle.getInt("viewHeight");
            this.prevViewWidth = bundle.getInt("viewWidth");
            this.imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered");
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.onDrawReady = true;
        this.imageRenderedAtLeastOnce = true;
        if (this.delayedZoomVariables != null) {
            this.setZoom(this.delayedZoomVariables.scale, this.delayedZoomVariables.focusX, this.delayedZoomVariables.focusY, this.delayedZoomVariables.scaleType);
            this.delayedZoomVariables = null;
        }
        super.onDraw(canvas);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.savePreviousImageValues();
    }

    /**
     * Get the max zoom multiplier.
     *
     * @return max zoom multiplier.
     */
    public float getMaxZoom() {
        return this.maxScale;
    }

    /**
     * Set the max zoom multiplier. Default value: 3.
     *
     * @param max max zoom multiplier.
     */
    public void setMaxZoom(float max) {
        this.maxScale = max;
        this.superMaxScale = ZoomImageView.SUPER_MAX_MULTIPLIER * this.maxScale;
    }

    /**
     * Get the min zoom multiplier.
     *
     * @return min zoom multiplier.
     */
    public float getMinZoom() {
        return this.minScale;
    }

    /**
     * Get the current zoom. This is the zoom relative to the initial
     * scale, not the original resource.
     *
     * @return current zoom multiplier.
     */
    public float getCurrentZoom() {
        return this.normalizedScale;
    }

    /**
     * Set the min zoom multiplier. Default value: 1.
     *
     * @param min min zoom multiplier.
     */
    public void setMinZoom(float min) {
        this.minScale = min;
        this.superMinScale = ZoomImageView.SUPER_MIN_MULTIPLIER * this.minScale;
    }

    /**
     * Reset zoom and translation to initial state.
     */
    public void resetZoom() {
        this.normalizedScale = 1;
        this.fitImageToView();
    }

    /**
     * Set zoom to the specified scale. Image will be centered by default.
     *
     * @param scale scale
     */
    public void setZoom(float scale) {
        this.setZoom(scale, 0.5f, 0.5f);
    }

    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     *
     * @param scale scale
     * @param focusX focusX
     * @param focusY focusY
     */
    public void setZoom(float scale, float focusX, float focusY) {
        this.setZoom(scale, focusX, focusY, this.mScaleType);
    }

    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     *
     * @param scale scale
     * @param focusX focusX
     * @param focusY focusY
     * @param scaleType scale type
     */
    public void setZoom(float scale, float focusX, float focusY, ScaleType scaleType) {
        //
        // setZoom can be called before the image is on the screen, but at this point,
        // image and view sizes have not yet been calculated in onMeasure. Thus, we should
        // delay calling setZoom until the view has been measured.
        //
        if (!this.onDrawReady) {
            this.delayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
            return;
        }

        if (scaleType != this.mScaleType) {
            this.setScaleType(scaleType);
        }
        this.resetZoom();
        this.scaleImage(scale, this.viewWidth / 2.0f, this.viewHeight / 2.0f, true);
        this.matrix.getValues(this.m);
        this.m[Matrix.MTRANS_X] = -((focusX * this.getImageWidth()) - (this.viewWidth * 0.5f));
        this.m[Matrix.MTRANS_Y] = -((focusY * this.getImageHeight()) - (this.viewHeight * 0.5f));
        this.matrix.setValues(this.m);
        this.fixTrans();
        this.setImageMatrix(this.matrix);
    }

    /**
     * Set zoom parameters equal to another TouchImageView. Including scale, position,
     * and ScaleType.
     *
     * @param img image
     */
    public void setZoom(ZoomImageView img) {
        PointF center = img.getScrollPosition();
        this.setZoom(img.getCurrentZoom(), center.x, center.y, img.getScaleType());
    }

    /**
     * Return the point at the center of the zoomed image. The PointF coordinates range
     * in value between 0 and 1 and the focus point is denoted as a fraction from the left
     * and top of the view. For example, the top left corner of the image would be (0, 0).
     * And the bottom right corner would be (1, 1).
     *
     * @return PointF representing the scroll position of the zoomed image.
     */
    public PointF getScrollPosition() {
        Drawable drawable = this.getDrawable();
        if (drawable == null) {
            return null;
        }
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        PointF point = this.transformCoordTouchToBitmap(this.viewWidth / 2.0f, this.viewHeight / 2.0f, true);
        point.x /= drawableWidth;
        point.y /= drawableHeight;
        return point;
    }

    /**
     * Set the focus point of the zoomed image. The focus points are denoted as a fraction from the
     * left and top of the view. The focus points can range in value between 0 and 1.
     *
     * @param focusX focusX
     * @param focusY focusY
     */
    public void setScrollPosition(float focusX, float focusY) {
        this.setZoom(this.normalizedScale, focusX, focusY);
    }

    /**
     * Performs boundary checking and fixes the image matrix if it
     * is out of bounds.
     */
    private void fixTrans() {
        this.matrix.getValues(this.m);
        float transX = this.m[Matrix.MTRANS_X];
        float transY = this.m[Matrix.MTRANS_Y];

        float fixTransX = this.getFixTrans(transX, this.viewWidth, this.getImageWidth());
        float fixTransY = this.getFixTrans(transY, this.viewHeight, this.getImageHeight());

        if (fixTransX != 0 || fixTransY != 0) {
            this.matrix.postTranslate(fixTransX, fixTransY);
        }
    }

    /**
     * When transitioning from zooming from focus to zoom from center (or vice versa)
     * the image can become unaligned within the view. This is apparent when zooming
     * quickly. When the content size is less than the view size, the content will often
     * be centered incorrectly within the view. fixScaleTrans first calls fixTrans() and
     * then makes sure the image is centered correctly within the view.
     */
    private void fixScaleTrans() {
        this.fixTrans();
        this.matrix.getValues(this.m);
        if (this.getImageWidth() < this.viewWidth) {
            this.m[Matrix.MTRANS_X] = (this.viewWidth - this.getImageWidth()) / 2;
        }

        if (this.getImageHeight() < this.viewHeight) {
            this.m[Matrix.MTRANS_Y] = (this.viewHeight - this.getImageHeight()) / 2;
        }
        this.matrix.setValues(this.m);
    }

    private float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans;
        float maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;

        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans) {
            return -trans + minTrans;
        }
        if (trans > maxTrans) {
            return -trans + maxTrans;
        }
        return 0;
    }

    private float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    private float getImageWidth() {
        return this.matchViewWidth * this.normalizedScale;
    }

    private float getImageHeight() {
        return this.matchViewHeight * this.normalizedScale;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = this.getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            this.setMeasuredDimension(0, 0);
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int totalViewWidth = this.setViewSize(widthMode, widthSize, drawableWidth);
        int totalViewHeight = this.setViewSize(heightMode, heightSize, drawableHeight);

        // Image view width, height must consider padding
        this.viewWidth = totalViewWidth - this.getPaddingLeft() - this.getPaddingRight();
        this.viewHeight = totalViewHeight - this.getPaddingTop() - this.getPaddingBottom();

        //
        // Set view dimensions
        //
        this.setMeasuredDimension(this.viewWidth, this.viewHeight);

        //
        // Fit content within view
        //
        this.fitImageToView();
    }

    /**
     * If the normalizedScale is equal to 1, then the image is made to fit the screen. Otherwise,
     * it is made to fit the screen according to the dimensions of the previous image matrix. This
     * allows the image to maintain its zoom after rotation.
     */
    private void fitImageToView() {
        Drawable drawable = this.getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return;
        }
        if (this.matrix == null || this.prevMatrix == null) {
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        //
        // Scale image for view
        //
        float scaleX = this.viewWidth / (float) drawableWidth;
        float scaleY = this.viewHeight / (float) drawableHeight;

        switch (this.mScaleType) {
            case CENTER:
                scaleX = scaleY = 1;
                break;
            case CENTER_CROP:
                scaleX = scaleY = Math.max(scaleX, scaleY);
                break;
            case CENTER_INSIDE:
                scaleX = scaleY = Math.min(1, Math.min(scaleX, scaleY));
                // fall through
            case FIT_CENTER:
                // fall-through
            case FIT_START:
                // fall-through
            case FIT_END:
                scaleX = scaleY = Math.min(scaleX, scaleY);
                break;
            case FIT_XY:
                // fall-through
            default:
                break;
        }

        //
        // Center the image
        //
        float redundantXSpace = this.viewWidth - (scaleX * drawableWidth);
        float redundantYSpace = this.viewHeight - (scaleY * drawableHeight);
        this.matchViewWidth = this.viewWidth - redundantXSpace;
        this.matchViewHeight = this.viewHeight - redundantYSpace;
        if (!this.isZoomed() && !this.imageRenderedAtLeastOnce) {
            //
            // Stretch and center image to fit view
            //
            this.matrix.setScale(scaleX, scaleY);
            switch (this.mScaleType) {
                case FIT_START:
                    this.matrix.postTranslate(0, 0);
                    break;
                case FIT_END:
                    this.matrix.postTranslate(redundantXSpace, redundantYSpace);
                    break;
                default:
                    this.matrix.postTranslate(redundantXSpace / 2.0f, redundantYSpace / 2.0f);
            }
            this.normalizedScale = 1;

        } else {
            //
            // These values should never be 0 or we will set viewWidth and viewHeight
            // to NaN in translateMatrixAfterRotate. To avoid this, call savePreviousImageValues
            // to set them equal to the current values.
            //
            //if (prevMatchViewWidth == 0 || prevMatchViewHeight == 0) {
            this.savePreviousImageValues();
            //}

            this.prevMatrix.getValues(this.m);

            //
            // Rescale Matrix after rotation
            //
            this.m[Matrix.MSCALE_X] = this.matchViewWidth / drawableWidth * this.normalizedScale;
            this.m[Matrix.MSCALE_Y] = this.matchViewHeight / drawableHeight * this.normalizedScale;

            //
            // TransX and TransY from previous matrix
            //
            float transX = this.m[Matrix.MTRANS_X];
            float transY = this.m[Matrix.MTRANS_Y];

            //
            // Width
            //
            float prevActualWidth = this.prevMatchViewWidth * this.normalizedScale;
            float actualWidth = this.getImageWidth();
            this.translateMatrixAfterRotate(Matrix.MTRANS_X, transX, prevActualWidth, actualWidth, this.prevViewWidth, this.viewWidth, drawableWidth);

            //
            // Height
            //
            float prevActualHeight = this.prevMatchViewHeight * this.normalizedScale;
            float actualHeight = this.getImageHeight();
            this.translateMatrixAfterRotate(Matrix.MTRANS_Y, transY, prevActualHeight, actualHeight, this.prevViewHeight, this.viewHeight, drawableHeight);

            //
            // Set the matrix to the adjusted scale and translate values.
            //
            this.matrix.setValues(this.m);
        }
        this.fixTrans();
        this.setImageMatrix(this.matrix);
    }

    /**
     * Set view dimensions based on layout params
     *
     * @param mode mode
     * @param size size
     * @param drawableWidth width
     * @return size
     */
    private int setViewSize(int mode, int size, int drawableWidth) {
        int viewSize;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                viewSize = size;
                break;

            case MeasureSpec.AT_MOST:
                viewSize = Math.min(drawableWidth, size);
                break;

            case MeasureSpec.UNSPECIFIED:
                viewSize = drawableWidth;
                break;

            default:
                viewSize = size;
                break;
        }
        return viewSize;
    }

    /**
     * After rotating, the matrix needs to be translated. This function finds the area of image
     * which was previously centered and adjusts translations so that is again the center, post-rotation.
     *
     * @param axis          Matrix.MTRANS_X or Matrix.MTRANS_Y
     * @param trans         the value of trans in that axis before the rotation
     * @param prevImageSize the width/height of the image before the rotation
     * @param imageSize     width/height of the image after rotation
     * @param prevViewSize  width/height of view before rotation
     * @param viewSize      width/height of view after rotation
     * @param drawableSize  width/height of drawable
     */
    private void translateMatrixAfterRotate(int axis, float trans, float prevImageSize, float imageSize, int prevViewSize, int viewSize, int drawableSize) {
        if (imageSize < viewSize) {
            //
            // The width/height of image is less than the view's width/height. Center it.
            //
            this.m[axis] = (viewSize - (drawableSize * this.m[Matrix.MSCALE_X])) * 0.5f;

        } else if (trans > 0) {
            //
            // The image is larger than the view, but was not before rotation. Center it.
            //
            this.m[axis] = -((imageSize - viewSize) * 0.5f);

        } else {
            //
            // Find the area of the image which was previously centered in the view. Determine its distance
            // from the left/top side of the view as a fraction of the entire image's width/height. Use that percentage
            // to calculate the trans in the new view width/height.
            //
            float percentage = (Math.abs(trans) + (0.5f * prevViewSize)) / prevImageSize;
            this.m[axis] = -((percentage * imageSize) - (viewSize * 0.5f));
        }
    }

    private void setState(State state) {
        this.state = state;
    }

    public boolean canScrollHorizontallyFroyo(int direction) {
        return this.canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        this.matrix.getValues(this.m);
        float x = this.m[Matrix.MTRANS_X];

        if (this.getImageWidth() < this.viewWidth) {
            return false;

        } else if (x >= -1 && direction < 0) {
            return false;

        } else {
            return !(Math.abs(x) + this.viewWidth + 1 >= this.getImageWidth()) || direction <= 0;
        }

    }

    @Override
    public boolean canScrollVertically(int direction) {
        this.matrix.getValues(this.m);
        float y = this.m[Matrix.MTRANS_Y];

        if (this.getImageHeight() < this.viewHeight) {
            return false;

        } else if (y >= -1 && direction < 0) {
            return false;

        } else {
            return !(Math.abs(y) + this.viewHeight + 1 >= this.getImageHeight()) || direction <= 0;
        }

    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (ZoomImageView.this.doubleTapListener != null) {
                return ZoomImageView.this.doubleTapListener.onSingleTapConfirmed(e);
            }
            return ZoomImageView.this.performClick();
        }

        @Override
        public void onLongPress(MotionEvent e) {
            ZoomImageView.this.performLongClick();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (ZoomImageView.this.fling != null) {
                //
                // If a previous fling is still active, it should be cancelled so that two flings
                // are not run simultaenously.
                //
                ZoomImageView.this.fling.cancelFling();
            }
            ZoomImageView.this.fling = new Fling((int) velocityX, (int) velocityY);
            ZoomImageView.this.compatPostOnAnimation(ZoomImageView.this.fling);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            boolean consumed = false;
            if (ZoomImageView.this.doubleTapListener != null) {
                consumed = ZoomImageView.this.doubleTapListener.onDoubleTap(e);
            }
            if (ZoomImageView.this.state == State.NONE) {
                float targetZoom = (Math.abs(ZoomImageView.this.normalizedScale - ZoomImageView.this.minScale) < 0.00001f) ? ZoomImageView.this.maxScale : ZoomImageView.this.minScale;
                DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, e.getX(), e.getY(), false);
                ZoomImageView.this.compatPostOnAnimation(doubleTap);
                consumed = true;
            }
            return consumed;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (ZoomImageView.this.doubleTapListener != null) {
                return ZoomImageView.this.doubleTapListener.onDoubleTapEvent(e);
            }
            return false;
        }
    }

    public interface OnTouchImageViewListener {
        /**
         * Callback of moving event
         */
        void onMove();
    }


    private class PrivateOnTouchListener implements OnTouchListener {

        //
        // Remember last point position for dragging
        //
        private PointF last = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ZoomImageView.this.mScaleDetector.onTouchEvent(event);
            ZoomImageView.this.mGestureDetector.onTouchEvent(event);
            PointF curr = new PointF(event.getX(), event.getY());

            if (ZoomImageView.this.state == State.NONE || ZoomImageView.this.state == State.DRAG || ZoomImageView.this.state == State.FLING) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        this.last.set(curr);
                        if (ZoomImageView.this.fling != null) {
                            ZoomImageView.this.fling.cancelFling();
                        }
                        ZoomImageView.this.setState(State.DRAG);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (ZoomImageView.this.state == State.DRAG) {
                            float deltaX = curr.x - this.last.x;
                            float deltaY = curr.y - this.last.y;
                            float fixTransX = ZoomImageView.this.getFixDragTrans(deltaX, ZoomImageView.this.viewWidth, ZoomImageView.this.getImageWidth());
                            float fixTransY = ZoomImageView.this.getFixDragTrans(deltaY, ZoomImageView.this.viewHeight, ZoomImageView.this.getImageHeight());
                            ZoomImageView.this.matrix.postTranslate(fixTransX, fixTransY);
                            ZoomImageView.this.fixTrans();
                            this.last.set(curr.x, curr.y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        ZoomImageView.this.setState(State.NONE);
                        break;
                    default:
                        break;
                }
            }

            ZoomImageView.this.setImageMatrix(ZoomImageView.this.matrix);

            //
            // User-defined OnTouchListener
            //
            if (ZoomImageView.this.userTouchListener != null) {
                ZoomImageView.this.userTouchListener.onTouch(v, event);
            }

            //
            // OnTouchImageViewListener is set: TouchImageView dragged by user.
            //
            if (ZoomImageView.this.touchImageViewListener != null) {
                ZoomImageView.this.touchImageViewListener.onMove();
            }

            //
            // indicate event was handled
            //
            return true;
        }
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            ZoomImageView.this.setState(State.ZOOM);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            ZoomImageView.this.scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);

            //
            // OnTouchImageViewListener is set: TouchImageView pinch zoomed by user.
            //
            if (ZoomImageView.this.touchImageViewListener != null) {
                ZoomImageView.this.touchImageViewListener.onMove();
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            ZoomImageView.this.setState(State.NONE);
            boolean animateToZoomBoundary = false;
            float targetZoom = ZoomImageView.this.normalizedScale;
            if (ZoomImageView.this.normalizedScale > ZoomImageView.this.maxScale) {
                targetZoom = ZoomImageView.this.maxScale;
                animateToZoomBoundary = true;

            } else if (ZoomImageView.this.normalizedScale < ZoomImageView.this.minScale) {
                targetZoom = ZoomImageView.this.minScale;
                animateToZoomBoundary = true;
            }

            if (animateToZoomBoundary) {
                DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, ZoomImageView.this.viewWidth / 2.0f, ZoomImageView.this.viewHeight / 2.0f, true);
                ZoomImageView.this.compatPostOnAnimation(doubleTap);
            }
        }
    }

    private void scaleImage(double deltaScale, float focusX, float focusY, boolean stretchImageToSuper) {
        float lowerScale;
        float upperScale;
        if (stretchImageToSuper) {
            lowerScale = this.superMinScale;
            upperScale = this.superMaxScale;

        } else {
            lowerScale = this.minScale;
            upperScale = this.maxScale;
        }

        float origScale = this.normalizedScale;
        this.normalizedScale *= deltaScale;
        if (this.normalizedScale > upperScale) {
            this.normalizedScale = upperScale;
            deltaScale = upperScale / origScale;
        } else if (this.normalizedScale < lowerScale) {
            this.normalizedScale = lowerScale;
            deltaScale = lowerScale / origScale;
        }

        this.matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);
        this.fixScaleTrans();
    }


    private class DoubleTapZoom implements Runnable {

        private long startTime;
        private static final float ZOOM_TIME = 500;
        private float startZoom;
        private float targetZoom;
        private float bitmapX;
        private float bitmapY;
        private boolean stretchImageToSuper;
        private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        private PointF startTouch;
        private PointF endTouch;

        DoubleTapZoom(float targetZoom, float focusX, float focusY, boolean stretchImageToSuper) {
            ZoomImageView.this.setState(State.ANIMATE_ZOOM);
            this.startTime = System.currentTimeMillis();
            this.startZoom = ZoomImageView.this.normalizedScale;
            this.targetZoom = targetZoom;
            this.stretchImageToSuper = stretchImageToSuper;
            PointF bitmapPoint = ZoomImageView.this.transformCoordTouchToBitmap(focusX, focusY, false);
            this.bitmapX = bitmapPoint.x;
            this.bitmapY = bitmapPoint.y;

            //
            // Used for translating image during scaling
            //
            this.startTouch = ZoomImageView.this.transformCoordBitmapToTouch(this.bitmapX, this.bitmapY);
            this.endTouch = new PointF(ZoomImageView.this.viewWidth / 2.0f, ZoomImageView.this.viewHeight / 2.0f);
        }

        @Override
        public void run() {
            float t = this.interpolate();
            double deltaScale = this.calculateDeltaScale(t);
            ZoomImageView.this.scaleImage(deltaScale, this.bitmapX, this.bitmapY, this.stretchImageToSuper);
            this.translateImageToCenterTouchPosition(t);
            ZoomImageView.this.fixScaleTrans();
            ZoomImageView.this.setImageMatrix(ZoomImageView.this.matrix);

            //
            // OnTouchImageViewListener is set: double tap runnable updates listener
            // with every frame.
            //
            if (ZoomImageView.this.touchImageViewListener != null) {
                ZoomImageView.this.touchImageViewListener.onMove();
            }

            if (t < 1f) {
                //
                // We haven't finished zooming
                //
                ZoomImageView.this.compatPostOnAnimation(this);

            } else {
                //
                // Finished zooming
                //
                ZoomImageView.this.setState(State.NONE);
            }
        }

        /**
         * Interpolate between where the image should start and end in order to translate
         * the image so that the point that is touched is what ends up centered at the end
         * of the zoom.
         *
         * @param t move scale
         */
        private void translateImageToCenterTouchPosition(float t) {
            float targetX = this.startTouch.x + t * (this.endTouch.x - this.startTouch.x);
            float targetY = this.startTouch.y + t * (this.endTouch.y - this.startTouch.y);
            PointF curr = ZoomImageView.this.transformCoordBitmapToTouch(this.bitmapX, this.bitmapY);
            ZoomImageView.this.matrix.postTranslate(targetX - curr.x, targetY - curr.y);
        }

        /**
         * Use interpolator to get t
         *
         * @return t
         */
        private float interpolate() {
            long currTime = System.currentTimeMillis();
            float elapsed = (currTime - this.startTime) / DoubleTapZoom.ZOOM_TIME;
            elapsed = Math.min(1f, elapsed);
            return this.interpolator.getInterpolation(elapsed);
        }

        /**
         * Interpolate the current targeted zoom and get the delta
         * from the current zoom.
         *
         * @param t t
         * @return scale
         */
        private double calculateDeltaScale(float t) {
            double zoom = this.startZoom + t * (this.targetZoom - this.startZoom);
            return zoom / ZoomImageView.this.normalizedScale;
        }
    }

    /**
     * This function will transform the coordinates in the touch event to the coordinate
     * system of the drawable that the imageview contain
     *
     * @param x            x-coordinate of touch event
     * @param y            y-coordinate of touch event
     * @param clipToBitmap Touch event may occur within view, but outside image content. True, to clip return value
     *                     to the bounds of the bitmap size.
     * @return Coordinates of the point touched, in the coordinate system of the original drawable.
     */
    private PointF transformCoordTouchToBitmap(float x, float y, boolean clipToBitmap) {
        this.matrix.getValues(this.m);
        float origW = this.getDrawable().getIntrinsicWidth();
        float origH = this.getDrawable().getIntrinsicHeight();
        float transX = this.m[Matrix.MTRANS_X];
        float transY = this.m[Matrix.MTRANS_Y];
        float finalX = ((x - transX) * origW) / this.getImageWidth();
        float finalY = ((y - transY) * origH) / this.getImageHeight();

        if (clipToBitmap) {
            finalX = Math.min(Math.max(finalX, 0), origW);
            finalY = Math.min(Math.max(finalY, 0), origH);
        }

        return new PointF(finalX, finalY);
    }

    /**
     * Inverse of transformCoordTouchToBitmap. This function will transform the coordinates in the
     * drawable's coordinate system to the view's coordinate system.
     *
     * @param bx x-coordinate in original bitmap coordinate system
     * @param by y-coordinate in original bitmap coordinate system
     * @return Coordinates of the point in the view's coordinate system.
     */
    private PointF transformCoordBitmapToTouch(float bx, float by) {
        this.matrix.getValues(this.m);
        float origW = this.getDrawable().getIntrinsicWidth();
        float origH = this.getDrawable().getIntrinsicHeight();
        float px = bx / origW;
        float py = by / origH;
        float finalX = this.m[Matrix.MTRANS_X] + this.getImageWidth() * px;
        float finalY = this.m[Matrix.MTRANS_Y] + this.getImageHeight() * py;
        return new PointF(finalX, finalY);
    }


    private class Fling implements Runnable {

        CompatScroller scroller;
        int currX;
        int currY;

        Fling(int velocityX, int velocityY) {
            ZoomImageView.this.setState(State.FLING);
            this.scroller = new CompatScroller(ZoomImageView.this.context);
            ZoomImageView.this.matrix.getValues(ZoomImageView.this.m);

            int startX = (int) ZoomImageView.this.m[Matrix.MTRANS_X];
            int startY = (int) ZoomImageView.this.m[Matrix.MTRANS_Y];
            int minX;
            int maxX;
            int minY;
            int maxY;

            if (ZoomImageView.this.getImageWidth() > ZoomImageView.this.viewWidth) {
                minX = ZoomImageView.this.viewWidth - (int) ZoomImageView.this.getImageWidth();
                maxX = 0;

            } else {
                minX = maxX = startX;
            }

            if (ZoomImageView.this.getImageHeight() > ZoomImageView.this.viewHeight) {
                minY = ZoomImageView.this.viewHeight - (int) ZoomImageView.this.getImageHeight();
                maxY = 0;

            } else {
                minY = maxY = startY;
            }

            this.scroller.fling(startX, startY, velocityX, velocityY, minX,
                    maxX, minY, maxY);
            this.currX = startX;
            this.currY = startY;
        }

        public void cancelFling() {
            if (this.scroller != null) {
                ZoomImageView.this.setState(State.NONE);
                this.scroller.forceFinished(true);
            }
        }

        @Override
        public void run() {

            //
            // OnTouchImageViewListener is set: TouchImageView listener has been flung by user.
            // Listener runnable updated with each frame of fling animation.
            //
            if (ZoomImageView.this.touchImageViewListener != null) {
                ZoomImageView.this.touchImageViewListener.onMove();
            }

            if (this.scroller.isFinished()) {
                this.scroller = null;
                return;
            }

            if (this.scroller.computeScrollOffset()) {
                int newX = this.scroller.getCurrX();
                int newY = this.scroller.getCurrY();
                int transX = newX - this.currX;
                int transY = newY - this.currY;
                this.currX = newX;
                this.currY = newY;
                ZoomImageView.this.matrix.postTranslate(transX, transY);
                ZoomImageView.this.fixTrans();
                ZoomImageView.this.setImageMatrix(ZoomImageView.this.matrix);
                ZoomImageView.this.compatPostOnAnimation(this);
            }
        }
    }

    @TargetApi(VERSION_CODES.GINGERBREAD)
    private static class CompatScroller {
        Scroller scroller;
        OverScroller overScroller;
        boolean isPreGingerbread;

        public CompatScroller(Context context) {
            if (VERSION.SDK_INT < VERSION_CODES.GINGERBREAD) {
                this.isPreGingerbread = true;
                this.scroller = new Scroller(context);

            } else {
                this.isPreGingerbread = false;
                this.overScroller = new OverScroller(context);
            }
        }

        public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
            if (this.isPreGingerbread) {
                this.scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            } else {
                this.overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            }
        }

        public void forceFinished(boolean finished) {
            if (this.isPreGingerbread) {
                this.scroller.forceFinished(finished);
            } else {
                this.overScroller.forceFinished(finished);
            }
        }

        public boolean isFinished() {
            if (this.isPreGingerbread) {
                return this.scroller.isFinished();
            } else {
                return this.overScroller.isFinished();
            }
        }

        public boolean computeScrollOffset() {
            if (this.isPreGingerbread) {
                return this.scroller.computeScrollOffset();
            } else {
                this.overScroller.computeScrollOffset();
                return this.overScroller.computeScrollOffset();
            }
        }

        public int getCurrX() {
            if (this.isPreGingerbread) {
                return this.scroller.getCurrX();
            } else {
                return this.overScroller.getCurrX();
            }
        }

        public int getCurrY() {
            if (this.isPreGingerbread) {
                return this.scroller.getCurrY();
            } else {
                return this.overScroller.getCurrY();
            }
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    private void compatPostOnAnimation(Runnable runnable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            this.postOnAnimation(runnable);

        } else {
            this.postDelayed(runnable, 1000 / 60);
        }
    }

    private static class ZoomVariables {
        public float scale;
        public float focusX;
        public float focusY;
        public ScaleType scaleType;

        public ZoomVariables(float scale, float focusX, float focusY, ScaleType scaleType) {
            this.scale = scale;
            this.focusX = focusX;
            this.focusY = focusY;
            this.scaleType = scaleType;
        }
    }
}
