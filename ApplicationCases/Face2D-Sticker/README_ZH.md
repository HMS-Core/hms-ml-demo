# Face2D Sticker
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

[English](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Face2D-Sticker/README.md) | 中文

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
Face2D-Sticker通过HUAWEI ML Kit的人脸检测功能，识别人脸轮廓点,然后在人脸上贴动画贴纸。

本demo演示了如何使用[HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发人脸检测的应用，目的是让您体验人脸检测功能，帮助您尽快集成HUAWEI ML Kit。

## 工程目录结构
Face2D-Sticker
    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- FaceDetectionActivity // 入口


## 更多场景
基于HUAWEI ML Kit提供的人脸检测能力，不仅可以做微笑拍照程序，还可以实现更加丰富多彩的应用，如：
1、检测人脸表情，通过不同表情对照片或视频添加不同的标签等。
2、跟踪视频中的人脸，开发好玩的人脸特效

## 运行步骤
- 准备工作
  - 在项目层build.gradle文件中增加华为Maven仓库。
  - 在应用层build.gradle文件中添加SDK依赖。
  - 在Android的manifest.xml文件中增加人脸检测Model。
  - Android系统manifest.xml中申请相机权限。

- 代码开发的关键步骤
  - 动态权限申请。
  - 创建人脸检测器。
  - 通过android.graphics.bitmap创建用于分析器检测图片的“MLFrame”对象。
  - 调用“asyncanalyseframe”方法进行人脸检测。
  - 获得轮廓点，通过FaceStickerFilter绘制。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（https://www.apache.org/licenses/LICENSE-2.0）。
