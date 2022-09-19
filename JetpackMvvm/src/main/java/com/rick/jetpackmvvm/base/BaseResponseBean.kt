package com.rick.jetpackmvvm.base

abstract class BaseResponseBean<T> {
    abstract fun isSuccess(): Boolean
    abstract fun getCode(): Int
    abstract fun getMsg(): String?
    abstract fun getData(): T?
}