## BeautyCamera
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/BeautyCamera/README_ZH.md)

## Table of Contents

  * [Introduction](#Introduction)
  * [Project directory structure](#project-directory-structure)
  * [More Scenarios](#more-scenarios)
  * [Procedure](#procedure)
  * [Supported Environment](#supported-environment)
  * [License](#license)


## Introduction
BeautyCamera uses the face detection function of HUAWEI ML Kit to identify a face and beauty it.
    
This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly develop a face detection app. The purpose is to help you experience the face detection function and integrate HUAWEI ML Kit as soon as possible.

## Project directory structure
BeautyCamera

    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- MainActivity // entry
            |-- CameraActivity // camera
            |-- BeautyActivity // photo processing
        |-- utils
            |-- ImageHelper // image processing tool class

## More Scenarios
With the face recognition capability provided by HUAWEI ML Kit, you can not only develop BeautyCamera applets, but also implement various functions, such as:
1. Face recognition access control.
2. Expression capture

## Procedure
- Preparations
  - Add the Huawei Maven repository to the build.gradle file in the root directory of the project.
  - Add build dependency on the SDK to the build.gradle file in the app directory.
  - Add the face detection model to the manifest.xml file of Android.
  - Apply for the camera permission in the manifest.xml file of Android.

- Key steps of code development
  - Submit a dynamic permission application.
  - Create a face analyzer.
  - Use android.graphics.bitmap to create an MLFrame object for the analyzer to detect images.
  - Call the asyncanalyseframe method to perform face detection.
  - Call the method in ImageHlper to beauty photo.

For details about the development procedure, please refer to [How to Implement Eye-Enlarging and Face-Shaping Functions Using HUAWEI ML Kit's Face Detection Capability](https://forums.developer.huawei.com/forumPortal/en/topic/0201379573209780438?ha_source=hms1).

## Supported Environments
Devices with Android 4.4 or later are recommended.

##  License
The face detection sample of HUAWEI ML Kit has obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).

