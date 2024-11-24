package com.bangkit.storyapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.datastore.DataStoreInstance
import com.bangkit.storyapp.data.datastore.UserPreference
import com.bangkit.storyapp.databinding.ActivitySplashBinding
import com.bangkit.storyapp.ui.auth.LoginActivity
import com.bangkit.storyapp.ui.main.MainActivity
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen", "SourceLockedOrientationActivity")
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    private val binding by viewBinding(ActivitySplashBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupOrientation()
        setupLogoAnimation()
        setupDataStoreObservers()
    }

    private fun setupOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setupLogoAnimation() {
        binding.ivLogo.animate().apply {
            duration = 2000
            rotationYBy(360f)
        }
    }

    private fun setupDataStoreObservers() {
        lifecycleScope.launch {
            userPreference.isUserLogin.collect { isLogin ->
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isLogin) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    }
                    finish()
                }, 2000)
            }
        }
    }
}