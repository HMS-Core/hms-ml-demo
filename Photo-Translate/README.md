# Photo-Translate

## Table of Contents

 * [Introduction](#introduction)
 * [Supported Environments](#supported-environments)
 * [Sample-Code](#Sample-Code)
 * [License](#license)


## Introduction
The Photo-Translate uses the text recognition and translation functions of HUAWEI ML Kit to
translate text in static photos into the required language. Currently, the following languages are supported:
Simplified Chinese, English, French, Arabic, Thai, Spanish, Turkish, Portuguese, Japanese, German, Italian, and Russian.

To use cloud services such as text recognition and translation, you need to apply for the
agconnect-services.json file on [HUAWEI Developers](https://developer.huawei.com/consumer/en/)
and replace sample-agconnect-services.json with the file in the project. For details,
please refer to [Adding the AppGallery Connect Configuration File.](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-add-agc).

You can only use a custom package name to apply for the agconnect-services.json file.
In this way, you only need to change the value of applicationId in Photo-Translate\app\build.gradle
to the package name used in agconnect-services.json. Then, you can use cloud services of HUAWEI ML Kit.


## Supported Environments
Devices with Android 4.4 or later are recommended.


## Sample Code
Sample code majors activitys as follows:
   1. Choose MainActivity to see a demo of the following:
      - Chooses the source language and target language.

   2. Choose RemoteTranslateActivity to see a demo of the following:
	  - Selects photo from album or take photo to do the translation


   Ability called by the sample:
   1. Text Recognition
	  - MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer()：Create a cloud text recognizer.
	  - MLTextAnalyzer.asyncAnalyseFrame(MLFrame frame): Parse text information in pictures.
	  - MLText.getBlocks(): Get text blocks. Generally, a text block represents one line. There is also a case where a text block corresponds to multiple lines.
      - MLText.Block.getContents(): Get list of text lines(MLText.TextLine).
   2. Translation
	  - MLTranslatorFactory.getInstance().getRemoteTranslator(MLRemoteTranslateSetting settings)：Create a translator.
	  - MLRemoteTranslator.asyncTranslate(String sourceText): Parse out text from source language to target language, sourceText indicates the language to be detected.


##  License
The Photo-Translate have obtained the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).
