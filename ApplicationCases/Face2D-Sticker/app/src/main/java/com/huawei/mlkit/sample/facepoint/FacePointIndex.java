/**
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
package com.huawei.mlkit.sample.facepoint;

public final class FacePointIndex {
    /**
     * Index of contour points (131 contour points)
     * TYPE_FACE: Length is 36, representing the overall face contour 0~35
     * TYPE_LEFT_EYE: The length is 16, representing the contour 36~51 of the left eye cavity of the image
     * TYPE_BOTTOM_OF_LEFT_EYEBROW: length of 5, on behalf of the image at the bottom of the left eyebrow 52 to 56
     * TYPE_TOP_OF_LEFT_EYEBROW: length of 5, on behalf of the left eyebrow at the top of the image contour 57 ~ 61
     * TYPE_BOTTOM_OF_LOWER_LIP: Length 9, representing the bottom contour of the lower lip 62~70
     * TYPE_TOP_OF_LOWER_LIP: Length is 9, representing the top contour of the lower lip 71~79
     * TYPE_BOTTOM_OF_NOSE: Length 3, representing the bottom contour of the nose 80~82
     * TYPE_BRIDGE_OF_NOSE: Length 2, representing outline of bridge of nose 83~84
     * TYPE_RIGHT_EYE: Length is 16, representing the outline of the right eye cavity of the image 85~100
     * TYPE_BOTTOM_OF_RIGHT_EYEBROW: length of 5, on behalf of the right eyebrow
                                        at the bottom of the image contour of 101 ~ 105
     * TYPE_TOP_OF_RIGHT_EYEBROW: length of 5, on behalf of the right eyebrow
                                    at the top of the image contour of 106 ~ 110
     * TYPE_OF_upper_lip: length 9, representing the bottom contour of the upper lip 111~119
     * TYPE_TOP_OF_UPPER_LIP: Length of 11, representing top outline of upper lip 120~130
     * Refer to the point bitmap of assets/face_map.jpg
     */
    // the forehead
    private static final int LEFT_HEAD = 5; // Left side of forehead
    private static final int HEAD_CENTER = 0; // Center of forehead
    private static final int RIGHT_HEAD = 31; // Right side of forehead
    private static final int EYE_CENTER = 83; // Eye center

    // face edge
    private static final int LEFT_CHEEK_EDGE_CENTER = 10; // Left cheek edge center
    private static final int RIGHT_CHEEK_EDGE_CENTER = 27; // Right cheek edge center

    // nose
    private static final int NOSE_TOP = 84; // tip
    private static final int NOSE_LEFT = 82; // Left side of nose
    private static final int NOSE_RIGHT = 80; // Right side of nose
    private static final int NOSE_LOWER_MIDDLE = 81; // Center of both nostrils

    // left eyebrow
    private static final int LEFT_EYEBROW_RIGHT_CORNER = 105; // The right corner of the left eyebrow
    private static final int LEFT_EYEBROW_LEFT_CORNER = 106; // Left eyebrow left Angle
    private static final int LEFT_EYEBROW_UPPER_MIDDLE = 108; // Upper center of left eyebrow
    private static final int LEFT_EYEBROW_LOWER_MIDDLE = 103; // Center under left eyebrow

    // right eyebrow
    private static final int RIGHT_EYEBROW_RIGHT_CORNER = 57; // The right corner of the right eyebrow
    private static final int RIGHT_EYEBROW_LEFT_CORNER = 56; // Left corner of right eyebrow
    private static final int RIGHT_EYEBROW_UPPER_MIDDLE = 59; // Upper center of right eyebrow
    private static final int RIGHT_EYEBROW_LOWER_MIDDLE = 54; // Center under right eyebrow

    // the left eye
    private static final int LEFT_EYE_TOP = 89; // Above left eye ball
    private static final int LEFT_EYE_BOTTOM = 97; // Under the left eye
    private static final int LEFT_EYE_LEFT_CORNER = 93; // Left Angle of left eye
    private static final int LEFT_EYE_RIGHT_CORNER = 85; // The right Angle of the left eye

    // in the right eye
    private static final int RIGHT_EYE_TOP = 40; // Above the right eye ball
    private static final int RIGHT_EYE_BOTTOM = 49; // Under the right eye
    private static final int RIGHT_EYE_LEFT_CORNER = 44; // The left Angle of your right eye
    private static final int RIGHT_EYE_RIGHT_CORNER = 36; // The right Angle of the right eye

    // mouth
    private static final int MOUTH_LEFT_CORNER = 130; // Left lip
    private static final int MOUTH_RIGHT_CORNER = 71; // Right lip
    private static final int MOUTH_LIP_TOP = 126; // Upper lip center
    private static final int MOUTH_LIP_BOTTOM = 115; // Lower center of upper lip
    private static final int MOUTH_LOWER_LIP_TOP = 75; // Upper center of lower lip
    private static final int MOUTH_LOWER_LIP_BOTTOM = 66; // Lower lip, lower center

    // chin
    private static final int CHIN_LEFT = 15; // Left side of chin
    private static final int CHIN_RIGHT = 23; // Right side of chin
    private static final int CHIN_CENTER = 18; // Center of chin

    private FacePointIndex() {
    }
}
