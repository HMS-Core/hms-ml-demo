# hms-ml-demo

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)

[English](https://github.com/HMS-Core/hms-ml-demo/blob/master/README.md) | 中文

## 简介

本项目包含基于华为机器学习服务(ML Kit)开发的APP。工程目录如下：

|-- MLKit-Sample // ML Kit场景化Demo，此Demo可以通过扫描二维码的方式在开发者联盟网站获取：[获取Demo](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Examples-V5/sample-code-0000001050265470-V5)

|-- ApplicationCases // 基于华为机器学习服务(ML Kit)开发的应用案例。

## 注意事项

hms-ml-demo工程包含两个独立的工程。下载代码后，您可以根据需求，将不同的嵌套工程加载到IDE，您可以单独运行每个工程。

#### 添加新的工程

根文件夹已经包含一个通用的build.gradle，该将最新的Android Gradle插件,AGConntect和Kotlin加载到类classpath。因此，如果添加新工程，删除工程的build.gradle文件，除非需要为此工程添加独特的Gradle插件。同样适用于`gradle.properties`。

所有工程和嵌套模块都在根`settings.gradle`设置文件中定义，添加新的工程，将所有模块条目添加到外部设置文件中，然后删除工程的设置文件。

## 技术支持
如果您对HMS Core还处于评估阶段，可在[Reddit社区](https://www.reddit.com/r/HuaweiDevelopers/)获取关于HMS Core的最新讯息，并与其他开发者交流见解。

如果您对使用HMS示例代码有疑问，请尝试：
- 开发过程遇到问题上[Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services)，在`huawei-mobile-services`标签下提问，有华为研发专家在线一对一解决您的问题。
- 到[华为开发者论坛](https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core板块与其他开发者进行交流。

如果您在尝试示例代码中遇到问题，请向仓库提交[issue](https://github.com/HMS-Core/hms-ml-demo/issues)，也欢迎您提交[Pull Request](https://github.com/HMS-Core/hms-ml-demo/pulls)。
