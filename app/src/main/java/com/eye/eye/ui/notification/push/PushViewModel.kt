package com.eye.eye.ui.notification.push

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.eye.eye.logic.MainPageRepository
import com.eye.eye.logic.model.PushMessage
import com.eye.eye.logic.network.api.MainPageService

class PushViewModel(private val repository: MainPageRepository):ViewModel() {

    var dataList = ArrayList<PushMessage.Message>()

    private var requestPragmaLiveData = MutableLiveData<String>()

    var nextPageUrl:String ?= null

    val dataListLiveData = Transformations.switchMap(requestPragmaLiveData){ url ->
        liveData {
            val result = try {
                val response = repository.refreshPushMessage(url)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure<PushMessage>(e)
            }
            emit(result)
        }
    }

    fun onRefresh(){
        requestPragmaLiveData.value = MainPageService.PUSHMESSAGE_URL
    }
    fun onLoadMore(){
        requestPragmaLiveData.value = nextPageUrl ?: ""
    }
}