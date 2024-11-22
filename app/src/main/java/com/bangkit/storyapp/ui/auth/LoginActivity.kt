package com.bangkit.storyapp.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.api.ApiClient
import com.bangkit.storyapp.data.datastore.DataStoreInstance
import com.bangkit.storyapp.data.datastore.UserPreference
import com.bangkit.storyapp.data.model.LoginBody
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.databinding.ActivityLoginBinding
import com.bangkit.storyapp.ui.main.MainActivity
import com.bangkit.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@SuppressLint("ClickableViewAccessibility")
class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val loginViewModel by lazy {
        val factory = ViewModelFactory(
            UserRepository(ApiClient.apiClient),
            StoryRepository(ApiClient.apiClient)
        )
        ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLoginButton()
        setupRegisterButton()
        setupObservers()
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startScaleDownAnimation(view)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> startScaleUpAnimation(view)
            }
            false
        }

        binding.btnLogin.setOnClickListener {
            userEmail = binding.edLoginEmail.text.toString()
            val userPassword = binding.edLoginPassword.text.toString()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                showToast(resources.getString(R.string.empty_input))
            } else {
                loginViewModel.userLogin(LoginBody(userEmail, userPassword))
            }
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
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupObservers() {
        loginViewModel.loginResponse.observe(this) { response ->
            lifecycleScope.launch {
                userPreference.updateUserLoginStatusAndToken(true, response.loginResult.token)
                userPreference.updateUsernameAndEmail(response.loginResult.name, userEmail)
            }
            startActivity(Intent(this, MainActivity::class.java))
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.tvLogin.visibility = View.GONE
                binding.progressBarLogin.visibility = View.VISIBLE
            } else {
                binding.tvLogin.visibility = View.VISIBLE
                binding.progressBarLogin.visibility = View.GONE
            }
        }

        loginViewModel.errorCode.observe(this) { code ->
            if (code == 400) {
                showToast(resources.getString(R.string.incorrect_email_format))
            } else {
                showToast(resources.getString(R.string.incorrect_password_format))
            }
        }

        loginViewModel.exception.observe(this) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.cannot_connect_to_server))
                loginViewModel.resetExceptionValue()
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