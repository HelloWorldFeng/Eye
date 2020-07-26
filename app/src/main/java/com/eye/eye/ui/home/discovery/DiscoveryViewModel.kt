package com.eye.eye.ui.home.discovery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.eye.eye.logic.MainPageRepository
import com.eye.eye.logic.model.Discovery
import com.eye.eye.logic.network.api.MainPageService

class DiscoveryViewModel(repository: MainPageRepository) : ViewModel() {

    var dataList = ArrayList<Discovery.Item>()

    private var requestParamLiveData = MutableLiveData<String>()

    var nextPageUrl: String? = null

    val dataListLiveData = Transformations.switchMap(requestParamLiveData) { url ->
        liveData {
            val resutlt = try {
                val discovery = repository.refreshDiscovery(url)
                Result.success(discovery)
            } catch (e: Exception) {
                Result.failure<Discovery>(e)
            }
            emit(resutlt)
        }
    }

    fun onRefresh() {
        requestParamLiveData.value = MainPageService.DISCOVERY_URL
    }

    fun onLoadMore() {
        requestParamLiveData.value = nextPageUrl ?: ""
    }
}