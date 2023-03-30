/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.mlkit.sample.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.huawei.hms.mlsdk.interactiveliveness.action.GuideDetectionInfo;
import com.huawei.hms.mlsdk.interactiveliveness.action.InteractiveLivenessStateCode;
import com.huawei.mlkit.sample.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 检测结果处理。
 *
 * @author
 * @since 2021-10-28
 */
public enum InteractiveLivenessDetectionResultEnum {
    // 代表正在检测
    IN_PROGRESS(InteractiveLivenessStateCode.IN_PROGRESS) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            if (guideDetectionIsEnd) {
                textView.setText(actionTypeId);
            }
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表非活体  遇到则需要退出检测
    SPOOFING(InteractiveLivenessStateCode.SPOOFING) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setVisibility(View.INVISIBLE);
            textView.setText(R.string.no_real_face);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {

        }
    },

    // 代表活体且动作正确
    LIVE_AND_ACTION_CORRECT(InteractiveLivenessStateCode.LIVE_AND_ACTION_CORRECT) {
        @Override
        public void customDetectionResultProcess(
                final TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            GuideDetectionInfo.getInstance().firstGuideDetectionIsEnd = true;
            textView.setText(R.string.action_correct);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表引导检测成功
    GUIDE_DETECTION_SUCCESS(InteractiveLivenessStateCode.GUIDE_DETECTION_SUCCESS) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            GuideDetectionInfo.getInstance().firstGuideDetectionIsEnd = true;
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表无人脸
    NO_FACE(InteractiveLivenessStateCode.NO_FACE) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.no_face);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表人脸偏移中心
    PART_FACE(InteractiveLivenessStateCode.PART_FACE) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.face_offset);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表图片质量不佳
    IMAGE_QUALITY_POOR(InteractiveLivenessStateCode.IMAGE_QUALITY_POOR) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.image_quality_poor);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表人脸过大
    BIG_FACE(InteractiveLivenessStateCode.BIG_FACE) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.big_face);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表人脸过小
    SMALL_FACE(InteractiveLivenessStateCode.SMALL_FACE) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.small_face);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表墨镜遮挡
    WEAR_SUNGLASSES(InteractiveLivenessStateCode.WEAR_SUNGLASSES) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.eyes_blocked);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表口罩遮挡
    WEAR_MASK(InteractiveLivenessStateCode.WEAR_MASK) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.mouth_blocked);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表动作互斥错误 遇到则需要退出检测
    ACTION_MUTUALLY_EXCLUSIVE_ERROR(InteractiveLivenessStateCode.ACTION_MUTUALLY_EXCLUSIVE_ERROR) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.action_collision);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {

        }
    },

    // 连续性检测失败  遇到则需要退出检测
    CONTINUITY_DETECTION_ERROR(InteractiveLivenessStateCode.CONTINUITY_DETECTION_ERROR) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setVisibility(View.INVISIBLE);
            textView.setText(R.string.no_real_face);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {

        }
    },

    // 基于算法状态判断的端测特殊状态
    // 三个动作连续正确状态，结束检测
    ALL_ACTION_CORRECT(InteractiveLivenessStateCode.ALL_ACTION_CORRECT) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.detection_passed);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 算法结果返回超时状态 遇到则需要退出检测
    RESULT_TIME_OUT(InteractiveLivenessStateCode.RESULT_TIME_OUT) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.detection_timeout);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {

        }
    },

    // 代表没有设置人脸框位置
    INIT_FACE_RECTANGLE_ERROR(InteractiveLivenessStateCode.INIT_FACE_RECTANGLE_ERROR) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            Log.d(getClass().getSimpleName(), "no setFaceRect");
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表环境光线暗
    DARK(InteractiveLivenessStateCode.DARK) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.poor_light);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表图片模糊
    BLUR(InteractiveLivenessStateCode.BLUR) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.blurry_image);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表环境逆光
    BACK_LIGHTING(InteractiveLivenessStateCode.BACK_LIGHTING) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.poor_light);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表环境光线亮
    BRIGHT(InteractiveLivenessStateCode.BRIGHT) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.poor_light);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表人脸朝向不符
    FACE_ASPECT(InteractiveLivenessStateCode.FACE_ASPECT) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.should_be_consistent);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 代表多人脸
    MULTI_FACES(InteractiveLivenessStateCode.MULTI_FACES) {
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            textView.setText(R.string.multi_faces);
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {

        }
    },

    // 前一次已经检查出异常结果，如SPOOFING，ACTION_MUTUALLY_EXCLUSIVE_ERROR，RESULT_TIME_OUT
    // 算法监测为多线程并行监测，可能存在监测未结束，前后两次超时的结果返回，前面页面出现一闪情况，但强行release算法会可能导致crash
    ERROR_RESULT_BEFORE(InteractiveLivenessStateCode.ERROR_RESULT_BEFORE) {
        @SuppressLint("LongLogTag")
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            Log.d(TAG, "Detection is over.");
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    },

    // 算法未返回处理结果时
    OTHER(InteractiveLivenessStateCode.OTHER) {
        @SuppressLint("LongLogTag")
        @Override
        public void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd) {
            Log.d(TAG, "detectionResult is null!");
        }

        @Override
        public void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom) {}
    };

    public abstract void customDetectionResultProcess(TextView textView, int actionTypeId, Boolean guideDetectionIsEnd);

    public abstract void goToFailActivity(Context packageContext, Class<?> cls, Boolean isCustom);

    private static final String TAG = "InteractiveLivenessDetectionResultEnum";

    private int value;

    private static long showTime = 1000L;

    private Boolean isGo = false;

    private static Map<Integer, InteractiveLivenessDetectionResultEnum> valueMap =
            new HashMap<Integer, InteractiveLivenessDetectionResultEnum>();

    static {
        for (InteractiveLivenessDetectionResultEnum interactiveLivenessDetectionResultEnum :
                InteractiveLivenessDetectionResultEnum.values()) {
            valueMap.put(interactiveLivenessDetectionResultEnum.value, interactiveLivenessDetectionResultEnum);
        }
    }

    public static InteractiveLivenessDetectionResultEnum fromValue(int value) {
        return valueMap.get(value);
    }

    InteractiveLivenessDetectionResultEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static void customDetectionResultProcess(
            TextView textView,
            int actionTypeId,
            Boolean guideDetectionIsEnd,
            int detectionResult,
            Context packageContext,
            Class<?> cls,
            Boolean isCustom) {
        InteractiveLivenessDetectionResultEnum interactiveLivenessDetectionResultEnum =
                InteractiveLivenessDetectionResultEnum.fromValue(detectionResult);
        if (interactiveLivenessDetectionResultEnum != null) {
            interactiveLivenessDetectionResultEnum.customDetectionResultProcess(
                    textView, actionTypeId, guideDetectionIsEnd);
            interactiveLivenessDetectionResultEnum.goToFailActivity(packageContext, cls, isCustom);
        }
    }

    public static void defaultDetectionResultProcess(
            int detectionResult, Context packageContext, Class<?> cls, Boolean isCustom) {
        InteractiveLivenessDetectionResultEnum interactiveLivenessDetectionResultEnum =
                InteractiveLivenessDetectionResultEnum.fromValue(detectionResult);
        if (interactiveLivenessDetectionResultEnum != null) {
            interactiveLivenessDetectionResultEnum.goToFailActivity(packageContext, cls, isCustom);
        }
    }

    public static void defaultDetectionFailResultProcess(
            TextView textView, int detectionResult, int actionTypeId, Boolean guideDetectionIsEnd) {
        InteractiveLivenessDetectionResultEnum interactiveLivenessDetectionResultEnum =
                InteractiveLivenessDetectionResultEnum.fromValue(detectionResult);
        if (interactiveLivenessDetectionResultEnum != null) {
            interactiveLivenessDetectionResultEnum.customDetectionResultProcess(
                    textView, actionTypeId, guideDetectionIsEnd);
        }
    }
}
