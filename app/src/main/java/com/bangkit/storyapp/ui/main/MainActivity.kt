package com.bangkit.storyapp.ui.main

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.databinding.ActivityMainBinding

@SuppressLint("SourceLockedOrientationActivity")
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMenu()
    }

    private fun setupMenu() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.bottomNavView.setupWithNavController(navController)
    }
}