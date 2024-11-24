package com.bangkit.storyapp.ui.addstory

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.api.ApiClient
import com.bangkit.storyapp.data.api.ApiClientBearer
import com.bangkit.storyapp.data.datastore.DataStoreInstance
import com.bangkit.storyapp.data.datastore.UserPreference
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.databinding.ActivityAddStoryBinding
import com.bangkit.storyapp.ui.main.MainActivity
import com.bangkit.storyapp.ui.viewmodel.ViewModelFactory
import com.bangkit.storyapp.util.ImageHelper
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("SourceLockedOrientationActivity", "ClickableViewAccessibility")
class AddStoryActivity : AppCompatActivity(R.layout.activity_add_story) {
    private val binding by viewBinding(ActivityAddStoryBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var token: String
    private var imageUri: Uri? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            setupGlide(imageUri!!, binding.ivAddPhoto)
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            setupGlide(imageUri!!, binding.ivAddPhoto)
        } else {
            imageUri = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupOrientation()
        setupToolbarButton()
        setupAddStoryButton()
    }

    private fun setupOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setupToolbarButton() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.ibGallery.setOnClickListener {
            startGallery()
        }
        binding.ibCamera.setOnClickListener {
            startCamera()
        }
    }

    private fun setupAddStoryButton() {
        binding.buttonAdd.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startScaleDownAnimation(view)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> startScaleUpAnimation(view)
            }
            false
        }

        binding.buttonAdd.setOnClickListener {
            if (imageUri != null) {
                val photo = ImageHelper.uriToFile(imageUri!!, this)
                setupUploadImage(photo, binding.edAddDescription.text.toString())
            } else {
                showToast(resources.getString(R.string.empty_photo))
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        imageUri = ImageHelper.getImageUri(this)
        launcherCamera.launch(imageUri!!)
    }

    private fun setupGlide(uri: Uri, target: ImageView) {
        Glide.with(this)
            .load(uri)
            .fitCenter()
            .into(target)
    }

    private fun setupUploadImage(photo: File, description: String) {
        lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                this@AddStoryActivity.token = token
                if (this@AddStoryActivity::token.isInitialized) {
                    setupViewModel(token)
                    uploadImage(photo, description)
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
        addStoryViewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]
    }

    private fun uploadImage(photo: File, description: String) {
        addStoryViewModel.uploadStory(photo, description)
    }

    private fun setupViewModelObservers() {
        addStoryViewModel.response.observe(this) { response ->
            showToast(response.message)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("destination_fragment", R.id.nav_home)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        addStoryViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.tvAdd.visibility = View.GONE
                binding.pbAdd.visibility = View.VISIBLE
            } else {
                binding.tvAdd.visibility = View.VISIBLE
                binding.pbAdd.visibility = View.GONE
            }
        }

        addStoryViewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }

        addStoryViewModel.exception.observe(this) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.cannot_connect_to_server))
                addStoryViewModel.resetExceptionValue()
            }
        }
    }

    private fun startScaleDownAnimation(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .start()
    }

    private fun startScaleUpAnimation(view: View) {
        view.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(100)
            .start()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}