package com.huawei.mlkit.sample.photoreader.util

import android.os.Bundle

fun Bundle.putEnum(key: String, enum: Enum<*>) {
    this.putString(key, enum.name)
}

inline fun <reified T: Enum<T>> Bundle.getEnumExtra(key:String, defaultValue : Enum<T>) : T {
    return enumValueOf(getString(key, defaultValue.name))
}