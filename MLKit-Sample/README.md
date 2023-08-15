# MLKit-Sample
English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/README_ZH.md)

## Table of Contents

 * [Introduction](#introduction)
 * [Project directory structure](#project-directory-structure)
 * [More Scenarios](#more-scenarios)
 * [Getting Started](#getting-started)
 * [Supported Environments](#supported-environments)
 * [License](#license)


## Introduction
Sample code demoing features of the [ML Kit](https://developer.huawei.com/consumer/en/doc/development/hiai-Guides/service-introduction-0000001050040017) and split into modules:

### Text module (`module-text`)
**Features:** text recognition, document recognition, ID card recognition, bank card recognition, general card recognition, text translation, language detection, real-time speech recognition, speech synthesis, audio file conversion, voice recognition, text embedding, and real-time speech translation.

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

### Vision module (`module-vision`)
**Features:** image segmentation, image classification, object detection and tracking, landmark recognition, image super-resolution, text image super-resolution, scene detection, table/form recognition, and document skew correction.

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

### Body module (`module-body`)
**Features:** face detection, skeleton detection, liveness detection, hand key points detection, 3D face detection, face comparison, and hand gesture recognition.

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/body_module.png" width=220 title="main page" border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/face.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/skeleton.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/handkey.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/gesture.png" width=220 border=2></td>
</tr></table>

### Custom Model Module (`module-custom`)
**Features:** use customized models for label and object detection.

<table><tr>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/main_custom.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/label_custom.jpg" width=220 border=2></td>
<td><img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/MLKit-Sample/resources/object_custom.jpg" width=220 border=2></td>
</tr></table>

## Project directory structure
`module-text`

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

`module-vision`

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

`module-body`

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

`module-custom`

    |-- com.huawei.mlkit.sample
        |-- Activity
            |-- CustModelActivity //Cust model service entry
            |-- CustModelLabelActivity // Demonstration entry for custom model labels
            |-- CustModelObjectActivity // Demonstration entry for custom model object detection
            |-- BaseActivity // Base class of an activity.
            |-- SettingActivity // Basic module costom information

## More Scenarios
HUAWEI ML Kit allows your apps to easily leverage Huawei's long track record in machine learning to support diverse artificial intelligence (AI) applications throughout a wide range of industries. Check out these [success stories](https://developer.huawei.com/consumer/en/doc/development/hiai-Guides/ml-case-banggood-0000001050990463).

## Getting Started
 - If you are not yet a Huawei developer, register an account [here](https://developer.huawei.com/consumer/en/doc/start/10104)
 - Login with your Huawei developer credentials to [AppGallery Connect](https://developer.huawei.com/consumer/en/service/josp/agc/index.html) (**AGC**)
 - Use one of your existent projects/apps or [create a new project and apps](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-help-projectintro-0000001146614683). You will need a separate app for each module and it makes sense to have them under the same project so that the API key and the enabled ML services are shared between apps.
 - In your project, access the *Manage APIs* tab and enable *ML Kit* (optionally, there are additional [various configuration](https://developer.huawei.com/consumer/en/doc/development/hiai-Guides/enable-service-0000001050038078) options under the dedicated *ML Kit* section under *Build*)
 - For each app, add in **AGC** the *SHA-256* fingerprint of the used signing configuration ([how to](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050166285#EN-US_TOPIC_0000001054452903__section10260203515546)) even if you're using the default debug keystore (`$ ./gradlew signingReport` reveals the signing certificates used and the corresponding *SHA-256* fingerprints)
 - Obtain the corresponding `agconnect-services.json` from each app on **AGC** and copy to the corresponding module's directory ([how to](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-get-started-android-0000001058210705##section641530103413))

 - Finally, replace the *applicationId* of each module (under `<module>/build.gradle`) with the package name entered when creating the apps in **AGC ** (at step #3)

## Supported Environments
Devices with Android 4.4 or later are recommended.


##  License
The MLKit-Sample have obtained the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
