package com.bangkit.storyapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.datastore.DataStoreInstance
import com.bangkit.storyapp.data.datastore.UserPreference
import com.bangkit.storyapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButton()
        setupDataStoreObservers()
    }

    private fun setupButton() {
        binding.actionLanguage.setOnClickListener {
            requireContext().startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.actionLogout.setOnClickListener {
            findNavController().navigate(R.id.nav_logout_dialog)
        }
    }

    private fun setupDataStoreObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                userPreference.username.collect { username ->
                    binding.tvUsername.text = username
                }
            }
            launch {
                userPreference.userEmail.collect { userEmail ->
                    binding.tvUserEmail.text = userEmail
                }
            }
        }
    }
}