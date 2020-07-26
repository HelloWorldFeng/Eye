package com.eye.eye.ui.home.commed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eye.eye.logic.MainPageRepository

class CommendViewModelFactory(private val repository: MainPageRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommendViewModel(repository) as T
    }
}