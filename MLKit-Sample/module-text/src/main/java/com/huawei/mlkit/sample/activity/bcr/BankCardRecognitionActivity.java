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

package com.huawei.mlkit.sample.activity.bcr;

import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.mlplugin.card.bcr.MLBcrCapture;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureConfig;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureFactory;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureResult;
import com.huawei.mlkit.sample.R;

import androidx.appcompat.app.AppCompatActivity;

public class BankCardRecognitionActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView bankCardFrontImg;
    private ImageView bankCardFrontSimpleImg;
    private ImageView bankCardFrontDeleteImg;
    private LinearLayout bankCardFrontAddView;
    private TextView showResult;
    private String lastFrontResult = "";
    private String lastBackResult = "";
    private Bitmap currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_recognition);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        initComponent();
    }

    private void initComponent() {
        bankCardFrontImg = findViewById(R.id.avatar_img);
        bankCardFrontSimpleImg = findViewById(R.id.avatar_sample_img);
        bankCardFrontDeleteImg = findViewById(R.id.avatar_delete);
        bankCardFrontAddView = findViewById(R.id.avatar_add);
        showResult = findViewById(R.id.show_result);

        bankCardFrontAddView.setOnClickListener(this);
        bankCardFrontDeleteImg.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.avatar_add:
                startCaptureActivity();
                break;
            case R.id.avatar_delete:
                showFrontDeleteImage();
                lastFrontResult = "";
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentImage != null && !currentImage.isRecycled()) {
            currentImage.recycle();
            currentImage = null;
        }
    }

    private void startCaptureActivity() {
        MLBcrCaptureConfig config = new MLBcrCaptureConfig.Factory()
                .setOrientation(MLBcrCaptureConfig.ORIENTATION_AUTO)
                .setResultType(MLBcrCaptureConfig.RESULT_ALL)
                .create();
        MLBcrCapture bcrCapture = MLBcrCaptureFactory.getInstance().getBcrCapture(config);

        bcrCapture.captureFrame(this, this.callback);
    }

    private String formatCardResult(MLBcrCaptureResult result) {
        StringBuilder resultBuilder = new StringBuilder();

        resultBuilder.append("Number：");
        resultBuilder.append(result.getNumber());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Issuer：");
        resultBuilder.append(result.getIssuer());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Expire: ");
        resultBuilder.append(result.getExpire());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Type: ");
        resultBuilder.append(result.getType());
        resultBuilder.append(System.lineSeparator());

        resultBuilder.append("Organization: ");
        resultBuilder.append(result.getOrganization());
        resultBuilder.append(System.lineSeparator());

        return resultBuilder.toString();
    }

    private MLBcrCapture.Callback callback = new MLBcrCapture.Callback() {
        @Override
        public void onSuccess(MLBcrCaptureResult result) {

            if (result == null) {
                return;
            }
            Bitmap bitmap = result.getOriginalBitmap();
            showSuccessResult(bitmap, result);
        }

        @Override
        public void onCanceled() {

        }

        @Override
        public void onFailure(int retCode, Bitmap bitmap) {

            showResult.setText(" RecFailed ");
        }

        @Override
        public void onDenied() {

        }
    };

    private void showSuccessResult(Bitmap bitmap, MLBcrCaptureResult idCardResult) {
        showFrontImage(bitmap);
        lastFrontResult = formatCardResult(idCardResult);
        showResult.setText(lastFrontResult);
        showResult.append(lastBackResult);
        ((ImageView) findViewById(R.id.number)).setImageBitmap(idCardResult.getNumberBitmap());
    }

    private void showFrontImage(Bitmap bitmap) {
        bankCardFrontImg.setVisibility(View.VISIBLE);
        bankCardFrontImg.setImageBitmap(bitmap);
        bankCardFrontSimpleImg.setVisibility(View.GONE);
        bankCardFrontAddView.setVisibility(View.GONE);
        bankCardFrontDeleteImg.setVisibility(View.VISIBLE);
    }

    private void showFrontDeleteImage() {
        bankCardFrontImg.setVisibility(View.GONE);
        bankCardFrontSimpleImg.setVisibility(View.VISIBLE);
        bankCardFrontAddView.setVisibility(View.VISIBLE);
        bankCardFrontDeleteImg.setVisibility(View.GONE);
    }
}
