package com.bangkit.storyapp.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.remote.model.RegisterResponse
import com.bangkit.storyapp.data.repository.StoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _response = MutableLiveData<RegisterResponse>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _errorMessage = MutableLiveData<String>()
    private val _exception = MutableLiveData<Boolean>()

    val response: LiveData<RegisterResponse> = _response
    val isLoading: LiveData<Boolean> = _isLoading
    val errorMessage: LiveData<String> = _errorMessage
    val exception: LiveData<Boolean> = _exception

    fun uploadStory(photo: File, description: String, lat: Double?, lng: Double?) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = if (lat != null && lng != null) {
                    storyRepository.uploadStoryWithLocation(photo, description, lat, lng)
                } else {
                    storyRepository.uploadStory(photo, description)
                }

                if (response.isSuccessful) {
                    _response.postValue(response.body())
                } else {
                    val errorJson = response.errorBody()?.string()
                    val apiError = Gson().fromJson(errorJson, RegisterResponse::class.java)
                    _errorMessage.postValue(apiError.message)
                }
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}