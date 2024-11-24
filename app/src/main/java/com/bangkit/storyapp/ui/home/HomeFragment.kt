package com.bangkit.storyapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var token: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressedDispatcher()
        setupAdapter()
        setupDataStoreObserver()
    }

    override fun onPause() {
        super.onPause()
        saveScrollPosition(layoutManager.findFirstCompletelyVisibleItemPosition())
    }

    private fun setupBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }

    private fun setupAdapter() {
        homeAdapter = HomeAdapter(object : HomeAdapter.OnItemClickListener {
            override fun onItemClick(storyId: String) {
                val action = HomeFragmentDirections.actionNavHomeToNavDetail()
                action.storyId = storyId
                findNavController().navigate(action)
            }
        })
        layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.layoutManager = layoutManager
        binding.rvStories.adapter = homeAdapter
    }

    private fun setupDataStoreObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                this@HomeFragment.token = token
                if (this@HomeFragment::token.isInitialized) {
                    setupViewModel(token)
                    getStories()
                    getScrollPosition()
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

    private fun getScrollPosition() {
        homeViewModel.scrollPosition.observe(viewLifecycleOwner) { position ->
            binding.rvStories.scrollToPosition(position)
        }
    }

    private fun setupViewModelObservers() {
        homeViewModel.stories.observe(viewLifecycleOwner) { stories ->
            homeAdapter.submitList(stories)
            binding.tvNoInternet.visibility = View.GONE
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.pbHome.visibility = View.VISIBLE
                binding.rvStories.visibility = View.GONE
            } else {
                binding.pbHome.visibility = View.GONE
                binding.rvStories.visibility = View.VISIBLE
            }
        }

        homeViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.cannot_connect_to_server))
                homeViewModel.resetExceptionValue()

                if (homeViewModel.stories.value.isNullOrEmpty()) {
                    binding.tvNoInternet.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun saveScrollPosition(position: Int) {
        homeViewModel.saveScrollPosition(position)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}