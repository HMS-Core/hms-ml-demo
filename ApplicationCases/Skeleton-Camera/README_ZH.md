# Skeleton-Camera
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Skeleton-Camera/README.md)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
Skeleton-Camera使用华为ML Kit的骨骼检测功能来识别人体动作并匹配对应动作进行抓拍。

本demo演示了如何使用[HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发骨骼抓拍的应用，目的是让您体验骨骼检测功能，帮助您尽快集成HUAWEI ML Kit。

更详细的开发步骤，请参考 [用华为HMS ML kit人体骨骼识别技术，Android快速实现人体姿势动作抓拍](https://developer.huawei.com/consumer/cn/forum/topicview?tid=0202333916402640253&fid=18).

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/Skeleton-Camera/start.gif" width=180 title="start" border=2>

## 工程目录结构
BeautyCamera
    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- MainActivity // 入口
            |-- HumanSkeletonActivity // 预览

## 更多场景
基于HUAWEI ML Kit提供的文本识别能力，不仅可以开发文本识别程序，还可以实现更加丰富多彩的应用，如：
1、人体骨骼点检测。
2、动作游戏。

## 运行步骤
- 准备工作
  - 在项目层build.gradle文件中增加华为Maven仓库。
  - 在应用层build.gradle文件中添加SDK依赖。
  - 在Android的manifest.xml文件中增加人脸检测Model。
  - Android系统manifest.xml中申请相机权限。

- 代码开发的关键步骤
  - 动态权限申请。
  - 创建骨骼检测器。
  - 创建LensEngine。
  - 调用lensEngine.run(holder)方法进行骨骼检测。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（https://www.apache.org/licenses/LICENSE-2.0）。
