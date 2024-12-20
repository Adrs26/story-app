package com.bangkit.storyapp.data.remote.api

import com.bangkit.storyapp.data.remote.model.HeaderStories
import com.bangkit.storyapp.data.remote.model.HeaderStory
import com.bangkit.storyapp.data.remote.model.LoginBody
import com.bangkit.storyapp.data.remote.model.LoginResponse
import com.bangkit.storyapp.data.remote.model.RegisterBody
import com.bangkit.storyapp.data.remote.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun userRegister(@Body registerBody: RegisterBody): Response<RegisterResponse>

    @POST("login")
    suspend fun userLogin(@Body loginBody: LoginBody): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): HeaderStories

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("size") size: Int,
        @Query("location") location: Int
    ): HeaderStories

    @GET("stories/{id}")
    suspend fun getDetailStory(@Path("id") id: String): HeaderStory

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Response<RegisterResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStoryWithLocation(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lng: RequestBody
    ): Response<RegisterResponse>
}