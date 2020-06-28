/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.mlkit.sample.homeworkreader;

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
import com.huawei.hms.mlsdk.common.internal.client.SmartLog;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;
import com.mlkit.sample.homeworkreader.camera.CapturePhotoActivity;
import com.mlkit.sample.homeworkreader.util.BitmapUtils;
import com.mlkit.sample.homeworkreader.util.Constant;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReadPhotoActivity extends AppCompatActivity {
    private static String TAG = "ReadPhotoActivity";
    private RelativeLayout relativeLayoutLoadPhoto;
    private RelativeLayout relativeLayoutTakePhoto;
    private RelativeLayout relativeLayoutRead;
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

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this.setContentView(R.layout.activity_read_photo);
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadPhotoActivity.this.finish();
            }
        });
        this.isLandScape =
                (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        this.initView();
        this.initAction();
        this.createLocalTextAnalyzer();
        this.createTtsEngine();
    }

    private void initView() {
        this.relativeLayoutLoadPhoto = this.findViewById(R.id.relativate_chooseImg);
        this.relativeLayoutTakePhoto = this.findViewById(R.id.relativate_camera);
        this.relativeLayoutRead = this.findViewById(R.id.relativate_read);
        this.preview = this.findViewById(R.id.previewPane);
        this.textView = this.findViewById(R.id.translate_result);
    }

    private void initAction() {
        this.relativeLayoutLoadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadPhotoActivity.this.selectLocalImage(ReadPhotoActivity.this.REQUEST_CHOOSE_ORIGINPIC);
            }
        });

        this.relativeLayoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadPhotoActivity.this.takePhoto(ReadPhotoActivity.this.REQUEST_TAKE_PHOTO);
            }
        });

        this.relativeLayoutRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ReadPhotoActivity.this.sourceText == null) {
                    Toast.makeText(ReadPhotoActivity.this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
                } else {
                    ReadPhotoActivity.this.mlTtsEngine.speak(sourceText, MLTtsEngine.QUEUE_APPEND);
                    Toast.makeText(ReadPhotoActivity.this.getApplicationContext(), R.string.read_start, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void takePhoto(int requestCode) {
        Intent intent = new Intent(ReadPhotoActivity.this, CapturePhotoActivity.class);
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
            this.startTextAnalyzer();
        } else if ((requestCode == this.REQUEST_TAKE_PHOTO)
                && (resultCode == Activity.RESULT_OK)
                && data != null) {
            this.path = data.getStringExtra(Constant.IMAGE_PATH_VALUE);
            this.loadCameraImage();
            this.startTextAnalyzer();
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
                    SmartLog.e(TAG, "Load camera image failed: " + error.getMessage());
                }
            }
        }
    }


    private MLTextAnalyzer textAnalyzer;
    private MLTtsEngine mlTtsEngine;

    private void createTtsEngine() {
        MLTtsConfig mlConfigs = new MLTtsConfig()
                .setLanguage(MLTtsConstants.TTS_ZH_HANS)
                .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_ZH)
                .setSpeed(0.2f)
                .setVolume(1.0f);
        this.mlTtsEngine = new MLTtsEngine(mlConfigs);
        MLTtsCallback callback = new MLTtsCallback() {
            @Override
            public void onError(String taskId, MLTtsError err) {
            }

            @Override
            public void onWarn(String taskId, MLTtsWarn warn) {
            }

            @Override
            public void onRangeStart(String taskId, int start, int end) {
            }

            @Override
            public void onEvent(String taskId, int eventName, Bundle bundle) {
                if (eventName == MLTtsConstants.EVENT_PLAY_STOP) {
                    if (!bundle.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED)) {
                        Toast.makeText(ReadPhotoActivity.this.getApplicationContext(), R.string.read_finish, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        mlTtsEngine.setTtsCallback(callback);
    }

    private void createLocalTextAnalyzer() {
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .setLanguage("zh")
                .create();
        this.textAnalyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);

    }

    private void startTextAnalyzer() {
        if (this.isChosen(this.originBitmap)) {
            MLFrame mlFrame = new MLFrame.Creator().setBitmap(this.originBitmap).create();
            Task<MLText> task = this.textAnalyzer.asyncAnalyseFrame(mlFrame);
            task.addOnSuccessListener(new OnSuccessListener<MLText>() {
                @Override
                public void onSuccess(MLText mlText) {
                    // Transacting logic for segment success.
                    if (mlText != null) {
                        ReadPhotoActivity.this.remoteDetectSuccess(mlText);
                    } else {
                        ReadPhotoActivity.this.displayFailure();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Transacting logic for segment failure.
                    ReadPhotoActivity.this.displayFailure();
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
        this.textView.setText(this.sourceText);
    }

    private static class SortComparator implements Comparator<MLText.TextLine> {
        @Override
        public int compare(MLText.TextLine o1, MLText.TextLine o2) {
            Point[] point1 = o1.getVertexes();
            Point[] point2 = o2.getVertexes();
            return point1[0].y - point2[0].y;
        }
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
        this.originBitmap = BitmapUtils.loadFromPath(ReadPhotoActivity.this, this.imageUri, targetWidth, maxHeight);
        // Determine how much to scale down the image.
        SmartLog.i(ReadPhotoActivity.TAG, "resized image size width:" + this.originBitmap.getWidth() + ",height: " + this.originBitmap.getHeight());
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
        SmartLog.i(ReadPhotoActivity.TAG, "height:" + targetHeight + ",width:" + targetWidth);
        return new Pair<>(targetWidth, targetHeight);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.textAnalyzer != null) {
            try {
                this.textAnalyzer.close();
            } catch (IOException e) {
                SmartLog.e(ReadPhotoActivity.TAG, "Stop analyzer failed: " + e.getMessage());
            }
        }
        if (this.mlTtsEngine != null) {
            this.mlTtsEngine.stop();
        }
        this.imageUri = null;
        this.path = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ReadPhotoActivity.KEY_IMAGE_URI, this.imageUri);
        if (this.maxWidthOfImage != null) {
            outState.putInt(ReadPhotoActivity.KEY_IMAGE_MAX_WIDTH, this.maxWidthOfImage);
        }
        if (this.maxHeightOfImage != null) {
            outState.putInt(ReadPhotoActivity.KEY_IMAGE_MAX_HEIGHT, this.maxHeightOfImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
