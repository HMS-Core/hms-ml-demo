# Translator
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/TranslatorKotlin/README_ZH.md)
## Contents

 * [Introduction](#Introduction)
 * [Project directory structure](#Project-directory-structure)
 * [More Scenarios](#More-Scenarios)
 * [Procedure](#Procedure)
 * [Supported Environment](#Supported-Environment)
 * [License](#License)


## Introduction
Translator uses HUAWEI ML Kit to translate English into Chinese in real time through the speech recognition, text translation, and text-to-speech capabilities. Through customized development, Translator facilitates communication between people in different countries.

For details about the service introduction and access guide, visit the following website:

[HUAWEI ML Kit Development Guide](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

[HUAWEI ML Kit API Reference](https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/commonoverview-0000001050169365-V5)

## Project directory structure
TranslatorKotlin

    |-- com.sample.translator
        |-- Activity
            |-- TranslatorActivity // Translator entry


## More Scenarios
Real-time speech recognition, text translation, and text-to-speech functions are applicable to a wider range of scenarios, such as:
1. Automatic speech recognition
Convert speech into text in real time. It is applicable to various scenarios such as the voice chat, voice input, voice search, voice order placement, and voice command. Audios such as meeting records, notes, and summaries can be converted into text in real time for content recording and real-time display.
2. Text translation
Translate text into various languages. It is applicable to scenarios such as movie subtitle translation and chat content translation.
3. Text to speech
Convert text into speech. It is applicable to various scenarios such as news reading, audio novels, stock information broadcast, voice navigation, and video dubbing.

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
