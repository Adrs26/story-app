package com.bangkit.storyapp.ui.maps

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.datastore.DataStoreInstance
import com.bangkit.storyapp.data.datastore.UserPreference
import com.bangkit.storyapp.data.remote.api.ApiClient
import com.bangkit.storyapp.data.remote.api.ApiClientBearer
import com.bangkit.storyapp.data.remote.model.Story
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.data.repository.UserRepository
import com.bangkit.storyapp.databinding.ActivityMapsBinding
import com.bangkit.storyapp.ui.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(R.layout.activity_maps), OnMapReadyCallback {
    private val binding by viewBinding(ActivityMapsBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var stories: List<Story>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getStoriesLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        if (this::stories.isInitialized) {
            setupLocationBounds(stories)
        }
    }

    private fun getStoriesLocation() {
        lifecycleScope.launch {
            userPreference.userToken.collect { token ->
                setupViewModel(token)
                mapsViewModel.getStoriesLocation()
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = ViewModelFactory(
            UserRepository(ApiClient.apiClient),
            StoryRepository(ApiClientBearer.create(token))
        )
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]
    }

    private fun setupObservers() {
        mapsViewModel.stories.observe(this) { stories ->
            setupMapFragment()
            this.stories = stories
        }

        mapsViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.pbMap.visibility = View.VISIBLE
            } else {
                binding.pbMap.visibility = View.GONE
            }
        }

        mapsViewModel.exception.observe(this) { exception ->
            if (exception) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
                mapsViewModel.resetExceptionValue()
            }
        }
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupLocationBounds(stories: List<Story>) {
        val boundsBuilder = LatLngBounds.builder()

        stories.forEach { story ->
            val latLng = LatLng(story.lat!!, story.lon!!)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(story.description)
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }
}