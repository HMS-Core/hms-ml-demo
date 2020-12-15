## Receipt-Text-Recognition
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Receipt-Text-Recognition/README_ZH.md)

## Table of Contents

  * [Introduction](#Introduction)
  * [Project directory structure](#Project-directory-structure)
  * [More Scenarios](#More-Scenarios)
  * [Procedure](#Procedure)
  * [Supported Environment](#Supported-Environment)
  * [License](#License)


## Introduction
The sample code describes how to use the text recognition service provided by the HMS Core ML SDK to recognize bill number.

More information please refer: [How to Implement the Automatic Bill Number Input Function Using HUAWEI ML Kit’s Text Recognition](https://forums.developer.huawei.com/forumPortal/en/topicview?tid=0202344967934290124&fid=0101187876626530001)
    
This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly develop a text recognition app. The purpose is to help you experience the text detection function and integrate HUAWEI ML Kit as soon as possible.

## Project directory structure
Receipt-Text-Recognition

    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- MainActivity // entry
            |-- CameraActivity // text recognition
            |-- OcrDetectorProcessor // result

## More Scenarios
With the text recognition capability provided by HUAWEI ML Kit, you can not only develop Receipt-Text-Recognition applets, but also implement various functions, such as:
1. Quick text entry.
2. Universal card recognition.

## Procedure
- Preparations
  - Add the Huawei Maven repository to the build.gradle file in the root directory of the project.
  - Add build dependency on the SDK to the build.gradle file in the app directory.
  - Add the text recognition model to the manifest.xml file of Android.
  - Apply for the camera permission in the manifest.xml file of Android.

- Key steps of code development
  - Submit a dynamic permission application.
  - Create a text analyzer.
  - Create a LensEngine.
  - Call the lensEngine.run(holder) method to perform text recognition.

## Supported Environments
    Devices with Android 4.4 or later are recommended.

##  License
    The face detection sample of HUAWEI ML Kit has obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).

