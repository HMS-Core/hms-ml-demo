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

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import com.huawei.hms.ml.common.parcel.ParcelReader;
import com.huawei.hms.ml.common.parcel.ParcelWriter;

import androidx.annotation.NonNull;

public class Parameter implements Parcelable {

    public NestParam nestParam;

    public int width;

    public int height;

    public Bitmap bitmap;

    public byte[] bytes;

    public Rect rect;

    public String aString;

    public Parameter() {

    }

    protected Parameter(Parcel in) {
        final ParcelReader reader = new ParcelReader(in);
        this.nestParam = reader.readParcelable(2, NestParam.CREATOR, null);
        this.width = reader.readInt(3, -1);
        this.height = reader.readInt(4, -1);
        this.bitmap = reader.readParcelable(5, Bitmap.CREATOR, null);
        this.bytes = reader.createByteArray(6, null);
        this.rect = reader.readParcelable(7, Rect.CREATOR, null);
        this.aString = reader.createString(8, null);
        reader.finish(); // Key steps
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final ParcelWriter writer = new ParcelWriter(dest);
        final int pos = writer.beginObjectHeader();
        writer.writeParcelable(2, this.nestParam, flags, false);
        writer.writeInt(3, this.width);
        writer.writeInt(4, this.height);
        writer.writeParcelable(5, this.bitmap, flags, false);
        writer.writeByteArray(6, this.bytes, false);
        writer.writeParcelable(7, this.rect, flags, false);
        writer.writeString(8, this.aString, false);
        writer.finishObjectHeader(pos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Parameter> CREATOR = new Creator<Parameter>() {
        @Override
        public Parameter createFromParcel(Parcel in) {
            return new Parameter(in);
        }

        @Override
        public Parameter[] newArray(int size) {
            return new Parameter[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("nestParam = " + this.nestParam);
        buffer.append(", ");
        buffer.append("width = " + this.width);
        buffer.append(", ");
        buffer.append("height = " + this.height);
        buffer.append(", ");
        buffer.append("bitmap.size = " + (this.bitmap == null ? "NULL" : this.bitmap.getByteCount()));
        buffer.append(", ");
        buffer.append("bytes = " + (this.bytes == null ? "NULL" : this.bytes.length));
        buffer.append(", ");
        buffer.append("rect = " + (this.rect == null ? "NULL" : this.rect));
        buffer.append(", ");
        buffer.append("aString = " + (this.aString == null ? "NULL" : this.aString));

        return buffer.toString();
    }
}
