# HMS ML Demo

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)  ![Android CI](https://github.com/HMS-Core/hms-ml-demo/workflows/Android%20CI/badge.svg)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/README_ZH.md)

## Introduction
This project contains apps developed based on HUAWEI ML Kit. The project directory is as follows:

|-- MLKit-Sample // ML Kit scenario-based demo, which can be obtained from the HUAWEI Developers website by scanning the QR code: https://developer.huawei.com/consumer/en/doc/development/HMSCore-Examples-V5/sample-code-0000001050265470-V5.

|-- ID-Photo-DIY // Uses the image segmentation function of HUAWEI ML Kit to synthesize static images of people into a certificate photo with a blue or white background.

|-- Smile-Camera // Uses the face detection function of HUAWEI ML Kit to identify whether a user is smiling and capture the smiling photos of the user.

|-- Photo-Translate // Uses the text recognition and translation functions of HUAWEI ML Kit to translate text in static photos into the required language.

|-- ASRSampleKotlin // Uses the automatic speech recognition function of HUAWEI ML Kit to convert speech into text in real time.

|-- TTSSampleKotlin // Uses the text to speech function of HUAWEI ML Kit to convert text into speech and allow users to choose the volume and speed.

|-- TranslatorKotlin // Use the automatic speech recognition, text translation, and  text to speech services of HUAWEI ML Kit to translate English speech into Chinese speech.

|-- PhotoReader // Use the text recognition, text translation, and TTS functions of the HUAWEI ML Kit to obtain text in photos and convert the text into audio output.

|-- Face2D-Sticker // Use the face detection function of the ML Kit to demonstrate 2D stickers.

|-- Receipt-Text-Recognition // ML Kit is used for text recognition and general text recognition.

|-- Skeleton-Camera // Use the bone detection function of the Huawei ML Kit to recognize human body movements and take snapshots.

|-- WoodenMan // The HUAWEI ML Kit supports human body bone detection, image segmentation, and face detection to segment faces and replace background images.

|-- Skeleton-Camera // uses the skeleton detection function of HUAWEI ML Kit to recognize the human movement and match the corresponding action to capture.

|-- CrazyRockets // Use the facial recognition and gesture recognition functions of Huawei ML Kit to move the rocket to avoid obstacles.

|-- CrazyShoppingCart // uses the key point recognition function of Huawei ML Kit to control the movement of the shopping cart to catch dropped items.

## Precautions

The project contains multiple independent projects. After downloading code, open the root project and it
will load all sub projects into IDE, you can execute each project individually.

#### Add a new project

The root folder already contains a generic `build.gradle` file which load latest Android Gradle plugin, AGConntect and Kotlin into classpath. So if a new project is added, you can remove the project build.gradle file unless you need to add an individual Gradle plugin for this project. Same applies for `gradle.properties`.

All projects and nested modules are defined in the root `settings.gradle` file, by adding a new project, add all module entries into the outer settings file and remove the settings file in you projects.

## Question or issues
If you want to evaluate more about HMS Core, [r/HMSCore on Reddit](https://www.reddit.com/r/HuaweiDevelopers/) is for you to keep up with latest news about HMS Core, and to exchange insights with other developers.

If you have questions about how to use HMS samples, try the following options:
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) is the best place for any programming questions. Be sure to tag your question with 
`huawei-mobile-services`.
- [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/home?fid=0101187876626530001) HMS Core Module is great for general questions, or seeking recommendations and opinions.

If you run into a bug in our samples, please submit an [issue](https://github.com/HMS-Core/hms-ml-demo/issues) to the Repository. Even better you can submit a [Pull Request](https://github.com/HMS-Core/hms-ml-demo/pulls) with a fix.
