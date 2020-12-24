# CrazyChristmas
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/CrazyChristmas/README_ZH.md)

## Table of Contents

  * [Introduction](#introduction)
  * [Project directory structure](#project-directory-structure)
  * [More Scenarios](#more-scenarios)
  * [Procedure](#procedure)
  * [Supported Environment](#supported-environments)
  * [License](#license)


## Introduction
   CrazyChristmas uses the hand key point recognition function of HUAWEI ML Kit to control the sled to move to catch falling goods.
   
   Obtain the [DemoAPK](https://h5hosting-drcn.dbankcdn.cn/cch5/AIBussiness-MLKit/christmas/apk_release_christmas_game.apk) for experience.

   This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly develop a red envelopes game app. The purpose is to      help you experience the hand key point function and integrate HUAWEI ML Kit as soon as possible.

## Project directory structure
CrazyChristmas
    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- GoodsActivity // game page

## More Scenarios
With the hand key point recognition capability provided by HUAWEI ML Kit, you can not only develop CrazyChristmas applets, but also implement various functions, such as:
1. Add special effects to hands.
2. Hand action switching background.

## Procedure
- Preparations
  - Add the Huawei Maven repository to the build.gradle file in the root directory of the project.
  - Add build dependency on the SDK to the build.gradle file in the app directory.
  - Add the hand key point detection model to the manifest.xml file of Android.
  - Apply for the camera permission in the manifest.xml file of Android.

- Key steps of code development
  - Submit a dynamic permission application.
  - Create a hand keypoint analyzer.
  - Create a LensEngine.
  - Call the lensEngine.run(holder) method to perform hand keypoint recognition to move sled.

## Supported Environments
Devices with Android 4.4 or later are recommended.

##  License
The face detection sample of HUAWEI ML Kit has obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
