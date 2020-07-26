package com.eye.eye.ui.common.callback

/*
* 在Activity或Fragment中进行网络请求所经历的生命周期
*/
interface RequestLifecycle {
    fun startLoading()

    fun loadFinished()

    fun loadFailed(mas:String?)
}