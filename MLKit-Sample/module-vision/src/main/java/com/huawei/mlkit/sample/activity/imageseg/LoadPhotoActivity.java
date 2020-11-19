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

package com.huawei.mlkit.sample.activity.imageseg;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.transactor.ImageTransactor;
import com.huawei.mlkit.sample.transactor.StillImageSegmentationTransactor;
import com.huawei.mlkit.sample.util.BitmapUtils;
import com.huawei.mlkit.sample.views.color.ColorSelector;
import com.huawei.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.util.Locale;

public class LoadPhotoActivity extends BaseActivity {
    private static final String TAG = "LoadPhotoActivity";

    private static final String[] CATEGORIES = {"背景", "人", "天空", "绿植", "食物", "猫狗", "建筑", "花朵", "水面", "沙滩", "山坡"};

    private static final String[] CATEGORIES_EN = {"Background", "People", "Sky", "Plant", "Food", "Animal", "Architecture", "Flower", "Water", "Beach", "Hill"};

    private Uri mImageUri;

    private ImageView preview;

    private static final int REQUEST_TAKE_PHOTOR = 1;

    private static final int REQUEST_SLECT_IMAGE = 2;

    private LinearLayout linearObjects;

    private ImageTransactor imageTransactor;

    private GraphicOverlay graphicOverlay;

    private Bitmap originBitmap;

    private int colorvalue = Color.GREEN;

    private int imageMaxWidth;
    private int imageMaxHeight;

    private ColorSelector colorSelector;

    private boolean isLandScape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_load_photo);
        this.preview = this.findViewById(R.id.image_preview);
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadPhotoActivity.this.finish();
            }
        });
        this.initView();
        this.isLandScape = (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void initView() {
        this.linearObjects = this.findViewById(R.id.linear_objects);
        final String[] categories;
        if (this.isEngLanguage()) {
            categories = LoadPhotoActivity.CATEGORIES_EN;
        } else {
            categories = LoadPhotoActivity.CATEGORIES;
        }
        for (int i = 0; i < categories.length; i++) {
            View view = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.layout, this.linearObjects, false);
            TextView textView = view.findViewById(R.id.text);
            textView.setText(categories[i]);
            this.linearObjects.addView(view);
            final int index = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory().setAnalyzerType(MLImageSegmentationSetting.IMAGE_SEG).create();
                    StillImageSegmentationTransactor transactor = new StillImageSegmentationTransactor(setting, LoadPhotoActivity.this.originBitmap, LoadPhotoActivity.this.preview, index);
                    transactor.setColor(LoadPhotoActivity.this.colorvalue);
                    LoadPhotoActivity.this.imageTransactor = transactor;
                    LoadPhotoActivity.this.imageTransactor.process(LoadPhotoActivity.this.originBitmap, LoadPhotoActivity.this.graphicOverlay);
                }
            });
        }
        this.graphicOverlay = this.findViewById(R.id.previewOverlay);

        // Color picker settings.
        this.colorSelector = this.findViewById(R.id.color_selector);
        this.colorSelector = this.findViewById(R.id.color_selector);
        this.colorSelector.initData();
        this.colorSelector.setColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.WHITE);
        this.colorSelector.setOnColorSelectorChangeListener(new ColorSelector.OnColorSelectorChangeListener() {
            @Override
            public void onColorChanged(ColorSelector picker, int color) {
                LoadPhotoActivity.this.colorvalue = color;
            }

            @Override
            public void onStartColorSelect(ColorSelector picker) {
            }

            @Override
            public void onStopColorSelect(ColorSelector picker) {
            }
        });

        this.colorSelector.post(new Runnable() {
            @Override
            public void run() {
                LoadPhotoActivity.this.selectLocalImage();
            }
        });
    }

    private void selectLocalImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(intent, LoadPhotoActivity.REQUEST_SLECT_IMAGE);
    }

    public boolean isEngLanguage() {
        Locale locale = Locale.getDefault();
        if (locale != null) {
            String strLan = locale.getLanguage();
            return strLan != null && "en".equals(strLan);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(this.TAG, "requestCode:" + requestCode + " : " + resultCode);
        if (requestCode == this.REQUEST_TAKE_PHOTOR && resultCode == Activity.RESULT_OK) {
            this.loadImage();
        } else if (requestCode == this.REQUEST_TAKE_PHOTOR && resultCode == Activity.RESULT_CANCELED) {
            this.finish();
        } else if (requestCode == this.REQUEST_SLECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                this.mImageUri = data.getData();
            }
            this.loadImage();
        } else if (requestCode == this.REQUEST_SLECT_IMAGE && resultCode == Activity.RESULT_CANCELED) {
            this.finish();
        }
    }

    private void loadImage() {
        this.originBitmap = BitmapUtils.loadFromPath(LoadPhotoActivity.this, this.mImageUri, this.getMaxWidthOfImage(), this.getMaxHeightOfImage());
        this.preview.setImageBitmap(this.originBitmap);
    }

    private int getMaxWidthOfImage() {
        if (this.imageMaxWidth == 0) {
            if (this.isLandScape) {
                this.imageMaxWidth = ((View) this.preview.getParent()).getHeight();
            } else {
                this.imageMaxWidth = ((View) this.preview.getParent()).getWidth();
            }
        }
        return this.imageMaxWidth;
    }

    private int getMaxHeightOfImage() {
        if (this.imageMaxHeight == 0) {
            if (this.isLandScape) {
                this.imageMaxHeight = ((View) this.preview.getParent()).getWidth();
            } else {
                this.imageMaxHeight = ((View) this.preview.getParent()).getHeight();
            }
        }
        return this.imageMaxHeight;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BitmapUtils.recycleBitmap(this.originBitmap);
        this.mImageUri = null;
        if (this.imageTransactor != null) {
            this.imageTransactor.stop();
            this.imageTransactor = null;
        }
        if (this.graphicOverlay != null) {
            this.graphicOverlay.clear();
            this.graphicOverlay = null;
        }
    }

    public void onBackPressed(View view) {
        this.finish();
    }
}
