/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.huawei.mlkit.sample.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;

import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.common.MLPosition;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceShape;
import com.huawei.mlkit.sample.facepoint.FacePointEngine;
import com.huawei.mlkit.sample.facepoint.EGLFace;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class EGLCamera implements PreviewCallback, Camera.AutoFocusCallback {
    private static final String TAG = "EGLCamera";

    private Activity mActivity;
    private Camera mCamera;
    private Parameters mParameters;
    private CameraInfo mCameraInfo = new CameraInfo();
    private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;
    private int mPreviewWidth = 1280;
    private int mPreviewHeight = 720;
    private float mPreviewScale = mPreviewHeight * 1f / mPreviewWidth;
    private int mOrientation;
    private MLFaceAnalyzer detector;

    public EGLCamera(Activity activity) {
        mActivity = activity;
    }

    public void openCamera() {
        Log.d(TAG, "openCamera cameraId: " + mCameraId);
        mCamera = Camera.open(mCameraId);
        Camera.getCameraInfo(mCameraId, mCameraInfo);
        initConfig();
        mCamera.setPreviewCallback(this);
        setDisplayOrientation();
    }

    public void releaseCamera() {
        if (mCamera != null) {
            Log.v(TAG, "releaseCamera");
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        FacePointEngine.getInstance().clearAll();
    }

    public void startPreview(SurfaceTexture surface) {
        if (mCamera != null) {
            Log.v(TAG, "startPreview");
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                Log.e(TAG, "setPreviewTexture fail." + e.getMessage());
            }
            mCamera.startPreview();
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            Log.v(TAG, "stopPreview");
            mCamera.stopPreview();
        }
    }

    public boolean isFrontCamera() {
        return mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    public boolean isBackCamera() {
        return mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    private void initConfig() {
        Log.v(TAG, "initConfig");
        try {
            mParameters = mCamera.getParameters();
            /**
             * If the camera doesn't support these parameters,
             * they will all go wrong, so make sure to check if they are supported
             */
            List<String> supportedFlashModes = mParameters.getSupportedFlashModes();
            if (supportedFlashModes != null && supportedFlashModes.contains(Parameters.FLASH_MODE_OFF)) {
                mParameters.setFlashMode(Parameters.FLASH_MODE_OFF); // Set focus mode
            }
            List<String> supportedFocusModes = mParameters.getSupportedFocusModes();
            if (supportedFocusModes != null && supportedFocusModes.contains(Parameters.FOCUS_MODE_AUTO)) {
                mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO); // Set focus mode
            }
            mParameters.setPreviewFormat(ImageFormat.NV21); // Set preview image format
            mParameters.setPictureFormat(ImageFormat.JPEG); // Set the format of photos
            mParameters.setExposureCompensation(0); // Set the exposure intensity
            Size previewSize = getSuitableSize(mParameters.getSupportedPreviewSizes());
            mPreviewWidth = previewSize.width;
            mPreviewHeight = previewSize.height;
            mParameters.setPreviewSize(mPreviewWidth, mPreviewHeight); // Set preview image size
            Log.d(TAG, "previewWidth: " + mPreviewWidth + ", previewHeight: " + mPreviewHeight);
            Size pictureSize = getSuitableSize(mParameters.getSupportedPictureSizes());
            mParameters.setPictureSize(pictureSize.width, pictureSize.height);
            Log.d(TAG, "pictureWidth: " + pictureSize.width + ", pictureHeight: " + pictureSize.height);
            mCamera.setParameters(mParameters); // Add parameters to the camera
        } catch (Exception e) {
            Log.e(TAG, "initConfig fail." + e.getMessage());
        }
    }

    private Size getSuitableSize(List<Size> sizes) {
        /**
         * The minimum difference, the initial value
         * should be set to a large point to
         * ensure that it will be reset later in the calculation
         */
        int minDelta = Integer.MAX_VALUE;
        int index = 0; // The index coordinates corresponding to the minimum difference
        for (int i = 0; i < sizes.size(); i++) {
            Size size = sizes.get(i);
            Log.v(TAG, "SupportedSize, width: " + size.width + ", height: " + size.height);
            // First determine if the proportion is equal
            if (Math.abs(size.width * mPreviewScale - size.height) < 0.001) {
                int delta = Math.abs(mPreviewWidth - size.width);
                if (delta == 0) {
                    return size;
                }
                if (minDelta > delta) {
                    minDelta = delta;
                    index = i;
                }
            }
        }
        return sizes.get(index);
    }

    /**
     * setDisplayOrientation
     */
    private void setDisplayOrientation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result;
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            result = (mCameraInfo.orientation - degrees + 360) % 360;
        }
        mOrientation = result;
        mCamera.setDisplayOrientation(result);
    }

    @Override
    public void onPreviewFrame(final byte[] imgData, final Camera camera) {
        int width = mPreviewWidth;
        int height = mPreviewHeight;
        SparseArray<MLFace> faces = null;

        long startTime = System.currentTimeMillis();
        //Set in the same direction
        if (isFrontCamera()) {
            mOrientation = 0;
        }else {
            mOrientation = 2;
        }
        MLFrame.Property property =
                new MLFrame.Property.Creator()
                        .setFormatType(ImageFormat.NV21)
                        .setWidth(width)
                        .setHeight(height)
                        .setQuadrant(mOrientation)
                        .create();

        ByteBuffer data = ByteBuffer.wrap(imgData);

        if (detector != null) {
            // Call face detection interface
            faces = detector.analyseFrame(MLFrame.fromByteBuffer(data, property));
        }

        //Determine whether face information is obtained
        if (faces != null && faces.size() > 0) {
            MLFace mLFace = faces.get(0);
            EGLFace eglFace = FacePointEngine.getInstance().getOneFace(0);
            eglFace.pitch = mLFace.getRotationAngleX();
            eglFace.yaw = mLFace.getRotationAngleY();
            eglFace.roll = mLFace.getRotationAngleZ() - 90;
            if (isFrontCamera())
                eglFace.roll = -eglFace.roll;
            if (eglFace.vertexPoints == null) {
                eglFace.vertexPoints = new PointF[131];
            }
            int index = 0;
            /**
             * Gets a person's contour point coordinates and
             * converts them to a floating point value
             * in the openGL normalized coordinate system
             */
            for (MLFaceShape contour : mLFace.getFaceShapeList()) {
                if (contour == null) {
                    continue;
                }
                List<MLPosition> points = contour.getPoints();

                for (int i = 0; i < points.size(); i++) {
                    MLPosition point = points.get(i);
                    float x = ( point.getY() / height) * 2 - 1;
                    float y = ( point.getX() / width ) * 2 - 1;
                    if (isFrontCamera())
                        x = -x;
                    PointF pointF = new PointF(x, y);
                    eglFace.vertexPoints[index] = pointF;
                    index++;
                }
            }
            // Insert face object
            FacePointEngine.getInstance().putOneFace(0, eglFace);
            // Set the number of faces
            FacePointEngine.getInstance().setFaceSize(faces.size());
        } else {
            FacePointEngine.getInstance().clearAll();
        }
        long endTime = System.currentTimeMillis();
        Log.d("TAG", "Face detect time: " + String.valueOf(endTime - startTime));
    }

    public void takePicture(Camera.PictureCallback pictureCallback) {
        mCamera.takePicture(null, null, pictureCallback);
    }

    public void switchCamera() {
        mCameraId ^= 1; // Change the camera first
        releaseCamera();
        openCamera();
    }

    public void setDetector(MLFaceAnalyzer detector) {
        this.detector = detector;
    }

    public void focusOnPoint(int x, int y, int width, int height) {
        Log.v(TAG, "touch point (" + x + ", " + y + ")");
        if (mCamera == null) {
            return;
        }
        Parameters parameters = mCamera.getParameters();
        if (parameters.getMaxNumFocusAreas() > 0) {
            int length = Math.min(width, height) >> 3;
            int left = x - length;
            int top = y - length;
            int right = x + length;
            int bottom = y + length;
            left = left * 2000 / width - 1000;
            top = top * 2000 / height - 1000;
            right = right * 2000 / width - 1000;
            bottom = bottom * 2000 / height - 1000;
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            Log.d(TAG, "focus area (" + left + ", " + top + ", " + right + ", " + bottom + ")");
            ArrayList<Camera.Area> areas = new ArrayList<>();
            areas.add(new Camera.Area(new Rect(left, top, right, bottom), 600));
            parameters.setFocusAreas(areas);
        }
        try {
            mCamera.cancelAutoFocus();
            mCamera.setParameters(parameters);
            mCamera.autoFocus(this);
        } catch (Exception e) {
            Log.e(TAG, "Fail to set mCamera." + e.getMessage());
        }
    }

    public void handleZoom(boolean isZoomIn) {
        if (mParameters.isZoomSupported()) {
            int maxZoom = mParameters.getMaxZoom();
            int zoom = mParameters.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            Log.d(TAG, "handleZoom: zoom: " + zoom);
            mParameters.setZoom(zoom);
            mCamera.setParameters(mParameters);
        } else {
            Log.i(TAG, "zoom not supported");
        }
    }

    public int getPreviewWidth() {
        return mPreviewWidth;
    }

    public int getPreviewHeight() {
        return mPreviewHeight;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        Log.d(TAG, "onAutoFocus: " + success);
        //OnAutoFocus code add
    }
}
