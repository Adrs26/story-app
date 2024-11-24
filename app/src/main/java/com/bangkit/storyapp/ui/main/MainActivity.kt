package com.bangkit.storyapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.databinding.ActivityMainBinding
import com.bangkit.storyapp.ui.addstory.AddStoryActivity

@SuppressLint("SourceLockedOrientationActivity")
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupOrientation()
        setupBottomNav()
        getIntentIfAvailable()
    }

    private fun setupOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setupBottomNav() {
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.bottomNavView.setupWithNavController(navController)

        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_add_story -> {
                    startActivity(Intent(this, AddStoryActivity::class.java))
                    false
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    true
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_detail) {
                hideBottomNav()
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }
    }

    private fun getIntentIfAvailable() {
        val destinationFragmentId = intent.getIntExtra("destination_fragment", -1)
        if (destinationFragmentId != -1) {
            navController.navigate(destinationFragmentId)
        }
    }

    private fun hideBottomNav() {
        startAnimation(binding.bottomNavView, R.anim.slide_out_left)
        binding.bottomNavView.visibility = View.GONE
    }

    fun showBottomNav() {
        startAnimation(binding.bottomNavView, R.anim.slide_in_left)
        binding.bottomNavView.visibility = View.VISIBLE
    }

    private fun startAnimation(view: View, animation: Int) {
        view.clearAnimation()
        view.startAnimation(AnimationUtils.loadAnimation(this, animation))
    }
}