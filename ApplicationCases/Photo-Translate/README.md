# Photo Translate
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Photo-Translate/README_ZH.md)
## Contents

 * [Introduction](#Introduction)
 * [Project directory structure](#Project-directory-structure)
 * [More Scenarios](#More-Scenarios)
 * [Procedure](#Procedure)
 * [Supported Environment](#Supported-Environment)
 * [License](#License)


## Introduction
Photo-Translate uses the text recognition and translation functions of HUAWEI ML Kit to translate the text in static photos into the required language. Currently, the following languages are supported: simplified Chinese, English, French, Arabic, Thai, Spanish, Turkish, Portuguese, Japanese, German, Italian, and Russian.

This demo demonstrates how to use [HUAWEI ML Kit] (https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly implement the image translation function in your app. The purpose is to help you experience the text recognition and translation functions and integrate HUAWEI ML Kit as soon as possible.

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/Photo-Translate/Photo%20Translate.gif" width=250 title="ID Photo DIY" div align=center border=5>

## Project directory structure
Photo-Translate

    |-- com.mlkit.sample.phototranslate
        |-- Activity
            |-- MainActivity // entry
            |-- RemoteTranslateActivity // RemoteTranslate
            |-- CapturePhotoActivity // photo

## More Scenarios
- Huawei's text recognition and translation services can help you implement more interesting and powerful functions, such as:
  - General text recognition
  - Recognition of text in bus license plates
  - Recognition of text in documents

- Translation
  - Roadmap and sign translation
  - Document translation
  - Web page translation. For example, recognize the language of website comments and translate the comments into the language of the corresponding country.
  - Introduction to and translation of products outside China
  - Canteen menu translation

## Procedure
 - Clone the code library to the local computer.

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - If you have not registered as a developer, [register and create an app in AppGallery Connect](https://developer.huawei.com/consumer/en/service/josp/agc/index.html).
 - Obtain the agconnect-services.json file from [Huawei Developers](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050990353).
 - Replace the sample-agconnect-services.json file in the project.
 - Compile and run on an Android device or simulator.

For details about the development procedure, please refer to [How to use Huawei HMS ML Kit service to quickly develop a photo translation app](https://forums.developer.huawei.com/forumPortal/en/topicview?tid=0201257535948780270&fid=0101187876626530001).

## Supported Environment
Android 4.4 or later is recommended.

## License
The sample code has obtained the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
