package com.bangkit.storyapp.data.repository

import com.bangkit.storyapp.data.api.ApiService
import com.bangkit.storyapp.data.model.HeaderStories
import com.bangkit.storyapp.data.model.HeaderStory
import com.bangkit.storyapp.data.model.RegisterResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class StoryRepository(private val apiService: ApiService) {
    suspend fun getStories(): HeaderStories {
        return apiService.getStories(20)
    }

    suspend fun getDetailStory(storyId: String): HeaderStory {
        return apiService.getDetailStory(storyId)
    }

    suspend fun uploadStory(photo: File, description: String): Response<RegisterResponse> {
        val requestPhoto = photo.asRequestBody("image/jpeg".toMediaType())
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val multipartPhoto = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestPhoto
        )

        return apiService.uploadStory(multipartPhoto, requestDescription)
    }
}