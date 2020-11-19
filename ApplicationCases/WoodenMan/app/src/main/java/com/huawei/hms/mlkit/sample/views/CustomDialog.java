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

package com.huawei.hms.mlkit.sample.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.huawei.hms.mlkit.sample.cn.R;

public class CustomDialog extends Dialog implements View.OnClickListener {
	private Context mContext;

	private Button mCancelButton;

	private Button mSureButton;

	private EditText mEditText;

	private static float sThresholdValue = 0.6f;

	public CustomDialog(@NonNull Context context) {
		this(context, R.style.Theme_AudioDialog);
	}

	public CustomDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
		mContext = context;
		initView();
	}

	public void initView() {
        getWindow().setGravity(Gravity.BOTTOM);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_recorder, null);
		setContentView(view);
		mCancelButton = view.findViewById(R.id.btn_cancle);
		mSureButton = view.findViewById(R.id.btn_sure);
		mEditText = view.findViewById(R.id.float_threshold_value);
		mEditText.setFocusable(true);
		mEditText.requestFocus();
		mEditText.setText(String.valueOf(sThresholdValue));
		mEditText.setSelection(String.valueOf(sThresholdValue).length());
		mCancelButton.setOnClickListener(this);
		mSureButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
        if (view.getId() == R.id.btn_cancle) {
            dismiss();
        } else if (view.getId() == R.id.btn_sure) {
	        String number = mEditText.getText().toString();
	        float result = Float.valueOf(number);
	        if (result < 0 || result > 1f) {
		        Toast.makeText(mContext, "Invalid data, please input again.", Toast.LENGTH_SHORT).show();
	        } else {
                sThresholdValue = result;
                dismiss();
	        }
        }
	}

	public static float getThresholdValue() {
		return sThresholdValue;
	}

	public static void setThresholdValue(float value) {
		CustomDialog.sThresholdValue = value;
	}
}

