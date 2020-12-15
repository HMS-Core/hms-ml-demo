# ASRSample
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/ASRSampleKotlin/README_ZH.md)
## Contents

 * [Introduction](#introduction)
 * [Project directory structure](#project-directory-structure)
 * [More Scenarios](#more-scenarios)
 * [Getting Started](#getting-started)
 * [Supported Environment](#supported-environment)
 * [License](#license)


## Introduction
ASRSample uses the real-time speech recognition function of HUAWEI ML Kit to convert real-time speech into text. Currently, ASRSample can recognize Mandarin Chinese, English, and French.

For details about the service introduction and access guide, visit the following website:
[HUAWEI ML Kit Development Guide](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)
[HUAWEI ML Kit API Reference](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/asrsdkoverview-0000001050747393-V5)

## Project directory structure
AsrSampleKotlin
    |-- com.sample.asrsamplekotlin
        |-- Activity
            |-- AsrAudioActivity //Automatic Speech Recognition

## More Scenarios
The real-time speech recognition function provided by HUAWEI ML Kit is also applicable to the following scenarios:
1. Speech input via mobile phone apps
Convert speech into text in real time. It is applicable to various scenarios such as the voice chat, voice input, voice search, voice order placement, and voice command.
2. Real-time automatic speech recognition (RASR)
Audios such as meeting records, notes, and summaries can be converted into text in real time for content recording and real-time display.

## Getting Started
 - Clone the code library to the local computer.

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - If you have not registered as a developer, [register and create an app in AppGallery Connect](https://developer.huawei.com/consumer/en/service/josp/agc/index.html).
 - Obtain the agconnect-services.json file from [Huawei Developers](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050990353).
 - Replace the sample-agconnect-services.json file in the project.
 - Compile and run on an Android device or simulator.

For details about the development procedure, please refer to [Machine Learning made Easy: - Automatic Speech Recognition using Kotlin and Huawei ML Kit](https://forums.developer.huawei.com/forumPortal/en/topicview?tid=0201264568431750009&fid=0101187876626530001).

## Supported Environment
Android 4.4 or later is recommended.

## License
The sample code has obtained the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
