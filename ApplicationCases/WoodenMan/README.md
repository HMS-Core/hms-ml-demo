# WoodenMan
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/WoodenMan/README_ZH.md)

## Directory

* [Introduction](#introduction)
* [Project directory structure](#project-directory-structure)
* [More Scenarios](#more-scenarios)
* [Running Procedure](#running-procedure)
* [Supported Environments](#supported-environment)
* [License](#license)


## Introduction
WoodenMan uses the body bone detection, image segmentation, and face detection capabilities of HUAWEI ML Kit to segment faces and replace the background.

This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/cn/hms/huawei-mlkit) to quickly develop human body bone detection, image segmentation, and face detection applications, helping you integrate with HUAWEI ML Kit as soon as possible.

## Project Directory Structure
Smile-Camera
|-- com.huawei.hms.mlkit.sample
    |-- activity
        |-- ChooserActivity // Entry
        |-- WoodenManActivity // Take a commemorative photo.
        |-- ModelGameStartOneActivity // Set the POSE to make a big breakthrough.
        |-- TongueTwisterActivity // Tongue twister to make a big breakthrough.
        |-- RecruitRulesActivity // Rules of the tongue twister game.

## More Scenarios
The HUAWEI ML Kit provides human body skeleton detection, image segmentation, face detection and ASR capabilities to replace the background of a face image and implement various applications, And writing out the words, such as:

1. Detects human bones and compares different shapes to implement game-breaking.

2. Track the face and body skeleton in the video to develop fun facial and bone special effects.

3. Send an instant message via voice input text.

## Running Procedure
- Clone the code library to the local host.
    
    git clone: https://github.com/HMS-Core/hms-ml-demo.git
- Compile and run on an Android device or simulator.

- More detailed development steps
    - Prep.
        - Add the Huawei Maven repository to the build.gradle file at the project level.
        - Add SDK dependency to the build.gradle file at the application layer.
        - Apply for the camera permission in AndroidManifest.xml.

    - Key steps of code development
        - Dynamic permission application.
        - Create a human skeleton detector.
        - Create an MLFrame object for the analyzer to detect images through android.graphics.bitmap.
        - Invoke the createImageTransactor method to segment images.
        - Invoke the createLensEngine method to initialize the human body skeleton detection and face detector.
        - Invoke the compareSimilarity method to implement bone similarity comparison.
        - Invoke the createAsrRecognizer method to implement speech recognizer。

For details about the development procedure, please refer to [How to Integrate ML Kit's Virtual Human Service](https://forums.developer.huawei.com/forumPortal/en/topic/0201390554975760549?ha_source=hms1).

## Supported Environments
Android 4.4 or later is recommended.

## License
This sample code has obtained [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
