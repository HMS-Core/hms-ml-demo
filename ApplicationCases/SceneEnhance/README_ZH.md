# SceneEnhance
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)
中文 | [English](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/SceneEnhance/README.md)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
SceneEnhance使用华为ML Kit的场景识别功能，智能修饰图片中的场景。

本Demo演示了如何使用[HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发修饰场景的小程序，目的是让您体验场景识别的功能，帮助您尽快集成HUAWEI ML Kit。

更详细的开发步骤，请参考 [场景识别帮助小白用户实现一键式智能识别相关场景](https://developer.huawei.com/consumer/cn/forum/topic/0204423925515690659?fid=18).

## 工程目录结构
SceneEnhance
    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- SceneActivity // 场景修饰

## 更多场景
基于HUAWEI ML Kit提供的场景识别能力，不仅可以做修饰场景程序，还可以实现更加丰富多彩的应用，如：
1、识别照片中的场景，对存储的图片信息进行精细化描述或分类。
2、识别周边的场景并进行特殊处理，如环境描述、树木的名称等。

## 运行步骤
- 准备工作
  - 在项目层build.gradle文件中增加华为Maven仓库。
    - 在应用层build.gradle文件中添加SDK依赖。
    - 在Android的manifest.xml文件中增加场景识别Model。
    - Android系统manifest.xml中申请相机权限。

- 代码开发的关键步骤
  - 动态权限申请。
  - 创建场景分析器。
  - 通过android.graphics.Bitmap创建用于分析器检测场景的“MLFrame”对象。
  - 调用“asyncAnalyseFrame”方法进行场景识别。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（https://www.apache.org/licenses/LICENSE-2.0）。
