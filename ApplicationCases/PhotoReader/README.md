# Photo Reader

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/PhotoReader/README_ZH.md)
## Contents

 * [Introduction](#introduction)
 * [More Scenarios](#more-scenarios)
 * [Procedure](#procedure)
 * [Supported Environment](#supported-environment)
 * [License](#license)


## Introduction

PhotoReader uses the text recognition and TTS functions of HUAWEI ML Kit to get the text in photos and convert it into audio output. Currently, the following languages are supported: simplified Chinese and English for TTS.
PhotoReader uses the text recognition, translate and TTS functions of HUAWEI ML Kit to get the text in photos and convert it into audio output. 

This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly implement the image reading function in your app. The purpose is to help you experience the text recognition, translate and TTS functions and integrate HUAWEI ML Kit as soon as possible.

##### Following Modules are available

- App Module, which is written in Java fully
- Kotlin Module, which is written in Kotlin fully
- Lensengine Module, which is encapsulating the camera implementation based on old android camera API, written in Java

## Project Directory Structure
PhotoReader
    |-- com.huawei.mlkit.sample.photoreader
        |-- Activity
            |-- MainActivity //Entry
            |-- ReadPhotoActivity // Function interface

## More Scenarios
Huawei's text recognition and translation services can help you implement more interesting and powerful functions, such as:
- General text recognition
  - Recognition of text in bus license plates
  - Recognition of text in documents

- TTS
  - Novel reading
  - Navigation voice


## Procedure
 - Clone the code library to the local computer.

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - If you have not registered as a developer, [register and create an app in AppGallery Connect](https://developer.huawei.com/consumer/en/service/josp/agc/index.html).
 - Obtain the agconnect-services.json file from [Huawei Developers](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050990353).
 - Replace the sample-agconnect-services.json file in the project.
 - Compile and run on an Android device or simulator.


## Supported Environment
Android 4.4 or later is recommended.

## License
The sample code has obtained the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
