# MLKit-Sample
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

中文 | [English](https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample)
## 目录

 * [介绍](#介绍)
 * [工程目录结构](#工程目录结构)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
本示例代码目的是为了介绍ML Kit SDK的使用，其中包含以下两个模块：

### 文本模块
Module-text。其中包括：文本识别、文档识别、身份证识别、银行卡识别、通用卡证识别、文本翻译、语种检测、实时语音识别、语音合成、音频文件转写、文档校正。

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/mainText.jpg" width=220 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/language.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/localLanguage.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/asr.jpg" width=220 border=2></td>
</tr></table>

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/tts.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/aft.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/text.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/bcr.jpg" width=220 border=2></td>
</tr></table>

### 视觉模块
Module-vision。其中包括：图像分割、图像分类、对象检测与跟踪、地标识别、图像超分。

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/mainVision.jpg" width=220 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/imageSegmentVideo.gif" width=220 border=2></td>
</tr></table>

### 人体模块
Module-body。其中包括：人脸检测、人体骨骼、活体检测、手部关键点。

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/face.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/skeleton.jpg" width=220 border=2></td>
</tr></table>

详细介绍请参考[华为机器学习SDK](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)。

## 工程目录结构
moduletext
    |-- com.mlkit.sample
        |-- Activity
            |-- AsrAudioActivity //实时语音识别服务
            |-- TtsAudioActivity //语音合成服务
	    |-- AudioFileTranscriptionActivity //音频文件转写服务
	    |-- IDCardRecognitionActivity // 身份证识别服务
	    |-- BankCardRecognitionActivity // 银行卡识别服务
	    |-- GeneralCardRecognitionActivity // 通用卡证识别服务
            |-- TextRecognitionActivity  // 文字识别服务
            |-- RemoteDetectionActivity  // 文档识别服务
	    |-- StartActivity  // 服务入口
            |-- BaseActivity  // Activity基类
	    |-- SettingActivity  // moduletext基本信息
            |-- TranslateActivity
            |-- LocalTranslateActivity // 端侧文本翻译
            |-- RemoteTranslateActivity // 云测文本翻译
            |-- DocumentSkewCorretionActivity // 文档校正


modulevision
    |-- com.mlkit.sample
        |-- Activity
            |-- ImageSegmentationActivity //图像分割
            |-- LoadPhotoActivity //图像分割相关
            |-- TakePhotoActivity //背景替换相关
            |-- StillCutPhotoActivity //人像抠图相关
	    |-- ObjectDetectionActivity //对象检测与跟踪
	    |-- ImageClassificationActivity //图像分类
	    |-- RemoteDetectionActivity //地标识别
	    |-- ImageSuperResolutionActivity //图像超分
	    |-- BaseActivity  // Activity基类
	    |-- StartActivity  // 服务入口
	    |-- SettingActivity  // modulevision基本信息


modulebody
    |-- com.mlkit.sample
        |-- Activity
        |-- FaceDetectionActivity //人脸检测
        |-- HumanSkeletonActivity //人体骨骼
        |-- TemplateActivity //骨骼模板选择类
        |-- HumanLivenessDetectionActivity // 活体检测
        |-- HandKeypointActivity // 手部关键点
        |-- HandKeypointImageActivity // 手部关键点静态检测
        |-- BaseActivity  // Activity基类
        |-- StartActivity  // 服务入口
        |-- SettingActivity  // modulebody基本信息

## 更多场景
华为机器学习服务（HMS ML Kit） 提供机器学习套件，为开发者应用机器学习能力开发各类应用提供优质体验。
更多应用场景，可参考：[华为机器学习服务集成案例](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-case-banggood)。

## 运行步骤
 - 将本代码库克隆到本地。

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - 如果您还没有注册成为开发者，请在[AppGalleryConnect上注册并创建应用](https://developer.huawei.com/consumer/cn/service/josp/agc/index.html)。
 - agconnect-services.json文件请从[华为开发者社区](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050990353)网站申请获取。
 - 替换工程中的sample-agconnect-services.json文件。
 - 编译并且在安卓设备或模拟器上运行。

注意：

该项目中的package name不能用于申请agconnect-services.json，您可以使用自定义package name来申请agconnect-services.json。
您只需将应用级build.gradle中的applicationId修改为与所申请的agconnect-services.json相同的package name，即可体验ML Kit云侧服务。

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0)。
