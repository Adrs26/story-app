package com.bangkit.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.remote.api.ApiClient
import com.bangkit.storyapp.data.remote.api.ApiClientBearer
import com.bangkit.storyapp.data.datastore.DataStoreInstance
import com.bangkit.storyapp.data.datastore.UserPreference
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.databinding.FragmentHomeBinding
import com.bangkit.storyapp.ui.maps.MapsActivity
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
        setupMapButton()
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

    private fun setupMapButton() {
        binding.ibMap.setOnClickListener {
            startActivity(Intent(requireContext(), MapsActivity::class.java))
        }
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
        binding.rvStories.adapter = homeAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { homeAdapter.retry() }
        )
    }

    private fun setupDataStoreObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                this@HomeFragment.token = token
                if (this@HomeFragment::token.isInitialized) {
                    setupViewModel(token)
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

    private fun getScrollPosition() {
        homeViewModel.scrollPosition.observe(viewLifecycleOwner) { position ->
            binding.rvStories.scrollToPosition(position)
        }
    }

    private fun setupViewModelObservers() {
        homeViewModel.stories.observe(viewLifecycleOwner) { stories ->
            homeAdapter.submitData(lifecycle, stories)
            binding.tvNoInternet.visibility = View.GONE
        }
    }

    private fun saveScrollPosition(position: Int) {
        homeViewModel.saveScrollPosition(position)
    }
}