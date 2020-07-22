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

package com.mlkit.sample.views.graphic;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;


import com.huawei.hms.mlsdk.skeleton.MLJoint;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.mlkit.sample.views.overlay.GraphicOverlay;

/**
 * Bone point custom view
 */
public class LocalSkeletonGraphic extends BaseGraphic {
    private List<MLSkeleton> skeletons;

    private Paint circlePaint;

    private Paint linePaint;

    /**
     * Construction method
     *
     * @param overlay custom view
     * @param skeletons bone data points
     */
    public LocalSkeletonGraphic(GraphicOverlay overlay, List<MLSkeleton> skeletons) {
        super(overlay);
        this.skeletons = skeletons;
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(10f);
        linePaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < skeletons.size(); i++) {
            MLSkeleton skeleton = skeletons.get(i);
            if (skeleton.getJoints() == null || skeleton.getJoints().size() < 14) {
                continue;
            }
            List<Path> paths = new ArrayList<>();
            // Draw your head and neck.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_HEAD_TOP), skeleton.getJointPoint(MLJoint.TYPE_NECK)));
            // Neck and left shoulder.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_NECK), skeleton.getJointPoint(MLJoint.TYPE_LEFT_SHOULDER)));
            // Left shoulder and left elbow.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_LEFT_SHOULDER), skeleton.getJointPoint(MLJoint.TYPE_LEFT_ELBOW)));
            // Left elbow and left wrist.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_LEFT_ELBOW), skeleton.getJointPoint(MLJoint.TYPE_LEFT_WRIST)));
            // Neck and left hip.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_NECK), skeleton.getJointPoint(MLJoint.TYPE_LEFT_HIP)));
            // Left hip and left knee.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_LEFT_HIP), skeleton.getJointPoint(MLJoint.TYPE_LEFT_KNEE)));
            // Left knee and left ankle
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_LEFT_KNEE), skeleton.getJointPoint(MLJoint.TYPE_LEFT_ANKLE)));

            // Neck and right shoulder.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_NECK), skeleton.getJointPoint(MLJoint.TYPE_RIGHT_SHOULDER)));
            // Right shoulder and right elbow.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_RIGHT_SHOULDER), skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ELBOW)));
            // Right elbow and right wrist.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ELBOW), skeleton.getJointPoint(MLJoint.TYPE_RIGHT_WRIST)));
            // Neck and right hip.
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_NECK), skeleton.getJointPoint(MLJoint.TYPE_RIGHT_HIP)));
            // Right hip and right knee
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_RIGHT_HIP), skeleton.getJointPoint(MLJoint.TYPE_RIGHT_KNEE)));
            // Right knee and right ankle
            paths.add(getPath(skeleton.getJointPoint(MLJoint.TYPE_RIGHT_KNEE), skeleton.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE)));

            for(int j = 0; j < paths.size(); j++){
                if (paths.get(j) != null){
                    canvas.drawPath(paths.get(j), linePaint);
                }
            }

            for (MLJoint joint : skeleton.getJoints()) {
                if (!(Math.abs(joint.getPointX() - 0f) == 0 && Math.abs(joint.getPointY() - 0f) == 0)) {
                    canvas.drawCircle(translateX(joint.getPointX()),
                            translateY(joint.getPointY()), 24f, circlePaint);
                }
            }
        }
    }

    private Path getPath(MLJoint point1, MLJoint point2) {
        if (point1 == null || point2 == null) {
            return null;
        }
        if (point1.getPointX() == 0f && point1.getPointY() == 0f) {
            return null;
        }
        if (point2.getPointX() == 0f && point2.getPointY() == 0f) {
            return null;
        }
        Path path = new Path();
        path.moveTo(translateX(point1.getPointX()),
                translateY(point1.getPointY()));
        path.lineTo(translateX(point2.getPointX()), translateY(point2.getPointY()));

        return path;
    }
}
