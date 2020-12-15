# Receipt-Text-Recognition
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)
Receipt-Text-Recognition

[English](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Photo-Translate/README.md) | 中文

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
此工程描述了如何使用HMS Core mlsdk提供的文本识别服务来识别单据信息。

更多信息可参考：[超简单集成华为HMS ML Kit文本识别SDK，一键实现账单号自动录入](https://developer.huawei.com/consumer/cn/forum/topicview?tid=0203343372058830370&fid=18)

本演示演示如何使用[HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit)快速开发一个文本识别应用程序。目的是帮助您体验文本检测功能，尽快集成华为ML套件。

## 工程目录结构
Receipt-Text-Recognition
    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- MainActivity // 入口
            |-- CameraActivity // 文本识别
            |-- OcrDetectorProcessor // 结果

## 更多场景
基于HUAWEI ML Kit提供的文本识别能力，不仅可以开发文本识别程序，还可以实现更加丰富多彩的应用，如：
1、快速文本输入。
2、通用卡识别。

## 运行步骤
- 准备工作
  - 在项目层build.gradle文件中增加华为Maven仓库。
  - 在应用层build.gradle文件中添加SDK依赖。
  - 在Android的manifest.xml文件中增加文本识别Model。
  - Android系统manifest.xml中申请相机权限。

- 代码开发的关键步骤
  - 动态权限申请。
  - 创建文本检测器。
  - 创建LensEngine。
  - 调用lensEngine.run(holder)方法进行文本检测。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（https://www.apache.org/licenses/LICENSE-2.0）。
