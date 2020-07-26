package com.eye.eye.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.multidex.BuildConfig
import com.eye.eye.Const
import com.eye.eye.R
import com.eye.eye.extension.setOnClickListenter
import com.eye.eye.extension.showToas
import com.eye.eye.ui.common.ui.BaseFragment
import com.eye.eye.ui.common.ui.WebViewActivity
import com.eye.eye.ui.common.ui.WebViewActivity.Companion.MODE_SONIC_WITH_OFFLINE_CACHE
import com.eye.eye.ui.login.LoginActivity
import com.eye.eye.ui.setting.AboutActivity
import com.eye.eye.ui.setting.SettingActivity
import com.eye.eye.util.GlobalUtil
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.fragment_mine.*

class MineFragment:BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater.inflate(R.layout.fragment_mine,container, false))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvVersionNumber.text = String.format(GlobalUtil.getString(R.string.version_show), GlobalUtil.eyeVersionName)
        setOnClickListenter(
            ivMore, ivAvatar, tvLoginTips, tvFavorites, tvCache, tvFollow, tvWatchRecord, tvNotificationToggle,
            tvMyBadge, tvFeedback, tvContribute, tvVersionNumber, rootView, llScrollViewContent
        ){
            when(this){
                ivMore -> SettingActivity.start(activity)

                ivAvatar,tvLoginTips,tvFavorites,tvCache,tvFollow,tvWatchRecord, tvNotificationToggle, tvMyBadge ->{
                    LoginActivity.start(activity)
                }
                tvContribute->{
                    //我要投稿
                    WebViewActivity.start(activity,WebViewActivity.DEFAULT_TITLE,Const.Url.AUTHOR_OPEN,false,false)
                }
                tvFeedback ->{
                    //意见反馈
                    LoginActivity.start(activity)
                }
                tvVersionNumber ->{
                    LoginActivity.start(activity)
                }
                this@MineFragment.rootView, llScrollViewContent -> {
                    AboutActivity.start(activity)
                }
            }
        }
        tvVersionNumber.setOnLongClickListener {
            String.format(GlobalUtil.getString(R.string.build_type), BuildConfig.BUILD_TYPE).showToas()
            true
        }
    }

    companion object {
        fun newInstance() = MineFragment()
    }
}