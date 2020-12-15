# Gesture-Change-Background
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Gesture-Change-Background)

## Table of Contents

  * [Introduction](#introduction)
  * [Project directory structure](#project-directory-structure)
  * [More Scenarios](#more-scenarios)
  * [Procedure](#procedure)
  * [Supported Environments](#supported-environments)
  * [License](#license)


## Introduction
Gesture-Change-Background uses the hand key point recognition and image segmentation function of HUAWEI ML Kit to switch the background by waving your hand。
    
This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly develop a red envelopes game app. The purpose is to help you experience the hand key point function and integrate HUAWEI ML Kit as soon as possible.

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Gesture-Change-Background/background.gif" width=180 title="start" border=2>

## Project directory structure
Gesture-Change-Background
    |-- com.huawei.mlkit.sample
        |-- activity
            |-- MainActivity // entry
            |-- BackgroundActivity // background page

## More Scenarios
With the hand key point recognition capability and image segmentation provided by HUAWEI ML Kit, you can not only develop Gesture-Change-Background applets, but also implement various functions, such as:
1. Add special effects to hands.
2. Hand action to take portrait photo.

## Procedure
- Preparations
  - Add the Huawei Maven repository to the build.gradle file in the root directory of the project.
  - Add build dependency on the SDK to the build.gradle file in the app directory.
  - Add the hand key point detection model and image segmentation model to the manifest.xml file of Android.
  - Apply for the camera permission in the manifest.xml file of Android.

- Key steps of code development
  - Submit a dynamic permission application.
  - Create a MLCompositeAnalyzer analyzer to add a hand keypoint analyzer and a image segmentation analyzer.
  - Create a LensEngine.
  - Call the lensEngine.run(holder) method to perform hand keypoint recognition to switch background.

For details about the development procedure, please refer to [Changing the Background with a Wave of the Hand to Deliver an Immersive Live Streaming Experience](https://forums.developer.huawei.com/forumPortal/en/topic/0203399495801750086?ha_source=hms1).

## Supported Environments
Devices with Android 4.4 or later are recommended.

##  License
The face detection sample of HUAWEI ML Kit has obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
