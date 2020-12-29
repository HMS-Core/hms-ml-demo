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

package com.huawei.mlkit.sample.activity.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.ml.language.common.utils.LanguageCodeUtil;

/**
 * Function Description
 *
 * @since 2020-09-05
 */
public class TranslateSpinnerAdapter extends ArrayAdapter<String> {
    private Spinner spinner;
    ArrayList<String> downloadModelList;
    ArrayList<String> languageCodeList;
    public TranslateSpinnerAdapter(@NonNull Context context, ArrayList<String> codes,
        ArrayList<String> models, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.downloadModelList = models;
        this.languageCodeList = codes;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView tv = (TextView) view.findViewById(android.R.id.text1);
        tv.setTextColor(Color.argb(0xff, 0xbf, 0xbf, 0xbf));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view.findViewById(android.R.id.text1);
        // Set the initial text color to prevent color errors caused by view reuse.
        tv.setTextColor(Color.argb(0xff, 0xbf, 0xbf, 0xbf));
        if(languageCodeList != null && languageCodeList.size() > position) {
            String langCode = languageCodeList.get(position);
            if("AUTO".equalsIgnoreCase(langCode) || "En".equalsIgnoreCase(langCode)) {
                tv.setTextColor(Color.argb(0xff, 0x33, 0x33, 0x33));
            } else {
                if(downloadModelList.contains(langCode.toLowerCase())
                        || downloadModelList.contains(langCode)) {
                    tv.setTextColor(Color.argb(0xff, 0x33, 0x33, 0x33));
                } else {
                    tv.setTextColor(Color.argb(0xff, 0xbf, 0xbf, 0xbf));
                }

                if (LanguageCodeUtil.ZHHK.equalsIgnoreCase(langCode)
                        && downloadModelList.contains(LanguageCodeUtil.ZH.toLowerCase(Locale.ENGLISH))) {
                    tv.setTextColor(Color.argb(0xff, 0x33, 0x33, 0x33));
                }
            }
        }

        return view;
    }
}
