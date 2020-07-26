package com.eye.eye.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eye.eye.R
import com.eye.eye.logic.model.TabEntity
import com.eye.eye.ui.common.ui.BaseViewPagerFragment
import com.eye.eye.ui.notification.inbox.InboxFragment
import com.eye.eye.ui.notification.interaction.InteractionFragment
import com.eye.eye.ui.notification.push.PushFragment
import com.eye.eye.util.GlobalUtil
import com.flyco.tablayout.listener.CustomTabEntity

class NotificationFragment:BaseViewPagerFragment() {

    override val createTitles: ArrayList<CustomTabEntity>
        get() = ArrayList<CustomTabEntity>().apply {
            add(TabEntity(GlobalUtil.getString(R.string.push)))
            add(TabEntity(GlobalUtil.getString(R.string.interaction)))
            add(TabEntity(GlobalUtil.getString(R.string.inbox)))
        }
    override val createFragments: Array<Fragment>
        get() = arrayOf(PushFragment.newInstance(),InteractionFragment.newInstance(),InboxFragment.newInstance())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater.inflate(R.layout.fragment_main_container,container,false))
    }

    companion object {
        fun newInstance() = NotificationFragment()
    }
}