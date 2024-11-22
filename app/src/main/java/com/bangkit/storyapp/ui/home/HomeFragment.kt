package com.bangkit.storyapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.api.ApiClient
import com.bangkit.storyapp.data.api.ApiClientBearer
import com.bangkit.storyapp.data.datastore.DataStoreInstance
import com.bangkit.storyapp.data.datastore.UserPreference
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.databinding.FragmentHomeBinding
import com.bangkit.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var token: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataStoreObserver()
        setupAdapter()
    }

    private fun setupDataStoreObserver() {
        lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                this@HomeFragment.token = token
                if (this@HomeFragment::token.isInitialized) {
                    setupViewModel(token)
                    getStories()
                    setupViewModelObservers()
                }
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = ViewModelFactory(
            UserRepository(ApiClient.apiClient),
            StoryRepository(ApiClientBearer.create(token))
        )
        homeViewModel = ViewModelProvider(requireActivity(), factory)[HomeViewModel::class.java]
    }

    private fun getStories() {
        homeViewModel.getStories()
    }

    private fun setupAdapter() {
        homeAdapter = HomeAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.adapter = homeAdapter
    }

    private fun setupViewModelObservers() {
        homeViewModel.stories.observe(viewLifecycleOwner) { stories ->
            homeAdapter.submitList(stories)
        }
    }
}