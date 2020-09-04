/**
 * Copyright (C) 2007-2008 OpenIntents.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.huawei.mlkit.sample.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private FileUtils() {}

    /**
     * convertToString
     * @param inputStream
     * @return
     * @throws IOException
     */
//    public static String convertToString(InputStream inputStream)
//            throws IOException {
//        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
//
//        StringBuilder localStringBuilder = new StringBuilder();
//        String str;
//        while ((str = localBufferedReader.readLine()) != null) {
//            localStringBuilder.append(str).append("\n");
//        }
//        return localStringBuilder.toString();
//    }
//
//    /**
//     * safetyClose
//     * @param closeable
//     * @return
//     */
//    public static boolean safetyClose(Closeable closeable) {
//        if (null != closeable) {
//            try {
//                closeable.close();
//            } catch (IOException var2) {
//                return false;
//            }
//        }
//        return true;
//    }
}
