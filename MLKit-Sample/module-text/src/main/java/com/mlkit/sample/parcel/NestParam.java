/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mlkit.sample.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.huawei.hms.ml.common.parcel.ParcelReader;
import com.huawei.hms.ml.common.parcel.ParcelWriter;

import androidx.annotation.NonNull;

/**
 * Custom nested parameters
 *
 * @since 2020-03-02
 */
public class NestParam implements Parcelable {
    public int filed1;

    public int filed2;

    public NestParam() {

    }

    protected NestParam(Parcel in) {
        final ParcelReader reader = new ParcelReader(in);
        this.filed1 = reader.readInt(2, -1);
        this.filed2 = reader.readInt(3, -1);
        reader.finish(); // Key steps
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final ParcelWriter writer = new ParcelWriter(dest);
        final int pos = writer.beginObjectHeader();
        writer.writeInt(2, this.filed1);
        writer.writeInt(3, this.filed2);
        writer.finishObjectHeader(pos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NestParam> CREATOR = new Creator<NestParam>() {
        @Override
        public NestParam createFromParcel(Parcel in) {
            return new NestParam(in);
        }

        @Override
        public NestParam[] newArray(int size) {
            return new NestParam[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("filed1 = " + this.filed1);
        buffer.append(", ");
        buffer.append("filed2 = " + this.filed2);

        return buffer.toString();
    }
}
