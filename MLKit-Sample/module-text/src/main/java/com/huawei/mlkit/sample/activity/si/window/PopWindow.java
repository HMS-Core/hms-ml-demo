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

package com.huawei.mlkit.sample.activity.si.window;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.si.adapter.PopAdapter;
import com.huawei.mlkit.sample.activity.si.entry.ItemBean;

import java.util.ArrayList;
import java.util.List;

import static com.huawei.hms.mlsdk.si.speech.MLSimultaneousInterpretationConstants.EN_FEMALE_VOICE;
import static com.huawei.hms.mlsdk.si.speech.MLSimultaneousInterpretationConstants.EN_MALE_VOICE;
import static com.huawei.hms.mlsdk.si.speech.MLSimultaneousInterpretationConstants.ZH_FEMALE_VOICE;
import static com.huawei.hms.mlsdk.si.speech.MLSimultaneousInterpretationConstants.ZH_MALE_VOICE;

/**
 * PopWindow
 */
public class PopWindow extends PopupWindow implements AdapterView.OnItemClickListener {
    private static final String TAG = PopWindow.class.getSimpleName();
    private PopAdapter popAdapter;
    private View mPopWindowLayout;
    private Activity mActivity;
    private ListView listView;
    private TextView btnCancle;
    private List<ItemBean> list = new ArrayList<>();
    private ItemChangeListerer itemChangeListener;
    private ArrayList<ItemBean> contentList;
    private ArrayList<ItemBean> voiceList;
    private int type = 0;


    private String[] timbreZhArray = {"", ZH_MALE_VOICE, ZH_FEMALE_VOICE};
    private String[] timbreEnArray = {"", EN_MALE_VOICE, EN_FEMALE_VOICE};

    public PopWindow(Activity activity) {
        mActivity = activity;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopWindowLayout = inflater.inflate(R.layout.popup_layout, null);
        this.setContentView(mPopWindowLayout);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);
        this.setTouchable(true);

        listView = mPopWindowLayout.findViewById(R.id.listView);
        btnCancle = mPopWindowLayout.findViewById(R.id.btn_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initData();
        initItemClick();
    }

    private void initData() {
        setInitContentList();
        voiceList = new ArrayList<>();
        String notBroadcastInit = mActivity.getResources().getString(R.string.notBroadcast);
        String englishMaleVoiceInit = mActivity.getResources().getString(R.string.englishMaleVoice);
        String englishFemaleVoiceInit = mActivity.getResources().getString(R.string.englishFemaleVoice);
        String[] strVoice = new String[]{notBroadcastInit, englishMaleVoiceInit, englishFemaleVoiceInit};
        for (int i = 0; i < strVoice.length; i++) {
            ItemBean itemBean = new ItemBean();
            itemBean.setName(strVoice[i]);
            itemBean.setId(timbreEnArray[i]);
            if (i == 0) {
                itemBean.setChecked(true);
            }
            voiceList.add(itemBean);
        }
    }

    private void setInitContentList() {
        contentList = new ArrayList<>();
        String asrText = mActivity.getResources().getString(R.string.asrText);
        String asrTextAndTranslation = mActivity.getResources().getString(R.string.asrTextAndTranslation);
        String[] str = new String[]{asrText, asrTextAndTranslation};
        for (int i = 0; i < str.length; i++) {
            ItemBean itemBean = new ItemBean();
            itemBean.setName(str[i]);
            itemBean.setId(i + "");
            if (i == 0) {
                itemBean.setChecked(true);
            }
            contentList.add(itemBean);
        }
    }

    private void initItemClick() {
        listView.setOnItemClickListener(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        backgroundAlpha(1f);
    }

    public void show() {
        popAdapter = new PopAdapter(mActivity, contentList);
        listView.setAdapter(popAdapter);
        backgroundAlpha(0.6f);
        showAtLocation(mPopWindowLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void showVoice() {
        popAdapter = new PopAdapter(mActivity, voiceList);
        listView.setAdapter(popAdapter);
        backgroundAlpha(0.6f);
        showAtLocation(mPopWindowLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        mActivity.getWindow().setAttributes(lp);
    }

    public void setItemChangeListener(ItemChangeListerer itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick: ");
        if (itemChangeListener != null) {
            itemChangeListener.itemChange(type == 0 ? contentList.get(position) : voiceList.get(position));
            dismiss();
            if (type == 0) {
                for (int i = 0; i < contentList.size(); i++) {
                    contentList.get(i).setChecked(position == i);
                }
            } else {
                for (int i = 0; i < voiceList.size(); i++) {
                    voiceList.get(i).setChecked(position == i);
                }
            }
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setLanguageType(String languageType) {
        setInitContentList();
        if ("zh".equals(languageType)) {
            voiceList = new ArrayList<>();
            String notBroadcastZh = mActivity.getResources().getString(R.string.notBroadcast);
            String chineseMaleVoiceZh = mActivity.getResources().getString(R.string.chineseMaleVoice);
            String chineseFemaleVoiceZh = mActivity.getResources().getString(R.string.chineseFemaleVoice);
            String[] strVoiceZh = new String[]{notBroadcastZh, chineseMaleVoiceZh, chineseFemaleVoiceZh};
            for (int i = 0; i < strVoiceZh.length; i++) {
                ItemBean itemBean = new ItemBean();
                itemBean.setName(strVoiceZh[i]);
                itemBean.setId(timbreZhArray[i]);
                if (i == 0) {
                    itemBean.setChecked(true);
                }
                voiceList.add(itemBean);
            }
        } else {
            voiceList = new ArrayList<>();
            String notBroadcastEn = mActivity.getResources().getString(R.string.notBroadcast);
            String englishMaleVoiceEn = mActivity.getResources().getString(R.string.englishMaleVoice);
            String englishFemaleVoiceEn = mActivity.getResources().getString(R.string.englishFemaleVoice);
            String[] strVoiceEn = new String[]{notBroadcastEn, englishMaleVoiceEn, englishFemaleVoiceEn};
            for (int i = 0; i < strVoiceEn.length; i++) {
                ItemBean itemBean = new ItemBean();
                itemBean.setName(strVoiceEn[i]);
                itemBean.setId(timbreEnArray[i]);
                if (i == 0) {
                    itemBean.setChecked(true);
                }
                voiceList.add(itemBean);
            }
        }
    }

    public interface ItemChangeListerer {
        void itemChange(ItemBean itemBean);
    }
}
