package com.eye.eye.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eye.eye.R
import com.eye.eye.event.MessageEvent
import com.eye.eye.event.RefreshEvent
import com.eye.eye.event.SwitchPageEvent
import com.eye.eye.logic.model.TabEntity
import com.eye.eye.ui.common.ui.BaseViewPagerFragment
import com.eye.eye.ui.community.commend.CommendFragment
import com.eye.eye.ui.community.follow.FollowFragment
import com.eye.eye.util.GlobalUtil
import com.flyco.tablayout.listener.CustomTabEntity
import org.greenrobot.eventbus.EventBus

/*
* 社区主界面
* */
class CommunityFragment:BaseViewPagerFragment() {

    override val createTitles: ArrayList<CustomTabEntity>
        get() = ArrayList<CustomTabEntity>().apply {
            add(TabEntity(GlobalUtil.getString(R.string.commend)))
            add(TabEntity(GlobalUtil.getString(R.string.follow)))
        }

    override val createFragments: Array<Fragment>
        get() = arrayOf(CommendFragment.newInstance(),FollowFragment.newInstance())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater.inflate(R.layout.fragment_main_container,container,false))
    }

    override fun onMessageEvent(message: MessageEvent) {
        super.onMessageEvent(message)
        if (message is RefreshEvent && message.activityClass == this::class.java) {
            when (viewPage?.currentItem) {
                0 -> EventBus.getDefault().post(RefreshEvent(CommendFragment::class.java))
                1 -> EventBus.getDefault().post(RefreshEvent(FollowFragment::class.java))
            }
        }else if (message is SwitchPageEvent) {
            when (message.activityClass) {
                CommendFragment::class.java -> viewPage?.currentItem = 0
                else ->{}
            }
        }
    }

    companion object {
        fun newInstance() = CommunityFragment()
    }
}