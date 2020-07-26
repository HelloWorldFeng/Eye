package com.eye.eye.ui.ugcdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.eye.eye.R
import com.eye.eye.extension.showToas
import com.eye.eye.logic.model.CommunityRecommend
import com.eye.eye.ui.common.callback.AutoPlayPageChangeListener
import com.eye.eye.ui.common.ui.BaseActivity
import com.eye.eye.util.GlobalUtil
import com.eye.eye.util.IntentDataHolderUtil
import com.shuyu.gsyvideoplayer.GSYVideoManager
import kotlinx.android.synthetic.main.activity_ugc_detail.*

/*
* 社区-推荐详情页
* */
class UgcDetailActivity : BaseActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(UgcDetailViewModel::class.java) }

    private lateinit var adapter: UgcDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ugc_detail)
    }

    override fun setContentView(layoutResID: Int) {
        if (checkArguments()) {
            super.setContentView(layoutResID)
            setStatusBarBackground(R.color.black)
        }
    }

    override fun setupViews() {
        super.setupViews()
        if (viewModel.dataList == null) {
            viewModel.itemPosition = getCurrentItemPosition()
            viewModel.dataList = IntentDataHolderUtil.getData<List<CommunityRecommend.Item>>(EXTRA_RECOMMEND_ITEM_LIST_JSON)
        }
        if (viewModel.dataList == null) {
            GlobalUtil.getString(R.string.jump_page_unknown_error).showToas()
            finish()
        } else {
            adapter = UgcDetailAdapter(this, viewModel.dataList!!)
            viewPager.adapter = adapter
            viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
            viewPager.offscreenPageLimit = 1
            viewPager.registerOnPageChangeCallback(AutoPlayPageChangeListener(viewPager,viewModel.itemPosition,R.id.videoPlayer))    //监听viewPage的向下滚动，视频的自动播放
            viewPager.setCurrentItem(viewModel.itemPosition)
        }

    }

    private fun checkArguments() = if (IntentDataHolderUtil.getData<List<CommunityRecommend.Item>>(EXTRA_RECOMMEND_ITEM_LIST_JSON).isNullOrEmpty()
        || IntentDataHolderUtil.getData<CommunityRecommend.Item>(EXTRA_RECOMMEND_ITEM_JSON) == null
    ) {
        GlobalUtil.getString(R.string.jump_page_unknown_error).showToas()
        finish()
        false
    } else {
        true
    }

    private fun getCurrentItemPosition(): Int {
        val list = IntentDataHolderUtil.getData<List<CommunityRecommend.Item>>(EXTRA_RECOMMEND_ITEM_LIST_JSON)
        val currentItem = IntentDataHolderUtil.getData<CommunityRecommend.Item>(EXTRA_RECOMMEND_ITEM_JSON)
        list?.forEachIndexed { index, item ->
            if (currentItem == item) {
                viewModel.itemPosition = index
                return@forEachIndexed
            }
        }
        return viewModel.itemPosition
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_bottom_out)
    }

    companion object {
        const val TAG = "UgcDetailActivity"
        const val EXTRA_RECOMMEND_ITEM_LIST_JSON = "recommend_item_list"
        const val EXTRA_RECOMMEND_ITEM_JSON = "recommend_item"

        fun start(
            context: Activity,
            dataList: List<CommunityRecommend.Item>,
            currentItem: CommunityRecommend.Item
        ) {
            IntentDataHolderUtil.setData(EXTRA_RECOMMEND_ITEM_LIST_JSON, dataList)
            IntentDataHolderUtil.setData(EXTRA_RECOMMEND_ITEM_JSON, currentItem)
            val starter = Intent(context, UgcDetailActivity::class.java)
            context.startActivity(starter)
            context.overridePendingTransition(R.anim.anl_push_bottom_in, R.anim.anl_push_up_out)
        }
    }
}