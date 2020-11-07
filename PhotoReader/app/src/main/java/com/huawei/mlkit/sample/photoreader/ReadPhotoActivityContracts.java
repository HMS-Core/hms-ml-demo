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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.mlkit.sample.photoreader.camera.CapturePhotoActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ReadPhotoActivityContracts {

    public static final String TAG = ReadPhotoActivityContracts.class.getCanonicalName();

    public static final String IMAGE_PATH_VALUE = "imagePath";

    static class ChoosePictureContract extends ActivityResultContract<Void, Uri> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            final Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            return intent;
        }

        @Nullable
        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            return intent != null ? intent.getData() : null;
        }
    }

    static class TakePictureContract extends ActivityResultContract<Void, Bitmap> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            return new Intent(context, CapturePhotoActivity.class);
        }

        @Override
        public Bitmap parseResult(int resultCode, @Nullable Intent intent) {
            if(intent == null) {
                return null;
            }

            final String path = intent.getStringExtra(IMAGE_PATH_VALUE);
            try {
                final FileInputStream fis = new FileInputStream(path);
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Camera image could not be found: " + e.getMessage());
                return null;
            }
        }
    }
}
