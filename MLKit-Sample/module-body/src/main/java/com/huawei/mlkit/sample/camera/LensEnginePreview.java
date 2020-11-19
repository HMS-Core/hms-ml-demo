/**
 *  Copyright 2018 Google LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  2020.2.21-Changed name from CameraSourcePreview to LensEnginePreview, in addition, the onTouchEvent, handleZoom, handleFocusMetering method is added.
 *                  Huawei Technologies Co., Ltd.
 */

package com.huawei.mlkit.sample.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.huawei.hms.common.size.Size;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LensEnginePreview extends ViewGroup {
    private static final String TAG = "LensEnginePreview";

    private static final int TEXTURE_NAME = 100;

    private Context context;

    private SurfaceView surfaceView;

    private SurfaceTexture surfaceTexture;

    private boolean startRequested;

    private LensEngine lensEngine;

    private float oldDist = 1f;

    /**
     * Determines whether the camera preview frame and detection result display frame are synchronous or asynchronous.
     */
    private boolean isSynchronous = false;

    public LensEnginePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.startRequested = false;
        this.surfaceView = new SurfaceView(context);
        this.surfaceView.getHolder().addCallback(new SurfaceCallback());
        this.addView(this.surfaceView);
    }

    public SurfaceHolder getSurfaceHolder() {
        return this.surfaceView.getHolder();
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.surfaceTexture;
    }

    public void start(LensEngine lensEngine, boolean isSynchronous) throws IOException {
        this.isSynchronous = isSynchronous;
        if (lensEngine == null) {
            this.stop();
        }
        this.lensEngine = lensEngine;
        if (this.lensEngine != null) {
            this.startRequested = true;
            this.startLensEngine();
        }
    }

    public void stop() {
        if (this.lensEngine != null) {
            this.lensEngine.stop();
        }
    }

    public void release() {
        if (this.lensEngine != null) {
            this.lensEngine.release();
            this.lensEngine = null;
        }
    }

    @SuppressLint("MissingPermission")
    private void startLensEngine() throws IOException {
        if (this.startRequested) {
            this.lensEngine.run();
            this.startRequested = false;
        }
    }

    public void takePicture(Camera.PictureCallback pictureCallback) {
        if (this.lensEngine != null) {
            this.lensEngine.takePicture(pictureCallback);
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            try {
                LensEnginePreview.this.startLensEngine();
            } catch (IOException e) {
                Log.e(LensEnginePreview.TAG, "Could not start lensEngine.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(LensEnginePreview.TAG, "surfaceChanged");
            Camera camera = LensEnginePreview.this.lensEngine.getCamera();
            if (camera == null) {
                return;
            }
            try {
                if (isSynchronous) {
                    surfaceTexture = new SurfaceTexture(TEXTURE_NAME);
                    camera.setPreviewTexture(surfaceTexture);
                } else {
                    camera.setPreviewDisplay(surfaceView.getHolder());
                }
            } catch (IOException e) {
                Log.e(LensEnginePreview.TAG, "IOException", e);

            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try {
            this.startLensEngine();
        } catch (IOException e) {
            Log.e(LensEnginePreview.TAG, "Could not start LensEngine.", e);
        }
        if (this.lensEngine == null) {
            return;
        }
        Size size = this.lensEngine.getPreviewSize();
        if (size == null) {
            return;
        }
        int width = size.getWidth();
        int height = size.getHeight();


        // When the phone is in portrait orientation, the width and height dimensions are interchangeable.
        if (this.isVertical()) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        final int viewWidth = right - left;
        final int viewHeight = bottom - top;

        int childWidth;
        int childHeight;
        int childXOffset = 0;
        int childYOffset = 0;
        float widthRatio = (float) viewWidth / (float) width;
        float heightRatio = (float) viewHeight / (float) height;

        // To fill the view with the camera preview, while also preserving the correct aspect ratio,
        // it is usually necessary to slightly oversize the child and to crop off portions along one
        // of the dimensions. We scale up based on the dimension requiring the most correction, and
        // compute a crop offset for the other dimension.
        if (widthRatio > heightRatio) {
            childWidth = viewWidth;
            childHeight = (int) ((float) height * widthRatio);
            childYOffset = (childHeight - viewHeight) / 2;
        } else {
            childWidth = (int) ((float) width * heightRatio);
            childHeight = viewHeight;
            childXOffset = (childWidth - viewWidth) / 2;
        }

        for (int i = 0; i < this.getChildCount(); ++i) {
            // One dimension will be cropped. We shift child over or up by this offset and adjust
            // the size to maintain the proper aspect ratio.
            this.getChildAt(i)
                    .layout(-1 * childXOffset, -1 * childYOffset, childWidth - childXOffset, childHeight - childYOffset);
        }
    }

    private boolean isVertical() {
        int orientation = this.context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (CameraConfiguration.getCameraFacing() == CameraConfiguration.CAMERA_FACING_FRONT) {
            return true;
        }
        if (event.getPointerCount() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (this.lensEngine != null) {
                        this.handleFocusMetering(event, this.lensEngine.getCamera());
                    }
                    break;
            }
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    this.oldDist = LensEnginePreview.getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(LensEnginePreview.TAG, "toby onTouch: ACTION_MOVE");
                    float newDist = LensEnginePreview.getFingerSpacing(event);
                    if (newDist > this.oldDist) {
                        this.handleZoom(true, this.lensEngine.getCamera());
                    } else if (newDist < this.oldDist) {
                        this.handleZoom(false, this.lensEngine.getCamera());
                    }
                    this.oldDist = newDist;
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private void handleZoom(boolean isZoomIn, Camera camera) {
        Camera.Parameters params = camera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            params.setZoom(zoom);
            camera.setParameters(params);
        } else {
            Log.i(LensEnginePreview.TAG, "zoom not supported");
        }
    }

    private void handleFocusMetering(MotionEvent event, Camera camera) {
        int viewWidth = this.getWidth();
        int viewHeight = this.getHeight();
        Rect focusRect = LensEnginePreview.calculateTapArea(event.getX(), event.getY(), 1f, viewWidth, viewHeight);
        Rect meteringRect = LensEnginePreview.calculateTapArea(event.getX(), event.getY(), 1.5f, viewWidth, viewHeight);
        if (camera == null) {
            return;
        }
        camera.cancelAutoFocus();
        Camera.Parameters params = camera.getParameters();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            Log.i(LensEnginePreview.TAG, "focus areas not supported");
        }
        if (params.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(meteringRect, 800));
            params.setMeteringAreas(meteringAreas);
        } else {
            Log.i(LensEnginePreview.TAG, "metering areas not supported");
        }
        final String currentFocusMode = params.getFocusMode();
        Log.d(LensEnginePreview.TAG, "toby onTouch:" + currentFocusMode);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(params);

        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
            }
        });
    }

    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private static Rect calculateTapArea(float x, float y, float coefficient, int width, int height) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / width * 2000 - 1000);
        int centerY = (int) (y / height * 2000 - 1000);

        int halfAreaSize = areaSize / 2;
        RectF rectF = new RectF(LensEnginePreview.clamp(centerX - halfAreaSize, -1000, 1000),
                LensEnginePreview.clamp(centerY - halfAreaSize, -1000, 1000),
                LensEnginePreview.clamp(centerX + halfAreaSize, -1000, 1000),
                LensEnginePreview.clamp(centerY + halfAreaSize, -1000, 1000));
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right),
                Math.round(rectF.bottom));
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
}
