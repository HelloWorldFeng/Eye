package com.eye.eye.ui.community.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eye.eye.R
import com.eye.eye.event.MessageEvent
import com.eye.eye.event.RefreshEvent
import com.eye.eye.extension.gone
import com.eye.eye.extension.logD
import com.eye.eye.extension.showToas
import com.eye.eye.extension.visible
import com.eye.eye.ui.common.callback.AutoPlayScrollListener
import com.eye.eye.ui.common.ui.BaseFragment
import com.eye.eye.util.GlobalUtil
import com.eye.eye.util.InjectorUtil
import com.eye.eye.util.ResponseHandler
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.fragment_refresh_layout.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.recyclerView

class FollowFragment : BaseFragment() {

    private val viewModel by lazy { ViewModelProvider(this,InjectorUtil.getFollowViewModelFactory()).get(FollowViewModel::class.java) }

    private lateinit var adapter:FollowAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater.inflate(R.layout.fragment_refresh_layout,container,false))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = FollowAdapter(this,viewModel.dataList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = null
        recyclerView.addOnScrollListener(AutoPlayScrollListener(R.id.videoPlayer,AutoPlayScrollListener.PLAY_RANGE_TOP,AutoPlayScrollListener.PLAY_RANGE_BOTTOM))  //实现滚动的自动播放
        refreshLayout.setOnRefreshListener { viewModel.onRefresh() }
        refreshLayout.setOnLoadMoreListener { viewModel.onLoadMore() }
        refreshLayout.gone()
        observe()
    }

    override fun onMessageEvent(messageEvent: MessageEvent) {
        super.onMessageEvent(messageEvent)
        if (messageEvent is RefreshEvent && javaClass == messageEvent.activityClass) {
            refreshLayout.autoRefresh()
            if (recyclerView.adapter?.itemCount ?: 0 > 0) recyclerView.scrollToPosition(0)
        }
    }

    private fun observe() {
        viewModel.dataListLiveData.observe(viewLifecycleOwner, Observer { result ->
            val response = result.getOrNull()
            if (response == null) {
                ResponseHandler.getFailureTips(result.exceptionOrNull()).let { if (viewModel.dataList.isNullOrEmpty()) loadFailed(it) else it.showToas() }
                refreshLayout.closeHeaderOrFooter()
                return@Observer
            }
            loadFinished()
            viewModel.nextPageUrl = response.nextPageUrl
            if (response.itemList.isNullOrEmpty() && viewModel.dataList.isEmpty()) {
                refreshLayout.closeHeaderOrFooter()
                return@Observer
            }
            if (response.itemList.isNullOrEmpty() && viewModel.dataList.isNotEmpty()) {
                refreshLayout.finishLoadMoreWithNoMoreData()
                return@Observer
            }
            when (refreshLayout.state) {
                RefreshState.None,RefreshState.Refreshing ->{
                    viewModel.dataList.clear()
                    viewModel.dataList.addAll(response.itemList)
                    adapter.notifyDataSetChanged()
                }
                RefreshState.Loading ->{
                    val itemCount = viewModel.dataList.size
                    viewModel.dataList.addAll(response.itemList)
                    adapter.notifyItemRangeInserted(itemCount,response.itemList.size)
                }
                else ->{}
            }
            if (response.nextPageUrl.isNullOrEmpty()) {
                refreshLayout.finishLoadMoreWithNoMoreData()
            } else {
                refreshLayout.closeHeaderOrFooter()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        GSYVideoManager.releaseAllVideos()
    }

    override fun loadDataOnce() {
        super.loadDataOnce()
        startLoading()
    }

    override fun startLoading() {
        super.startLoading()
        viewModel.onRefresh()
        refreshLayout.gone()
    }

    override fun loadFinished() {
        super.loadFinished()
        refreshLayout.visible()
    }

    override fun loadFailed(mas: String?) {
        super.loadFailed(mas)
        showLoadErrorView(mas?: GlobalUtil.getString(R.string.unknown_error)){
            startLoading()
        }
    }

    companion object {
        fun newInstance() = FollowFragment()
    }
}