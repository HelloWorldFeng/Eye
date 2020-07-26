package com.eye.eye.ui.community.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eye.eye.logic.MainPageRepository

class FollowViewModelFactory(private val repository: MainPageRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FollowViewModel(repository) as T
    }
}