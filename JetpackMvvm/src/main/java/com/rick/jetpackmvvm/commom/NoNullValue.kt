package com.rick.jetpackmvvm.commom

import androidx.annotation.NonNull

interface NoNullValue<T> {
    @NonNull
    fun get(): T
}