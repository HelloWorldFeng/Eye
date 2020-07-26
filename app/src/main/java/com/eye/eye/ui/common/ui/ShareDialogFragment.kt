package com.eye.eye.ui.common.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.eye.eye.R
import com.eye.eye.extension.setDrawable
import com.eye.eye.extension.share
import com.eye.eye.util.SHARE_MORE
import com.eye.eye.util.SHARE_QQ
import com.eye.eye.util.SHARE_WECHAT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_share_dialog.*

/*
* 分享对话框的弹出界面
* */
open class ShareDialogFragment : BottomSheetDialogFragment() {

    private lateinit var shareContent: String;

    private lateinit var attachedActivity:Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_share_dialog,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { act ->
            attachedActivity = act
            tvToWechatFriends.setDrawable(ContextCompat.getDrawable(act,R.drawable.ic_share_wechat_black_30dp),30f,30f,1)
            tvShareToQQ.setDrawable(ContextCompat.getDrawable(act,R.drawable.ic_share_qq_black_30dp),30f,30f,1)

            tvToWechatFriends.setOnClickListener {
                share(attachedActivity,shareContent, SHARE_WECHAT)
                dismiss()
            }
            tvShareToQQ.setOnClickListener {
                share(attachedActivity,shareContent, SHARE_QQ)
                dismiss()
            }
            llMore.setOnClickListener {
                share(attachedActivity,shareContent, SHARE_MORE)
                dismiss()
            }
            tvCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    fun showDialog(activity: AppCompatActivity, shareContent: String) {
        show(activity.supportFragmentManager, "share_dialog")
        this.shareContent = shareContent
    }
}