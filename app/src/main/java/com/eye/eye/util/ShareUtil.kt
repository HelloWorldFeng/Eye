package com.eye.eye.util

import android.app.Activity
import android.content.Intent
import com.eye.eye.R
import com.eye.eye.extension.showToas


const val SHARE_MORE = 0
const val SHARE_QQ = 1
const val SHARE_WECHAT = 2
const val SHARE_WEIBO = 3
const val SHARE_QQZONE = 4
const val SHARE_WECHAT_MEMORIES = 5

/*
* 调用系统原生分享工具类
* */
object ShareUtil {

    private fun processShare(activity: Activity, shareContent: String, shareType: Int) {
        when (shareType) {
            SHARE_QQ -> {
                if (!GlobalUtil.isQQInstalled()) {
                    GlobalUtil.getString(R.string.your_phone_does_not_install_qq).showToas()
                    return
                }
                share(
                    activity,
                    shareContent,
                    "com.tencent.mobileqq",
                    "com.tencent.mobileqq.activity.JumpActivity"
                )
            }
            SHARE_WECHAT -> {
                if (!GlobalUtil.isWechatInstalled()) {
                    GlobalUtil.getString(R.string.your_phone_does_not_install_wechat).showToas()
                }
                share(
                    activity,
                    shareContent,
                    "com.tencent.mm",
                    "com.tencent.mm.ui.tools.ShareImgUI"
                )
            }
            SHARE_MORE -> {
                share(activity, shareContent)
            }
        }
    }

    private fun share(activity: Activity, shareContent: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareContent)
        }
        activity.startActivity(
            Intent.createChooser(
                shareIntent,
                GlobalUtil.getString(R.string.share_to)
            )
        )
    }

    private fun share(
        activity: Activity,
        shareContent: String,
        packageName: String,
        className: String
    ) {
        try {
            val shareInt = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareContent)
                setClassName(packageName, className)
            }
            activity.startActivity(shareInt)
        } catch (e: Exception) {
            GlobalUtil.getString(R.string.share_unknown_error).showToas()
        }
    }

    /*
    * 调用系统原生分享
    * @param shareContent 分享的内容
    * @param shareType 分享的平台
    * */
    fun share(activity: Activity, shareContent: String, shareType: Int) {
        processShare(activity,shareContent,shareType)
    }
}
