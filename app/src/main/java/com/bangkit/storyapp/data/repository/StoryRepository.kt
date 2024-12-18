package com.bangkit.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.storyapp.data.remote.api.ApiService
import com.bangkit.storyapp.data.remote.model.HeaderStories
import com.bangkit.storyapp.data.remote.model.HeaderStory
import com.bangkit.storyapp.data.remote.model.RegisterResponse
import com.bangkit.storyapp.data.remote.model.Story
import com.bangkit.storyapp.data.paging.StoryPagingSource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class StoryRepository(private val apiService: ApiService) {
    fun getStories(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): HeaderStories {
        return apiService.getStoriesWithLocation(12, 1)
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

    suspend fun uploadStoryWithLocation(
        photo: File,
        description: String,
        lat: Double,
        lng: Double
    ): Response<RegisterResponse> {
        val requestPhoto = photo.asRequestBody("image/jpeg".toMediaType())
        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestLat = lat.toString().toRequestBody("text/plain".toMediaType())
        val requestLng = lng.toString().toRequestBody("text/plain".toMediaType())
        val multipartPhoto = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestPhoto
        )

        return apiService.uploadStoryWithLocation(
            multipartPhoto,
            requestDescription,
            requestLat,
            requestLng
        )
    }
}