# ID Photo DIY
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/ApplicationCases/ID-Photo-DIY/README_ZH.md)
## Contents

 * [Introduction](#Introduction)
 * [Project directory structure](#Projectdirectorystructure)
 * [More Scenarios](#MoreScenarios)
 * [Procedure](#Procedure)
 * [Supported Environment](#SupportedEnvironment)
 * [License](#License)


## Introduction
ID-Photo-DIY uses the image segmentation function to synthesize a static face image into a certificate photo with a blue or white background.

This demo demonstrates how to use [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit) to quickly develop an ID-Photo-DIY applet. The purpose is to enable you to experience the image segmentation function and integrate HUAWEI ML Kit as soon as possible.

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/ID-Photo-DIY/ID%20Photo%20DIY.gif" width=250 title="ID Photo DIY" div align=center border=5>

## Project directory structure
ID-Photo-DIY

    |-- com.mlkit.sample.idphoto
        |-- Activity
            |-- MainActivity //DIY Entry
            |-- StillCutPhotoActivity // DIY customization page

## More Scenarios
Based on the image segmentation capability provided by HUAWEI ML Kit, you can not only develop an ID-Photo-DIY applet, but also implement the following functions:
1. Tailor photos containing portraits. Photo backgrounds can be changed or blurred, so that more beautiful or artistic photos can be obtained.
2. Recognize elements such as the sky, plants, food, cats, dogs, flowers, water, sand, buildings, and mountains in images, and beautify these elements. For example, make the sky bluer and water clearer.
3. Identify portraits in video streams, edit special effects of video streams, and change the background.

## Getting Started
- Preparations
  - Add the Huawei Maven repository to the build.gradle file in the root directory of the project.
  - Add build dependency on the SDK to the build.gradle file in the app directory.
  - Add the image segmentation model to the manifest.xml file of Android.
  - Apply for camera and storage permissions in the manifest.xml file of Android.

- Key steps of code development
  - Submit a dynamic permission application.
  - Create an image segmentation analyzer.
  - Use android.graphics.bitmap to create an MLFrame object for the analyzer to detect images.
  - Call the asyncanalyseframe method to perform image segmentation.
  - Change the image background.

For details about the development procedure, please refer to [How to Develop an ID Photo DIY Applet in 30 Min via huawei HMS ML Kit image segmentation service](https://developer.huawei.com/consumer/cn/forum/topicview?tid=0201246020746500305&fid=18).

## Supported Environment
Android 4.4 or later is recommended.

## License
The sample code has obtained the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
