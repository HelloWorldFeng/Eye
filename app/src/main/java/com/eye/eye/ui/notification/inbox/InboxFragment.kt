package com.eye.eye.ui.notification.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eye.eye.R
import com.eye.eye.ui.common.ui.BaseFragment
import com.eye.eye.ui.login.LoginActivity
import com.eye.eye.ui.notification.push.PushFragment
import kotlinx.android.synthetic.main.fragment_notification_login_tips.*

/*
* 通知-私信界面
* */
class InboxFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater.inflate(R.layout.fragment_notification_login_tips,container,false))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvLogin.setOnClickListener {
            LoginActivity.start(activity)
        }
    }

    companion object {
        fun newInstance() = InboxFragment()
    }
}