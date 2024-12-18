package com.bangkit.storyapp.ui.auth

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.remote.api.ApiClient
import com.bangkit.storyapp.data.remote.model.RegisterBody
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.databinding.ActivityRegisterBinding
import com.bangkit.storyapp.ui.viewmodel.ViewModelFactory

@SuppressLint("ClickableViewAccessibility", "SourceLockedOrientationActivity")
class RegisterActivity : AppCompatActivity(R.layout.activity_register) {
    private val binding by viewBinding(ActivityRegisterBinding::bind)
    private val registerViewModel by lazy {
        val factory = ViewModelFactory(
            UserRepository(ApiClient.apiClient),
            StoryRepository(ApiClient.apiClient)
        )
        ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupOrientation()
        setupBackButton()
        setupRegisterButton()
        setupObservers()
    }

    private fun setupOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setupBackButton() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startScaleDownAnimation(view)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> startScaleUpAnimation(view)
            }
            false
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.edRegisterName.text.toString()
            val userEmail = binding.edRegisterEmail.text.toString()
            val userPassword = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edRegisterConfirmPassword.text.toString()

            if (listOf(username, userEmail, userPassword, confirmPassword).any { it.isEmpty() }) {
                showToast(resources.getString(R.string.empty_input))
            } else if (userPassword != confirmPassword) {
                showToast(resources.getString(R.string.incorrect_confirmation_password))
            } else {
                registerViewModel.userRegister(RegisterBody(username, userEmail, userPassword))
            }
        }
    }

    private fun setupObservers() {
        registerViewModel.registerResponse.observe(this) { response ->
            showToast(response.message)
            finish()
        }

        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.tvRegister.visibility = View.GONE
                binding.pbRegister.visibility = View.VISIBLE
            } else {
                binding.tvRegister.visibility = View.VISIBLE
                binding.pbRegister.visibility = View.GONE
            }
        }

        registerViewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }

        registerViewModel.exception.observe(this) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.cannot_connect_to_server))
                registerViewModel.resetExceptionValue()
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