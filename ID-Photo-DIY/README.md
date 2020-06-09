# ID Photo DIY
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

## Introduction
The ID-Photo-DIY uses the image segmentation function to synthesize static images of people into a certificate photo with a blue or white background. This demo demonstrates how to quickly develop an ID photo DIY applet using [HUAWEI ML Kit](https://developer.huawei.com/consumer/en/hms/huawei-mlkit).
The goal is to let you experience image segmentation function and help you integrate HUAWEI ML Kit as quickly as possible.

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/ID-Photo-DIY/ID%20Photo%20DIY.gif" width=250 title="ID Photo DIY" div align=center border=5>

## Steps to run the demo
- Preparation
  -Add Huawei Maven Warehouse in Project Level Gradle
  - Add SDK Dependency in Application Level build.gradle
  - Add Model in Android manifest.xml File
  - Apply for Camera and Storage Permission in Android manifest.xml File

- Two Key Steps of Code Development
  - Dynamic Authority Application
  - Creating an Image Segmentation Detector
  - Create "mlframe" Object through android.graphics.bitmap for Analyzer to Detect Pictures
  - Call "asyncanalyseframe" Method for Image Segmentation
  - Change the Picture Background

For more detailed development steps, please refer to [How to Develop an ID Photo DIY Applet in 30 Min](https://developer.huawei.com/consumer/cn/forum/topicview?tid=0201246020746500305&fid=18)

## More scenario
Based on the ability of image segmentation, it cannot only be used to do the DIY program of ID photo, but also realize the following related functions:
1. People's portraits in daily life can be cut out, some interesting photos can be made by changing the background, or the background can be virtualized to get more beautiful and artistic photos.
2. Identify the sky, plants, food, cats and dogs, flowers, water surface, sand surface, buildings, mountains and other elements in the image, and make special beautification for these elements, such as making the sky bluer and the water clearer.
3. Identify the objects in the video stream, edit the special effects of the video stream, and change the background.

For a more detailed development guide about ML Kit, please refer to [Huawei developer Alliance](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-introduction-4)

## License
The ID-Photo_DIY have obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
