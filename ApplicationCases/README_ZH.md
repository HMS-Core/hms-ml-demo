# hms-ml-demo

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5) ![Android CI](https://github.com/HMS-Core/hms-ml-demo/workflows/Android%20CI/badge.svg)

[English](https://github.com/HMS-Core/hms-ml-demo/blob/master/README.md) | 中文


## 简介

本项目包含基于华为机器学习服务(ML Kit)开发的应用案例。工程目录如下：

|-- ID-Photo-DIY // 使用ML Kit的图片分割功能，将人物静态图片合成蓝色或白色背景的证件照。

|-- Smile-Camera // 使用ML Kit的人脸检测功能，识别用户是否在微笑，并抓拍用户的微笑照片。

|-- Photo-Translate // 使用ML Kit的文字识别和翻译功能，将静态照片中的文字翻译成用户所需的语言。

|-- ASRSampleKotlin // 使用ML Kit的语音自动识别功能，实时将语音转换成文字。

|-- TTSSampleKotlin // 使用ML Kit的文本语音转换功能，将文本转换成语音，用户可以选择音量、语速以及音色。

|-- TranslatorKotlin // 使用ML Kit的自动语音识别、文本翻译、文本转语音等服务，将英文语音翻译成中文语音。

|-- PhotoReader // 使用HUAWEI ML Kit的文字识别，文本翻译和TTS功能，获取照片中的文字，并进行翻译后转换为音频输出。

|-- Face2D-Sticker // 使用ML Kit的人脸检测功能，为用户演示2D贴纸。

|-- Receipt-Text-Recognition // 使用ML Kit的文字识别，通用文本识别。

|-- Skeleton-Camera // 使用华为ML Kit的骨骼检测功能来识别人体动作并抓拍。
    
|-- WoodenMan // 通过HUAWEI ML Kit的人体骨骼检测 图像分割 人脸检测能力，给人像分割和照片背景替换。

|-- Skeleton-Camera // 使用华为ML Kit的骨骼检测功能来识别人体动作并匹配对应动作进行抓拍。

|-- CrazyRockets // 使用华为ML Kit的人脸识别功能和手势识别功能来移动小火箭躲避障碍物。

|-- CrazyShoppingCart // 使用华为ML Kit的手部关键点识别功能来控制购物车进行移动以接住掉落的商品。

|-- Gesture-Change-Background // 使用华为ML Kit的手部关键点识别和图像分割功能通过挥动手臂来切换背景。

|-- SceneEnhance // 使用华为ML Kit的场景识别功能，智能修饰图片中的场景。

|-- CrazyChristmas // 使用华为ML Kit的手部关键点识别功能来控制雪橇进行移动以接住掉落的礼物。

## 注意事项

ApplicationCases 工程包含多个独立的工程。下载代码后，您需要打开根工程，将所有的嵌套工程加载到IDE，您可以单独运行每个工程。

#### 添加新的工程

根文件夹已经包含一个通用的build.gradle，该将最新的Android Gradle插件,AGConntect和Kotlin加载到类classpath。因此，如果添加新工程，删除工程的build.gradle文件，除非需要为此工程添加独特的Gradle插件。同样适用于`gradle.properties`。

所有工程和嵌套模块都在根`settings.gradle`设置文件中定义，添加新的工程，将所有模块条目添加到外部设置文件中，然后删除工程的设置文件。

## 技术支持
如果您对HMS Core还处于评估阶段，可在[Reddit社区](https://www.reddit.com/r/HuaweiDevelopers/)获取关于HMS Core的最新讯息，并与其他开发者交流见解。

如果您对使用HMS示例代码有疑问，请尝试：
- 开发过程遇到问题上[Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services)，在`huawei-mobile-services`标签下提问，有华为研发专家在线一对一解决您的问题。
- 到[华为开发者论坛](https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core板块与其他开发者进行交流。

如果您在尝试示例代码中遇到问题，请向仓库提交[issue](https://github.com/HMS-Core/hms-ml-demo/issues)，也欢迎您提交[Pull Request](https://github.com/HMS-Core/hms-ml-demo/pulls)。
