# MLKit-Sample
English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/README_ZH.md)

## Table of Contents

 * [Introduction](#introduction)
 * [More Scenarios](#more-scenarios)
 * [Getting Started](#getting-started)
 * [Supported Environment](#supported-environment)
 * [License](#license)


## Introduction
The sample code is used to describe how to use the HMS Core ML SDK. The code consists of the following modules:

Text module: Module-text. This module is used for text recognition, document recognition,
ID card recognition, bank card recognition, general card recognition, text translation, language detection, text to speech and automatic speech recognition.

Vision module: Module-vision. This module is used for face detection, image segmentation,
product visual search, image classification, object detection and tracking, and landmark recognition.

For details about the HMS Core ML SDK, please refer to [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

APK running page screenshots are as follows.
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

## More Scenarios
HUAWEI ML Kit allows your apps to easily leverage Huawei's long-term proven expertise in machine learning to support diverse artificial intelligence (AI) applications throughout a wide range of industries.
For more application scenarios, see: [Huawei Machine Learning Service Integration Cases.](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-case-banggood)

## Getting Started
 - Clone the code library to the local computer.

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - If you have not registered as a developer, [register and create an app in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/start/10115).
 - Obtain the agconnect-services.json file from [Huawei Developers](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-add-agc).
 - Replace the sample-agconnect-services.json file in the project.
 - Compile and run on an Android device or simulator.

Attention:

You can only use a custom package name to apply for the agconnect-services.json file.
In this way, you only need to change the value of applicationId in HUAWEI-HMS-MLKit-Sample\app\build.gradle to the package name used in agconnect-services.json. Then, you can use cloud services of HUAWEI ML Kit.

## Supported Environments
Devices with Android 4.4 or later are recommended.


##  License
The MLKit-Sample have obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
