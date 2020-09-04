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


package com.mlkit.sample.phototranslate;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;

import com.huawei.hms.mlsdk.text.MLRemoteTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.mlkit.sample.phototranslate.camera.CapturePhotoActivity;
import com.mlkit.sample.phototranslate.util.BitmapUtils;
import com.mlkit.sample.phototranslate.util.Constant;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RemoteTranslateActivity extends AppCompatActivity {
    private static String TAG = "RemoteTranslateActivity";
    private RelativeLayout relativeLayoutLoadPhoto;
    private RelativeLayout relativeLayoutTakePhoto;
    private RelativeLayout relativeLayoutTranslate;
    private ImageView preview;
    private TextView textView;
    private Uri imageUri;
    private String path;
    private Bitmap originBitmap;
    private Integer maxWidthOfImage;
    private Integer maxHeightOfImage;
    boolean isLandScape;
    private int REQUEST_CHOOSE_ORIGINPIC = 2001;
    private int REQUEST_TAKE_PHOTO = 2000;
    private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";
    private static final String KEY_IMAGE_MAX_WIDTH =
            "KEY_IMAGE_MAX_WIDTH";
    private static final String KEY_IMAGE_MAX_HEIGHT =
            "KEY_IMAGE_MAX_HEIGHT";
    private String sourceText = "";

    private String srcLanguage = "Auto";
    private String dstLanguage = "EN";

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this.setContentView(R.layout.activity_translate_photo);
        Intent intent = this.getIntent();
        try {
            this.srcLanguage = intent.getStringExtra(Constant.SOURCE_VALUE);
            this.dstLanguage = intent.getStringExtra(Constant.DEST_VALUE);
        } catch (RuntimeException e) {
            Log.e(RemoteTranslateActivity.TAG, "Get intent value failed:" + e.getMessage());
        }
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteTranslateActivity.this.finish();
            }
        });
        this.isLandScape =
                (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        this.initView();
        this.initAction();
    }

    private void initView() {
        this.relativeLayoutLoadPhoto = this.findViewById(R.id.relativate_chooseImg);
        this.relativeLayoutTakePhoto = this.findViewById(R.id.relativate_camera);
        this.relativeLayoutTranslate = this.findViewById(R.id.relativate_translate);
        this.preview = this.findViewById(R.id.previewPane);
        this.textView = this.findViewById(R.id.translate_result);
    }

    private void initAction() {
        this.relativeLayoutLoadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteTranslateActivity.this.selectLocalImage(RemoteTranslateActivity.this.REQUEST_CHOOSE_ORIGINPIC);
            }
        });

        // Outline the edge.
        this.relativeLayoutTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RemoteTranslateActivity.this.imageUri == null
                        && RemoteTranslateActivity.this.path == null) {
                    Toast.makeText(RemoteTranslateActivity.this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
                } else {
                    RemoteTranslateActivity.this.createRemoteTextAnalyzer();
                    Toast.makeText(RemoteTranslateActivity.this.getApplicationContext(), R.string.translate_start, Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.relativeLayoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteTranslateActivity.this.takePhoto(RemoteTranslateActivity.this.REQUEST_TAKE_PHOTO);
            }
        });
    }

    private void takePhoto(int requestCode) {
        Intent intent = new Intent(RemoteTranslateActivity.this, CapturePhotoActivity.class);
        this.startActivityForResult(intent, requestCode);
    }

    private void selectLocalImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == this.REQUEST_CHOOSE_ORIGINPIC)
                && (resultCode == Activity.RESULT_OK)) {
            // In this case, imageUri is returned by the chooser, save it.
            this.imageUri = data.getData();
            this.loadOriginImage();
        } else if ((requestCode == this.REQUEST_TAKE_PHOTO)
                && (resultCode == Activity.RESULT_OK)
                && data != null) {
            this.path = data.getStringExtra(Constant.IMAGE_PATH_VALUE);
            this.loadCameraImage();
        }
    }

    private void loadCameraImage() {
        FileInputStream fis = null;

        try {
            if (path == null) {
                return;
            }
            fis = new FileInputStream(path);
            this.originBitmap = BitmapFactory.decodeStream(fis);
            this.originBitmap = this.originBitmap.copy(Bitmap.Config.ARGB_4444, true);
            this.preview.setImageBitmap(this.originBitmap);
        } catch (IOException e) {
            Log.e(TAG, "file not found");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException error) {
                    Log.e(TAG, "Load camera image failed: " + error.getMessage());
                }
            }
        }
    }


    private MLTextAnalyzer textAnalyzer;
    private MLRemoteTranslator translator;

    private void createRemoteTranslator() {
        MLRemoteTranslateSetting.Factory factory = new MLRemoteTranslateSetting
                .Factory()
                // Set the target language code. The ISO 639-1 standard is used.
                .setTargetLangCode(this.dstLanguage);
        if (!this.srcLanguage.equals("AUTO")) {
            // Set the source language code. The ISO 639-1 standard is used.
            factory.setSourceLangCode(this.srcLanguage);
        }
        this.translator = MLTranslatorFactory.getInstance().getRemoteTranslator(factory.create());
        final Task<String> task = translator.asyncTranslate(this.sourceText);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                if (text != null) {
                    RemoteTranslateActivity.this.remoteDisplaySuccess(text);
                } else {
                    RemoteTranslateActivity.this.displayFailure();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                RemoteTranslateActivity.this.displayFailure();
            }
        });
    }

    private void createRemoteTextAnalyzer() {
        MLRemoteTextSetting setting = (new MLRemoteTextSetting.Factory())
                .setTextDensityScene(MLRemoteTextSetting.OCR_LOOSE_SCENE)
                .create();
        this.textAnalyzer = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer(setting);
        if (this.isChosen(this.originBitmap)) {
            MLFrame mlFrame = new MLFrame.Creator().setBitmap(this.originBitmap).create();
            Task<MLText> task = this.textAnalyzer.asyncAnalyseFrame(mlFrame);
            task.addOnSuccessListener(new OnSuccessListener<MLText>() {
                @Override
                public void onSuccess(MLText mlText) {
                    // Transacting logic for segment success.
                    if (mlText != null) {
                        RemoteTranslateActivity.this.remoteDetectSuccess(mlText);
                    } else {
                        RemoteTranslateActivity.this.displayFailure();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Transacting logic for segment failure.
                    RemoteTranslateActivity.this.displayFailure();
                    return;
                }
            });
        } else {
            Toast.makeText(this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void remoteDetectSuccess(MLText mlTexts) {
        this.sourceText = "";
        List<MLText.Block> blocks = mlTexts.getBlocks();
        List<MLText.TextLine> lines = new ArrayList<>();
        for (MLText.Block block : blocks) {
            for (MLText.TextLine line : block.getContents()) {
                if (line.getStringValue() != null) {
                    lines.add(line);
                }
            }
        }
        Collections.sort(lines, new SortComparator());
        for (int i = 0; i < lines.size(); i++) {
            this.sourceText = this.sourceText + lines.get(i).getStringValue().trim() + "\n";
        }
        this.createRemoteTranslator();
    }

    private static class SortComparator implements Comparator<MLText.TextLine> {
        @Override
        public int compare(MLText.TextLine o1, MLText.TextLine o2) {
            Point[] point1 = o1.getVertexes();
            Point[] point2 = o2.getVertexes();
            return point1[0].y - point2[0].y;
        }
    }

    private void remoteDisplaySuccess(String test) {
        String[] sourceLines = sourceText.split("\n");
        String[] drtLines = test.split("\n");
        for (int i = 0; i < sourceLines.length && i < drtLines.length; i++) {
            this.textView.append(sourceLines[i] + "-> " + drtLines[i] + "\n");
        }
        Toast.makeText(RemoteTranslateActivity.this.getApplicationContext(), R.string.translate_success, Toast.LENGTH_SHORT).show();
    }

    private void displayFailure() {
        Toast.makeText(this.getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
    }

    private boolean isChosen(Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        } else {
            return true;
        }
    }

    private void loadOriginImage() {
        if (this.imageUri == null) {
            return;
        }
        Pair<Integer, Integer> targetedSize = this.getTargetSize();
        int targetWidth = targetedSize.first;
        int maxHeight = targetedSize.second;
        this.originBitmap = BitmapUtils.loadFromPath(RemoteTranslateActivity.this, this.imageUri, targetWidth, maxHeight);
        // Determine how much to scale down the image.
        Log.i(RemoteTranslateActivity.TAG, "resized image size width:" + this.originBitmap.getWidth() + ",height: " + this.originBitmap.getHeight());
        this.preview.setImageBitmap(this.originBitmap);
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

    // Gets the targeted size(width / height).
    private Pair<Integer, Integer> getTargetSize() {
        Integer targetWidth;
        Integer targetHeight;
        Integer maxWidth = this.getMaxWidthOfImage();
        Integer maxHeight = this.getMaxHeightOfImage();
        targetWidth = this.isLandScape ? maxHeight : maxWidth;
        targetHeight = this.isLandScape ? maxWidth : maxHeight;
        Log.i(RemoteTranslateActivity.TAG, "height:" + targetHeight + ",width:" + targetWidth);
        return new Pair<>(targetWidth, targetHeight);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.textAnalyzer != null) {
            try {
                this.textAnalyzer.close();
            } catch (IOException e) {
                Log.e(RemoteTranslateActivity.TAG, "Stop analyzer failed: " + e.getMessage());
            }
        }
        if (this.translator != null) {
            this.translator.stop();
        }
        this.imageUri = null;
        this.path = null;
        this.srcLanguage = "Auto";
        this.dstLanguage = "EN";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RemoteTranslateActivity.KEY_IMAGE_URI, this.imageUri);
        if (this.maxWidthOfImage != null) {
            outState.putInt(RemoteTranslateActivity.KEY_IMAGE_MAX_WIDTH, this.maxWidthOfImage);
        }
        if (this.maxHeightOfImage != null) {
            outState.putInt(RemoteTranslateActivity.KEY_IMAGE_MAX_HEIGHT, this.maxHeightOfImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
