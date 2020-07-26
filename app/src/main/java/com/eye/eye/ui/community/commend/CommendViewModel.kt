package com.eye.eye.ui.community.commend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.eye.eye.logic.MainPageRepository
import com.eye.eye.logic.model.CommunityRecommend
import com.eye.eye.logic.network.api.MainPageService

class CommendViewModel(repository: MainPageRepository): ViewModel() {

    var dataList = ArrayList<CommunityRecommend.Item>()

    private var requestParamLiveData = MutableLiveData<String>()

    var nextPageUrl: String? = null

    val dataListLiveData = Transformations.switchMap(requestParamLiveData) { url ->
        liveData {
            val resutlt = try {
                val recommend = repository.refreshCommunityRecommend(url)
                Result.success(recommend)
            } catch (e: Exception) {
                Result.failure<CommunityRecommend>(e)
            }
            emit(resutlt)
        }
    }

    fun onRefresh() {
        requestParamLiveData.value = MainPageService.COMMUNITY_RECOMMEND_URL
    }

    fun onLoadMore() {
        requestParamLiveData.value = nextPageUrl ?: ""
    }
}