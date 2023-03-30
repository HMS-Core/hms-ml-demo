/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * @description 蒙版view
 *
 * @author mlkit
 * @time 2021/12/14 10:09
 */
public class MaskingView extends View {
    private static final String TAG = com.huawei.hms.mlsdk.interactiveliveness.view.MaskingView.class.getSimpleName();

    public static final float SURFACE_HEIGHT = 1000f;
    public static final float SURFACE_RATIO = 0.75f;
    public static final float WIDTH_SPACE_RATIO = 0.344f;
    public static final float HEIGHT_RATIO = 0.1f;
    public static final float HEIGHT_EXT_RATIO = 0.2f;
    public static final int CIRCLE_SPACE = 5;
    public static final int PATH_SPACE = 16;
    public static final int PATH_SMALL_SPACE = 12;
    public static final int PATH_WIDTH = 4;

    public static final int COLOR_BG = Color.parseColor("#F1F3F5");
    public static final int COLOR_RECT = Color.parseColor("#FFFFFF");
    public static final int COLOR_ROUND = Color.parseColor("#FFA800");

    private PathEffect mFaceRoundPathEffect = null;
    private Paint mBGPaint;
    private Paint mPathPaint;
    private Paint mFaceRectPaint;
    private Paint mFaceRoundPaint;
    private Rect mFaceRect;
    private Rect mFaceDetectRect;

    private float mX;
    private float mY;
    private float mR;
    private boolean mIsDrawDash = true;
    private boolean mIsMaskFace = false;

    public MaskingView(Context context) {
        this(context, null);
    }

    public MaskingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float pathSpace = dip2px(context, PATH_SPACE);
        float pathSmallSpace = dip2px(context, PATH_SMALL_SPACE);
        float pathWidth = dip2px(context, PATH_WIDTH);
        mFaceRoundPathEffect =
                new DashPathEffect(
                        new float[] {pathSpace, dm.heightPixels < SURFACE_HEIGHT ? pathSmallSpace : pathSpace}, 1);

        mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBGPaint.setColor(COLOR_BG);
        mBGPaint.setStyle(Paint.Style.FILL);
        mBGPaint.setAntiAlias(true);
        mBGPaint.setDither(true);

        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(COLOR_ROUND);
        mPathPaint.setStrokeWidth(pathWidth);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);

        mFaceRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFaceRectPaint.setColor(COLOR_RECT);
        mFaceRectPaint.setStrokeWidth(pathWidth);
        mFaceRectPaint.setStyle(Paint.Style.STROKE);
        mFaceRectPaint.setAntiAlias(true);
        mFaceRectPaint.setDither(true);

        mFaceRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFaceRoundPaint.setColor(COLOR_ROUND);
        mFaceRoundPaint.setStyle(Paint.Style.FILL);
        mFaceRoundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mFaceRoundPaint.setAntiAlias(true);
        mFaceRoundPaint.setDither(true);
    }

    public static int dip2px(Context context, float dip) {
        float density = getDensity(context);
        return (int) (dip * density + DOT_FIVE);
    }

    /**
     * 四舍五入
     */
    private static final float DOT_FIVE = 0.5f;

    /**
     * 得到显示密度
     *
     * @param context Context
     * @return 密度
     */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public void processDrawState(boolean isDrawDash) {
        mIsDrawDash = isDrawDash;
        postInvalidate();
    }

    public float getRound() {
        return mR;
    }

    public Rect getFaceRoundRect() {
        if (mFaceRect != null) {
            Log.e(TAG, mFaceRect.toString());
        }
        return mFaceRect;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        float canvasWidth = right - left;
        float canvasHeight = bottom - top;

        float x = canvasWidth / 2;
        float y = (canvasHeight / 2) - ((canvasHeight / 2) * HEIGHT_RATIO);
        float r = (canvasWidth / 2) - ((canvasWidth / 2) * WIDTH_SPACE_RATIO);

        if (mFaceRect == null) {
            mFaceRect = new Rect((int) (x - r), (int) (y - r), (int) (x + r), (int) (y + r));
        }
        if (mFaceDetectRect == null) {
            float hr = r + (r * HEIGHT_EXT_RATIO);
            mFaceDetectRect = new Rect((int) (x - r), (int) (y - hr), (int) (x + r), (int) (y + hr));
        }
        mX = x;
        mY = y;
        mR = r;
        Log.d(TAG, "mX: " + mX + " mY: " + mY + " mR: " + mR);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawPaint(mBGPaint);
        if (mIsDrawDash) {
            mPathPaint.setPathEffect(mFaceRoundPathEffect);
        } else {
            mPathPaint.setPathEffect(null);
        }
        canvas.drawCircle(mX, mY, mR, mFaceRoundPaint);
        if (mIsMaskFace) {
            canvas.drawCircle(mX, mY, mR + CIRCLE_SPACE, mPathPaint);
            if (mFaceRect != null) {
                canvas.drawRect(mFaceRect, mFaceRectPaint);
            }
            if (mFaceDetectRect != null) {
                canvas.drawRect(mFaceDetectRect, mFaceRectPaint);
            }
        }
    }

    public static Rect getPreviewDetectRect(int w, int pw, int ph) {
        float round = (float) (w / 2) - ((float) (w / 2) * WIDTH_SPACE_RATIO);
        float x = (float) pw / 2;
        float y = (float) (ph / 2) - ((float) (ph / 2) * HEIGHT_RATIO);
        float r = Math.min(((float) pw / 2), round);
        float hr = r + (r * HEIGHT_EXT_RATIO);
        Rect rect = new Rect((int) (x - r), (int) (y - hr), (int) (x + r), (int) (y + hr));
        Log.e(TAG, "FaceRoundView getPreviewDetectRect " + pw + "-" + ph + "-" + rect.toString());
        return rect;
    }
}
