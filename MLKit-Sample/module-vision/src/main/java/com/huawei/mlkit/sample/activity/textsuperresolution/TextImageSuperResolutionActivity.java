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

package com.huawei.mlkit.sample.activity.textsuperresolution;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;

import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.textimagesuperresolution.MLTextImageSuperResolution;
import com.huawei.hms.mlsdk.textimagesuperresolution.MLTextImageSuperResolutionAnalyzer;
import com.huawei.hms.mlsdk.textimagesuperresolution.MLTextImageSuperResolutionAnalyzerFactory;
import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.util.BitmapUtils;


import java.util.ArrayList;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class TextImageSuperResolutionActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SuperResolutionActivity";
    private static final String STR_PX = "px";
    private static final int IMAGE_MAX_SIZE = 1024;
    private static final int REQUEST_SELECT_IMAGE = 1000;

    private static final int INDEX_3X = 0;
    private static final int INDEX_ORIGINAL = 1;

    private ImageViewTouch desImageView;
    private ImageView srcImageView;
    private TextView tvImageSize;
    private ImageButton adjustImgButton;
    private Bitmap srcBitmap;
    private Bitmap desBitmap;
    private Dialog dialog;
    private Uri imageUri;
    private int selectItem = INDEX_3X;
    private MLTextImageSuperResolutionAnalyzer analyzer;
    private RelativeLayout rlScale3X;
    private RelativeLayout rlScaleOriginal;
    private RelativeLayout rlHelp;
    private String strWidth;
    private String strHeight;
    private List<ImageView> imageViewList = new ArrayList<>();
    private boolean isShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_super_resolution);
        setStatusBarColor(this, R.color.black);
        strWidth = getResources().getString(R.string.isr_image_width);
        strHeight = getResources().getString(R.string.isr_image_height);
        analyzer = createAnalyzer();
        adjustImgButton = findViewById(R.id.adjust);
        srcImageView = findViewById(R.id.src_image);
        desImageView = findViewById(R.id.des_image);
        tvImageSize = findViewById(R.id.image_size_info);
        rlScale3X = findViewById(R.id.rl_3x);
        rlScaleOriginal = findViewById(R.id.rl_original);
        imageViewList.add((ImageView) findViewById(R.id.ic_3x));
        imageViewList.add((ImageView) findViewById(R.id.ic_original));
        rlHelp = findViewById(R.id.rl_help);
        adjustImgButton.setOnClickListener(this);
        rlScaleOriginal.setOnClickListener(this);
        rlScale3X.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.rl_chooseImg).setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        selectLocalImage();
    }

    private void showTipsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_layout, null);
        TextView content = v.findViewById(R.id.dialog_content);
        content.setText(R.string.tsr_tips_content);
        Button mBtn_cancel = v.findViewById(R.id.dialog_btn_cancel);
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        mBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_3x) {
            onClickItem(INDEX_3X);
        } else if (v.getId() == R.id.rl_original) {
            onClickItem(INDEX_ORIGINAL);
        } else if (v.getId() == R.id.back) {
            finish();
        } else if (v.getId() == R.id.rl_chooseImg) {
            selectLocalImage();
        } else if (v.getId() == R.id.adjust) {
            showScaleChooseViews();
        } else if (v.getId() == R.id.rl_help){
            showTipsDialog();
        }
    }

    private void showScaleChooseViews() {
        if (!isShow) {
            rlScale3X.setVisibility(View.VISIBLE);
            rlScaleOriginal.setVisibility(View.VISIBLE);
            isShow = true;
        } else {
            rlScaleOriginal.setVisibility(View.GONE);
            rlScale3X.setVisibility(View.GONE);
            isShow = false;
        }

    }

    private void onClickItem(int index) {
        selectItem = index;
        resetSelectItem(selectItem);
        reloadAndDetectImage(false);
    }

    private MLTextImageSuperResolutionAnalyzer createAnalyzer() {
        return MLTextImageSuperResolutionAnalyzerFactory.getInstance().getTextImageSuperResolutionAnalyzer();
    }

    private void selectLocalImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    private void resetSelectItem(int position) {
        for (int i = 0; i < imageViewList.size(); i++) {
            if (i == position) {
                imageViewList.get(i).setBackgroundResource(R.drawable.ic_circle_selected);
            } else {
                imageViewList.get(i).setBackgroundResource(R.drawable.ic_circle);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
            }
            reloadAndDetectImage(true);
        } else if (resultCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    private void reloadAndDetectImage(boolean isReload) {
        if (isReload) {
            if (imageUri == null) {
                return;
            }
            srcBitmap = BitmapUtils.loadFromPathWithoutZoom(this, imageUri, IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);
            setImage(srcImageView, srcBitmap);
        }

        if (selectItem == INDEX_ORIGINAL) {
            setImage(desImageView, srcBitmap);
            setImageSizeInfo(srcBitmap.getWidth(), srcBitmap.getHeight());
            return;
        }
        // Create an MLFrame by using the bitmap.
        MLFrame frame = MLFrame.fromBitmap(srcBitmap);
        Task<MLTextImageSuperResolution> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLTextImageSuperResolution>() {
            public void onSuccess(MLTextImageSuperResolution result) {
                // Recognition success.
                desBitmap = result.getBitmap();
                setImage(desImageView, desBitmap);
                setImageSizeInfo(desBitmap.getWidth(), desBitmap.getHeight());
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(Exception e) {
                // Recognition failure.
                Log.e(TAG, "Failed." + e.getMessage());
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setImageSizeInfo(final int width, final int height) {
        final StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(strWidth)
                .append(width)
                .append(STR_PX)
                .append("     ")
                .append(strHeight)
                .append(height)
                .append(STR_PX);
        TextImageSuperResolutionActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvImageSize.setText(resultBuilder.toString());
            }
        });
    }

    private void setImage(final ImageView imageView, final Bitmap bitmap) {
        TextImageSuperResolutionActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (srcBitmap != null) {
            srcBitmap.recycle();
        }
        if (desBitmap != null) {
            desBitmap.recycle();
        }
        if (analyzer != null) {
            analyzer.stop();
        }
    }
}