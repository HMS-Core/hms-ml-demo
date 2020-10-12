# WoodenMan
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

## Directory

* [Introduction](#Introduction)
* [Project directory structure](#Project directory structure)
* [More Scenarios](#More Scenarios)
* [Run Step](#Run Step)
* [Supported Environment] (#Supported Environment)
* [Licence] (#Licence)


## Introduction
WoodenMan uses the human body bone detection image segmentation and face detection capability of the HUAWEI ML Kit to segment faces and replace the background.

This demo demonstrates how to use [HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit) to quickly develop an application for human bone detection Image segmentation Face detection, helping you integrate the application with HUAWEI ML Kit as soon as possible.

## Project Directory Structure
Smile-Camera
    |-- com.huawei.hms.mlkit.sample
        |-- activity
            |-- ChooserActivity // Entry
            |-- WoodenManActivity // Take a commemorative photo.
            |-- ModelGameStartOneActivity // Set the POSE to make a big breakthrough.

## More Scenarios
Based on the human body bone detection image segmentation and face detection capability provided by the HUAWEI ML Kit, you can replace the background with a face image and implement various applications, such as:
1. Detects human bones and compares different shapes to implement game-breaking.
2. Track the face and body skeleton in the video to develop fun facial and bone special effects.

## Running Procedure
- Prep.
  - Add the Huawei Maven repository to the build.gradle file at the project level.
  - Add SDK dependency to the build.gradle file at the application layer.
  - Apply for the camera permission in the manifest.xml file of the Android system.

- Key steps of code development
  - Dynamic permission application.
  - Create a human skeleton detector.
  - Create the MLFrame object used by the analyzer to detect images through android.graphics.bitmap.
  - Invoke the createImageTransactor method to segment images.
  - Invoke the createLensEngine method to initialize the human body skeleton detection and face detector.
  - Invoke the compareSimilarity method to implement bone similarity comparison.

## Supported Environments
Android 4.4 or later is recommended.

## License
This sample code has obtained [Apache 2.0 license] (https://www.apache.org/licenses/LICENSE-2.0).