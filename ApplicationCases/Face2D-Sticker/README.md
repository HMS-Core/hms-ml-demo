# Face2D Sticker
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Face2D-Sticker/README_ZH.md)

## Contents

 * [Introduction](#introduction)
 * [Project directory structure](#project-directory-structure)
 * [More Scenarios](#more-scenarios)
 * [Procedure](#procedure)
 * [Supported Environment](#supported-environment)
 * [License](#license)


## Introduction
Face2D-Sticker uses the face detection function of HUAWEI ML Kit to Identify the contour points of the face, and then stick animated stickers on the face..

This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly develop a face detection app. The purpose is to help you experience the face detection function and integrate HUAWEI ML Kit as soon as possible.

## Project directory structure

Face2D-Sticker
    |-- com.huawei.mlkit.sample
        |-- Activity
        |-- MainActivity // entry

## More Scenarios
With the face detection capability provided by HUAWEI ML Kit, you can not only develop Smile-Camera applets, but also implement various functions, such as:
1. Detect facial expressions and add different labels to photos or videos based on different expressions.
2. Track faces in the video and develop interesting facial effects.

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
  - Contour points are obtained and drawn by the FaceStickerFilter.

For details about the development procedure, please refer to [How to Integrate Face Stickers into Your Apps with HUAWEI ML Kit](https://forums.developer.huawei.com/forumPortal/en/topic/0201333611965550036?fid=0101187876626530001).

## Supported Environment
Android 4.4 or later is recommended.

## License
The sample code has obtained the [Apache 2.0 license] (https://www.apache.org/licenses/LICENSE-2.0).
