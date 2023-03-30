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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.custom.MLModelOutputs;
import com.huawei.mlkit.sample.activity.adapter.CustModelAdapter;
import com.huawei.mlkit.sample.camera.CameraConfiguration;
import com.huawei.mlkit.sample.camera.FrameMetadata;
import com.huawei.mlkit.sample.camera.GraphicOverlay;
import com.huawei.mlkit.sample.camera.LensEngine;
import com.huawei.mlkit.sample.camera.LensEnginePreview;
import com.huawei.mlkit.sample.custom.R;
import com.huawei.mlkit.sample.model.HebingModel;
import com.huawei.mlkit.sample.model.InterpreterManager;
import com.huawei.mlkit.sample.model.LabelTfModel;
import com.huawei.mlkit.sample.model.ModelOperator;
import com.huawei.mlkit.sample.camera.transactor.ImageTransactor;
import com.huawei.mlkit.sample.utils.BitmapUtils;

import java.io.IOException;
import java.nio.ByteBuffer;


public class CustModelLabelActivity extends BaseActivity implements View.OnClickListener, InterpreterManager.ExceutorResult {
    private static final String TAG = "CustModelLabelActivity";

    private ConstraintLayout constraintlayout;
    private LensEnginePreview camera_preview;
    private GraphicOverlay camera_overlay;
    private TextView tv_result_titile;
    private RecyclerView recycleview;
    private CustModelAdapter mAdapter;
    private AppCompatSpinner spinner;

    private LensEngine lensEngine;
    private InterpreterManager interpreterManager;

    private ModelOperator modelOperator;
    private ImageView iv_slide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_model_lebal_result);
        findview();
        modelOperator = new LabelTfModel(this);
//        modelOperator = new HebingModel(this);
        MLApplication.initialize(getApplicationContext());
        interpreterManager = new InterpreterManager(modelOperator, this);

    }


    private void changeModel(int position) {
        switch (position) {
            case 0:
                if (!(interpreterManager.getmModelOperator() instanceof LabelTfModel)) {
                    modelOperator = new LabelTfModel(this);
                    interpreterManager.changeModel(modelOperator);

                }
                break;
            case 1:
                if (!(interpreterManager.getmModelOperator() instanceof HebingModel)) {
                    modelOperator = new HebingModel(this);
                    interpreterManager.changeModel(modelOperator);

                }
                break;
            default:
                break;
        }
    }

    private void findview() {
        findViewById(R.id.back).setOnClickListener(this);
        constraintlayout = findViewById(R.id.constraintlayout);
        tv_result_titile = findViewById(R.id.tv_result_titile);
        recycleview = findViewById(R.id.recycleview);
        camera_preview = findViewById(R.id.camera_preview);
        camera_overlay = findViewById(R.id.camera_overlay);
        spinner = findViewById(R.id.spinner);
        iv_slide = findViewById(R.id.iv_slide);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                changeModel(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAdapter = new CustModelAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycleview.setLayoutManager(layoutManager);
        recycleview.setAdapter(mAdapter);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(constraintlayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setStatusBarColor(this, R.color.black);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        iv_slide.setImageResource(R.mipmap.custmodel_up);
                        tv_result_titile.setText(getString(R.string.results));
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        iv_slide.setImageResource(R.mipmap.custmodel_down);
                        tv_result_titile.setText(getString(R.string.switchover));
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back || v.getId() == R.id.iv_back) {
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        lensEngine = new LensEngine(this, new CameraConfiguration(), camera_overlay);
        lensEngine.setMachineLearningFrameTransactor(new ImageTransactor() {
            @Override
            public void process(final ByteBuffer data, final FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
                if (!interpreterManager.isNeedFrame())
                    return;
                interpreterManager.exec(BitmapUtils.getBitmap(data, frameMetadata));
            }

            @Override
            public void process(Bitmap bitmap, GraphicOverlay graphicOverlay) {
            }

            @Override
            public void stop() {
                interpreterManager.close();
            }

            @Override
            public boolean isFaceDetection() {
                return false;
            }
        });
        try {
            camera_preview.start(lensEngine, false);
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lensEngine != null)
            lensEngine.stop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lensEngine != null)
            lensEngine.release();
    }


    @Override
    public boolean onResult(MLModelOutputs outputs) {
        mAdapter.setData((String[][]) modelOperator.resultPostProcess(outputs));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
        return true;
    }
}
