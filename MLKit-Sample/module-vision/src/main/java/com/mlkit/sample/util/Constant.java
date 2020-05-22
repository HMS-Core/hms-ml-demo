/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mlkit.sample.util;

import android.graphics.Color;

import com.mlkit.sample.R;

public class Constant {

    public static final int GET_DATA_SUCCESS = 100;

    public static final int GET_DATA_FAILED = 101;

    public static final String CAMERA_FACING = "facing";

    public static final String CLOUD_IMAGE_CLASSIFICATION = "Cloud Classification";
    public static final String CLOUD_LANDMARK_DETECTION = "Landmark";
    public static final String MODEL_TYPE = "model_type";

    public static final String ADD_PICTURE_TYPE = "picture_type";
    public static final String TYPE_TAKE_PHOTO = "take photo";
    public static final String TYPE_SELECT_IMAGE = "select image";

    public static final String DEFAULT_VERSION = "1.0.3.300";

    public static int[] IMAGES = {R.mipmap.img_001, R.mipmap.img_002, R.mipmap.img_003, R.mipmap.img_004,
            R.mipmap.img_005, R.mipmap.img_006, R.mipmap.img_007, R.mipmap.img_008, R.mipmap.img_009};

    public static int[] COLOR_TABLE = {
            Color.rgb(255, 0, 0),
            Color.rgb(255, 255, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(0, 255, 255),
            Color.rgb(0, 0, 255),
            Color.rgb(255, 0, 255),
            Color.rgb(255, 0, 0)
    };

    /**
     * Number of the background image used in the background replacement.
     */
    public static final String VALUE_KEY = "index_value";
}
