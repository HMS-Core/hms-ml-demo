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
package com.huawei.mlkit.sample.photoreader

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.huawei.mlkit.sample.photoreader.util.getEnumExtra
import com.huawei.mlkit.sample.photoreader.util.putEnum
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var translationMode : TranslationMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultTranslationMode = if(Locale.getDefault().isO3Country == "CHN") {
            TranslationMode.ZH_EN
        } else {
            TranslationMode.EN_ZH
        }
        translationMode = savedInstanceState?.getEnumExtra(EXTRA_TRANSLATION_MODE, defaultTranslationMode) ?: defaultTranslationMode

        btnActMainStartTranslate.isEnabled = allPermissionsGranted()
        btnActMainStartTranslate.setOnClickListener {
            startActivity(ReadPhotoActivity.createIntent(this, translationMode))
        }

        updateSrcDestLanguage()
        buttonSwitchLang.setOnClickListener {
            translationMode = if(translationMode == TranslationMode.ZH_EN) {
                TranslationMode.EN_ZH
            } else {
                TranslationMode.ZH_EN
            }
            updateSrcDestLanguage()
        }

        if (!allPermissionsGranted()) {
            askForPermissions()
        }
    }

    private fun updateSrcDestLanguage() {
        when (translationMode) {
            TranslationMode.EN_ZH -> {
                tvActMainSrcLanguage.text = getString(R.string.english)
                tvActMainDstLanguage.text = getString(R.string.chinese)
            }
            TranslationMode.ZH_EN -> {
                tvActMainSrcLanguage.text = getString(R.string.chinese)
                tvActMainDstLanguage.text = getString(R.string.english)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putEnum(EXTRA_TRANSLATION_MODE, translationMode)
    }

    private fun askForPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions.filter { !isPermissionGranted(it) }.toTypedArray(), PERMISSION_REQUESTS)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionsGranted(): Boolean {
        return requiredPermissions.all { isPermissionGranted(it) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQUESTS) {
            return
        }

        btnActMainStartTranslate.isEnabled = allPermissionsGranted()

        var isNeedShowDialog = false
        for (i in permissions.indices) {
            if (permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[i] != PackageManager.PERMISSION_GRANTED
                    || permissions[i] == Manifest.permission.CAMERA && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isNeedShowDialog = true
            }
        }
        if (isNeedShowDialog && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            val dialog = AlertDialog.Builder(this)
                    .setMessage(getString(R.string.camera_permission_rationale))
                    .setPositiveButton(getString(R.string.settings)) { _: DialogInterface, _: Int ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> finish() }.create()
            dialog.show()
        }
    }

    enum class TranslationMode {
        EN_ZH,
        ZH_EN
    }

    companion object {
        private const val EXTRA_TRANSLATION_MODE = "EXTRA_TRANSLATION_MODE"
        private const val PERMISSION_REQUESTS = 1
    }
}