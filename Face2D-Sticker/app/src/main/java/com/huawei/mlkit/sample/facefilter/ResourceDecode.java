/**
 * Copyright 2018 cain.huang@outlook.com
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
package com.huawei.mlkit.sample.facefilter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ResourceDecode {
    protected static final String TAG = "ResourceDecode";

    protected ByteBuffer mDataBuffer;

    private String mDataPath;

    public ResourceDecode( String dataPath) {
        mDataPath = dataPath;
    }

    public void init() throws IOException {
        File file = new File(mDataPath);

        mDataBuffer = ByteBuffer.allocateDirect((int)file.length());
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[2048];
        boolean result = false;
        try {
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                mDataBuffer.put(buffer, 0, length);
            }
            result = true;
        } catch (IOException e) {
            Log.e(TAG, "init: ", e);
        } finally {
            safetyClose(inputStream);
        }
        if (!result) {
            throw new IOException("Failed to parse data file!");
        }
    }

//    private static Map<String, Pair<Integer, Integer>> parseIndexFile(String indexPath) throws IOException {
//        String indexString = FileUtils.convertToString(new FileInputStream(new File(indexPath)));
//        HashMap<String, Pair<Integer, Integer>> map = new HashMap<>();
//
//        String[] indexArray = indexString.split(";");
//        for (int i = 0; i < indexArray.length; ++i) {
//            if (!TextUtils.isEmpty(indexArray[i])) {
//
//                String[] subIndexArray = indexArray[i].split(":");
//                if (subIndexArray.length == 3) {
//                    int offset = parseInt(subIndexArray[1], -1);
//                    int length = parseInt(subIndexArray[2], -1);
//                    if (-1 == offset || -1 == length) {
//                        throw new IOException("Failed to parse offset or length for " + indexArray[i]);
//                    }
//                    map.put(subIndexArray[0], new Pair<>(offset, length));
//                }
//            }
//        }
//        return map;
//    }

//    private static int parseInt(String str, int defaultValue) {
//        int result = defaultValue;
//        try {
//            result = Integer.parseInt(str);
//        } catch (NumberFormatException e) {
//            Log.e(TAG, "parseInt: ", e);
//        }
//        return result;
//    }

    public static Pair<String, String> getResourceFile(String folder) {
        String index = null;
        String data = null;
        File file = new File(folder);
        String[] list = file.list();
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.length; ++i) {
            if (list[i].equals("index.idx")) {
                index = list[i];
            } else if (list[i].equals("resource.res")) {
                data = list[i];
            }
        }
        if (!TextUtils.isEmpty(index) && !TextUtils.isEmpty(data)) {
            return new Pair<>(index, data);
        } else {
            return null;
        }
    }

    public static List<FaceStickerJson> decodeStickerData(Context context, String folderPath)
            throws IOException, JSONException {
        InputStream is = context.getAssets().open(folderPath);
        String stickerJson = convertToString(is);

        JSONObject jsonObject = new JSONObject(stickerJson);
        List<FaceStickerJson> dynamicSticker = new ArrayList<>();

        JSONArray stickerList = jsonObject.getJSONArray("stickerList");
        for (int i = 0; i < stickerList.length(); i++) {
            JSONObject jsonData = stickerList.getJSONObject(i);
            FaceStickerJson data;
            data = new FaceStickerJson();
            JSONArray centerIndexList = jsonData.getJSONArray("centerIndexList");
            data.centerIndexList = new int[centerIndexList.length()];
            for (int j = 0; j < centerIndexList.length(); j++) {
                data.centerIndexList[j] = centerIndexList.getInt(j);
            }
            data.offsetX = (float) jsonData.getDouble("offsetX");
            data.offsetY = (float) jsonData.getDouble("offsetY");
            data.baseScale = (float) jsonData.getDouble("baseScale");
            data.startIndex = jsonData.getInt("startIndex");
            data.endIndex = jsonData.getInt("endIndex");
            data.width = jsonData.getInt("width");
            data.height = jsonData.getInt("height");
            data.frames = jsonData.getInt("frames");
            data.action = jsonData.getInt("action");
            data.stickerName = jsonData.getString("stickerName");
            data.duration = jsonData.getInt("duration");
            data.stickerLooping = (jsonData.getInt("stickerLooping") == 1);
            data.maxCount = jsonData.optInt("maxCount", 5);

            dynamicSticker.add(data);
        }

        return dynamicSticker;
    }

    public static String convertToString(InputStream inputStream)
            throws IOException {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

        StringBuilder localStringBuilder = new StringBuilder();
        String str;
        while ((str = localBufferedReader.readLine()) != null) {
            localStringBuilder.append(str).append("\n");
        }
        return localStringBuilder.toString();
    }

    public static boolean safetyClose(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException var2) {
                return false;
            }
        }
        return true;
    }
}
