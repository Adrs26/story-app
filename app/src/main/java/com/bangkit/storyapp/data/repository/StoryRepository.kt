package com.bangkit.storyapp.data.repository

import com.bangkit.storyapp.data.api.ApiService
import com.bangkit.storyapp.data.model.HeaderStories

class StoryRepository(private val apiService: ApiService) {
    suspend fun getStories(): HeaderStories {
        return apiService.getStories(20)
    }
}