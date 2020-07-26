package com.eye.eye.ui.common.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.eye.eye.R
import com.eye.eye.event.MessageEvent
import com.eye.eye.extension.logD
import com.eye.eye.ui.common.callback.RequestLifecycle
import com.eye.eye.util.ShareUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 应用程序中所有Fragment的基类。
 */
open class BaseFragment : Fragment(), RequestLifecycle {

    /*
    * 是否已经加载过数据
    * */
    private var mHasLoadedData = false

    /*
    * Fragment中由于服务器或网络异常导致加载失败显示的布局
    * */
    private var loadErrorView: View? = null

    /*
    * Fragment中inflate出来的布局
    * */
    protected var rootView: View? = null

    /*
    * Fragment中显示加载等待的控件
    * */
    protected var loading: ProgressBar? = null

    /*
    * 依附的Activity
    * */
    lateinit var activity: Activity

    /*
    * 日志输出标准
    * */
    protected val TAG: String = this.javaClass.simpleName

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //缓存当前依附的Activity
        activity = getActivity()!!
    }

    override fun onResume() {
        super.onResume()
        //当Fragment在屏幕上可见并且没有加载过数据是调用
        if (!mHasLoadedData) {
            loadDataOnce()
            mHasLoadedData = true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        if (rootView?.parent != null) (rootView?.parent as ViewGroup).removeView(rootView)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(message: MessageEvent) {
    }

    /*开始加载，将加载等待控件显示
    * */
    @CallSuper
    override fun startLoading() {
        loading?.visibility = View.VISIBLE
        hideLoadErrorView()
    }


    /*
    * 加载完成，将加载控件隐藏
    * */
    @CallSuper
    override fun loadFinished() {
        loading?.visibility = View.GONE
    }

    /*
    * 加载失败，将加载控件隐藏
    * */
    @CallSuper
    override fun loadFailed(mas: String?) {
        loading?.visibility = View.GONE
    }

    /*
    * 在Fragment基类中获取通用的控件，对将传入的View实例原封不动返回
    * @param view Fragment中inflate出来的View实例
    * @return Fragment中inflate出来的View实例原封不动返回
    * */
    fun onCreateView(view: View): View {
        rootView = view
        loading = view.findViewById(R.id.loading)
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
        return view
    }


    /*
    * 页面首次可见时调用一次改方法，在这里可以进行网络请求
    * */
    open fun loadDataOnce() {
    }

    /*
    *当Fragment中的加载内容服务器返回失败或网络异常，通过此方法显示提示界面给用户
    * @param tip界面中的提示信息
    * @param block 点击界面重新加载，回调处理
    * */
    protected fun showLoadErrorView(tip: String, block: View.() -> Unit) {
        if (loadErrorView != null) {
            loadErrorView?.visibility = View.VISIBLE
            return
        }
        if (rootView != null) {
            val viewStub = rootView?.findViewById<ViewStub>(R.id.loadErrorView)
            if (viewStub != null) {
                loadErrorView = viewStub.inflate()
                val loadErrorText = loadErrorView?.findViewById<TextView>(R.id.loadErrorText)
                loadErrorText?.setText(tip)
                val loadErrorkRootView = loadErrorView?.findViewById<View>(R.id.loadErrorkRootView)
                loadErrorkRootView?.setOnClickListener {
                    it?.block()
                }
            }
        }
    }

    /*
    * 将load error view 进行隐藏
    * */
    open fun hideLoadErrorView() {
        loadErrorView?.visibility = View.GONE
    }

    /*
    * 调用系统原生分享
    *@param 分享内容
    * @param 分享平台 */
    protected fun share(shareContent: String, shareType: Int) {
        ShareUtil.share(this.activity, shareContent, shareType)
    }

    /*
    * 弹出分享对话框
    * @param shareContent 分享内容
    * */
    protected fun showDialogShare(shareContent: String) {
        if (this.activity is AppCompatActivity) {
            com.eye.eye.extension.showDialogShare(this.activity as AppCompatActivity,shareContent)
        }
    }
}