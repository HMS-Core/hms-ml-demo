# SceneEnhance
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/SceneEnhance/README_ZH.md)

## Contents

 * [Introduction](#Introduction)
 * [Project directory structure](#Project directory structure)
 * [More Scenarios](#More Scenarios)
 * [Procedure](#Procedure)
 * [Supported Environment](#Supported Environment)
 * [License](#License)


## Introduction
SceneEnhance uses scene recognition function of HUAWEI ML Kit to intelligently decorate the scene in the picture.

This demo demonstrates how to use [HUAWEI ML Kit] (https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly develop modify the scene recognition app. The purpose is to let you experience the functions of OCR and help you integrate Huawei ml kit as soon as possible.

## Project directory structure
SceneEnhance
    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- SceneActivity // Scenarios to modify

## More Scenarios
With the Scene detection capability provided by HUAWEI ML Kit, you can not only develop SceneEnhance applets, but also implement various functions, such as:
1. Identify the scene in the photo, and describe or classify the stored image information.
2. And describe the special environment, such as the name of the surrounding trees.

## Procedure
- Preparations
  - Add the Huawei Maven repository to the build.gradle file in the root directory of the project.
  - Add build dependency on the SDK to the build.gradle file in the app directory.
  - Add the Scene detection model to the manifest.xml file of Android.
  - Apply for the camera permission in the manifest.xml file of Android.

- Key steps of code development
  - Submit a dynamic permission application.
  - Create a text analyzer.
  - Use android.graphics.bitmap to create an MLFrame object for the analyzer to detect scene.
  - Call the asyncAnalyseFrame method to scene recognition.

## Supported Environment
Android 4.4 or later is recommended.

## License
The sample code has obtained the [Apache 2.0 license] (https://www.apache.org/licenses/LICENSE-2.0).
