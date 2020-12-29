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
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.translate.LocalTranslateActivity;


/**
 * Function Description
 *
 * @since 2020-09-04
 */
public class TranslateDownloadAdapter extends BaseAdapter {

    ArrayList<String> languageList;
    ArrayList<String> downloadModelList;
    ArrayList<String> languageCodeList;
    LocalTranslateActivity.CallBcak callBcak;
    HashMap<String, String> downloadMap;
    Context context;

    public TranslateDownloadAdapter(Context context, ArrayList<String> languages, ArrayList<String> codes,
        ArrayList<String> models, HashMap<String, String> downloadMap, LocalTranslateActivity.CallBcak callBcak){
        this.languageList = languages;
        this.downloadModelList = models;
        this.languageCodeList = codes;
        this.callBcak = callBcak;
        this.context = context;
        this.downloadMap = downloadMap;
    }

    @Override
    public int getCount() {
        return languageList == null ? 0 : languageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.translate_listview_item, null);
        }

        TextView tvLanguage = convertView.findViewById(R.id.language);
        TextView tvDownload = convertView.findViewById(R.id.has_download);
        ImageView ivDelete = convertView.findViewById(R.id.delete_model);
        ImageView ivDownLoad = convertView.findViewById(R.id.download);

        final String languageCode = languageCodeList.get(position);
        if (downloadModelList.contains(languageCode)) {
            tvLanguage.setText(languageList.get(position));
            tvDownload.setText("(" + context.getString(R.string.downloaded) + ")");
            tvLanguage.setTextColor(Color.argb(0xff, 0x33, 0x33, 0x33));
            tvDownload.setTextColor(Color.argb(0xff, 0x33, 0x33, 0x33));
            ivDownLoad.setSelected(true);
            ivDelete.setVisibility(View.VISIBLE);
            tvDownload.setVisibility(View.VISIBLE);
        } else {
            tvLanguage.setText(languageList.get(position));
            tvLanguage.setTextColor(Color.argb(0xff, 0xbf, 0xbf, 0xbf));
            ivDownLoad.setSelected(false);
            ivDelete.setVisibility(View.INVISIBLE);
            tvDownload.setVisibility(View.INVISIBLE);
        }

        Log.e("LocalTranslateActivity", "downloadMap:" + downloadMap.size() + "  " + languageCode);
        String progress =  downloadMap.get(languageCode);
        if(progress != null){
            tvDownload.setVisibility(View.VISIBLE);
            tvDownload.setText("(" + progress + ")");
        }

        ivDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!downloadModelList.contains(languageCode)) {
                    if (callBcak != null) {
                        callBcak.download(languageCode);
                    }
                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBcak != null) {
                    callBcak.delete(languageCode);
                }
            }
        });

        return convertView;
    }
}
