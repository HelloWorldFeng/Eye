package com.eye.eye.extension

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.eye.eye.EyeApplication
import com.eye.eye.ui.common.ui.ShareDialogFragment
import com.eye.eye.util.GlobalUtil
import com.eye.eye.util.ShareUtil

/*
* 获取SharePreference实例
*/
val sharedPreferences:SharedPreferences = EyeApplication.context.getSharedPreferences(GlobalUtil.appPackage+"_sharedPreference",Context.MODE_PRIVATE)

/*
* 批量设置控件相同的点击事件
* @param v 点击的控件
* @param block 处理点击事件回调代码块
* */
fun setOnClickListenter(vararg v: View?, block: View.() -> Unit) {
    val listener = View.OnClickListener { it.block() }
    v.forEach { it?.setOnClickListener(listener) }
}

/*
* 批量设置控件点击事件
* @param v 点击的控件
* @param listener 处理点击事件监听器
* */
fun setOnClickListenter(vararg v: View?, listener: View.OnClickListener) {
    v.forEach { it?.setOnClickListener(listener) }
}

/*
* 调用系统原生分享
* @param activity上下文
* @param shareContent 分享内容
* @param shareType 分享平台
* */
fun share(activity: Activity, shareContent: String, shareType: Int) {
    ShareUtil.share(activity,shareContent,shareType)
}

/*
* 弹出分享对话框
* @param activity上下文
* @param shareContent 分享内容
* */
fun showDialogShare(activity: Activity, shareContent: String) {
    if (activity is AppCompatActivity) {
        ShareDialogFragment().showDialog(activity,shareContent)
    }
}