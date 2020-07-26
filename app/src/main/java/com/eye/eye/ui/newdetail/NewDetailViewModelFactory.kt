package com.eye.eye.ui.newdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eye.eye.logic.VideoRepository

class NewDetailViewModelFactory(private val repository: VideoRepository):ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewDetailViewModel(repository) as T
    }
}