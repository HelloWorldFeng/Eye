package com.eye.eye.ui.home

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
import com.eye.eye.ui.home.commed.CommendFragment
import com.eye.eye.ui.home.daily.DailyFragment
import com.eye.eye.ui.home.discovery.DiscoveryFragment
import com.eye.eye.util.GlobalUtil
import com.flyco.tablayout.listener.CustomTabEntity
import kotlinx.android.synthetic.main.layout_main_page_title_bar.*
import org.greenrobot.eventbus.EventBus

class HomePageFragment: BaseViewPagerFragment() {

    override val createTitles: ArrayList<CustomTabEntity> = ArrayList<CustomTabEntity>().apply {
        add(TabEntity(GlobalUtil.getString(R.string.discovery)))
        add(TabEntity(GlobalUtil.getString(R.string.commend)))
        add(TabEntity(GlobalUtil.getString(R.string.daily)))
    }

    override val createFragments: Array<Fragment> = arrayOf(DiscoveryFragment.newInstance(),CommendFragment.newInstance(),DailyFragment.newInstance())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater.inflate(R.layout.fragment_main_container,container,false))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ivCalendar.visibility = View.VISIBLE
        viewPage?.currentItem = 1
    }

    //当homePage收到刷新消息时，判断当前是哪个Fragment并发送刷新消息给对应的Fragment
    override fun onMessageEvent(message: MessageEvent) {
        super.onMessageEvent(message)
        if (message is RefreshEvent && this::class.java == message.activityClass) {
            when (viewPage?.currentItem) {
                0 -> EventBus.getDefault().post(RefreshEvent(DiscoveryFragment::class.java))
                1 -> EventBus.getDefault().post(RefreshEvent(CommendFragment::class.java))
                2 -> EventBus.getDefault().post(RefreshEvent(DailyFragment::class.java))
            }
        //当homePage收到切换消息时，判断要切换的是哪个Fragment并改变viewPager下标
        }else if(message is SwitchPageEvent){
            when (message.activityClass) {
                DiscoveryFragment::class.java -> viewPage?.currentItem = 0
                CommendFragment::class.java -> viewPage?.currentItem = 1
                DailyFragment::class.java -> viewPage?.currentItem = 2
            }
        }
    }

    companion object {
        fun newInstance() = HomePageFragment()
    }
}