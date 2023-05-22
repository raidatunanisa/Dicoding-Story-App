package com.raida.intermediatesubmission.ui

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.raida.intermediatesubmission.R
import com.raida.intermediatesubmission.databinding.ActivityMapsBinding
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.viewmodel.MapsViewModel
import com.raida.intermediatesubmission.viewmodel.ViewModelFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        mapsViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MapsViewModel::class.java]
        mapsViewModel.getUser().observe(this@MapsActivity) { preference ->
            val token = getString(R.string.bearer) + preference.token
            mapsViewModel.getStoriesWithLocation(token)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
        mapsViewModel.listStories.observe(this@MapsActivity) { story ->
            story.forEach { location ->
                val latLng = LatLng(location.lat as Double, location.lon as Double)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.addMarker(MarkerOptions().position(latLng).title(location.name).snippet(location.description))
            }
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

}