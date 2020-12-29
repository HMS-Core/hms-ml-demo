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

package com.huawei.mlkit.sample.activity.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.util.CommonUtils;

import androidx.annotation.NonNull;

/**
 * Custom dialog for recording
 *
 * @since 2020-03-15
 */

public class RecordDialog extends Dialog {
    public static final int TYPE_WITH_COMPLETE_BUTTON = 1;

    public static final int TYPE_WITHOUT_COMPLETE_BUTTON = 2;

    private ImageView mVolumeLevelIcon;

    private Context mContext;

    private Button btnComplete;

    private int mType;

    public RecordDialog(@NonNull Context context, int type) {
        this(context, R.style.MyDialogStyle, type);
    }

    private OnCompleteListener completeListener;
    private OnBackPressedListener backPressedListener;

    private RecordDialog(@NonNull Context context, int themeResId, int type) {
        super(context, themeResId);
        mContext = context;
        mType = type;
        initView();
    }

    private void initView() {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            @SuppressLint("InflateParams")
            View view = inflater.inflate(R.layout.dialog_record, null);
            btnComplete = view.findViewById(R.id.btn_stop_record);
            if (mType == TYPE_WITHOUT_COMPLETE_BUTTON) {
                btnComplete.setVisibility(View.GONE);
            } else {
                btnComplete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (completeListener != null) {
                            completeListener.onComplete();
                        }
                    }
                });
            }
            window.setContentView(view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = (int) CommonUtils.dp2px(mContext, 25);
            params.leftMargin  = (int) CommonUtils.dp2px(mContext, 20);
            params.rightMargin = (int) CommonUtils.dp2px(mContext, 20);
            view.setLayoutParams(params);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            setCanceledOnTouchOutside(false);
            mVolumeLevelIcon = view.findViewById(R.id.id_recorder_dialog_voice);
            this.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        if (backPressedListener != null) {
                            backPressedListener.onBackPressed();
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.completeListener = listener;
    }

    public void setOnBackPressedListener(OnBackPressedListener listener){
        this.backPressedListener = listener;
    }

    public interface OnCompleteListener {
        /**
         *  Complete
         */
        void onComplete();
    }

    public interface OnBackPressedListener {
        /**
         *  back
         */
        void onBackPressed();
    }

    private static int dip2px(Context context) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) 48 * scale + 0.5f);
    }

    /**
     * update volume image by level
     *
     * @param level level
     */
    public void updateVolumeLevel(int level) {
        if (isShowing()) {
            int resId = mContext.getResources().getIdentifier("volume_" + level, "mipmap", mContext.getPackageName());
            mVolumeLevelIcon.setImageResource(resId);
        }
    }
}
