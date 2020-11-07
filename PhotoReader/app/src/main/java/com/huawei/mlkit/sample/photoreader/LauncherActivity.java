/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.mlkit.sample.photoreader;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.mlkit.sample.photoreader.databinding.ActivityLauncherBinding;
import com.huawei.mlkit.sample.photoreader.java.MainActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityLauncherBinding binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnActLauncherJava.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.btnActLauncherKotlin.setOnClickListener(v -> {
            startActivity(new Intent(this, com.huawei.mlkit.sample.photoreader.kotlin.MainActivity.class));
        });
    }
}
