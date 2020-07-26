package com.eye.eye.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.eye.eye.logic.MainPageRepository

class SearchViewModel(repository: MainPageRepository):ViewModel() {

    var dataList = ArrayList<String>()

    private var requestParamLiveData = MutableLiveData<Any>()

    val dataListLiveData = Transformations.switchMap(requestParamLiveData){
        liveData {
            val result = try {
                val hotSearch = repository.refreshHotSearch()
                Result.success(hotSearch)
            } catch (e: Exception) {
                Result.failure<List<String>>(e)
            }
            emit(result)
        }
    }

    fun onRefrsh(){
        requestParamLiveData.value = requestParamLiveData.value
    }
}