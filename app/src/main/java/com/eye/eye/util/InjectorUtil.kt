package com.eye.eye.util

import com.eye.eye.logic.MainPageRepository
import com.eye.eye.logic.VideoRepository
import com.eye.eye.logic.dao.EyeDatabase
import com.eye.eye.logic.network.EyeNetWork
import com.eye.eye.ui.community.follow.FollowViewModelFactory
import com.eye.eye.ui.home.commed.CommendViewModelFactory
import com.eye.eye.ui.home.daily.DailyViewModelFactory
import com.eye.eye.ui.home.discovery.DiscoveryViewModelFactory
import com.eye.eye.ui.newdetail.NewDetailViewModelFactory
import com.eye.eye.ui.notification.push.PushViewModelFactory
import com.eye.eye.ui.search.SearchViewModelFactory

/**
 * 应用程序逻辑控制管理类。
 */
object InjectorUtil {

    private fun getMainPageRepository() = MainPageRepository.getInstance(EyeDatabase.getMainPageDao(), EyeNetWork.getInstance())

    private fun getViedoRepository() = VideoRepository.getInstance(EyeDatabase.getVideoDao(),EyeNetWork.getInstance())

    fun getDiscoveryViewModelFactory() = DiscoveryViewModelFactory(getMainPageRepository())

    fun getHomePageCommendViewModelFactory() = CommendViewModelFactory(getMainPageRepository())

    fun getDailyViewModelFactory() = DailyViewModelFactory(getMainPageRepository())

    fun getNewDetailViewModelFactory() = NewDetailViewModelFactory(getViedoRepository())

    fun getSearchViewModelFactory() = SearchViewModelFactory(getMainPageRepository())

    fun getCommunityCommendViewModelFactory() = com.eye.eye.ui.community.commend.CommendViewModelFactory(getMainPageRepository())

    fun getFollowViewModelFactory() = FollowViewModelFactory(getMainPageRepository())

    fun getPushViewModelFactory() = PushViewModelFactory(getMainPageRepository())
}