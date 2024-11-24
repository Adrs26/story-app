package com.bangkit.storyapp.data.repository

import com.bangkit.storyapp.data.api.ApiService
import com.bangkit.storyapp.data.model.LoginBody
import com.bangkit.storyapp.data.model.LoginResponse
import com.bangkit.storyapp.data.model.RegisterBody
import com.bangkit.storyapp.data.model.RegisterResponse
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    suspend fun userRegister(registerBody: RegisterBody): Response<RegisterResponse> {
        return apiService.userRegister(registerBody)
    }

    suspend fun userLogin(loginBody: LoginBody): Response<LoginResponse> {
        return apiService.userLogin(loginBody)
    }
}