# HMS ML Demo

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)  ![Android CI](https://github.com/HMS-Core/hms-ml-demo/workflows/Android%20CI/badge.svg)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/README_ZH.md)

## Introduction
This project contains HUAWEI ML Kit APIs and apps developed based on the HMS Core ML SDK. The project directory is as follows:

HMS ML Demo

    |-- MLKit-Sample // Provides examples of using basic functions of HUAWEI ML Kit.
    |-- ID-Photo-DIY // Uses the image segmentation function of HUAWEI ML Kit to synthesize static images of people into a certificate photo with a blue or white background.
    |-- Photo-Translate // Uses the text recognition and translation functions of HUAWEI ML Kit to translate text in static photos into the required language.
    |-- Smile-Camera // Uses the face detection function of HUAWEI ML Kit to identify whether a user is smiling and capture the smiling photos of the user.
    |-- Homework-Reader // Uses the text recognition and TTS functions of HUAWEI ML Kit to get the text in photos and convert it into audio output.
    |-- ASRSampleKotlin // Uses the automatic speech recognition function of HUAWEI ML Kit to convert speech into text in real time.
    |-- TTSSampleKotlin // Uses the text to speech function of HUAWEI ML Kit to convert text into speech and allow users to choose the volume and speed.
    |-- TranslatorKotlin // Use the automatic speech recognition, text translation, and  text to speech services of HUAWEI ML Kit to translate English speech into Chinese speech.

## Precautions

The project contains multiple independent projects. After downloading code,
you can open a project in Android Studio to build your app or add multiple apps to a project.
You do not have to create a separate project for each app. You can open setting.gradle to select the project to build.

## Question or issues
If you have questions about how to use HMS samples, try the following options:
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) is the best place for any programming questions. Be sure to tag your question with 
**huawei-mobile-services**.
- [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001) HMS Core Module is great for general questions, or seeking recommendations and opinions.

If you run into a bug in our samples, please submit an [issue](https://github.com/HMS-Core/hms-ml-demo/issues) to the Repository. Even better you can submit a [Pull Request](https://github.com/HMS-Core/hms-ml-demo/pulls) with a fix.
