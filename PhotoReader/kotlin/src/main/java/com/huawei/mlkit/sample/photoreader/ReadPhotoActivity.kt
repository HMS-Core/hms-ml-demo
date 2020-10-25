/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.mlkit.sample.photoreader

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Pair
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLLocalTextSetting
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting
import com.huawei.hms.mlsdk.tts.*
import com.huawei.mlkit.lensengine.BitmapUtils
import com.huawei.mlkit.sample.photoreader.camera.CapturePhotoActivity
import com.huawei.mlkit.sample.photoreader.camera.CapturePhotoActivity.Companion.IMAGE_PATH_VALUE
import com.huawei.mlkit.sample.photoreader.util.*
import kotlinx.android.synthetic.main.activity_read_photo.*
import kotlinx.coroutines.launch
import java.io.FileInputStream

class ReadPhotoActivity : AppCompatActivity() {

    private val srcLanguage: String by lazy {
        intent.getStringExtra(EXTRA_SOURCE_LANGUAGE) ?: ML_CHINESE
    }
    private val dstLanguage: String by lazy {
        intent.getStringExtra(EXTRA_DESTINATION_LANGUAGE) ?: ML_ENGLISH
    }

    private val textAnalyzer: MLTextAnalyzer by lazy {
        val setting = MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .setLanguage(srcLanguage)
                .create()
        MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting)
    }

    private val remoteTranslator by lazy {
        val setting = MLRemoteTranslateSetting.Factory()
                .setSourceLangCode(srcLanguage)
                .setTargetLangCode(dstLanguage)
                .create()
        MLTranslatorFactory.getInstance().getRemoteTranslator(setting)
    }

    private val ttsEngine : MLTtsEngine by lazy {
        val language = if (dstLanguage == ML_CHINESE) MLTtsConstants.TTS_ZH_HANS else MLTtsConstants.TTS_EN_US
        val person = if (dstLanguage == ML_CHINESE) MLTtsConstants.TTS_SPEAKER_FEMALE_ZH else MLTtsConstants.TTS_SPEAKER_FEMALE_EN

        val mlConfigs = MLTtsConfig()
        mlConfigs.setLanguage(language)
                .setPerson(person)
                .setSpeed(1.0f)
                .volume = 1.0f

        val mlTtsEngine = MLTtsEngine(mlConfigs)
        val callback: MLTtsCallback = object : MLTtsCallback {
            override fun onError(taskId: String, err: MLTtsError) {
                Log.e(TAG, err.errorMsg)
            }

            override fun onWarn(taskId: String, warn: MLTtsWarn) {}

            override fun onRangeStart(taskId: String, start: Int, end: Int) {}

            override fun onAudioAvailable(s: String, mlTtsAudioFragment: MLTtsAudioFragment,
                                          i: Int, pair: Pair<Int, Int>, bundle: Bundle?) {}

            override fun onEvent(taskId: String, eventName: Int, bundle: Bundle?) {
                if (eventName == MLTtsConstants.EVENT_PLAY_STOP) {
                    if (bundle?.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED) != false) {
                        Toast.makeText(this@ReadPhotoActivity, R.string.read_finish, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        mlTtsEngine.setTtsCallback(callback)
        mlTtsEngine
    }

    private val pictureChooserRequest = (this as ComponentActivity).registerForActivityResult(ChoosePictureContract()) { uri: Uri? ->
        uri?.let {
            val bitmap = loadBitMap(it)
            processBitmap(bitmap)
        }
    }

    private val takePictureRequest = (this as ComponentActivity).registerForActivityResult(TakePictureContract()) { bitmap: Bitmap? ->
        bitmap?.let {
            processBitmap(it)
        }
    }

    private class ChoosePictureContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

    private class TakePictureContract : ActivityResultContract<Unit, Bitmap?>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            return Intent(context, CapturePhotoActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
            return intent?.getStringExtra(IMAGE_PATH_VALUE)?.let {
                val fis = FileInputStream(it)
                BitmapFactory.decodeStream(fis)
            }
        }
    }

    public override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.activity_read_photo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etActReadPhotoInput.hint = getString(R.string.recognised_text, srcLanguage)
        etActReadPhotoTranslated.hint = getString(R.string.translated_text, dstLanguage)
        initActions()
    }

    private fun initActions() {
        tvActReadPhotoSelectPicture.setOnClickListener {
            pictureChooserRequest.launch(Unit)
        }

        tvActReadPhotoTakePicture.setOnClickListener {
            takePictureRequest.launch(Unit)
        }

        tvActReadPhotoTranslate.setOnClickListener {
            val inputText = etActReadPhotoInput.text.toString().trim()
            if(inputText.isNotBlank()) {
                lifecycleScope.launch {
                    try {
                        setActionButtonEnabled(false)
                        val translated = remoteTranslator.asyncTranslate(inputText).await()
                        etActReadPhotoTranslated.setText(translated)
                        setActionButtonEnabled(true)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to translate", e)
                        setActionButtonEnabled(true)
                        Toast.makeText(this@ReadPhotoActivity, "Fail", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, R.string.no_text, Toast.LENGTH_SHORT).show()
            }
        }

        tvActReadPhotoRead.setOnClickListener {
            val translatedText = etActReadPhotoTranslated.text.toString().trim()
            if(translatedText.isNotBlank()) {
                Toast.makeText(this, R.string.read_start, Toast.LENGTH_SHORT).show()
                val ttsText = if(translatedText.length > ML_TTS_MAX_ALLOWED_CHAR_LENGTH) {
                    translatedText.substring(0, ML_TTS_MAX_ALLOWED_CHAR_LENGTH)
                } else {
                    translatedText
                }
                ttsEngine.speak(ttsText, MLTtsEngine.QUEUE_APPEND)
            } else {
                Toast.makeText(this, R.string.no_text_to_speak, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        ivActReadPhotoPreview.setImageBitmap(bitmap)
        lifecycleScope.launch {
            try {
                setActionButtonEnabled(false)
                val mlText = textAnalyzer.asyncAnalyseFrame(MLFrame.fromBitmap(bitmap)).await()
                val sourceText = mlText.stringValue
                etActReadPhotoInput.setText(sourceText)

                if(sourceText.isNotBlank()) {
                    val translated = remoteTranslator.asyncTranslate(sourceText.trim()).await()
                    etActReadPhotoTranslated.setText(translated)
                }

                setActionButtonEnabled(true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to process bitmap", e)
                setActionButtonEnabled(true)
                Toast.makeText(this@ReadPhotoActivity, "Fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBitMap(imageUri: Uri) : Bitmap {
        val targetWidth = svActReadPhotoPaneContainer.width
        val targetHeight = svActReadPhotoPaneContainer.height
        return BitmapUtils.loadFromPath(contentResolver, imageUri, targetWidth, targetHeight)
    }

    private fun setActionButtonEnabled(enabled: Boolean) {
        tvActReadPhotoSelectPicture.isEnabled = enabled
        tvActReadPhotoTakePicture.isEnabled = enabled
        tvActReadPhotoTranslate.isEnabled = enabled
        tvActReadPhotoRead.isEnabled = enabled
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

    override fun onStop() {
        super.onStop()
        ttsEngine.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        textAnalyzer.release()
        ttsEngine.shutdown()
    }

    companion object {
        fun createIntent(context: Context, translationMode: MainActivity.TranslationMode) : Intent {
            val intent = Intent(context, ReadPhotoActivity::class.java)
            when (translationMode) {
                MainActivity.TranslationMode.EN_ZH -> {
                    intent.putExtra(EXTRA_SOURCE_LANGUAGE, ML_ENGLISH)
                    intent.putExtra(EXTRA_DESTINATION_LANGUAGE, ML_CHINESE)
                }
                MainActivity.TranslationMode.ZH_EN -> {
                    intent.putExtra(EXTRA_SOURCE_LANGUAGE, ML_CHINESE)
                    intent.putExtra(EXTRA_DESTINATION_LANGUAGE, ML_ENGLISH)
                }
            }
            return intent
        }

        private const val TAG = "ReadPhotoActivity"
        private const val EXTRA_SOURCE_LANGUAGE = "EXTRA_SOURCE_LANGUAGE"
        private const val EXTRA_DESTINATION_LANGUAGE = "EXTRA_DESTINATION_LANGUAGE"
    }
}