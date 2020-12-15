## CrazyRockets
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/CrazyRockets/README_ZH.md)

## Directory

* [Introduction](#introduction)
* [Project directory structure](#project-directory-structure)
* [More Scenarios](#more-scenarios)
* [Procedure](#procedure)
* [Supported Environments](#supported-environments)
* [License] (#license)


## Introduction
CrazyRockets uses the facial recognition and gesture recognition functions of Huawei ML Kit to move small rockets to avoid obstacles.

This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/cn/hms/huawei-mlkit) to quickly develop Crazy Rocket applications. The purpose is to experience facial recognition and gesture recognition, helping you integrate HUAWEI ML Kit as soon as possible.

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/CrazyRockets/hand.gif" width=180 title="start" border=2></td>
</tr></table>

## Project directory structure
BeautyCamera
    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- MainActivity // entry
            |-- FaceGameActivity // Facial recognition
            |-- HandGameActivity // Gesture recognition

## More Scenarios
With the face recognition capability and gesture recognition capability provided by HUAWEI ML Kit, you can not only develop CrazyRockets applets, but also implement various functions, such as:
1. Gesture capture.
2. Smile capture.

## Procedure
- Preparations
  - Add the Huawei Maven repository to the build.gradle file in the root directory of the project.
  - Add build dependency on the SDK to the build.gradle file in the app directory.
  - Add the face detection model to the manifest.xml file of Android.
  - Apply for the camera permission in the manifest.xml file of Android.

- Key steps of code development
  - Submit a dynamic permission application.
  - Create a skeleton analyzer.
  - Create a LensEngine.
  - Call the lensEngine.run(holder) method to perform CrazyRockets.

For details about the development procedure, see [Crazy Rockets - How to Integrate Face Detection and Gesture Recognition with Huawei HMS ML Kit to Create a Hilarious Game](https://forums.developer.huawei.com/forumPortal/en/topic/0203394887034330038?fid=0101187876626530001?ha_source=hms1).

## Supported Environments
Devices with Android 4.4 or later are recommended.

##  License
The face detection sample of HUAWEI ML Kit has obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).

