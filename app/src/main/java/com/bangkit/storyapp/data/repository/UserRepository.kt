package com.bangkit.storyapp.data.repository

import com.bangkit.storyapp.data.remote.api.ApiService
import com.bangkit.storyapp.data.remote.model.LoginBody
import com.bangkit.storyapp.data.remote.model.LoginResponse
import com.bangkit.storyapp.data.remote.model.RegisterBody
import com.bangkit.storyapp.data.remote.model.RegisterResponse
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    suspend fun userRegister(registerBody: RegisterBody): Response<RegisterResponse> {
        return apiService.userRegister(registerBody)
    }

    suspend fun userLogin(loginBody: LoginBody): Response<LoginResponse> {
        return apiService.userLogin(loginBody)
    }
}