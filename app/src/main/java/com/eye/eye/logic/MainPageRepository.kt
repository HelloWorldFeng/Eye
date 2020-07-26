package com.eye.eye.logic

import com.eye.eye.logic.dao.MainPageDao
import com.eye.eye.logic.network.EyeNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class MainPageRepository private constructor(
    private val mainPageDao: MainPageDao,
    private val eyeNetWork: EyeNetWork

){

    suspend fun refreshDiscovery(url:String) = requestDiscovery(url)

    suspend fun refreshHomePageRecommend(url: String) = requestHomePageRecommend(url)

    suspend fun refreshDaily(url: String) = requestDaily(url)

    suspend fun refreshCommunityRecommend(url: String) = requestCommunityRecommend(url)

    suspend fun refreshFollow(url: String) = requestFollow(url)

    suspend fun refreshPushMessage(url: String) = requestPushMessage(url)

    suspend fun refreshHotSearch() = requestHotSearch()


    private suspend fun requestDiscovery(url: String) = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                val response = eyeNetWork.fetchDiscovery(url)
//                mainPageDao.cacheDiscovery(response)
                response
            }.await()
        }
    }

    private suspend fun requestHomePageRecommend(url: String) = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                val response = eyeNetWork.fetchHomePageRecommend(url)
//                mainPageDao.cacheHomePageRecommend(response)
                response
            }.await()
        }
    }

    private suspend fun requestDaily(url: String) = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                val response = eyeNetWork.fetchDaily(url)
//                mainPageDao.cacheDaily(response)
                response
            }.await()
        }

    }

    private suspend fun requestCommunityRecommend(url: String) = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                val response = eyeNetWork.fetchCommunityRecommend(url)
//                mainPageDao.cacheCommunityRecommend(response)
                response
            }.await()
        }

    }

    private suspend fun requestFollow(url: String) = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                val response = eyeNetWork.fetchFollow(url)
//                mainPageDao.cacheFollow(response)
                response
            }.await()
        }

    }

    private suspend fun requestPushMessage(url: String) = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                val response = eyeNetWork.fetchPushMessage(url)
//                mainPageDao.cachePushMessageInfo(response)
                response
            }.await()
        }

    }

    private suspend fun requestHotSearch() = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                val response = eyeNetWork.fetchHotSearch()
//                mainPageDao.cacheHotSearch(response)
                response
            }.await()
        }

    }

    companion object {

        var repository: MainPageRepository ?= null

        fun getInstance(dao: MainPageDao, network: EyeNetWork):MainPageRepository {
            if (repository == null) {
                synchronized(MainPageRepository::class.java) {
                    if (repository == null) {
                        repository = MainPageRepository(dao,network)
                    }
                }
            }
            return repository!!
        }
    }
}