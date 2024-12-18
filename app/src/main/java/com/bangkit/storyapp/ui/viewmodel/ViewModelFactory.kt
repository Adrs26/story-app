package com.bangkit.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.ui.addstory.AddStoryViewModel
import com.bangkit.storyapp.ui.auth.LoginViewModel
import com.bangkit.storyapp.ui.auth.RegisterViewModel
import com.bangkit.storyapp.ui.detail.DetailViewModel
import com.bangkit.storyapp.ui.home.HomeViewModel
import com.bangkit.storyapp.ui.maps.MapsViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}