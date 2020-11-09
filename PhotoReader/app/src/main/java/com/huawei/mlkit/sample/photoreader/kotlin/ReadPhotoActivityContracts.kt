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
package com.huawei.mlkit.sample.photoreader.kotlin

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.huawei.mlkit.lensengine.BitmapUtils
import com.huawei.mlkit.sample.photoreader.Constant.EXTRA_IMAGE_PATH
import kotlinx.android.parcel.Parcelize

class ChoosePictureContract : ActivityResultContract<Unit, BitmapFactory?>() {
    override fun createIntent(context: Context, input: Unit?): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): BitmapFactory? {
        return intent?.data?.let {
            MediaDataBitmapFactory(it)
        }
    }
}

class TakePictureContract : ActivityResultContract<Unit, BitmapFactory?>() {
    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(context, CapturePhotoActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): BitmapFactory? {
        return intent?.getStringExtra(EXTRA_IMAGE_PATH)?.let {
            CameraPictureBitmapFactory(it)
        }
    }
}

interface BitmapFactory : Parcelable {
    fun buildBitmap(contentResolver: ContentResolver, width : Int, height : Int) : Bitmap
}

@Parcelize
private class CameraPictureBitmapFactory(private val picturePath : String) : BitmapFactory {
    override fun buildBitmap(contentResolver: ContentResolver, width: Int, height: Int): Bitmap {
        return BitmapUtils.loadFromFilePath(picturePath, width, height)
    }
}

@Parcelize
private class MediaDataBitmapFactory(private val fileUri : Uri) : BitmapFactory {
    override fun buildBitmap(contentResolver: ContentResolver, width: Int, height: Int): Bitmap {
        return BitmapUtils.loadFromMediaUri(contentResolver, fileUri, width, height);
    }
}