package com.eye.eye.ui.common.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import com.eye.eye.R
import com.eye.eye.event.MessageEvent
import com.eye.eye.util.ActivityCollector
import com.eye.eye.util.ShareUtil
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    /*
    * 判断当前Activity是否在前台
    * */
    protected var isActive: Boolean = false

    /*
    * 当前Activity的实例
    * */
    protected var activity: Activity? = null

    /*
    * 当前Activity的弱引用，防止内存泄露*/
    private var activityWR: WeakReference<Activity>? = null

    /*
    * 日志输出标志
    * */
    protected val TAG: String = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //将Activity弱引用压入栈
        activity = this
        activityWR = WeakReference(activity!!)
        ActivityCollector.pushTask(activityWR)

        EventBus.getDefault().register(this)

    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        //Activity销毁时将弱引用退出栈
        activity = null
        ActivityCollector.removeTask(activityWR)
        //将EventBus解绑
        EventBus.getDefault().unregister(this)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setStatusBarBackground(R.color.colorPrimaryDark)
        setupViews()
    }

    override fun setContentView(layoutView: View) {
        super.setContentView(layoutView)
        setStatusBarBackground(R.color.colorPrimaryDark)
        setupViews()
    }

    protected open fun setupViews() {
        val navigateBefore = findViewById<ImageView>(R.id.ivNavigateBefore)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        navigateBefore?.setOnClickListener { finish() }
        tvTitle?.isSelected = true  //获取焦点，实现跑马灯效果。

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(message: MessageEvent) {
    }

    /**
     * 设置状态栏背景色
     */
    open fun setStatusBarBackground(@ColorRes statusBarColor: Int) {
        ImmersionBar.with(this)
            .autoStatusBarDarkModeEnable(true, 0.2f)
            .statusBarColor(statusBarColor)
            .fitsSystemWindows(true)
            .init()
    }

    /*
    * 调用系统原生分享
    *
    * @param shareContent 分享内容
    * @param shareType 分享平台
    * */
    protected fun share(shareContent: String,shareType:Int){
        ShareUtil.share(this,shareContent,shareType)
    }

    /**
     * 弹出分享对话框
     *
     * @param shareContent 分享内容
     */
    protected fun showDialogShare(shareContent: String) {
        com.eye.eye.extension.showDialogShare(this, shareContent)
    }
}