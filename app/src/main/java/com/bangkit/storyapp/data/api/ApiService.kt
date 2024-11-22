package com.bangkit.storyapp.data.api

import com.bangkit.storyapp.data.model.HeaderStories
import com.bangkit.storyapp.data.model.LoginBody
import com.bangkit.storyapp.data.model.LoginResponse
import com.bangkit.storyapp.data.model.RegisterBody
import com.bangkit.storyapp.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun userRegister(@Body registerBody: RegisterBody): RegisterResponse

    @POST("login")
    suspend fun userLogin(@Body loginBody: LoginBody): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(@Query("size") size: Int): HeaderStories
}