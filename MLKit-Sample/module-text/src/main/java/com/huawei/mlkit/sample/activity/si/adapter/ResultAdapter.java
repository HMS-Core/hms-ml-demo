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

package com.huawei.mlkit.sample.activity.si.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.si.entry.ResultBean;

import java.util.List;

/**
 * ResultAdapter
 */
public class ResultAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<ResultBean> mDatas = null;

    public ResultAdapter(Context mContext, List<ResultBean> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return this.mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh;
        if (view == null) {
            vh = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.result_item, null);
            vh.tv = (TextView) view.findViewById(R.id.originTV);
            vh.translateTv = (TextView) view.findViewById(R.id.translateTV);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        vh.tv.setText(mDatas.get(position).getText());
        vh.translateTv.setText(mDatas.get(position).getTranslateText());

        return view;
    }

    /**
     * ViewHolder
     */
    public final class ViewHolder {
        /**
         * tv
         */
        public TextView tv;

        /**
         * translateTv
         */
        public TextView translateTv;
    }

}
