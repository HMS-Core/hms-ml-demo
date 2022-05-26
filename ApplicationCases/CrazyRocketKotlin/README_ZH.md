# CrazyRockets
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/CrazyRockets/README.md)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
CrazyRockets使用华为ML Kit的人脸识别功能和手势识别功能来移动小火箭躲避障碍物。

本demo演示了如何使用[HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发疯狂火箭的应用，目的是让您体验人脸识别功能和手势识别功能，帮助您尽快集成HUAWEI ML Kit。

更详细的开发步骤，请参考 [Crazy Rockets-教你如何集成华为HMS ML Kit人脸检测和手势识别打造爆款小游戏](https://developer.huawei.com/consumer/cn/forum/topic/0201388581574050067?fid=18).

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/CrazyRockets/hand.gif" width=180 title="start" border=2></td>
</tr></table>

## 工程目录结构
BeautyCamera
    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- MainActivity // 入口
            |-- FaceGameActivity // 人脸识别
            |-- HandGameActivity // 手势识别

## 更多场景
基于HUAWEI ML Kit提供的人脸识别能力能力和手势识别能力，不仅可以开发疯狂火箭程序，还可以实现更加丰富多彩的应用，如：
1、手势抓拍。
2、微笑抓拍。

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
  - 调用lensEngine.run(holder)方法进行疯狂火箭。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（https://www.apache.org/licenses/LICENSE-2.0）。
