# hms-ml-demo

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4) 

![Android CI](https://github.com/HMS-Core/hms-ml-demo/workflows/Android%20CI/badge.svg)

[English](https://github.com/HMS-Core/hms-ml-demo/blob/master/README.md) | 中文

## 工程目录结构
   HMS ML Demo


## 简介

本项目包含华为ML Kit API和基于HMS Core ML Kit SDK开发的APP。工程目录如下：

|-- MLKit-Sample // ML Kit基本功能使用示例。

|-- ID-Photo-DIY // 使用ML Kit的图片分割功能，将人物静态图片合成蓝色或白色背景的证件照。

|-- Photo-Translate // 使用ML Kit的文字识别和翻译功能，将静态照片中的文字翻译成用户所需的语言。

|-- Smile-Camera // 使用ML Kit的人脸检测功能，识别用户是否在微笑，并抓拍用户的微笑照片。

|-- Homework-Reader // 使用HUAWEI ML Kit的文字识别和TTS功能，获取照片中的文字，并转换为音频输出。
    
|-- ASRSampleKotlin // 使用ML Kit的语音自动识别功能，实时将语音转换成文字。

|-- TTSSampleKotlin // 使用ML Kit的文本语音转换功能，将文本转换成语音，用户可以选择音量、语速以及音色。

|-- TranslatorKotlin // 使用ML Kit的自动语音识别、文本翻译、文本转语音等服务，将英文语音翻译成中文语音。

## 注意事项

工程包含多个独立的工程。下载代码后，您可以在Android Studio中打开其中一个工程，
也可以将多个应用添加到同一个工程中，这种情况下，您不必为每个应用程序创建单独的项目，可以通过打开setting.gradle来选择要构建的工程。
