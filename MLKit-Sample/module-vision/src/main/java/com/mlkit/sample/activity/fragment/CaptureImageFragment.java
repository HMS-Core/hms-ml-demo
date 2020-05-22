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
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.mlkit.sample.R;
import com.mlkit.sample.activity.StillCutPhotoActivity;

public class CaptureImageFragment extends Fragment {
    private ImageView mLoadPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture_image, container, false);
        this.mLoadPhoto = view.findViewById(R.id.center_image_slice);
        this.mLoadPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CaptureImageFragment.this.startActivity(new Intent(CaptureImageFragment.this.getContext(), StillCutPhotoActivity.class));
            }
        });
        return view;
    }
}
