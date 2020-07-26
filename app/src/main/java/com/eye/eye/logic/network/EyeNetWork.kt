package com.eye.eye.logic.network

import com.eye.eye.logic.network.api.MainPageService
import com.eye.eye.logic.network.api.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
* 管理所有网络请求
* */

class EyeNetWork {

    private val mainPageService = ServiceCreator.create(MainPageService::class.java)

    private val videoService = ServiceCreator.create(VideoService::class.java)

    suspend fun fetchDiscovery(url: String) = mainPageService.getDiscovery(url).await()

    suspend fun fetchHomePageRecommend(url:String) = mainPageService.getHomePageRecommend(url).await()

    suspend fun fetchDaily(url:String) = mainPageService.getDaily(url).await()

    suspend fun fetchCommunityRecommend(url:String) = mainPageService.getCommunityRecommend(url).await()

    suspend fun fetchFollow(url: String) = mainPageService.getFollow(url).await()

    suspend fun fetchPushMessage(url: String) = mainPageService.getPushMessage(url).await()

    suspend fun fetchHotSearch() = mainPageService.getHotSearch().await()

    suspend fun fetchVideoBeanForClient(videoId:Long) = videoService.getVideoBeanForClient(videoId).await()

    suspend fun fetchVideoRelated(videoId: Long) = videoService.getVideoRelated(videoId).await()

    suspend fun fetchVideoReplies(url:String) = videoService.getVideoReplies(url).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    System.out.println("fail")
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    System.out.println("success")
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }
            })
        }
    }

    companion object {
        private var network:EyeNetWork ?= null

        fun getInstance(): EyeNetWork {
            if (network == null) {
                synchronized(EyeNetWork::class.java) {
                    if (network == null) {
                        network = EyeNetWork()
                    }
                }
            }
            return network!!
        }
    }
}
