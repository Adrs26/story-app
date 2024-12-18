package com.bangkit.storyapp.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.remote.api.ApiClient
import com.bangkit.storyapp.data.remote.api.ApiClientBearer
import com.bangkit.storyapp.data.datastore.DataStoreInstance
import com.bangkit.storyapp.data.datastore.UserPreference
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.databinding.FragmentDetailBinding
import com.bangkit.storyapp.ui.main.MainActivity
import com.bangkit.storyapp.ui.viewmodel.ViewModelFactory
import com.bangkit.storyapp.util.DateHelper.convertDateFormat
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private val binding by viewBinding(FragmentDetailBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var token: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButton()
        setupBackPressedDispatcher()
        setupDataStoreObserver()
    }

    private fun setupBackButton() {
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
            detailViewModel.resetExceptionValue()
            (activity as MainActivity).showBottomNav()
        }
    }

    private fun setupBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                    detailViewModel.resetExceptionValue()
                    (activity as MainActivity).showBottomNav()
                }
            }
        )
    }

    private fun setupDataStoreObserver() {
        lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                this@DetailFragment.token = token
                if (this@DetailFragment::token.isInitialized) {
                    val storyId = DetailFragmentArgs.fromBundle(arguments as Bundle).storyId
                    setupViewModel(token)
                    getDetailStory(storyId)
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
        detailViewModel = ViewModelProvider(requireActivity(), factory)[DetailViewModel::class.java]
    }

    private fun getDetailStory(storyId: String) {
        detailViewModel.getDetailStory(storyId)
    }

    private fun setupViewModelObservers() {
        detailViewModel.story.observe(viewLifecycleOwner) { story ->
            binding.apply {
                content.visibility = View.VISIBLE
                tvDetailName.text = story.name
                tvDetailDescription.text = story.description
                tvDetailPostedOn.text = convertDateFormat(story.createdAt.substring(0, 10))
                setupGlide(story.photoUrl, ivDetailPhoto)
            }
        }

        detailViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.pbDetail.visibility = View.VISIBLE
                binding.content.visibility = View.GONE
            } else {
                binding.pbDetail.visibility = View.GONE
            }
        }

        detailViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.cannot_connect_to_server))
                binding.tvNoInternet.visibility = View.VISIBLE
                binding.content.visibility = View.GONE
            } else {
                binding.tvNoInternet.visibility = View.GONE
            }
        }
    }

    private fun setupGlide(data: String, target: ImageView) {
        Glide.with(requireContext())
            .load(data)
            .fitCenter()
            .into(target)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}