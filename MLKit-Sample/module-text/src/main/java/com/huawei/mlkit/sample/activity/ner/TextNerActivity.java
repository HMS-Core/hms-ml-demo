/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
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

package com.huawei.mlkit.sample.activity.ner;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.entity.MLNerFactory;
import com.huawei.hms.mlsdk.entity.cloud.MLRemoteNer;
import com.huawei.hms.mlsdk.entity.cloud.MLRemoteNerSetting;
import com.huawei.hms.mlsdk.entity.cloud.bo.RemoteNerResultItem;
import com.huawei.mlkit.sample.R;

public class TextNerActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String API_KEY = "client/api_key";
    private EditText input;
    private TextView inputSize;
    private TextView nerOutput;
    private MLRemoteNer ner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_ner);
        setApiKey();
        initView();
        onClickListener();
        initNer();
    }

    private void setApiKey() {
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getApplication());
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }

    private void initView() {
        input = findViewById(R.id.ner_input);
        nerOutput = findViewById(R.id.ner_output);
        inputSize = findViewById(R.id.input_size);
        int size = getInput().trim().length();
        updateView(size+"/T",inputSize);
    }

    private void onClickListener() {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int size = getInput().trim().length();
                updateView(size+"/T",inputSize);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initNer() {
        MLRemoteNerSetting setting = new MLRemoteNerSetting.Factory()
                .setSourceLangCode("zh")
                .create();
        ner = MLNerFactory.getInstance().getRemoteNer(setting);
    }

    private String getInput(){
        return  input.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ner_back:
                TextNerActivity.this.finish();
                break;
            case R.id.btn_ner:
                TextNerActivity.this.doNer();
                break;
            case R.id.delete_ner_text:
                TextNerActivity.this.input.setText("");
                break;
            default:
                break;
        }
    }

    private void doNer() {
        ner.asyncEntityExtract(getInput().toString().trim()).addOnSuccessListener(new OnSuccessListener<RemoteNerResultItem[]>() {
            @Override
            public void onSuccess(RemoteNerResultItem[] remoteNerResults) {
                showToast("onSuccess");
                if(remoteNerResults != null){
                    StringBuffer buffer = new StringBuffer();
                    for (RemoteNerResultItem item: remoteNerResults) {
                        buffer.append("[").append("entityWord："+item.getEntity()+" entityType："+item.getType()+" startSpan："+item.getMention()).append("]").append(";").append("\n");
                    }
                    updateView(buffer.toString(),nerOutput);
                }else {
                    updateView("The recognition result is empty.",nerOutput);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                showToast("onFailure");
                try {
                    MLException mlException = (MLException) e;
                    String errorMessage = mlException.getMessage();
                    updateView(errorMessage,nerOutput);
                } catch (Exception error) {
                    updateView(error.getMessage(),nerOutput);
                }
            }
        });
    }

    private void updateView(final String text,TextView view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(text);
            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.ner != null) {
            this.ner.stop();
        }
    }
}
