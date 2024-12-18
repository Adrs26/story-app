package com.bangkit.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.storyapp.data.remote.model.Story
import com.bangkit.storyapp.data.repository.StoryRepository

class HomeViewModel(storyRepository: StoryRepository) : ViewModel() {
    private val _scrollPosition = MutableLiveData<Int>().apply { value = 0 }
    val scrollPosition: LiveData<Int> = _scrollPosition

    val stories: LiveData<PagingData<Story>> = storyRepository.getStories().cachedIn(viewModelScope)

    fun saveScrollPosition(position: Int) {
        _scrollPosition.value = position
    }
}