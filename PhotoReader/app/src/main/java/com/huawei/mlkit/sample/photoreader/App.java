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

import android.app.Application;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlsdk.common.MLApplication;

public class App extends Application {

    private static final String API_KEY = "client/api_key";

    @Override
    public void onCreate() {
        super.onCreate();
        setApiKey();
    }

    /**
     * Read the ApiKey field in the agconnect-services.json to obtain the API key of the application and set it.
     * For details about how to apply for the agconnect-services.json, see section https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-add-agc.
     */
    private void setApiKey() {
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(this);
        MLApplication.getInstance().setApiKey(config.getString(API_KEY));
    }
}
