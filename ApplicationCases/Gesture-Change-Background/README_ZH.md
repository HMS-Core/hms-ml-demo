# Gesture-Change-Background
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/tree/master/ApplicationCases/Gesture-Change-Background)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
Gesture-Change-Background使用华为ML Kit的手部关键点识别和图像分割功能通过挥动手臂来切换背景。

本demo演示了如何使用[HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发手势切换背景的应用，目的是让您体验手部关键点识别功能，帮助您尽快集成HUAWEI ML Kit。

更详细的开发步骤，请参考 [手势切换背景，让直播带货更加身临其境](https://developer.huawei.com/consumer/cn/forum/topic/0204395267288570031?fid=18).

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/Gesture-Change-Background/background.gif" width=180 title="start" border=2>

## 工程目录结构
Gesture-Change-Background
    |-- com.huawei.mlkit.sample
        |-- activity
            |-- MainActivity // 入口
            |-- BackgroundActivity //  背景页面

## 更多场景
基于HUAWEI ML Kit提供的手部关键点识别和图像分割能力，不仅可以开发挥手切换背景程序，还可以实现更加丰富多彩的应用，如：
1、手部添加特效。
2、手部动作拍人像。

## 运行步骤
- 准备工作
  - 在项目层build.gradle文件中增加华为Maven仓库。
  - 在应用层build.gradle文件中添加SDK依赖。
  - 在Android的manifest.xml文件中增加手部关键点检测Model。
  - Android系统manifest.xml中申请相机权限。

- 代码开发的关键步骤
  - 动态权限申请。
  - 创建混合分析器用来添加手部关键点分析器和图像分割分析器。
  - 创建LensEngine。
  - 调用lensEngine.run(holder)方法进行手部关键点检测来切换背景。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（https://www.apache.org/licenses/LICENSE-2.0）。