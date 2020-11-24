# hms-ml-demo

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)  ![Android CI](https://github.com/HMS-Core/hms-ml-demo/workflows/Android%20CI/badge.svg)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/README_ZH.md)

## Introduction

This project includes apps developed based on Huawei Machine Learning Service (ML Kit). The project directory is as follows:

    |-- MLKit-Sample // ML Kit scenario-based demo. You can scan the QR code to obtain [the demo](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Examples-V5/sample-code-0000001050265470-V5).

    |-- ApplicationCases // Application cases developed based on Huawei Machine Learning Service (ML Kit).


## Precautions

The hms-ml-demo project contains two independent projects. After downloading the code, you can load different nested projects to the IDE as required. You can run each project independently.

#### Add a new project.

The root folder already contains a common build.gradle, which loads the latest Android Gradle plug-ins, AGConnect, and Kotlin to classpath. Therefore, if you add a new project, delete the project's build.gradle file unless you need to add a unique Gradle plug-in for the project. The same applies to `gradle.properties'.

All projects and nested modules are defined in the root `settings.gradle` setting file, new projects are added, all module entries are added to the external setting file, and then the configuration file of the project is deleted.

## Technical Support
If you are still evaluating HMS Core, obtain the latest information about HMS Core and share your insights with other developers at (https://www.reddit.com/r/HuaweiDevelopers/).

If you have any questions about using the HMS sample code, try:
- If you encounter any problem during the development, ask the [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) under the `huawei-mobile-services' tag. Huawei R&D experts can solve your problem online one-to-one.
- Communicate with other developers in the (https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core section of the Huawei Developer Forum.

If you have problems trying the sample code, submit [issue](https://github.com/HMS-Core/hms-ml-demo/issues) to the repository, and you are welcome to submit [Pull Request](https://github.com/HMS-Core/hms-ml-demo/pulls).
