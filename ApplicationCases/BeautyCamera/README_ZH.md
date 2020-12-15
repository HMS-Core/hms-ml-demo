# BeautyCamera
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

[English](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/BeautyCamera/README.md) | 中文

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
BeautyCamera使用华为ML Kit的人脸检测功能来识别一张脸并对其进行美颜。

本demo演示了如何使用[HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发人脸检测的应用，目的是让您体验人脸检测功能，帮助您尽快集成HUAWEI ML Kit。

## 工程目录结构
BeautyCamera

    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- MainActivity // 入口
            |-- CameraActivity // 相机
            |-- BeautyActivity // 美颜界面
        |-- utils
            |-- ImageHelper // 图片处理工具类

## 更多场景
基于HUAWEI ML Kit提供的人脸检测能力，不仅可以开发美颜功能，还可以实现更加丰富多彩的应用，如：
1、人脸识别门禁。
2、表情抓拍。

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
  - 调用ImageHelper中方法进行“美颜”。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（https://www.apache.org/licenses/LICENSE-2.0）。
