# Photo Translate
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/tree/master/ApplicationCases/Photo-Translate)

## 目录

 * [介绍](#介绍)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
Photo-Translate使用华为ML Kit的文字识别和翻译功能，将静态照片中的文字翻译成所需的语言。目前支持的语言包括：简体中文、英语、法语、阿拉伯语、泰语、西班牙语、土耳其语、葡萄牙语、日语、德语、意大利语、俄语。

本demo演示了如何使用[HUAWEI ML Kit](https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发您的应用中的图片翻译功能，目的是让您体验文字识别和翻译功能，帮助您尽快集成HUAWEI ML Kit。

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/Photo-Translate/Photo%20Translate.gif" width=250 title="ID Photo DIY" div align=center border=5>

## 工程目录
Photo-Translate

    |-- com.mlkit.sample.phototranslate
      |-- Activity
        |-- MainActivity // 入口
        |-- RemoteTranslateActivity // 翻译功能
        |-- CapturePhotoActivity // 照片选择功能
        
## 更多场景
华为的文字识别和翻译还可以帮助开发者实现更多有趣和强大的功能，例如：
- 通用文本识别。
  - 公交牌照的文字识别。
  - 文档阅读中的文本识别。

- 翻译
  - 路标及招牌翻译。
  - 文件翻译。
  - 网页翻译，例如识别网站评论区的语言类型，并将其翻译成相应国家的语言。
  - 海外产品介绍及翻译。
  - 餐厅菜单翻译。

## 运行步骤
 - 将本代码库克隆到本地。

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)。
 - agconnect-services.json文件请从[华为开发者社区](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050990353)网站申请获取。
 - 替换工程中的sample-agconnect-services.json文件。
 - 编译并且在安卓设备或模拟器上运行。

更详细的开发步骤，请参考[安卓开发实战，用华为HMS MLKit 图像分割 SDK开发一个证件照DIY小程序 ](https://developer.huawei.com/consumer/cn/forum/topicview?tid=0201203408959360433&fid=18)

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0)。
