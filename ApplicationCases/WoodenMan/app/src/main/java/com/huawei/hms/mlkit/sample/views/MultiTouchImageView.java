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
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MultiTouchImageView extends ImageView {
    private float startDis;
    private PointF midPoint;
    private float oldRotation = 0;
    private float rotation = 0;
    private PointF startPoint = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix currentMatrix = new Matrix();
    public int width;
    public int height;

    private enum MODE {
        NONE, DRAG, ZOOM

    };

    private MODE mode = MODE.NONE;

    public MultiTouchImageView(Context context) {
        super(context);
    }

    public MultiTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);

    }

    private int lastX, lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setScaleType(ImageView.ScaleType.MATRIX);

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = MODE.DRAG;

                    currentMatrix.set(this.getImageMatrix());
                    matrix.set(currentMatrix);
                    startPoint.set(event.getX(), event.getY());
                    postInvalidate();
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE.ZOOM;
                    oldRotation = rotation(event);
                    startDis = distance(event);
                    if (startDis > 10f) {
                        midPoint = mid(event);
                        currentMatrix.set(this.getImageMatrix());
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == MODE.DRAG) {
                        float dx = event.getX() - startPoint.x;
                        float dy = event.getY() - startPoint.y;
                        matrix.set(currentMatrix);
                        matrix.postTranslate(dx, dy);
                    } else if (mode == MODE.ZOOM) {
                        float endDis = distance(event);
                        rotation = (rotation(event) - oldRotation);
                        if (endDis > 10f) {
                            float scale = endDis / startDis;

                            float[] values=new float[9];
                            getImageMatrix().getValues(values);
                            float scale1=values[Matrix.MSCALE_X];
                            matrix.getValues(values);

                            if (scale1 > 0.25f || scale > 1f) {
                                matrix.set(currentMatrix);
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                                matrix.postRotate(rotation, midPoint.x, midPoint.y);
                            }

                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    int left = this.getLeft() + dx;
                    int top = this.getTop() + dy;
                    int right = this.getRight() + dx;
                    if (left < 0) {
                        left = 0;
                        right = left + this.getWidth();
                    }
                    if (right > width) {
                        right = (int) width;
                        left = right - this.getWidth();
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = MODE.NONE;
                    break;
            }
            this.setImageMatrix(matrix);
        return true;
    }

}

