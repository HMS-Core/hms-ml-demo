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
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.mlkit.sample.camera.CameraImageGraphic;
import com.huawei.hms.mlkit.sample.cn.R;
import com.huawei.hms.mlkit.sample.transactor.LocalSketlonTranstor;
import com.huawei.hms.mlkit.sample.util.BitmapUtils;
import com.huawei.hms.mlkit.sample.views.GridItem;
import com.huawei.hms.mlkit.sample.views.SkeletonGraphic;
import com.huawei.hms.mlkit.sample.views.TemplateGridViewAdapter;
import com.huawei.hms.mlkit.sample.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateActivity extends BaseActivity {
    private static final String TAG = "TemplateActivity";

    private static final int REQUEST_IMAGE_CAPTURE = 1001;

    private static final int REQUEST_CHOOSE_IMAGE = 1002;

    private static final int UNKNOW = -1;

    private static Map<String, GridItem> templateDataMap = new HashMap<String, GridItem>();

    private static int sSelectedIndex = UNKNOW;

    private GridView mGridView;

    private TemplateGridViewAdapter mAdapter;

    private ImageView mLoadPhoto;

    private Uri imageUri;

    private Bitmap originBitmap;

    private List<GridItem> templateDatalist = new ArrayList<GridItem>();

    private LocalSketlonTranstor imageProcessor;

    private GraphicOverlay graphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        imageProcessor = new LocalSketlonTranstor(null, this);
        initData();
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        graphicOverlay = findViewById(R.id.previewOverlay);
        mGridView = findViewById(R.id.gridview);
        mAdapter = new TemplateGridViewAdapter(templateDatalist, this);
        mGridView.setAdapter(mAdapter);
        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (templateDatalist.get(position).isSelected()) {
                    templateDatalist.get(position).setSelected(false);
                } else {
                    templateDatalist.get(position).setSelected(true);
                }
                setSelectedIndex(UNKNOW);
                for (int i = 0; i < templateDatalist.size(); i++) {
                    if (i != position) {
                        templateDatalist.get(i).setSelected(false);
                    }
                    if (templateDatalist.get(i).isSelected()) {
                        setSelectedIndex(i);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        mLoadPhoto = findViewById(R.id.load_photo);
        mLoadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(view);
            }
        });
    }

    private void initData() {
        for (int i = 0; i < templateDataMap.size(); i++) {
            GridItem gridItem = templateDataMap.get("key" + i);
            if (getSelectedIndex() == UNKNOW) {
                if (i == 0) {
                    gridItem.setSelected(true);
                    setSelectedIndex(0);
                } else {
                    gridItem.setSelected(false);
                }
            } else {
                if (getSelectedIndex() != i) {
                    gridItem.setSelected(false);
                } else {
                    gridItem.setSelected(true);
                }
            }
            templateDatalist.add(gridItem);
        }
    }

    @SuppressLint("RestrictedApi")
    private void showChooseDialog(View view) {
        PopupMenu popup = new PopupMenu(TemplateActivity.this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.select_images_from_local:
                        startChooseImageIntentForResult();
                        return true;
                    case R.id.take_photo_using_camera:
                        startCameraIntentForResult();
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.camera_button_menu, popup.getMenu());
        // Use Reflection to force the display of menu icons
        try {
            Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popup);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "IllegalAccessException " + e.getMessage());
        } catch (NoSuchFieldException e) {
            Log.w(TAG, "NoSuchFieldException " + e.getMessage());
        }
        popup.show();
    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    private void startCameraIntentForResult() {
        imageUri = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            loadImage();
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            loadImage();
        }
    }

    private void loadImage() {
        originBitmap = BitmapUtils.loadFromPath(TemplateActivity.this, imageUri, getWidth(), getHeight());
        if (originBitmap == null) {
            return;
        }
        MLFrame frame = new MLFrame.Creator().setBitmap(originBitmap).create();
        imageProcessor.detectInImage(frame).addOnSuccessListener(new OnSuccessListener<List<MLSkeleton>>() {
            @Override
            public void onSuccess(List<MLSkeleton> results) {
                if (results == null) {
                    Log.d(TAG, "results == null");
                    return;
                }
                if (results.size() > 0) {
                    graphicOverlay.clear();
                    CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originBitmap);
                    graphicOverlay.addGraphic(imageGraphic);

                    SkeletonGraphic skeletonGraphic = new SkeletonGraphic(graphicOverlay, results);
                    graphicOverlay.addGraphic(skeletonGraphic);
                    graphicOverlay.postInvalidate();

                    GridItem gridItem = new GridItem();
                    gridItem.setBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, originBitmap.getWidth(),
                            originBitmap.getHeight()));
                    gridItem.setSkeletonList(results);
                    templateDataMap.put("key" + 0, gridItem);
                    templateDatalist.add(gridItem);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TemplateActivity.this, "No skeleton data, create template fail.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    private int getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        int width;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = display.getHeight();
        } else {
            width = display.getWidth();
        }
        return width;
    }

    private int getHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        int height;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            height = display.getWidth();
        } else {
            height = display.getHeight();
        }
        return height;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageProcessor.stop();
    }


    public static void clearData() {
        templateDataMap.clear();
    }

    public static Map<String, GridItem> getTemplateDataMap() {
        return templateDataMap;
    }

    public static int getSelectedIndex() {
        return sSelectedIndex;
    }

    public static void setSelectedIndex(int index) {
        TemplateActivity.sSelectedIndex = index;
    }
}
