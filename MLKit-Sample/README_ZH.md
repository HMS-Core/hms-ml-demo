# MLKit-Sample
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample)
## 目录

 * [介绍](#介绍)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
本示例代码目的是为了介绍ML Kit SDK的使用，其中包含以下两个模块：

文本模块：Module-text。其中包括：文本识别、文档识别、身份证识别、银行卡识别、通用卡证识别、文本翻译、语种检测、实时语音识别、语音合成、音频文件转写。

视觉模块：Module-vision。其中包括：人脸检测、图像分割、拍照购物、图片分类、对象检测与跟踪、地标识别。

详细介绍请参考[华为机器学习SDK](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)。

以下是apk运行界面的截图：

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/mainText.jpg" width=180 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/language.jpg" width=180 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/text.jpg" width=180 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/bcr.jpg" width=180 border=2></td>
</tr></table>

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/mainVision.jpg" width=180 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/imageSegmentVideo.gif" width=180 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/face.jpg" width=180 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/object.jpg" width=180 border=2></td>
</tr></table>

## 运行步骤
 - 将本代码库克隆到本地。

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/doc/start/10101)。
 - agconnect-services.json文件请从[华为开发者社区](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-add-agc)网站申请获取。
 - 替换工程中的sample-agconnect-services.json文件。
 - 编译并且在安卓设备或模拟器上运行。

注意：

该项目中的package name不能用于申请agconnect-services.json，您可以使用自定义package name来申请agconnect-services.json。
您只需将应用级build.gradle中的applicationId修改为与所申请的agconnect-services.json相同的package name，即可体验ML Kit云侧服务。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0)。
