## Skeleton-Camera
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/Skeleton-Camera/README_ZH.md)

## Directory

* [Introduction](#Introduction)
* [Project directory structure](#Project directory structure)
* [More Scenarios](#More Scenarios)
* [Run Step](#Run Step)
* [Supported Environment] (#Supported Environment)
* [Licence] (#Licence)


## Introduction
The Skeleton-Camera uses the bone detection function of Huawei ML Kit to identify human body movements and take snapshots.

This demo demonstrates how to use [HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit) to quickly develop a bone capture app. The purpose is to experience bone detection and help you integrate the HUAWEI ML Kit as soon as possible.

For details about the development procedure, see (https://developer.huawei.com/consumer/cn/forum/topicview?tid=0202333916402640253&fid=18)..

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/Skeleton-Camera/start.gif" width=180 title="start" border=2>

## Project Directory Structure
BeautyCamera
|-- com.huawei.mlkit.sample
|-- Activity
|-- MainActivity // Entry
|-- HumanSkeletonActivity // Preview

## More Scenarios
Based on the text recognition capability provided by HUAWEI ML Kit, you can develop text recognition programs and implement various applications, such as:
1. Detect human bone points.
2. Action games.

## Running Procedure
- Prep.
- Add the Huawei Maven repository to the build.gradle file at the project level.
- Add SDK dependency to the build.gradle file at the application layer.
- Add the face detection model to the manifest.xml file of Android.
- Apply for the camera permission in the manifest.xml file of the Android system.

- Key steps of code development
- Dynamic permission application.
- Create a bone detector.
- Create a LensEngine.
- Invoke the lensEngine.run(holder) method to detect bones.

## Supported Environments
Android 4.4 or later is recommended.

## License
This sample code has obtained [Apache 2.0 license] (https://www.apache.org/licenses/LICENSE-2.0).

