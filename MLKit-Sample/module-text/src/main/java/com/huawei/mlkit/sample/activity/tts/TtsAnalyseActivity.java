/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.mlkit.sample.activity.tts;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.huawei.mlkit.sample.R;
import com.huawei.mlkit.sample.fragment.OfflineModeFragment;
import com.huawei.mlkit.sample.fragment.OnlineModeFragment;

public class TtsAnalyseActivity extends AppCompatActivity {
    private RadioGroup mRadioGroup;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_tts);
        mRadioGroup = findViewById(R.id.radioGroup);
        mViewPager = findViewById(R.id.viewPager);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initView();
    }

    private void initView() {
        List<Fragment> fragments = new ArrayList<>();
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        fragments.add(OnlineModeFragment.newInstance());
        mRadioGroup.addView(createItem(0, getString(R.string.online_tts)), layoutParams);
        fragments.add(OfflineModeFragment.newInstance());
        mRadioGroup.addView(createItem(1, getString(R.string.offline_tts)), layoutParams);

        Adapter adapter = new Adapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(adapter.getCount());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mRadioGroup.check(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                mViewPager.setCurrentItem(checkedId, true);
            }
        });
    }

    private static class Adapter extends FragmentStatePagerAdapter {

        private List<Fragment> itemlist;

        public Adapter(FragmentManager fm, List<Fragment> itemlist) {
            super(fm);
            this.itemlist = itemlist;
        }

        @Override
        public Fragment getItem(int position) {

            return itemlist.get(position);

        }

        @Override
        public int getCount() {
            return itemlist.size();
        }
    }

    private RadioButton createItem(int id, String title) {
        RadioButton rb = new RadioButton(this);
        rb.setId(id);
        if (id == 0) {
            rb.setChecked(true);
        }
        rb.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.transparent)));
        rb.setButtonDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.transparent)));
        rb.setGravity(Gravity.CENTER);
        rb.setText(title);
        rb.setTextSize(18);
        rb.setTextColor(ContextCompat.getColorStateList(this, R.color.rb_bottom_text));
        rb.setGravity(Gravity.CENTER);
        return rb;
    }
}
