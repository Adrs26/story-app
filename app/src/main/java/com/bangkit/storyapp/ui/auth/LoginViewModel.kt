package com.bangkit.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapp.data.model.LoginBody
import com.bangkit.storyapp.data.model.LoginResponse
import com.bangkit.storyapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _errorCode = MutableLiveData<Int>()
    private val _exception = MutableLiveData<Boolean>()

    val loginResponse: LiveData<LoginResponse> = _loginResponse
    val isLoading: LiveData<Boolean> = _isLoading
    val errorCode: LiveData<Int> = _errorCode
    val exception: LiveData<Boolean> = _exception

    fun userLogin(loginBody: LoginBody) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userRepository.userLogin(loginBody)

                if (response.isSuccessful) {
                    _loginResponse.postValue(response.body())
                } else {
                    _errorCode.postValue(response.code())
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