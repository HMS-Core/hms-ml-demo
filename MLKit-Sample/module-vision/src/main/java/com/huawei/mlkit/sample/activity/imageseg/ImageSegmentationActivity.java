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

package com.huawei.mlkit.sample.activity.imageseg;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.activity.BaseActivity;
import com.huawei.mlkit.sample.activity.adapter.TabFragmentAdapter;
import com.huawei.mlkit.sample.activity.fragment.BackgroundChangeFragment;
import com.huawei.mlkit.sample.activity.fragment.CaptureImageFragment;
import com.huawei.mlkit.sample.activity.fragment.SliceImageFragment;
import com.huawei.mlkit.sample.activity.fragment.HairImageFragment;

import java.util.ArrayList;
import java.util.List;

public class ImageSegmentationActivity extends BaseActivity implements View.OnClickListener {

    private List<Fragment> mFragmentList;
    private TextView mBgChangeTv;
    private TextView mCaptureImgTv;
    private TextView mSliceTv;
    private TextView mHairTv;
    private ViewPager mViewPager;
    private View mBgChangeLine;
    private View mCaptureImgLine;
    private View mSliceLine;
    private View mHairLine;
    private TabFragmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_segmentation);
        this.initView();
        this.setStatusBar();
        this.setStatusBarFontColor();
    }

    private void initView() {
        this.mBgChangeTv = this.findViewById(R.id.fragment_one);
        this.mCaptureImgTv = this.findViewById(R.id.fragment_two);
        this.mSliceTv = this.findViewById(R.id.fragment_three);
        this.mHairTv = this.findViewById(R.id.fragment_four);
        this.mBgChangeLine = this.findViewById(R.id.line_one);
        this.mCaptureImgLine = this.findViewById(R.id.line_two);
        this.mSliceLine = this.findViewById(R.id.line_three);
        this.mHairLine = this.findViewById(R.id.line_four);
        this.mViewPager = this.findViewById(R.id.view_pager);

        this.findViewById(R.id.back).setOnClickListener(this);
        this.mBgChangeTv.setOnClickListener(this);
        this.mCaptureImgTv.setOnClickListener(this);
        this.mSliceTv.setOnClickListener(this);
        this.mHairTv.setOnClickListener(this);
        this.mViewPager.setOnPageChangeListener(new PagerChangeListener());

        this.mFragmentList = new ArrayList<>();
        this.mFragmentList.add(new BackgroundChangeFragment());
        this.mFragmentList.add(new CaptureImageFragment());
        this.mFragmentList.add(new SliceImageFragment());
        this.mFragmentList.add(new HairImageFragment());

        this.mAdapter = new TabFragmentAdapter(this.getSupportFragmentManager(), this.mFragmentList);
        this.mViewPager.setAdapter(this.mAdapter);
        this.mViewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_one:
                this.mViewPager.setCurrentItem(0);
                this.setBgChangeView();
                break;
            case R.id.fragment_two:
                this.mViewPager.setCurrentItem(1);
                this.setCaptureImageView();
                break;
            case R.id.fragment_three:
                this.mViewPager.setCurrentItem(2);
                this.setSliceImageView();
                break;
            case R.id.fragment_four:
                this.mViewPager.setCurrentItem(3);
                this.setHairImageView();
                break;
            case R.id.back:
                this.finish();
                break;
            default:
                break;
        }
    }

    private void setBgChangeView() {
        this.mBgChangeTv.setTextColor(this.getResources().getColor(R.color.button_background));
        this.mCaptureImgTv.setTextColor(Color.BLACK);
        this.mSliceTv.setTextColor(Color.BLACK);
        this.mHairTv.setTextColor(Color.BLACK);
        this.mBgChangeLine.setVisibility(View.VISIBLE);
        this.mCaptureImgLine.setVisibility(View.GONE);
        this.mSliceLine.setVisibility(View.GONE);
        this.mHairLine.setVisibility(View.GONE);
    }

    private void setCaptureImageView() {
        this.mBgChangeTv.setTextColor(Color.BLACK);
        this.mCaptureImgTv.setTextColor(this.getResources().getColor(R.color.button_background));
        this.mSliceTv.setTextColor(Color.BLACK);
        this.mHairTv.setTextColor(Color.BLACK);
        this.mBgChangeLine.setVisibility(View.GONE);
        this.mCaptureImgLine.setVisibility(View.VISIBLE);
        this.mSliceLine.setVisibility(View.GONE);
        this.mHairLine.setVisibility(View.GONE);
    }

    private void setSliceImageView() {
        this.mBgChangeTv.setTextColor(Color.BLACK);
        this.mCaptureImgTv.setTextColor(Color.BLACK);
        this.mSliceTv.setTextColor(this.getResources().getColor(R.color.button_background));
        this.mHairTv.setTextColor(Color.BLACK);
        this.mBgChangeLine.setVisibility(View.GONE);
        this.mCaptureImgLine.setVisibility(View.GONE);
        this.mSliceLine.setVisibility(View.VISIBLE);
        this.mHairLine.setVisibility(View.GONE);
    }

    private void setHairImageView() {
        this.mBgChangeTv.setTextColor(Color.BLACK);
        this.mCaptureImgTv.setTextColor(Color.BLACK);
        this.mSliceTv.setTextColor(Color.BLACK);
        this.mHairTv.setTextColor(this.getResources().getColor(R.color.button_background));
        this.mBgChangeLine.setVisibility(View.GONE);
        this.mCaptureImgLine.setVisibility(View.GONE);
        this.mSliceLine.setVisibility(View.GONE);
        this.mHairLine.setVisibility(View.VISIBLE);
    }

    /**
     * Set a ViewPager listening event. When the ViewPager is swiped left or right, the menu bar selected state changes accordingly.
     *
     */
    public class PagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    ImageSegmentationActivity.this.setBgChangeView();
                    break;
                case 1:
                    ImageSegmentationActivity.this.setCaptureImageView();
                    break;
                case 2:
                    ImageSegmentationActivity.this.setSliceImageView();
                    break;
                case 3:
                    ImageSegmentationActivity.this.setHairImageView();
                    break;
                default:
                    break;
            }
        }
    }

    public void onBackPressed(View view) {
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
