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

package com.huawei.mlkit.sample.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.mlkit.sample.activity.dialog.AddPictureDialog;
import com.huawei.mlkit.sample.transactor.ImageTransactor;
import com.huawei.mlkit.sample.transactor.RemoteImageClassificationTransactor;
import com.huawei.mlkit.sample.transactor.RemoteLandmarkTransactor;
import com.huawei.mlkit.sample.util.BitmapUtils;
import com.huawei.mlkit.sample.util.Constant;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.mlkit.sample.R;

import java.lang.ref.WeakReference;

public final class RemoteDetectionActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "RemoteDetectionActivity";

    private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";
    private static final String KEY_IMAGE_MAX_WIDTH =
            "KEY_IMAGE_MAX_WIDTH";
    private static final String KEY_IMAGE_MAX_HEIGHT =
            "KEY_IMAGE_MAX_HEIGHT";

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_SELECT_IMAGE = 2;

    private static final int TIMEOUT = 20 * 1000;
    private static final int DELAY_TIME = 600;
    private Button getImageButton;
    private ImageView preview;
    private TextView title;
    private GraphicOverlay graphicOverlay;
    private String selectedMode = Constant.CLOUD_IMAGE_CLASSIFICATION;

    boolean isLandScape;

    private Uri imageUri;
    private Integer maxWidthOfImage;
    private Integer maxHeightOfImage;
    private ImageTransactor imageTransactor;

    private Bitmap imageBitmap;

    private Dialog progressDialog;

    private AddPictureDialog addPictureDialog;

    private Handler mHandler = new MsgHandler(this);

    private static class MsgHandler extends Handler {
        WeakReference<RemoteDetectionActivity> mMainActivityWeakReference;

        public MsgHandler(RemoteDetectionActivity mainActivity) {
            this.mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RemoteDetectionActivity mainActivity = this.mMainActivityWeakReference.get();
            if (mainActivity == null) {
                return;
            }
            Log.d(RemoteDetectionActivity.TAG, "msg what :" + msg.what);
            if (msg.what == Constant.GET_DATA_SUCCESS) {
                mainActivity.handleGetDataSuccess();
            } else if (msg.what == Constant.GET_DATA_FAILED) {
                mainActivity.handleGetDataFailed();
            }
        }
    }
    private void handleGetDataSuccess() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
        this.mHandler.removeCallbacks(this.myRunnable);
    }

    private void handleGetDataFailed() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
        this.mHandler.removeCallbacks(this.myRunnable);
        Toast.makeText(this, this.getString(R.string.get_data_failed), Toast.LENGTH_SHORT).show();
    }

    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            if (RemoteDetectionActivity.this.progressDialog != null) {
                RemoteDetectionActivity.this.progressDialog.dismiss();
            }
            Toast.makeText(RemoteDetectionActivity.this.getApplicationContext(), RemoteDetectionActivity.this.getString(R.string.get_data_failed), Toast.LENGTH_SHORT).show();
        }
    };

    private Runnable detectRunnable = new Runnable() {
        @Override
        public void run() {
            RemoteDetectionActivity.this.loadImageAndSetTransactor();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        String type = null;
        try {
            this.selectedMode = intent.getStringExtra(Constant.MODEL_TYPE);
            type = intent.getStringExtra(Constant.ADD_PICTURE_TYPE);
        } catch (RuntimeException e) {
            Log.e(RemoteDetectionActivity.TAG, "Get intent value failed: " + e.getMessage());
        }
        if (savedInstanceState != null) {
            this.imageUri = savedInstanceState.getParcelable(RemoteDetectionActivity.KEY_IMAGE_URI);
            if (this.imageUri != null) {
                this.maxWidthOfImage = savedInstanceState.getInt(RemoteDetectionActivity.KEY_IMAGE_MAX_WIDTH);
                this.maxHeightOfImage = savedInstanceState.getInt(RemoteDetectionActivity.KEY_IMAGE_MAX_HEIGHT);
            }
        }
        this.setContentView(R.layout.activity_remote_detection);
        this.initTitle();
        this.findViewById(R.id.back).setOnClickListener(this);
        this.preview = this.findViewById(R.id.still_preview);
        this.graphicOverlay = this.findViewById(R.id.still_overlay);
        this.getImageButton = this.findViewById(R.id.getImageButton);
        this.getImageButton.setOnClickListener(this);
        this.createImageTransactor();
        this.createDialog();
        this.isLandScape = (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        this.setStatusBar();
        if (type == null) {
            this.selectLocalImage();
        } else if (type.equals(Constant.TYPE_SELECT_IMAGE)) {
            this.selectLocalImage();
        } else {
            this.startCamera();
        }
    }

    private void initTitle() {
        this.title = this.findViewById(R.id.page_title);
        if (this.selectedMode.equals(Constant.CLOUD_IMAGE_CLASSIFICATION)) {
            this.title.setText(this.getResources().getText(R.string.cloud_classification));
        } else if (this.selectedMode.equals(Constant.CLOUD_LANDMARK_DETECTION)) {
            this.title.setText(this.getResources().getText(R.string.landmark_s));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.getImageButton) {
            this.showDialog();
        } else if (view.getId() == R.id.back) {
            this.finish();
        }
    }

    private void createDialog() {
        this.addPictureDialog = new AddPictureDialog(this);
        final Intent intent = new Intent(RemoteDetectionActivity.this, RemoteDetectionActivity.class);
        intent.putExtra(Constant.MODEL_TYPE, Constant.CLOUD_IMAGE_CLASSIFICATION);
        this.addPictureDialog.setClickListener(new AddPictureDialog.ClickListener() {
            @Override
            public void takePicture() {
                RemoteDetectionActivity.this.startCamera();
            }

            @Override
            public void selectImage() {
                RemoteDetectionActivity.this.selectLocalImage();
            }

            @Override
            public void doExtend() {

            }
        });
    }

    private void showDialog() {
        this.addPictureDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RemoteDetectionActivity.KEY_IMAGE_URI, this.imageUri);
        if (this.maxWidthOfImage != null) {
            outState.putInt(RemoteDetectionActivity.KEY_IMAGE_MAX_WIDTH, this.maxWidthOfImage);
        }
        if (this.maxHeightOfImage != null) {
            outState.putInt(RemoteDetectionActivity.KEY_IMAGE_MAX_HEIGHT, this.maxHeightOfImage);
        }
    }

    private void reloadAndDetectImage() {
        if (this.preview == null || this.maxHeightOfImage == null || (this.maxHeightOfImage == 0
                && ((View) this.preview.getParent()).getHeight() == 0)) {
            this.mHandler.postDelayed(this.detectRunnable, RemoteDetectionActivity.DELAY_TIME);
        } else {
            this.loadImageAndSetTransactor();
        }
    }

    private void startCamera() {
        this.imageUri = null;
        this.preview.setImageBitmap(null);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            this.imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
            this.startActivityForResult(takePictureIntent, RemoteDetectionActivity.REQUEST_TAKE_PHOTO);
        }
    }

    private void selectLocalImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(intent, RemoteDetectionActivity.REQUEST_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RemoteDetectionActivity.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            this.reloadAndDetectImage();
        } else if (requestCode == RemoteDetectionActivity.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_CANCELED) {
            this.finish();
        } else if (requestCode == RemoteDetectionActivity.REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                this.imageUri = data.getData();
            }
            this.reloadAndDetectImage();
        } else if (requestCode == RemoteDetectionActivity.REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_CANCELED) {
            this.finish();
        }
    }

    private void loadImageAndSetTransactor() {
        if (this.imageUri == null) {
            return;
        }
        this.showLoadingDialog();
        this.graphicOverlay.clear();
        this.mHandler.postDelayed(this.myRunnable, RemoteDetectionActivity.TIMEOUT);
        this.imageBitmap = BitmapUtils.loadFromPath(RemoteDetectionActivity.this, this.imageUri, this.getMaxWidthOfImage(), this.getMaxHeightOfImage());
        this.preview.setImageBitmap(this.imageBitmap);
        if (this.imageBitmap != null) {
            this.imageTransactor.process(this.imageBitmap, this.graphicOverlay);
        }
    }

    private Integer getMaxWidthOfImage() {
        if (this.maxWidthOfImage == null || this.maxWidthOfImage == 0) {
            if (this.isLandScape) {
                this.maxWidthOfImage = ((View) this.preview.getParent()).getHeight();
            } else {
                this.maxWidthOfImage = ((View) this.preview.getParent()).getWidth();
            }
        }
        return this.maxWidthOfImage;
    }

    private Integer getMaxHeightOfImage() {
        if (this.maxHeightOfImage == null || this.maxHeightOfImage == 0) {
            if (this.isLandScape) {
                this.maxHeightOfImage = ((View) this.preview.getParent()).getWidth();
            } else {
                this.maxHeightOfImage = ((View) this.preview.getParent()).getHeight();
            }
        }
        return this.maxHeightOfImage;
    }

    private void createImageTransactor() {
        switch (this.selectedMode) {
            case Constant.CLOUD_IMAGE_CLASSIFICATION:
                this.imageTransactor = new RemoteImageClassificationTransactor(this.getApplicationContext(), mHandler);
                break;
            case Constant.CLOUD_LANDMARK_DETECTION:
                this.imageTransactor = new RemoteLandmarkTransactor(mHandler);
                break;
            default:
                throw new IllegalStateException("Unknown selectedMode: " + this.selectedMode);
        }
        Log.d(RemoteDetectionActivity.TAG, this.imageTransactor.getClass().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.imageTransactor != null) {
            this.imageTransactor.stop();
            this.imageTransactor = null;
        }
        this.imageUri = null;
        if (this.progressDialog != null) {
            if (this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
            this.progressDialog = null;
        }
    }

    private void showLoadingDialog() {
        if (this.progressDialog == null) {
            this.progressDialog = new Dialog(RemoteDetectionActivity.this, R.style.progress_dialog);
            this.progressDialog.setContentView(R.layout.dialog);
            this.progressDialog.setCancelable(false);
            this.progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView msg = this.progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText(this.getString(R.string.loading_data));
        }
        this.progressDialog.show();
    }
}
