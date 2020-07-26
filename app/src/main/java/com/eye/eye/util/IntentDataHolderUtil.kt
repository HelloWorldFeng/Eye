package com.eye.eye.util

import java.lang.ref.WeakReference

/*
* 数据传输工具类，处理Inten携带大量数据时，超过1MB限制出现的异常
* */

object IntentDataHolderUtil {
    private val dataList = hashMapOf<String, WeakReference<Any>>()

    fun setData(key: String, t: Any) {
        val value = WeakReference(t)
        dataList[key] = value
    }

    fun <T> getData(key: String): T? {
        val reference = dataList[key]
        return try {
            reference?.get() as T
        } catch (e: Exception) {
            null
        }
    }
}