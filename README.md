# hms-ml-demo

[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/service-introduction-0000001050040017-V5)  ![Android CI](https://github.com/HMS-Core/hms-ml-demo/workflows/Android%20CI/badge.svg)

English | [中文](https://github.com/HMS-Core/hms-ml-demo/blob/master/README_ZH.md)

## Introduction

This project includes apps developed based on HUAWEI ML Kit. The project directory is as follows:

|-- MLKit-Sample // ML Kit scenario-based demo. You can scan the QR code to obtain the demo.

|-- ApplicationCases // Application cases developed based on ML Kit.


## Precautions

The hms-ml-demo project contains two independent projects. After downloading the code, you can load different nested projects to the IDE as required. And you can run each project independently.

#### Create a Project

The root folder already contains a common build.gradle file, which loads the latest Android Gradle plugins, AGConnect, and Kotlin to classpath. Therefore, if you create a project, delete the build.gradle file in the root directory of the project, unless a unique Gradle plugin is needed. The same applies to gradle.properties.

All projects and nested modules are defined in the root directory of settings.gradle file. You can create a project, add all module entries of the project to the external settings.gradle file, and delete the settings.gradle file in the new project.

## Technical Support
If you are still evaluating HMS Core, obtain the latest information about HMS Core and share your insights with other developers at Reddit.

If you have any questions about using the HMS sample code, try:
- To resolve development issues, please go to Stack Overflow. You can ask questions below the huawei-mobile-services tag, and Huawei R&D experts can solve your problem online on a one-to-one basis.
- To join the developer discussion, please visit Huawei Developer Forum.

If you have problems using the sample code, submit issues and pull requests to the repository.
