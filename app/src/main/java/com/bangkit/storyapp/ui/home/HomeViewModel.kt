package com.bangkit.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.model.Story
import com.bangkit.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<Story>>()
    private val _scrollPosition = MutableLiveData<Int>().apply { value = 0 }
    private val _isLoading = MutableLiveData<Boolean>()
    private val _exception = MutableLiveData<Boolean>()

    val stories: LiveData<List<Story>> = _stories
    val scrollPosition: LiveData<Int> = _scrollPosition
    val isLoading: LiveData<Boolean> = _isLoading
    val exception: LiveData<Boolean> = _exception

    fun getStories() {
        if (_stories.value == null) {
            _isLoading.value = true

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _stories.postValue(storyRepository.getStories().listStory)
                    _exception.postValue(false)
                } catch (e: Exception) {
                    _exception.postValue(true)
                } finally {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun saveScrollPosition(position: Int) {
        _scrollPosition.value = position
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}