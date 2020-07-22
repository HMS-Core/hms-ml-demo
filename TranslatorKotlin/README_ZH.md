# Translator
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/tree/master/TranslatorKotlin)

## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
Translator使用HUAWEI ML Kit实时语音识别，文本翻译，语音合成的能力，实现将英文语音翻译为中文语音的能力，通过定制化的开发，可以方便不同国家人与人之间的沟通交流。

服务介绍和接入指导，请参考以下链接：
[华为机器学习服务开发指南](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)。
[华为机器学习服务API参考](https://developer.huawei.com/consumer/cn/doc/development/HMS-References/MLRemoteTranslator-4)。

## 工程目录结构
TranslatorKotlin
    |-- com.sample.translator
        |-- Activity
            |-- TranslatorActivity // 翻译，实时语音识别，语音合成集合

## 更多场景
实时语音识别、文本翻译以及语音合成功能，还可适用于更加广泛的场景，如：
1、实时语音识别：
    将语音实时识别为文字，适用于语音聊天、语音输入、语音搜索、语音下单、语音指令等多种场景；可将会议记录、笔记、总结、等音频实时转写为文字，进行内容记录、实时展示。
2、文本翻译
    将文本转换为指定语言文字，支持多种语言文字的互译，适用于电影字幕翻译、聊天内容翻译等多种场景。
3、语音合成
    将文字信息转换为语音输出，适用于新闻资讯阅读、有声小说、股票信息播报、语音导航、视频配音等多种场景。

## 运行步骤
 - 将本代码库克隆到本地。

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/doc/start/10101)。
 - agconnect-services.json文件请从[华为开发者社区]（https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-add-agc）网站申请获取。
 - 替换工程中的sample-agconnect-services.json文件。
 - 编译并且在安卓设备或模拟器上运行。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（http://www.apache.org/licenses/LICENSE-2.0）。
