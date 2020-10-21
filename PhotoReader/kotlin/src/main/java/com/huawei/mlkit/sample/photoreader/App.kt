package com.huawei.mlkit.sample.photoreader

import android.app.Application
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.mlsdk.common.MLApplication

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setApiKey()
    }

    /**
     * Read the ApiKey field in the agconnect-services.json to obtain the API key of the application and set it.
     * For details about how to apply for the agconnect-services.json, see section https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-add-agc.
     */
    private fun setApiKey() {
        val config = AGConnectServicesConfig.fromContext(this)
        MLApplication.getInstance().apiKey = config.getString("client/api_key")
    }
}