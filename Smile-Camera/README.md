# Smile-Camera

## Table of Contents

 * [Introduction](#introduction)
 * [Supported Environments](#supported-environments)
 * [Sample-Code](#Sample-Code)
 * [License](#license)


## Introduction
The Smile-Camera uses the face detection function of HUAWEI ML Kit to identify whether a user is smiling and capture the smiling photos of the user.


## Supported Environments
Devices with Android 4.4 or later are recommended.


## Sample Code
Sample code majors activity as follows:
   1. Choose LiveFaceAnalyseActivity to see a demo of the following:
      - Face detection (Detects and tracks faces)
      - Camera front and back flip


   Ability called by the sample:
   1. Face Recognition:
      - MLAnalyzerFactory.getInstance().GetFaceAnalyzer(MLFaceAnalyzerSetting settings): Create a face recognizer. This is the most core class of face recognition.
      - MLFaceAnalyzer.setTransactor(): Set the face recognition result processor for subsequent processing of the recognition result.
      - MLFaceAnalyzerSetting.Factory().SetFeatureType (MLFaceAnalyzerSetting.TYPE_FEATURES): Turn on facial expression and feature detection, including smile, eyes open, beard and age.
      - MLFaceAnalyzerSetting.Factory().AllowTracing (): Whether to start face tracking mode.

##  License
The Smile-Camera have obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
