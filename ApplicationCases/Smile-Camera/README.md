# Smile-Camera
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/Smile-Camera/README_ZH.md)
## Contents

 * [Introduction](#Introduction)
 * [Project directory structure](#Project-directory-structure)
 * [More Scenarios](#More-Scenarios)
 * [Procedure](#Procedure)
 * [Supported Environment](#Supported-Environment)
 * [License](#License)


## Introduction
Smile-Camera uses the face detection function of HUAWEI ML Kit to identify whether a user is smiling and capture the smiling photos of the user.

This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly develop a face detection app. The purpose is to help you experience the face detection function and integrate HUAWEI ML Kit as soon as possible.

## Project directory structure
Smile-Camera

    |-- com.mlkit.sample
        |-- Activity
            |-- MainActivity // entry
            |-- LiveFaceAnalyseActivity // Smile detection

## More Scenarios
With the face detection capability provided by HUAWEI ML Kit, you can not only develop Smile-Camera applets, but also implement various functions, such as:
1. Detect facial expressions and add different labels and stickers to photos or videos based on different expressions.
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
  - Determine whether the smile degree exceeds the threshold. If so, take a photo.

## Supported Environment
Android 4.4 or later is recommended.

## License
The sample code has obtained the [Apache 2.0 license] (https://www.apache.org/licenses/LICENSE-2.0).
