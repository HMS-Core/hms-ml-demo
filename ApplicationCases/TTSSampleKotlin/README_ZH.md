# TTSSample
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/tree/master/ApplicationCases/TTSSampleKotlin)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
TTSSample使用HUAWEI ML Kit的，基于语音合成的能力实现文字转语音。可实现即输即说，高自然度，效果接近普通人的朗读水平，提供有感情、个性化的语音合成服务。

服务介绍和接入指导，请参考以下链接：
[华为机器学习服务开发指南](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)。
[华为机器学习服务API参考](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/mlsdktts-overview-0000001050167594-V5)。

## 工程目录结构

TTSSampleKotlin

    |-- com.sample.ttssamplekotlin
        |-- Activity
            |-- TtsActivity // 语音合成服务

## 更多场景
HUAWEI ML Kit提供的语音合成功能，还可适用于更加广泛的场景，如：
新闻资讯阅读、有声小说、股票信息播报、语音导航、视频配音等多种场景。

## 运行步骤
 - 将本代码库克隆到本地。

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)。
 - agconnect-services.json文件请从[华为开发者社区]（https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050990353）网站申请获取。
 - 替换工程中的sample-agconnect-services.json文件。
 - 编译并且在安卓设备或模拟器上运行。

更详细的开发步骤，请参考 更详细的开发步骤，请参考 [Machine Learning made Easy: - Automatic Speech Recognition using Kotlin and Huawei ML Kit] (https://forums.developer.huawei.com/forumPortal/en/topicview?tid=0201264568431750009&fid=0101187876626530001)。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（https://www.apache.org/licenses/LICENSE-2.0）。

