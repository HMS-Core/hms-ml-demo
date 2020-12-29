# MLKit-Sample
English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/README_ZH.md)

## Table of Contents

 * [Introduction](#introduction)
 * [Project directory structure](#Project-directory-structure)
 * [More Scenarios](#more-scenarios)
 * [Getting Started](#getting-started)
 * [Supported Environment](#supported-environment)
 * [License](#license)


## Introduction
This sample code describes how to use the ML Kit SDK, including the following modules:

### Text module
Module-text. including text recognition, document recognition, ID card recognition, bank card recognition, general card recognition, text translation, language detection, real-time speech recognition, speech synthesis, audio file conversion, voice recognition, text embedding, and real-time speech translation.

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/main_text.png" width=220 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/language.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/localLanguage.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/asr.jpg" width=220 border=2></td>
</tr></table>

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/tts.png" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/tts_offline.gif" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/aft.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/text.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/bcr.jpg" width=220 border=2></td>
</tr></table>

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/sound.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/text_emd.gif" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/bcr.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/asr_long.jpg" width=220 border=2></td>
</tr></table>

### Vision module
Module-vision. It includes image segmentation, image classification, object detection and tracking, location identification, image overcommitment, text image overcommitment, scene recognition, table recognition, and document correction.

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/main_vision.png" width=220 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/table.gif" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/imageSegmentVideo.gif" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/object.jpg" width=220 title="main page" border=2></td>
</tr></table>

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/image_super.jpg" width=220 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/scene.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/doc_skew.gif" width=220 border=2></td>
</tr></table>

### Body module
Module-body. including face detection, human skeleton detection, liveness detection, hand key points detection, 3D face detection, face comparison, and gesture recognition.

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/body_module.png" width=220 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/face.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/skeleton.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/handkey.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/gesture.png" width=220 border=2></td>
</tr></table>

### Custom Model Module
Module-costom. including the demonstration of the customized model of the label and the customized model of object detection.

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/main_custom.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/label_custom.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/object_custom.jpg" width=220 border=2></td>
</tr></table>

For details about the HMS Core ML SDK, please refer to [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

## Project directory structure

moduletext

    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- AsrActivity //Real-time speech recognition entry
            |-- AsrAudioActivity //Automatic Speech Recognition
            |-- TtsAnalyseActivity //Text to speech entry
            |-- AudioFileTranscriptionActivity //Audio File Transcription
            |-- IDCardRecognitionActivity // ID card recognition
            |-- BankCardRecognitionActivity // Bank card recognition
            |-- GeneralCardRecognitionActivity // General card recognition
            |-- TextRecognitionActivity  // Text recognition
            |-- RemoteDetectionActivity  // Document recognition
            |-- StartActivity  // Service entry
            |-- BaseActivity  // Activity base class
            |-- SettingActivity  // moduletext Basic Information
            |-- TranslateActivity // Translation entry
            |-- LocalTranslateActivity // LocalTranslation
            |-- RemoteTranslateActivity // RemoteTranslation
            |-- SoundDectActivity // Voice recognition
            |-- TextEmbeddingActivity // Text Embedding



modulevision

    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- ImageSegmentationActivity //Image Segmentation
            |-- LoadPhotoActivity //Image Segmentation Related
            |-- TakePhotoActivity //Background Change Related
            |-- StillCutPhotoActivity //Capture Image Related
            |-- ObjectDetectionActivity //Object detection and tracking
            |-- ImageClassificationActivity //Image classification
            |-- RemoteDetectionActivity //Landmark recognition
            |-- ImageSuperResolutionStartActivity // image super resolution entry
            |-- ImageSuperResolutionActivity // image super resolution
            |-- TextImageSuperResolutionActivity // text image super resolution
            |-- SceneStartActivity // Scene Dection entry
            |-- SceneDectionActivity // Scene Dection
            |-- TableRecognitionStartActivity // Table recognition entry
            |-- TableRecognitionActivity // Table recognition
            |-- DocumentSkewStartActivity // Document correction entry
            |-- DocumentSkewCorretionActivity // Document correction
            |-- StartActivity  // Service entry
            |-- BaseActivity  // Activity base class
            |-- SettingActivity  // modulevision Basic Information


modulebody

    |-- com.mlkit.sample
        |-- Activity
            |-- FaceDetectionActivity //Face detection
            |-- Live3DFaceAnalyseActivity //3D face detection
            |-- FaceVerificationActivity // Face match
            |-- HumanSkeletonActivity // Human skeleton
            |-- TemplateActivity // Bone Template Selection Class
            |-- HumanLivenessDetectionActivity // Liveness detection
            |-- HandKeypointActivity // Key points of the hand
            |-- HandKeypointImageActivity // Hand static detection
            |-- GestureActivity // Gesture recognition
            |-- GestureImageActivity // Static detection of gesture recognition
            |-- StartActivity  // Service entry
            |-- BaseActivity  // Activity base class
            |-- SettingActivity  // modulebody Basic Information

modulecostom

    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- CustModelActivity //Cust model service entry
            |-- CustModelLabelActivity // Demonstration entry for custom model labels
            |-- CustModelObjectActivity // Demonstration entry for custom model object detection
            |-- BaseActivity // Base class of an activity.
            |-- SettingActivity // Basic module costom information

## More Scenarios
HUAWEI ML Kit allows your apps to easily leverage Huawei's long-term proven expertise in machine learning to support diverse artificial intelligence (AI) applications throughout a wide range of industries.
For more application scenarios, see: [Huawei Machine Learning Service Integration Cases.](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-case-banggood)

## Getting Started
 - Clone the code library to the local computer.

       git clone https://github.com/HMS-Core/hms-ml-demo.git

 - If you have not registered as a developer, [register and create an app in AppGallery Connect](https://developer.huawei.com/consumer/en/service/josp/agc/index.html).
 - Obtain the agconnect-services.json file from [Huawei Developers](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050990353).
 - Replace the sample-agconnect-services.json file in the project.
 - Compile and run on an Android device or simulator.

Attention:

You can only use a custom package name to apply for the agconnect-services.json file.
In this way, you only need to change the value of applicationId in HUAWEI-HMS-MLKit-Sample\app\build.gradle to the package name used in agconnect-services.json. Then, you can use cloud services of HUAWEI ML Kit.

## Supported Environments
Devices with Android 4.4 or later are recommended.


##  License
The MLKit-Sample have obtained the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
