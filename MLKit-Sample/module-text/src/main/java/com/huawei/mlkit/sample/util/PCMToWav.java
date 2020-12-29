/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.mlkit.sample.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;

import android.util.Log;

public class PCMToWav {
    private static final String TAG = PCMToWav.class.getSimpleName();

    // Transformation function.
    public static String convertWaveFile(String fileNamePcm, String fileNameWav, int mediaRate, int mediaChannel, int mediaFormat) {
        // Cached data size.
        int bufferSize = AudioRecord.getMinBufferSize(mediaRate, mediaChannel, mediaFormat) * 2;

        if (!FileUtils.deleteFile(fileNameWav)) {
            Log.e(TAG, "Delete wav file failed.");
        }

        try {
            byte[] data = new byte[bufferSize];
            FileInputStream pcm = new FileInputStream(fileNamePcm);
            FileOutputStream wav = new FileOutputStream(fileNameWav);
            long totalAudioLen = pcm.getChannel().size();
            long totalDataLen = totalAudioLen + 36;
            int channels = 1;
            long byteRate;
            if (mediaFormat == AudioFormat.ENCODING_PCM_16BIT) {
                byteRate = 16 * mediaRate * channels / 8;
            } else if (mediaFormat == AudioFormat.ENCODING_PCM_8BIT) {
                byteRate = 8 * mediaRate * channels / 8;
            } else {
                Log.e(TAG, "mediaFormat is neither AudioFormat.ENCODING_PCM_16BIT nor AudioFormat.ENCODING_PCM_8BIT, convert failed.");
                return "";
            }
            addWaveHeader(wav, totalAudioLen, totalDataLen, mediaRate, channels, byteRate, mediaChannel, mediaFormat);
            while (pcm.read(data) != -1) {
                wav.write(data);
            }
            pcm.close();
            wav.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        if (!FileUtils.deleteFile(fileNamePcm)) {
            Log.e(TAG, "Delete pcm file failed.");
        }
        return fileNameWav;
    }

    // Adding the WAV header information.
    private static void addWaveHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
        int channels, long byteRate, int mediaChannel, int mediaFormat) throws IOException {
        byte[] header = new byte[44];
        // Add RIFF header.
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        // Data Size.
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);

        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // Fmt data size.
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // Encoding mode.
        header[20] = 1;
        header[21] = 0;
        // Channel.
        header[22] = (byte) channels;
        header[23] = 0;
        // Sampling rate.
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        // Audio data transmission rate, which is calculated as follows: Sampling rate x channels x Sampling depth/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        int tongdaowei = 1;
        if (mediaChannel == AudioFormat.CHANNEL_IN_MONO) {
            tongdaowei = 1;
        } else {
            tongdaowei = 2;
        }

        if (mediaFormat == AudioFormat.ENCODING_PCM_16BIT) {
            header[32] = (byte) (tongdaowei * 16 / 8);
        } else if (mediaFormat == AudioFormat.ENCODING_PCM_8BIT) {
            header[32] = (byte) (tongdaowei * 8 / 8);
        }
        header[33] = 0;
        if (mediaFormat == AudioFormat.ENCODING_PCM_16BIT) {
            header[34] = 16;
        } else if (mediaFormat == AudioFormat.ENCODING_PCM_8BIT) {
            header[34] = 8;
        }
        header[35] = 0;

        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}

