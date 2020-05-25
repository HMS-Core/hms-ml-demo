# MLKit-Sample


## Table of Contents

 * [Introduction](#introduction)
 * [Installation](#installation)
 * [Supported Environments](#supported-environments)
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
<td><img src="https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample/resources/mainText.jpg" width=180 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample/resources/language.jpg" width=180 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample/resources/text.jpg" width=180 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample/resources/bcr.jpg" width=180 border=2></td>
</tr></table>

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample/resources/mainVision.jpg" width=180 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample/resources/imageSegmentVideo.gif" width=180 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample/resources/face.jpg" width=180 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/tree/master/MLKit-Sample/resources/object.jpg" width=180 border=2></td>
</tr></table>

To use cloud services such as text recognition, document recognition, image classification, landmark recognition,
text translation, and language detection, you need to apply for the agconnect-services.json file on
[HUAWEI Developers](https://developer.huawei.com/consumer/en/) and replace sample-agconnect-services.json
with the file in the project. For details, please refer to [Adding the AppGallery Connect Configuration File.](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-add-agc).

You can only use a custom package name to apply for the agconnect-services.json file.
In this way, you only need to change the value of applicationId in HUAWEI-HMS-MLKit-Sample\app\build.gradle to the package name used in agconnect-services.json. Then, you can use cloud services of HUAWEI ML Kit.


## Installation
Download the sample code and open it in Android Studio. Ensure that your device has been connected to the Internet and obtain the APK by building a project.


## Supported Environments
Devices with Android 4.4 or later are recommended.


##  License
The MLKit-Sample have obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
