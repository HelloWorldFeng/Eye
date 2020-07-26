package com.eye.eye.ui.home.commed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.eye.eye.logic.MainPageRepository
import com.eye.eye.logic.model.HomePageRecommend
import com.eye.eye.logic.network.api.MainPageService
import java.lang.Exception

class CommendViewModel(val repository: MainPageRepository) : ViewModel() {

    var dataList = ArrayList<HomePageRecommend.Item>()

    private var requestParamLiveData = MutableLiveData<String>()

    var nextPageUrl:String ?= null

    val dataListLiveData = Transformations.switchMap(requestParamLiveData) { url ->
        liveData {
            val resutlt = try {
                val recommend = repository.refreshHomePageRecommend(url)
                Result.success(recommend)
            } catch (e: Exception) {
                Result.failure<HomePageRecommend>(e)
            }
            emit(resutlt)
        }
    }

    fun onRefresh(){
        requestParamLiveData.value = MainPageService.HOMEPAGE_RECOMMEND_RUL
    }

    fun onLoadMore(){
        requestParamLiveData.value = nextPageUrl ?: ""
    }
}