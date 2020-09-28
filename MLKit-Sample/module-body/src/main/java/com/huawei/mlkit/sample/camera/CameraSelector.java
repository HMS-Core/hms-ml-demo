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
 *  2020.2.21-Changed the name from CameraSource to CameraSelector, CameraSelector was part of the original CameraSource.
 *  2020.2.21-Changed method CameraSelector, change SizePair to CameraSize.
 *                 Huawei Technologies Co., Ltd.
 */

package com.huawei.mlkit.sample.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.huawei.hms.common.size.Size;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraSelector {

    private static final String TAG = "CameraSelector";

    private CameraConfiguration configuration;

    private static final float ASPECT_RATIO_TOLERANCE = 0.01f;

    private Size previewSize;

    protected Activity activity;

    private int mRotation;

    public CameraSelector(Activity activity, CameraConfiguration configuration) {
        this.activity = activity;
        this.configuration = configuration;
    }

    /**
     * Opens the camera and applies the user settings.
     *
     * @throws IOException if camera cannot be found or preview cannot be processed
     */
    public Camera createCamera() throws IOException {
        int cameraId = CameraSelector.getIdForRequestedCamera(this.configuration.getCameraFacing());
        if (cameraId == -1) {
            throw new IOException("Could not find the requested camera.");
        }
        Camera camera = Camera.open(cameraId);
        CameraSize cameraSize = CameraSelector.selectSizePair(camera, this.configuration.getPreviewWidth(), this.configuration.getPreviewHeight());
        if (cameraSize == null) {
            throw new IOException("Could not find suitable preview size.");
        }
        Size pictureSize = cameraSize.getPictureSize();
        this.previewSize = cameraSize.getPreviewSize();
        int[] previewFpsRange = CameraSelector.selectPreviewFpsRange(camera, this.configuration.getFps());
        if (previewFpsRange == null) {
            throw new IOException("Could not find suitable preview frames per second range.");
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(this.previewSize.getWidth(), this.previewSize.getHeight());
        if (pictureSize != null) {
            parameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
        }
        parameters.setPreviewFpsRange(
                previewFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                previewFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        parameters.setPreviewFormat(ImageFormat.NV21);

        this.setRotation(camera, parameters, cameraId);

        if (this.configuration.isAutoFocus()) {
            if (parameters
                    .getSupportedFocusModes()
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else {
                Log.i(CameraSelector.TAG, "Camera auto focus is not supported on this device.");
            }
        }
        camera.setParameters(parameters);
        return camera;
    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    public int getFacing() {
        return this.configuration.getCameraFacing();
    }

    /**
     * Calculates the correct rotation for the given camera id and sets the rotation in the
     * parameters. It also sets the camera's display orientation and rotation.
     *
     * @param parameters the camera parameters for which to set the rotation
     * @param cameraId the camera id to set rotation based on
     */
    private void setRotation(Camera camera, Camera.Parameters parameters, int cameraId) {
        WindowManager windowManager = (WindowManager) this.activity.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
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
                Log.e(CameraSelector.TAG, "Rotation value invaild: " + this.mRotation);
        }

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int angle;
        int displayAngle;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            angle = (cameraInfo.orientation + degrees) % 360;
            displayAngle = (360 - angle) % 360;
        } else {
            angle = (cameraInfo.orientation - degrees + 360) % 360;
            displayAngle = angle;
        }
        this.mRotation = angle / 90;

        camera.setDisplayOrientation(displayAngle);
        parameters.setRotation(angle);
    }

    /**
     * Selects the most suitable preview and picture size, given the desired width and height.
     *
     * <p>Even though we only need to find the preview size, it's necessary to find both the preview
     * size and the picture size of the camera together, because these need to have the same aspect
     * ratio. On some hardware, if you would only set the preview size, you will get a distorted
     * image.
     *
     * @param camera the camera to select a preview size from
     * @param desiredWidth the desired width of the camera preview frames
     * @param desiredHeight the desired height of the camera preview frames
     * @return the selected preview and picture size pair
     */
    private static CameraSize selectSizePair(Camera camera, int desiredWidth, int desiredHeight) {
        List<CameraSize> validPreviewSizes = CameraSelector.generateValidPreviewSizeList(camera);
        CameraSize selectedPair = null;
        int minDiff = Integer.MAX_VALUE;
        for (CameraSize cameraSize : validPreviewSizes) {
            Size size = cameraSize.getPreviewSize();
            int diff =
                    Math.abs(size.getWidth() - desiredWidth) + Math.abs(size.getHeight() - desiredHeight);
            if (diff < minDiff) {
                selectedPair = cameraSize;
                minDiff = diff;
            }
        }
        return selectedPair;
    }

    /**
     * Selects the most suitable preview frames per second range, given the desired frames per second.
     *
     * @param camera the camera to select a frames per second range from
     * @param desiredPreviewFps the desired frames per second for the camera preview frames
     * @return the selected preview frames per second range
     */
    private static int[] selectPreviewFpsRange(Camera camera, float desiredPreviewFps) {
        // The camera API uses integers scaled by a factor of 1000 instead of floating-point frame
        // rates.
        int desiredPreviewFpsScaled = (int) (desiredPreviewFps * 1000.0f);

        // The method for selecting the best range is to minimize the sum of the differences between
        // the desired value and the upper and lower bounds of the range.  This may select a range
        // that the desired value is outside of, but this is often preferred.  For example, if the
        // desired frame rate is 29.97, the range (30, 30) is probably more desirable than the
        // range (15, 30).
        int[] selectedFpsRange = null;
        int minDiff = Integer.MAX_VALUE;
        List<int[]> previewFpsRangeList = camera.getParameters().getSupportedPreviewFpsRange();
        for (int[] range : previewFpsRangeList) {
            int deltaMin = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            int deltaMax = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            int diff = Math.abs(deltaMin) + Math.abs(deltaMax);
            if (diff < minDiff) {
                selectedFpsRange = range;
                minDiff = diff;
            }
        }
        return selectedFpsRange;
    }

    public int getRotation() {
        return this.mRotation;
    }

    /**
     * Generates a list of acceptable preview sizes. Preview sizes are not acceptable if there is not
     * a corresponding picture size of the same aspect ratio. If there is a corresponding picture size
     * of the same aspect ratio, the picture size is paired up with the preview size.
     *
     * <p>This is necessary because even if we don't use still pictures, the still picture size must
     * be set to a size that is the same aspect ratio as the preview size we choose. Otherwise, the
     * preview images may be distorted on some devices.
     */
    private static List<CameraSize> generateValidPreviewSizeList(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes =
                parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedPictureSizes =
                parameters.getSupportedPictureSizes();
        List<CameraSize> validPreviewSizes = new ArrayList<>();
        for (Camera.Size previewSize : supportedPreviewSizes) {
            float previewAspectRatio = (float) previewSize.width / (float) previewSize.height;

            // By looping through the picture sizes in order, we favor the higher resolutions.
            // We choose the highest resolution in order to support taking the full resolution
            // picture later.
            for (Camera.Size pictureSize : supportedPictureSizes) {
                float pictureAspectRatio = (float) pictureSize.width / (float) pictureSize.height;
                if (Math.abs(previewAspectRatio - pictureAspectRatio) < CameraSelector.ASPECT_RATIO_TOLERANCE) {
                    validPreviewSizes.add(new CameraSize(previewSize, pictureSize));
                    break;
                }
            }
        }
        if (validPreviewSizes.size() == 0) {
            Log.w(CameraSelector.TAG, "No preview sizes have a corresponding same-aspect-ratio picture size");
            for (Camera.Size previewSize : supportedPreviewSizes) {
                // The null picture size will let us know that we shouldn't set a picture size.
                validPreviewSizes.add(new CameraSize(previewSize, null));
            }
        }

        return validPreviewSizes;
    }

    /**
     * Gets the id for the camera specified by the direction it is facing. Returns -1 if no such
     * camera was found.
     *
     * @param facing the desired camera (front-facing or rear-facing)
     */
    private static int getIdForRequestedCamera(int facing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return i;
            }
        }
        return -1;
    }

    private static class CameraSize {
        private final Size previewSize;
        private Size pictureSize;

        CameraSize(Camera.Size preview, Camera.Size picture) {
            this.previewSize = new Size(preview.width, preview.height);
            if (picture != null) {
                this.pictureSize = new Size(picture.width, picture.height);
            }
        }

        Size getPreviewSize() {
            return this.previewSize;
        }

        Size getPictureSize() {
            return this.pictureSize;
        }
    }
}
