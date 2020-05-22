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

package com.mlkit.sample.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.mlkit.sample.R;

import com.mlkit.sample.activity.adapter.imgseg.MyGridViewAdapter;
import com.mlkit.sample.activity.TakePhotoActivity;
import com.mlkit.sample.activity.entity.Entity;
import com.mlkit.sample.util.Constant;
import com.mlkit.sample.util.SharedPreferencesUtil;

import java.util.ArrayList;

public class BackgroundChangeFragment extends Fragment {
    private GridView mGridView;
    private ArrayList<Entity> mDataList;
    private MyGridViewAdapter mAdapter;
    private ImageView mTakePhoto;
    private int mIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bg_change, container, false);
        this.initData();
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        this.mTakePhoto = view.findViewById(R.id.take_photo);
        this.mGridView = view.findViewById(R.id.gridview);
        this.mAdapter = new MyGridViewAdapter(this.mDataList, this.getContext());
        this.mGridView.setAdapter(this.mAdapter);
        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (BackgroundChangeFragment.this.mDataList.get(position).isSelected()) {
                    BackgroundChangeFragment.this.mDataList.get(position).setSelected(false);
                } else {
                    BackgroundChangeFragment.this.mDataList.get(position).setSelected(true);
                }
                BackgroundChangeFragment.this.mIndex = -1;
                for (int i = 0; i < BackgroundChangeFragment.this.mDataList.size(); i++) {
                    if (i != position) {
                        BackgroundChangeFragment.this.mDataList.get(i).setSelected(false);
                    }
                    if (BackgroundChangeFragment.this.mDataList.get(i).isSelected()) {
                        BackgroundChangeFragment.this.mIndex = i;
                    }
                }
                SharedPreferencesUtil.getInstance(BackgroundChangeFragment.this.getContext()).putIntValue(Constant.VALUE_KEY, BackgroundChangeFragment.this.mIndex);
                /**
                 * Notify the adapter that the bound data has changed and the view should be refreshed.
                 */
                BackgroundChangeFragment.this.mAdapter.notifyDataSetChanged();
            }
        });
        this.mTakePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BackgroundChangeFragment.this.getActivity(), TakePhotoActivity.class);
                intent.putExtra(Constant.VALUE_KEY, BackgroundChangeFragment.this.mIndex);
                BackgroundChangeFragment.this.startActivity(intent);
            }
        });
    }

    private void initData() {
        this.mDataList = new ArrayList<Entity>();
        Entity entity;
        int saveIndex = SharedPreferencesUtil.getInstance(this.getContext()).getIntValue(Constant.VALUE_KEY);
        this.mIndex = saveIndex;
        for (int i = 0; i < Constant.IMAGES.length; i++) {
            if (i == saveIndex) {
                entity = new Entity(Constant.IMAGES[i], true);
            } else {
                entity = new Entity(Constant.IMAGES[i], false);
            }
            this.mDataList.add(entity);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferencesUtil.getInstance(this.getContext()).putIntValue(Constant.VALUE_KEY, 0);
    }
}
