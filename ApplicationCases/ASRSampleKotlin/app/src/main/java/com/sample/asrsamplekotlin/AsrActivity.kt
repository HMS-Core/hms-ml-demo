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

package com.sample.asrsamplekotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.common.MLApplication
import com.sample.asrsamplekotlin.databinding.ActivityAsrBinding

class AsrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsrBinding
    private lateinit var asrViewModel: AsrViewModel
    private var isPermissionGranted: Boolean = false
    private var  API_KEY : String  = "client/api_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_asr)
        binding.lifecycleOwner = this
        asrViewModel = ViewModelProvider(this).get(AsrViewModel::class.java)
        binding.asrViewModel = asrViewModel
        supportActionBar?.title = getString(R.string.automatic_speech_recognition)
        asrViewModel.speech.observe(this, Observer {
            if(isPermissionGranted)
                startASR()
        })

        val permission = arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO)
        ActivityCompat.requestPermissions(this, permission,1)
    }

    private fun setApiKey(){
        val  config  = AGConnectServicesConfig.fromContext(getApplication())
        MLApplication.getInstance().setApiKey(config.getString(API_KEY))
    }

    private fun startASR() {
        val intent = Intent(this, MLAsrCaptureActivity::class.java)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, "en-US")
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            when (resultCode) {
                MLAsrCaptureConstants.ASR_SUCCESS -> if (data != null) {
                    val bundle = data.extras
                    if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                        val text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT).toString()
                        Toast.makeText(this, text,Toast.LENGTH_LONG).show()
                    }
                }
                MLAsrCaptureConstants.ASR_FAILURE -> if (data != null) {
                    val bundle = data.extras
                    if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                        val errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE)
                        Toast.makeText(this, "Error Code $errorCode",Toast.LENGTH_LONG).show()

                    }
                    if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                        val errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)
                        Toast.makeText(this, "Error Code $errorMsg",Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    Toast.makeText(this, "Failed to get data",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                    isPermissionGranted = true
                } else {
                    // permission denied,
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}
