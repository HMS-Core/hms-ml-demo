# ID-Photo-DIY

## Table of Contents

 * [Introduction](#introduction)
 * [Supported Environments](#supported-environments)
 * [Sample-Code](#Sample-Code)
 * [License](#license)


## Introduction
The ID-Photo-DIY uses the image segmentation function of HUAWEI ML Kit to synthesize static images of people into a certificate photo with a blue or white background.


## Supported Environments
Devices with Android 4.4 or later are recommended.


## Sample Code
Sample code majors activitys as follows:
   1. Choose MainActivity to see a demo of the following:
      - Chooses ID photo background

   2. Choose StillCutPhotoActivity to see a demo of the following:
	  - Segments the pixels representing human body from an image


   Ability called by the sample:
   1. Image Segmentation
      - MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(MLImageSegmentationSetting settings)：Create a image segment analyzer.
      - MLImageSegmentationSetting.Factory.setExact()：Set detection mode, true is fine detection mode, false is speed priority detection mode.
      - MLImageSegmentationAnalyzer.asyncAnalyseFrame(MLFrame frame): Parse out all target contained in the picture.

##  License
The ID-Photo_DIY have obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
