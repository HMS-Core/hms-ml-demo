# ID Photo DIY
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/ml-introduction-4)

## 目录

 * [介绍](#介绍)
 * [更多场景](#更多场景)
 * [运行步骤](#运行步骤)
 * [支持的环境](#支持的环境)
 * [许可证](#许可证)


## 介绍
ID-Photo-DIY使用图像分割功能，将人像图片合成一张蓝白背景的证件照。

本Demo演示如何通过[HUAWEI ML Kit] (https://developer.huawei.com/consumer/cn/hms/huawei-mlkit)快速开发一个身份证照片DIY小程序，目的是让您体验图像分割功能，帮助您尽快集成HUAWEI ML Kit。

<img src="https://github.com/HMS-Core/hms-ml-demo/blob/master/ID-Photo-DIY/ID%20Photo%20DIY.gif" width=250 title="ID Photo DIY" div align=center border=5>

## 更多场景
基于HUAWEI ML Kit提供的图像分割能力，不仅可以做身份证照片的DIY程序，还可以实现以下相关功能：
1、可以裁剪日常生活中的人物肖像，一些有趣的照片可以通过改变背景来制作，或者背景可以被虚化，以得到更美丽或者艺术化的照片。
2、识别图像中的天空、植物、食物、猫狗、花卉、水面、沙面、建筑物、山体等元素，并对这些元素进行特殊美化，如使天空更蓝、水更清。
3、识别视频流中的人像，编辑视频流特效，更换背景。

## 运行步骤
- 准备工作
  - 在项目层build.gradle文件中增加华为Maven仓库。
  - 在应用层build.gradle文件中添加SDK依赖。
  - 在Android的manifest.xml文件中增加图像分割Model。
  - Android系统manifest.xml中申请相机和存储权限。

- 代码开发的关键步骤
  - 动态权限申请。
  - 创建图像分割检测器。
  - 通过android.graphics.bitmap创建用于分析器检测图片的“MLFrame”对象。
  - 调用“asyncanalyseframe”方法进行图像分割。
  - 更改图片背景。

更详细的开发步骤，请参考[How to Develop an ID Photo DIY Applet in 30 Min via huawei HMS ML Kit image segmentation service](https://developer.huawei.com/consumer/cn/forum/topicview?tid=0201246020746500305&fid=18)

## 支持的环境
推荐使用Android 4.4及以上版本的设备。

##  许可证
此示例代码已获得[Apache 2.0 license]（http://www.apache.org/licenses/LICENSE-2.0）。