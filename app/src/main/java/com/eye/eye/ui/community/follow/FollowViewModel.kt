package com.eye.eye.ui.community.follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.eye.eye.logic.MainPageRepository
import com.eye.eye.logic.model.Follow
import com.eye.eye.logic.network.api.MainPageService

class FollowViewModel(private val repository: MainPageRepository):ViewModel() {

    var dataList = ArrayList<Follow.Item>()

    private var requestParamLiveData = MutableLiveData<String>()

    var nextPageUrl:String ?= null

    val dataListLiveData = Transformations.switchMap(requestParamLiveData){ url ->
        liveData {
            val result = try {
                val response = repository.refreshFollow(url)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure<Follow>(e)
            }
            emit(result)
        }
    }

    fun onRefresh(){
        requestParamLiveData.value = MainPageService.FOLLOW_URL
    }
    fun onLoadMore() {
        requestParamLiveData.value = nextPageUrl ?: ""
    }
}