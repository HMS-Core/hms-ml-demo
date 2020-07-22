# TTSSample
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/TTSSampleKotlin/README_ZH.md)
## Contents

 * [Introduction](#Introduction)
 * [Project directory structure](#Project directory structure)
 * [More Scenarios](#More Scenarios)
 * [Procedure](#Procedure)
 * [Supported Environment](#Supported Environment)
 * [License](#License)


## Introduction
TTSSample uses the text-to-speech capability of HUAWEI ML Kit to convert text into speech. It realizes the function of speaking out immediately and generates speech that mimics human voices naturally, providing emotional and personalized text-to-speech services.

For details about the service introduction and access guide, visit the following website:
[HUAWEI ML Kit Development Guide] (https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)
[HUAWEI ML Kit API Reference] (https://developer.huawei.com/consumer/en/doc/development/HMS-References/mltts-4)

## Project directory structure

TTSSampleKotlin
    |-- com.sample.ttssamplekotlin
        |-- Activity
            |-- TtsActivity // Text to speech

## More Scenarios
The text-to-speech function provided by HUAWEI ML Kit is also applicable to the following scenarios:
News reading, audio novels, stock information broadcast, voice navigation, and video dubbing.

## Procedure
 - Clone the code library to the local computer.

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - If you have not registered as a developer, [register and create an app in AppGallery Connect] (https://developer.huawei.com/consumer/en/doc/start/10115).
 - Obtain the agconnect-services.json file from [Huawei Developers] (https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-add-agc).
 - Replace the sample-agconnect-services.json file in the project.
 - Compile and run on an Android device or simulator.

For details about the development procedure, please refer to [Machine Learning made Easy: - Text To Speech using Kotlin and Huawei ML Kit] (https://forums.developer.huawei.com/forumPortal/en/topicview?tid=0201272341735060076&fid=0101187876626530001).

## Supported Environment
Android 4.4 or later is recommended.

## License
The sample code has obtained the [Apache 2.0 license] (http://www.apache.org/licenses/LICENSE-2.0).