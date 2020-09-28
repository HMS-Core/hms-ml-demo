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

package com.huawei.hms.mlkit.sample.util;

import android.view.View;

public class ClickRepeat implements View.OnClickListener{
    private View.OnClickListener clickListener;
    private long lastClick = 0;
    private static final long times = 2000;

    public ClickRepeat(View.OnClickListener origin) {
        clickListener = origin;
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClick >= times) {
            clickListener.onClick(v);
            lastClick = System.currentTimeMillis();
        }
    }
}
