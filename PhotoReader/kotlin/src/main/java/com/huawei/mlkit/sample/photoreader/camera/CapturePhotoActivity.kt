/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.mlkit.sample.photoreader.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.huawei.mlkit.lensengine.CameraConfiguration
import com.huawei.mlkit.lensengine.LensEngine
import com.huawei.mlkit.sample.photoreader.R
import kotlinx.android.synthetic.main.activity_capture_photo.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CapturePhotoActivity : AppCompatActivity() {

    private val lensEngine: LensEngine by lazy {
        val cameraConfiguration = CameraConfiguration()
        cameraConfiguration.setCameraFacingBack(CameraConfiguration.CAMERA_FACING_BACK)
        LensEngine(this, cameraConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_photo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ivActCapturePhotoTakePhoto.setOnClickListener { toTakePhoto() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        try {
            lepActCapturePhoto.start(lensEngine, false)
        } catch (e: IOException) {
            Log.e(TAG, "Unable to start lensEngine.", e)
            lensEngine.release()
        }
    }

    override fun onPause() {
        super.onPause()
        lepActCapturePhoto.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        lensEngine.release()
    }

    private fun toTakePhoto() {
        lensEngine.takePicture { data, _ ->
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            try {
                val filePath = saveBitmapToDisk(bitmap)
                val intent = Intent()
                intent.putExtra(IMAGE_PATH_VALUE, filePath)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Save bitmap failed: " + e.message)
            }
        }
    }

    private fun saveBitmapToDisk(bitmap: Bitmap): String {
        val storePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + "PhotoTranslate"
        val appDir = File(storePath)
        if (!appDir.exists()) {
            val res = appDir.mkdirs()
            if (!res) {
                throw RuntimeException("Create directory for image failed")
            }
        }
        val file = File(appDir, System.currentTimeMillis().toString() + ".jpg")
        val fos = FileOutputStream(file)
        fos.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
        }
        return file.canonicalPath
    }

    companion object {
        const val IMAGE_PATH_VALUE = "imagePath"

        private const val TAG = "CapturePhotoActivity"
    }
}