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

package com.huawei.hms.mlkit.sample.util;

public class ConstantData {
    public final static String [] scene = {"Bluesky", "Food", "Flower", "Greenplant", "Night"};
    // Color matrix
    public final static float [] sky = new float[] {
            1.438f, -0.122f, -0.016f, 0.0f, 1.0f,
            -0.062f, 1.378f, -0.016f, 0.0f, 1.0f,
            -0.062f, -0.122f, 1.483f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    public final static float [] food = new float[] {
            1.0f, 0.0f, 0.0f, 0.0f, 10.0f,
            0.0f, 1.0f, 0.0f, 0.0f, 10.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 10.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    public final static float [] flower = new float[] {
            1.438f, -0.122f, -0.016f, 0.0f, -0.03f,
            -0.062f, 1.378f, -0.016f, 0.0f, 0.05f,
            -0.062f, -0.122f, 1.483f, 0.0f, -0.02f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    public final static float [] grass = new float[] {
            1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.4f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    public final static float [] darkness = new float[] {
            1.438f, -0.122f, -0.016f, 0.0f, -0.03f,
            -0.062f, 1.378f, -0.016f, 0.0f, 0.05f,
            -0.062f, -0.122f, 1.483f, 0.0f, -0.02f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
}
