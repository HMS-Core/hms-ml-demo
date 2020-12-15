# ASRSample
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/tree/master/ApplicationCases/ASRSampleKotlin)
## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
ASRSample使用HUAWEI ML Kit的实时语音识别功能将实时语音转换成文本信息，目前支持识别:汉语普通话，英语，法语。

服务介绍和接入指导，请参考以下链接：
[华为机器学习服务开发指南](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)。
[华为机器学习服务API参考](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/asrsdkoverview-0000001050747393-V5)。

## 工程目录结构
AsrSampleKotlin
    |-- com.sample.asrsamplekotlin
        |-- Activity
            |-- AsrAudioActivity //实时语音识别服务

## 更多场景
HUAWEI ML Kit提供的实时语音识别功能，还可适用于更加广泛的场景，如：
1.手机应用语音输入：
将语音实时识别为文字，适用于语音聊天、语音输入、语音搜索、语音下单、语音指令等多种场景。
2.实时语音转写：
可将会议记录、笔记、总结、等音频实时转写为文字，进行内容记录、实时展示。

## 运行步骤
 - 将本代码库克隆到本地。

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)。
 - agconnect-services.json文件请从[华为开发者社区](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050990353)网站申请获取。
 - 替换工程中的sample-agconnect-services.json文件。
 - 编译并且在安卓设备或模拟器上运行。

更详细的开发步骤，请参考 [Machine Learning made Easy: - Automatic Speech Recognition using Kotlin and Huawei ML Kit] (https://forums.developer.huawei.com/forumPortal/en/topicview?tid=0201264568431750009&fid=0101187876626530001)。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0)。
