package com.eye.eye

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.eye.eye.event.MessageEvent
import com.eye.eye.event.RefreshEvent
import com.eye.eye.event.SwitchPageEvent
import com.eye.eye.extension.logD
import com.eye.eye.extension.setOnClickListenter
import com.eye.eye.extension.showToas
import com.eye.eye.logic.network.EyeNetWork
import com.eye.eye.logic.network.api.MainPageService
import com.eye.eye.ui.common.ui.BaseActivity
import com.eye.eye.ui.community.CommunityFragment
import com.eye.eye.ui.home.HomePageFragment
import com.eye.eye.ui.login.LoginActivity
import com.eye.eye.ui.mine.MineFragment
import com.eye.eye.ui.notification.NotificationFragment
import com.eye.eye.util.DialogAppraiseTipsWorker
import com.eye.eye.util.GlobalUtil
import kotlinx.android.synthetic.main.layout_bottom_navigation_bar.*
import kotlinx.android.synthetic.main.layout_bottom_navigation_bar.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

/*
* Eye的主界面
* */

class MainActivity : BaseActivity() {

    private var backPressTime = 0L

    private var homePageFragement: HomePageFragment? = null

    private var communityFragement: CommunityFragment?= null

    private var notificationFragment: NotificationFragment? = null

    private var mineFragment: MineFragment ?= null

    private val fragmentManager: FragmentManager by lazy { supportFragmentManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun setupViews() {
        observe()
        //点击tabBar时通知主界面和tabBar切换
        setOnClickListenter(btnHomePage, btnCommunity, btnNotification, ivRelease, btnMine) {
            when (this) {
                btnHomePage ->{
                    notificationUiRefresh(0)
                    setTabSelection(0)
                }
                btnCommunity ->{
                    notificationUiRefresh(1)
                    setTabSelection(1)
                }
                btnNotification ->{
                    notificationUiRefresh(2)
                    setTabSelection(2)
                }
                ivRelease ->{
                    LoginActivity.start(this@MainActivity)
                }
                btnMine ->{
                    notificationUiRefresh(3)
                    setTabSelection(3)
                }
            }
        }
        setTabSelection(0)
    }

    override fun onMessageEvent(message: MessageEvent) {
        super.onMessageEvent(message)
        when{
            message is SwitchPageEvent && message.activityClass==this::class.java ->{
                btnCommunity.performClick() //模拟点击的
            }
            else ->{}
        }

    }

    private fun setTabSelection(index: Int) {
        clearAllSelected()
        fragmentManager.beginTransaction().apply {
            hideFragment(this)
            when (index) {
                0 -> {
                    ivHomePage.isSelected = true
                    tvHomePage.isSelected = true
                    if (homePageFragement == null) {
                        homePageFragement = HomePageFragment.newInstance()
                        add(R.id.homeActivityFragContainer, homePageFragement!!)
                    } else {
                        show(homePageFragement!!)
                    }
                }
                1 -> {
                    ivCommunity.isSelected = true
                    tvCommunity.isSelected = true
                    if (communityFragement == null) {
                        communityFragement = CommunityFragment.newInstance()
                        add(R.id.homeActivityFragContainer, communityFragement!!)
                    } else {
                        show(communityFragement!!)
                    }
                }
                2 -> {
                    ivNotification.isSelected = true
                    tvNotification.isSelected = true
                    if (notificationFragment == null) {
                        notificationFragment = NotificationFragment.newInstance()
                        add(R.id.homeActivityFragContainer, notificationFragment!!)
                    } else {
                        show(notificationFragment!!)
                    }
                }
                3 -> {
                    ivMine.isSelected = true
                    tvMine.isSelected = true
                    if (mineFragment == null) {
                        mineFragment = MineFragment.newInstance()
                        add(R.id.homeActivityFragContainer, mineFragment!!)
                    } else {
                        show(mineFragment!!)
                    }
                }
                else ->{
                    ivHomePage.isSelected = true
                    tvHomePage.isSelected = true
                    if (homePageFragement == null) {
                        homePageFragement = HomePageFragment.newInstance()
                        add(R.id.homeActivityFragContainer, homePageFragement!!)
                    } else {
                        show(homePageFragement!!)
                    }
                }
            }
        }.commitAllowingStateLoss()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        transaction.run {
            if (homePageFragement != null) hide(homePageFragement!!)
            if (communityFragement != null) hide(communityFragement!!)
            if (notificationFragment != null) hide(notificationFragment!!)
            if (mineFragment != null) hide(mineFragment!!)
        }

    }

    private fun clearAllSelected() {
        ivHomePage.isSelected = false
        tvHomePage.isSelected = false
        ivCommunity.isSelected = false
        tvCommunity.isSelected = false
        ivNotification.isSelected = false
        tvNotification.isSelected = false
        ivMine.isSelected = false
        tvMine.isSelected = false
    }

    //发出消息事件
    private fun notificationUiRefresh(index: Int) {
        when (index) {
            0 -> {
                if (ivHomePage.isSelected) EventBus.getDefault().post(RefreshEvent(HomePageFragment::class.java))
            }
            1 -> {
                if (ivCommunity.isSelected) EventBus.getDefault().post(RefreshEvent(CommunityFragment::class.java))
            }
            2 -> {
                if (ivNotification.isSelected) EventBus.getDefault().post(RefreshEvent(NotificationFragment::class.java))
            }
            3 -> {
                if (ivMine.isSelected) EventBus.getDefault().post(RefreshEvent(MineFragment::class.java))
            }
        }
    }

    //WorkManager组件，执行弹出框后台任务
    private fun observe() {
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(DialogAppraiseTipsWorker.showDialogWorkRequest.id).observe(this, Observer { workInfo ->
            logD(TAG, "observe: workInfo.state = ${workInfo.state}")
            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                WorkManager.getInstance(this).cancelAllWork()
            } else if (workInfo.state == WorkInfo.State.RUNNING) {
                if (isActive) {
                    DialogAppraiseTipsWorker.showDialog(this)
                    WorkManager.getInstance(this).cancelAllWork()
                }
            }
        })
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }else{
            processBackPressed()
        }
    }

    private fun processBackPressed() {
        val now = System.currentTimeMillis()
        if (now - backPressTime > 2000){
            String.format(GlobalUtil.getString(R.string.press_again_to_exit),GlobalUtil.appName).showToas()
            backPressTime = now
        }else{
            super.onBackPressed()
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}
