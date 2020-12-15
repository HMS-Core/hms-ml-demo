# Photo Reader

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/tree/master/ApplicationCases/PhotoReader)

## 目录

 * [介绍](#介绍)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍

PhotoReader使用华为ML Kit的文字识别和语音合成功能，将照片中的文字读取出来再利用语音合成把文字变成语音输出。目前语音合成支持的语言包括：简体中文和英语。

Photo Reader使用华为ML Kit的文字识别，翻译和语音合成功能，将照片中的文字读取出来，翻译后再利用语音合成把文字变成语音输出。

本demo演示了如何使用[HUAWEI ML Kit](https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发您的应用中的图片朗读功能，目的是让您体验文字识别，翻译和语音合成功能，帮助您尽快集成HUAWEI ML Kit。

##### 代码模块目录结构

- App, 全部用Java实现的
- Kotlin, 全部用Kotlin实现的
- Lensengine, 封装基于用Java编写的相机实现，用旧android相机API

## 工程目录结构
PhotoReader
    |-- com.huawei.mlkit.sample.photoreader
        |-- Activity
            |-- MainActivity //入口
            |-- ReadPhotoActivity // 功能界面

## 更多场景
华为的文字识别和语音合成还可以帮助开发者实现更多有趣和强大的功能，例如：
- 通用文本识别。
  - 公交牌照的文字识别。
  - 文档阅读中的文本识别。

- 语音合成
  - 小说朗读。
  - 导航语音。


## 运行步骤
 - 将本代码库克隆到本地。

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)。
 - agconnect-services.json文件请从[华为开发者社区](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050990353)网站申请获取。
 - 替换工程中的sample-agconnect-services.json文件。
 - 编译并且在安卓设备或模拟器上运行。


## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0)。
