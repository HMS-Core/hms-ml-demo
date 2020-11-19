/**
 * Copyright 2018 cain.huang@outlook.com
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
package com.huawei.mlkit.sample.util;

public class TextureRotationUtils {
//    public static final int COORDS_PER_VERTEX = 2;
//
//    public static final float CUBE_VERTICES[] = {
//            -1.0f, -1.0f,  // 0 bottom left
//            1.0f,  -1.0f,  // 1 bottom right
//            -1.0f,  1.0f,  // 2 top left
//            1.0f,   1.0f,  // 3 top right
//    };
//
//    public static final float TEXTURE_VERTICES[] = {
//            0.0f, 0.0f,     // 0 left bottom
//            1.0f, 0.0f,     // 1 right bottom
//            0.0f, 1.0f,     // 2 left top
//            1.0f, 1.0f      // 3 right top
//    };
//
//    public static final float TEXTURE_VERTICES_FLIPX[] = {
//            1.0f, 0.0f,     // 0 right bottom
//            0.0f, 0.0f,     // 1 left  bottom
//            1.0f, 1.0f,     // 2 right top
//            0.0f, 1.0f      // 3 left  top
//    };
//
//    public static final float TEXTURE_VERTICES_90[] = {
//            1.0f, 0.0f,
//            1.0f, 1.0f,
//            0.0f, 0.0f,
//            0.0f, 1.0f,
//    };
//
//    public static final float TEXTURE_VERTICES_180[] = {
//            1.0f, 1.0f, // right top
//            0.0f, 1.0f, // left top
//            1.0f, 0.0f, // right bottom
//            0.0f, 0.0f, // left bottom
//    };
//
//    public static final float TEXTURE_VERTICES_270[] = {
//            0.0f, 1.0f,
//            0.0f, 0.0f,
//            1.0f, 1.0f,
//            1.0f, 0.0f,
//    };

    /**
     * INDICES，glDrawElements using
     */
//    public static final short[] INDICES = {
//            0, 1, 2,
//            2, 1, 3,
//    };

    private TextureRotationUtils() {}

    /**
     * getRotation
     * @param rotation
     * @param flipHorizontal
     * @param flipVertical
     * @return
     */
//    public static float[] getRotation(final int rotation, final boolean flipHorizontal,
//                                        final boolean flipVertical) {
//        float[] rotatedTex;
//        switch (rotation) {
//            case 90:
//                rotatedTex = TEXTURE_VERTICES_90;
//                break;
//
//            case 180:
//                rotatedTex = TEXTURE_VERTICES_180;
//                break;
//
//            case 270:
//                rotatedTex = TEXTURE_VERTICES_270;
//                break;
//
//            case 0:
//            default:
//                rotatedTex = TEXTURE_VERTICES;
//                break;
//        }
//
//        if (flipHorizontal) {
//            rotatedTex = new float[] {
//                    flip(rotatedTex[0]), rotatedTex[1],
//                    flip(rotatedTex[2]), rotatedTex[3],
//                    flip(rotatedTex[4]), rotatedTex[5],
//                    flip(rotatedTex[6]), rotatedTex[7],
//            };
//        }
//
//        if (flipVertical) {
//            rotatedTex = new float[]{
//                    rotatedTex[0], flip(rotatedTex[1]),
//                    rotatedTex[2], flip(rotatedTex[3]),
//                    rotatedTex[4], flip(rotatedTex[5]),
//                    rotatedTex[6], flip(rotatedTex[7]),
//            };
//        }
//        return rotatedTex;
//    }

    /**
     * flip
     * @param i
     * @return
     */
//    private static float flip(final float i) {
//        if (i == 0.0f) {
//            return 1.0f;
//        }
//        return 0.0f;
//    }
}

