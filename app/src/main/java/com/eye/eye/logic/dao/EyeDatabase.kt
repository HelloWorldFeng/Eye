package com.eye.eye.logic.dao

object EyeDatabase {


    private var mainPageDao:MainPageDao ?= null
    private var videoPageDao: VideoDao? = null

    fun getMainPageDao(): MainPageDao {
        if (mainPageDao == null) mainPageDao = MainPageDao()
        return mainPageDao!!
    }

    fun getVideoDao(): VideoDao {
        if (videoPageDao == null) videoPageDao = VideoDao()
        return videoPageDao!!
    }
}