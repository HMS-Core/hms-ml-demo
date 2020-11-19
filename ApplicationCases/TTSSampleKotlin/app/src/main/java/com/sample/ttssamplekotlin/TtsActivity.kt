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

package com.sample.ttssamplekotlin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.tts.*
import com.sample.ttssamplekotlin.databinding.ActivityTtsBinding
import kotlinx.android.synthetic.main.activity_tts.*


class TtsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTtsBinding
    private lateinit var ttsViewModel: TtsViewModel
    private var sourceText: String = ""
    private var  API_KEY : String  = "client/api_key";
    private lateinit var mlTtsEngine: MLTtsEngine
    private lateinit var mlConfigs: MLTtsConfig
    private val TAG: String = TtsActivity::class.java.simpleName

    private var callback: MLTtsCallback = object : MLTtsCallback {
        override fun onError(taskId: String, err: MLTtsError) {

        }

        override fun onWarn(taskId: String, warn: MLTtsWarn) {

        }

        override fun onRangeStart(taskId: String, start: Int, end: Int) {
            Log.d("",start.toString())
            imageView3.setImageResource(R.drawable.on)
        }

        override fun onAudioAvailable(p0: String?, p1: MLTtsAudioFragment?, p2: Int, p3: android.util.Pair<Int, Int>?, p4: Bundle?) {

        }

        override fun onEvent(taskId: String, eventName: Int, bundle: Bundle?) {
            if (eventName == MLTtsConstants.EVENT_PLAY_STOP) {
                Toast.makeText(applicationContext, "Service Stopped",Toast.LENGTH_LONG).show()
            }
            imageView3.setImageResource(R.drawable.off)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tts)
        binding.lifecycleOwner = this
        ttsViewModel = ViewModelProvider(this).get(TtsViewModel::class.java)
        binding.ttsViewModel = ttsViewModel
        supportActionBar?.title = "Text to Speech Conversion"

        mlConfigs = MLTtsConfig()
            .setLanguage(MLTtsConstants.TTS_EN_US)
            .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN)
            .setSpeed(1.0f)
            .setVolume(1.0f)
        mlTtsEngine = MLTtsEngine(mlConfigs)
        mlTtsEngine.setTtsCallback(callback)

        ttsViewModel.ttsService.observe(this, Observer {
            startTtsService()
        })

        ttsViewModel.textData.observe(this, Observer {
            sourceText = it
        })
    }

    private fun startTtsService() {
        mlConfigs = MLTtsConfig()
            .setLanguage(MLTtsConstants.TTS_EN_US)
            .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN)
            .setSpeed(1.0f)
            .setVolume(1.0f)
        mlTtsEngine = MLTtsEngine(mlConfigs)
        mlTtsEngine.setTtsCallback(callback)
        //ID to use for Audio Visualizer.
        val id = mlTtsEngine.speak(sourceText, MLTtsEngine.QUEUE_APPEND)
        Log.i(TAG, id)
    }

    private fun setApiKey(){
        val  config  = AGConnectServicesConfig.fromContext(getApplication())
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }

    override fun onDestroy() {
        super.onDestroy()
        mlTtsEngine.shutdown()
    }

    override fun onPause() {
        super.onPause()
        mlTtsEngine.stop()
    }
}
