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

package com.huawei.hms.mlkit.sample.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlkit.sample.camera.CameraConfiguration;
import com.huawei.hms.mlkit.sample.camera.LensEngine;
import com.huawei.hms.mlkit.sample.camera.LensEnginePreview;
import com.huawei.hms.mlkit.sample.cn.R;
import com.huawei.hms.mlkit.sample.transactor.LocalSketlonTranstor;
import com.huawei.hms.mlkit.sample.util.BitmapUtils;
import com.huawei.hms.mlkit.sample.util.ImageUtils;
import com.huawei.hms.mlkit.sample.views.MultiTouchImageView;
import com.huawei.hms.mlkit.sample.views.ImageAdapter;
import com.huawei.hms.mlkit.sample.views.ImageInfo;
import com.huawei.hms.mlkit.sample.views.LocalDataManager;
import com.huawei.hms.mlkit.sample.views.SpaceItemDecoration;
import com.huawei.hms.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class WoodenManActivity extends BaseActivity
        implements OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "WoodenManActivity";

    private static Context mContext;

    public static final int AUTO_TAKE_PHOTO = 101;

    private int REQUEST_CHOOSE_BACKGROUND = 2002;

    private static int mOrientation;

    private static MediaPlayer mShootMP;

    private LensEnginePreview preview;

    private GraphicOverlay graphicOverlay;

    private Handler mHandler = new MsgHandler(this);

    private Bitmap bitmap;

    private Bitmap bitmapCopy;

    private RelativeLayout zoomImageLayout;

    private MultiTouchImageView zoomImageView;

    private android.widget.LinearLayout mLinearLayout;

    private TextView image_save;

    private TextView image_play;

    private OrientationEventListener mOrientationListener;

    private ToggleButton toggleButton;

    private Drawable backgroundDrawable;

    private List<ImageInfo> mImageInfoList = new ArrayList<>();

    private ImageAdapter mImageAdapter;

    private ImageInfo mImageInfo;

    private int mPosition;

    private Bitmap mProcessedImage;

    private Bitmap mForeground;

    boolean isSaveFlag;

    boolean isLandScape;

    private Integer maxWidthOfImage;

    private Integer maxHeightOfImage;

    private ImageInfo mImageInfoResult;

    private boolean isClickBG;

    private CameraConfiguration cameraConfiguration = null;

    private LensEngine lensEngine = null;

    private LocalSketlonTranstor localSketlonTranstor;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_human_skeleton);
        mContext = this;
        ChooserActivity.setIsAsynchronous(false);
        preview = findViewById(R.id.firePreview);
        zoomImageLayout = findViewById(R.id.zoomImageLayout);
        ImageButton mZoomImageBackGround = findViewById(R.id.zoomImageBackGround);
        mZoomImageBackGround.setOnClickListener(this);
        zoomImageView = findViewById(R.id.take_picture_overlay);
        localSketlonTranstor = ChooserActivity.localSketlonTranstor;
        initImageInfo();
        RecyclerView mImage_select = findViewById(R.id.rv_image);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImage_select.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.list_item_margin)));
        mImage_select.setLayoutManager(layoutManager);

        mImageAdapter = new ImageAdapter(mContext, mImageInfoList);
        mImage_select.setAdapter(mImageAdapter);

        mImageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, final int position) {
                if (mImageInfoList == null || mImageInfoList.size() == 0) {
                    Log.d(TAG, "mImageInfoList is null");
                    return;
                }
                mImageInfo = mImageInfoList.get(position);
                if (mPosition != position || position == 0) {
                    zoomImageView.setBackground(null);
                    isSaveFlag = false;
                    isClickBG = false;
                    idSetBg = true;
                    createImageTransactor(mImageInfo);
                    mImageAdapter.setThisPosition(position);
                    mImageAdapter.notifyDataSetChanged();
                }

                mPosition = position;

            }
        });

        RelativeLayout mRelativeLayoutCut = findViewById(R.id.relativate_cut);
        RelativeLayout mRelativeLayoutBackgrounds = findViewById(R.id.relativate_backgrounds);
        RelativeLayout mRelativeLayoutSave = findViewById(R.id.relativate_save);

        mRelativeLayoutCut.setOnClickListener(this);
        mRelativeLayoutBackgrounds.setOnClickListener(this);
        mRelativeLayoutSave.setOnClickListener(this);

        findViewById(R.id.zoomImageClose).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.model_title).setOnClickListener(this);
        toggleButton = findViewById(R.id.camera_takePicture);
        toggleButton.setOnClickListener(this);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
        ToggleButton facingSwitch = findViewById(R.id.facingSwitch);
        mLinearLayout = findViewById(R.id.linear_views);
        image_save = findViewById(R.id.image_save);
        image_play = findViewById(R.id.image_play);
        image_save.setOnClickListener(this);
        image_play.setOnClickListener(this);
        facingSwitch.setOnCheckedChangeListener(this);
        if (Camera.getNumberOfCameras() == 1) {
            facingSwitch.setVisibility(View.GONE);
        }

        this.isLandScape =
                (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(CameraConfiguration.CAMERA_FACING_FRONT);


        initOrientationListener();
        createLensEngine();
    }

    private void initOrientationListener() {
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                mOrientation = (orientation + 45) / 90;
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    public static int getOrientation() {
        return mOrientation;
    }

    public static void setOrientation(int orientation) {
        WoodenManActivity.mOrientation = orientation;
    }

    private void selectLocalImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(intent, requestCode);
    }

    private Pair<Integer, Integer> getTargetSize() {
        Integer targetWidth;
        Integer targetHeight;
        Integer maxWidth = this.getMaxWidthOfImage();
        Integer maxHeight = this.getMaxHeightOfImage();
        targetWidth = this.isLandScape ? maxHeight : maxWidth;
        targetHeight = this.isLandScape ? maxWidth : maxHeight;
        Log.i(TAG, "height:" + targetHeight + ",width:" + targetWidth);
        return new Pair<>(targetWidth, targetHeight);
    }

    // Returns max width of image.
    private Integer getMaxWidthOfImage() {
        if (this.maxWidthOfImage == null) {
            if (this.isLandScape) {
                this.maxWidthOfImage = ((View) this.preview.getParent()).getHeight();
            } else {
                this.maxWidthOfImage = ((View) this.preview.getParent()).getWidth();
            }
        }
        return this.maxWidthOfImage;
    }

    // Returns max height of image.
    private Integer getMaxHeightOfImage() {
        if (this.maxHeightOfImage == null) {
            if (this.isLandScape) {
                this.maxHeightOfImage = ((View) this.preview.getParent()).getWidth();
            } else {
                this.maxHeightOfImage = ((View) this.preview.getParent()).getHeight();
            }
        }
        return this.maxHeightOfImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == this.REQUEST_CHOOSE_BACKGROUND)) {
            if (data == null) {
                Toast.makeText(this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
            } else {
                Uri imgBackgroundUri = data.getData();
                Pair<Integer, Integer> targetedSize = this.getTargetSize();
                this.backgroundDrawable = new BitmapDrawable(getResources(), BitmapUtils.loadFromPath(WoodenManActivity.this, imgBackgroundUri, targetedSize.first, targetedSize.second));
                mImageInfoResult = new ImageInfo();
                mImageInfoResult.setImageView(backgroundDrawable);
                createImageTransactor(mImageInfoResult);
                mPosition = 0;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.zoomImageClose) {
            zoomImageLayout.setVisibility(View.GONE);
            isSaveFlag = false;
            zoomImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mLinearLayout.setVisibility(View.GONE);
            image_save.setVisibility(View.GONE);
            image_play.setVisibility(View.GONE);
            toggleButton.setVisibility(View.VISIBLE);
            recycleBitmap();
            reStartLensEngine();
        } else if (view.getId() == R.id.zoomImageBackGround) {
            idSetBg = false;
            isClickBG = true;
            isSaveFlag = false;
            WoodenManActivity.this.selectLocalImage(WoodenManActivity.this.REQUEST_CHOOSE_BACKGROUND);
        } else if (view.getId() == R.id.back || view.getId() == R.id.model_title) {
            mLinearLayout.setVisibility(View.GONE);
            image_save.setVisibility(View.GONE);
            image_play.setVisibility(View.GONE);
            toggleButton.setVisibility(View.VISIBLE);
            finish();
        } else if (view.getId() == R.id.camera_takePicture) {
            mHandler.sendEmptyMessage(WoodenManActivity.AUTO_TAKE_PHOTO);
        } else if (view.getId() == R.id.image_save) {
            isSaveFlag = false;
            if (mProcessedImage == null) {
                Toast.makeText(getApplicationContext(), R.string.no_pic_neededSave, Toast.LENGTH_SHORT).show();
            } else {
                isSaveFlag = true;
                if (isClickBG) {
                    createImageTransactor(mImageInfoResult);
                } else {
                    createImageTransactor(mImageInfo);
                }
            }
        } else if (view.getId() == R.id.image_play) {
            if (zoomImageLayout.getVisibility() == View.VISIBLE) {
                mImageAdapter.setThisPosition(0);
                mImageAdapter.notifyDataSetChanged();
                idSetBg = false;
                mPosition = 0;
                mProcessedImage = null;
                mForeground = null;

                toggleButton.setVisibility(View.VISIBLE);
                zoomImageLayout.setVisibility(View.GONE);
                isSaveFlag = false;
                zoomImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mLinearLayout.setVisibility(View.GONE);
                image_save.setVisibility(View.GONE);
                image_play.setVisibility(View.GONE);
                recycleBitmap();
                reStartLensEngine();
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
        if (this.lensEngine != null) {
            int facing = CameraConfiguration.CAMERA_FACING_BACK;
            if (!isChecked) {
                facing = CameraConfiguration.CAMERA_FACING_FRONT;
                this.cameraConfiguration.setCameraFacing(facing);
            } else {
                facing = CameraConfiguration.CAMERA_FACING_BACK;
                this.cameraConfiguration.setCameraFacing(facing);
            }
        }

        this.preview.stop();
        reStartLensEngine();
    }

    private void reStartLensEngine() {
        startLensEngine();
        if (null != this.lensEngine) {
            Camera mCamera = this.lensEngine.getCamera();
            try {
                mCamera.setPreviewTexture(this.preview.getSurfaceTexture());
            } catch (IOException e) {
                Log.d(TAG, "initViews IOException");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            this.lensEngine.setMachineLearningFrameTransactor(localSketlonTranstor, null);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Can not create face detection transactor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, true);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    private void releaseLensEngine() {
        if (this.lensEngine != null) {
            this.lensEngine.release();
            this.lensEngine = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (isClickBG && mImageInfoResult != null) {
            zoomImageView.setBackground(null);
            isSaveFlag = false;
            createImageTransactor(mImageInfoResult);
        } else if (idSetBg && mImageInfo != null) {
            zoomImageView.setBackground(null);
            isSaveFlag = false;
            createImageTransactor(mImageInfo);
        }
        mImageAdapter.notifyDataSetChanged();

        startLensEngine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (zoomImageLayout.getVisibility() == View.VISIBLE) {
            zoomImageView.setBackground(null);
            zoomImageView.setBackground(backgroundDrawable);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (zoomImageLayout.getVisibility() == View.VISIBLE) {
            preview.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseLensEngine();
        ChooserActivity.setIsAsynchronous(false);
        mOrientationListener.disable();
        BitmapUtils.recycleBitmap(this.mForeground, this.mProcessedImage);
    }

    public static void shootSound() {
        AudioManager meng = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (mShootMP == null)
                mShootMP = MediaPlayer.create(mContext, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mShootMP != null)
                mShootMP.start();
        }
    }

    private void takePicture() {
        Log.d(TAG, "takePicture");

        LocalDataManager localDataManager = new LocalDataManager();
        if (localSketlonTranstor.getFrameMetadata() == null) {
            return;
        }
        shootSound();
        preview.stop();
        mHandler.removeMessages(AUTO_TAKE_PHOTO);
        toggleButton.setVisibility(View.GONE);
        zoomImageLayout.setVisibility(View.VISIBLE);
        mImageAdapter.notifyDataSetChanged();

        localDataManager.setLandScape(false);
        bitmap = BitmapUtils.getBitmap(localSketlonTranstor.getProcessingImage(),
                localSketlonTranstor.getFrameMetadata());

        float previewWidth = localDataManager.getImageMaxWidth(localSketlonTranstor.getFrameMetadata());
        float previewHeight = localDataManager.getImageMaxHeight(localSketlonTranstor.getFrameMetadata());
        bitmapCopy = Bitmap.createBitmap(bitmap).copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmapCopy);
        float min = Math.min(previewWidth, previewHeight);
        float max = Math.max(previewWidth, previewHeight);
        setBitmapBorder(canvas);
        localDataManager.setCameraInfo(graphicOverlay, canvas, min, max);
        zoomImageView.setImageBitmap(bitmapCopy);

        image_save.setVisibility(View.VISIBLE);
        image_play.setVisibility(View.VISIBLE);
    }



    private void setBitmapBorder(Canvas canvas) {
        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(15);
        Rect rect = canvas.getClipBounds();
        canvas.drawRect(rect, paint);
    }

    private void recycleBitmap() {
        zoomImageView.setBackground(getResources().getDrawable(R.drawable.white));
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        if (bitmapCopy != null && !bitmapCopy.isRecycled()) {
            bitmapCopy.recycle();
            bitmapCopy = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (zoomImageLayout.getVisibility() == View.VISIBLE) {
            mImageAdapter.setThisPosition(0);
            mImageAdapter.notifyDataSetChanged();
            idSetBg = false;
            mPosition = 0;
            mProcessedImage = null;
            mForeground = null;

            zoomImageLayout.setVisibility(View.GONE);
            isSaveFlag = false;
            zoomImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mLinearLayout.setVisibility(View.GONE);
            image_save.setVisibility(View.GONE);
            image_play.setVisibility(View.GONE);
            toggleButton.setVisibility(View.VISIBLE);
            recycleBitmap();
            reStartLensEngine();
        } else {
            super.onBackPressed();
        }
    }

    private boolean idSetBg = false;

    private void createImageTransactor(final ImageInfo imageInfo) {
        MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory().setAnalyzerType(MLImageSegmentationSetting.BODY_SEG).create();
        MLImageSegmentationAnalyzer mAnalyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(setting);
        if (this.isChosen(this.bitmapCopy)) {
            MLFrame mlFrame = new MLFrame.Creator().setBitmap(this.bitmapCopy).create();
            Task<MLImageSegmentation> task = mAnalyzer.asyncAnalyseFrame(mlFrame);
            task.addOnSuccessListener(new OnSuccessListener<MLImageSegmentation>() {
                @Override
                public void onSuccess(MLImageSegmentation mlImageSegmentationResults) {
                    // Transacting logic for segment success.
                    if (mlImageSegmentationResults != null) {
                        mForeground = mlImageSegmentationResults.getForeground();
                        zoomImageView.setImageBitmap(mForeground);
                        mProcessedImage = ((BitmapDrawable) (zoomImageView).getDrawable()).getBitmap();

                        backgroundDrawable = imageInfo.getImageView();

                        if (isChosen(mForeground) && isChosenDrawable(backgroundDrawable)) {
                            zoomImageView.setDrawingCacheEnabled(true);
                            zoomImageView.setBackground(backgroundDrawable);
                            zoomImageView.setImageBitmap(mForeground);
                            mProcessedImage = Bitmap.createBitmap(zoomImageView.getDrawingCache());
                            zoomImageView.setDrawingCacheEnabled(false);

                            if (isSaveFlag) {
                                ImageUtils imageUtils = new ImageUtils(mContext);
                                imageUtils.saveToAlbum(mProcessedImage);
                                Toast.makeText(getApplicationContext(), R.string.Saved, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        displayFailure();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Transacting logic for segment failure.
                    displayFailure();
                    return;
                }
            });
        } else {
            Toast.makeText(this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private boolean isChosen(Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isChosenDrawable(Drawable drawable) {
        if (drawable == null) {
            return false;
        } else {
            return true;
        }
    }

    private void displayFailure() {
        Toast.makeText(this.getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        preview.stop();
        createLensEngine();
        startLensEngine();
    }

    private static class MsgHandler extends Handler {
        WeakReference<WoodenManActivity> mMainActivityWeakReference;

        MsgHandler(WoodenManActivity mainActivity) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WoodenManActivity mainActivity = mMainActivityWeakReference.get();
            if (mainActivity == null) {
                return;
            }
            Log.d(TAG, "msg what :" + msg.what);
            switch (msg.what) {
                case AUTO_TAKE_PHOTO:
                    if (mainActivity.zoomImageLayout.getVisibility() == View.VISIBLE) {
                        return;
                    }
                    mainActivity.takePicture();
                    break;
                default:
                    break;
            }
        }
    }

    public void initImageInfo() {
        ImageInfo imageInfo1 = new ImageInfo("", ResourcesCompat.getDrawable(getResources(), R.drawable.background1, null));
        ImageInfo imageInfo2 = new ImageInfo("", ResourcesCompat.getDrawable(getResources(), R.drawable.background2, null));
        ImageInfo imageInfo3 = new ImageInfo("", ResourcesCompat.getDrawable(getResources(), R.drawable.background3, null));
        ImageInfo imageInfo4 = new ImageInfo("", ResourcesCompat.getDrawable(getResources(), R.drawable.background4, null));
        ImageInfo imageInfo5 = new ImageInfo("", ResourcesCompat.getDrawable(getResources(), R.drawable.background5, null));
        ImageInfo imageInfo6 = new ImageInfo("", ResourcesCompat.getDrawable(getResources(), R.drawable.background6, null));

        mImageInfoList.add(imageInfo1);
        mImageInfoList.add(imageInfo2);
        mImageInfoList.add(imageInfo3);
        mImageInfoList.add(imageInfo4);
        mImageInfoList.add(imageInfo5);
        mImageInfoList.add(imageInfo6);
    }

}
