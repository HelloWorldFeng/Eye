package com.eye.eye.ui.notification.push

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eye.eye.logic.MainPageRepository

class PushViewModelFactory(private val repository: MainPageRepository):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PushViewModel(repository) as T
    }
}